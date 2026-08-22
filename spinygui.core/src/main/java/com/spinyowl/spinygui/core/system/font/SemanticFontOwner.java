package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Owns semantic font identities and their monotonic generation on one explicitly installed thread.
 *
 * <p>Production font mutation aliases publish through this owner. Font preparation is staged before
 * an immutable registry snapshot and its generation are published together.
 */
public final class SemanticFontOwner {
  private final List<FontRequest> builtIns;
  private final FontChainResolver resolver = new SemanticFontChainResolver(this);
  private final List<MutationPreflight> mutationPreflights = new ArrayList<>();
  private final List<ResourceCloseDependency> resourceCloseDependencies = new ArrayList<>();
  private Snapshot snapshot = new Snapshot(0, Map.of(), Map.of());
  private Lifecycle lifecycle = Lifecycle.NEW;
  private Thread ownerThread;
  private int activeReadUseScopes;
  private boolean mutationInProgress;

  /**
   * Creates an uninstalled owner whose supplied requests form its atomic built-in bootstrap.
   *
   * @param builtIns complete built-in bootstrap requests
   */
  public SemanticFontOwner(List<FontRequest> builtIns) {
    this.builtIns = List.copyOf(builtIns);
  }

  /**
   * Installs this owner on the current thread and publishes all built-ins as one transaction.
   *
   * @return the installed thread and bootstrap result
   * @throws IllegalStateException if installation was already attempted successfully or bootstrap
   *     preparation fails
   */
  public Installation install() {
    requireNoMutationInProgress();
    if (lifecycle != Lifecycle.NEW) {
      throw new IllegalStateException("Semantic font owner is already installed");
    }

    ownerThread = Thread.currentThread();
    lifecycle = Lifecycle.INSTALLING;
    Mutation bootstrap;
    try {
      bootstrap = runMutation(() -> mutateBatch(builtIns));
    } catch (RuntimeException | Error failure) {
      ownerThread = null;
      lifecycle = Lifecycle.NEW;
      throw failure;
    }
    if (bootstrap.outcome() == MutationOutcome.REJECTED) {
      ownerThread = null;
      lifecycle = Lifecycle.NEW;
      throw new IllegalStateException("Semantic font built-in bootstrap failed");
    }

    lifecycle = Lifecycle.ACTIVE;
    return new Installation(ownerThread, bootstrap);
  }

  /** Returns the exact thread on which this owner was installed. */
  public Thread ownerThread() {
    requireActiveOwnerThread();
    return ownerThread;
  }

  /** Returns the current monotonic semantic generation. */
  public long generation() {
    requireActiveOwnerThread();
    return snapshot.generation();
  }

  /** Returns one immutable generation and identity observation. */
  public Observation observation() {
    requireActiveOwnerThread();
    Snapshot current = snapshot;
    return new Observation(current.generation(), List.copyOf(current.identities().values()));
  }

  /** Returns the immutable registered descriptor view owned by the current semantic snapshot. */
  public List<Font> registeredFonts() {
    requireActiveOwnerThread();
    return List.copyOf(snapshot.descriptors().values());
  }

  /** Returns the current immutable descriptor view for owner-internal resolution. */
  Collection<Font> registeredFontView() {
    requireActiveOwnerThread();
    return snapshot.descriptors().values();
  }

  /**
   * Returns this owner's single resolver after verifying active owner-thread use.
   *
   * @return resolver backed only by this owner's current immutable snapshot
   */
  public FontChainResolver resolver() {
    requireActiveOwnerThread();
    return resolver;
  }

  /**
   * Atomically prepares and publishes the supplied bootstrap requests as one transaction.
   *
   * @param requests bootstrap requests
   * @return transaction result
   */
  public Mutation bootstrap(List<FontRequest> requests) {
    requireMutationAllowed();
    return runMutation(() -> mutateBatch(requests));
  }

  /**
   * Adds or replaces one semantic face after successful staged preparation.
   *
   * @param request font preparation request
   * @return transaction result
   */
  public Mutation add(FontRequest request) {
    requireMutationAllowed();
    return runMutation(() -> mutateOne(request));
  }

  /**
   * Reloads one semantic face after successful staged preparation.
   *
   * @param request font preparation request
   * @return transaction result
   */
  public Mutation reload(FontRequest request) {
    requireMutationAllowed();
    return runMutation(() -> mutateOne(request));
  }

  /** Loads, parses, and publishes one descriptor whose semantic traits are discovered from bytes. */
  public Mutation load(FontLoadRequest request) {
    requireMutationAllowed();
    return runMutation(() -> mutateLoad(request));
  }

  /**
   * Loads one descriptor and coordinates publication of its dependent resource aggregate under the
   * same mutation guard. If resource publication fails, the prior semantic snapshot is restored
   * before the failure escapes and no read/use scope can observe the provisional state.
   *
   * @param request staged descriptor load
   * @param resourcePublication dependent resource publication performed after provisional semantic
   *     publication
   * @return transaction result after both semantic and resource publication succeed
   */
  public Mutation load(
      FontLoadRequest request, ResourcePublicationCoordinator resourcePublication) {
    Objects.requireNonNull(resourcePublication, "resourcePublication");
    requireMutationAllowed();
    return runMutation(
        () -> {
          Snapshot previous = snapshot;
          Mutation mutation = mutateLoad(request);
          try {
            resourcePublication.publish(mutation);
            return mutation;
          } catch (RuntimeException | Error failure) {
            snapshot = previous;
            throw failure;
          }
        });
  }

  /**
   * Applies one independent semantic transaction per system-font request, preserving sibling
   * results when another request is rejected.
   *
   * @param requests ordered system-font preparation requests
   * @return immutable per-request transaction results
   */
  public List<Mutation> loadSystemFonts(List<FontRequest> requests) {
    requireMutationAllowed();
    return runMutation(
        () -> {
          List<Mutation> results = new ArrayList<>(requests.size());
          for (FontRequest request : List.copyOf(requests)) {
            results.add(mutateOne(request));
          }
          return List.copyOf(results);
        });
  }

  /** Clears a non-empty semantic registry as one generation change. */
  public Mutation clear() {
    requireMutationAllowed();
    return runMutation(this::clearRegistry);
  }

  private Mutation clearRegistry() {
    Snapshot current = snapshot;
    if (current.identities().isEmpty()) {
      return mutation(MutationOutcome.UNCHANGED, current);
    }
    if (current.generation() == Long.MAX_VALUE) {
      return mutation(MutationOutcome.REJECTED, current);
    }
    Snapshot replacement = new Snapshot(current.generation() + 1, Map.of(), Map.of());
    snapshot = replacement;
    return mutation(MutationOutcome.CHANGED, replacement);
  }

  /**
   * Rejects unsupported arbitrary single-face removal without changing semantic state.
   *
   * @param ignored unsupported face key
   * @return rejected transaction result
   */
  public Mutation remove(FaceKey ignored) {
    requireMutationAllowed();
    return runMutation(() -> mutation(MutationOutcome.REJECTED, snapshot));
  }

  /** Verifies that a registry, resolver, measurement, storage, or cache use is owner-thread legal. */
  public void verifyUse() {
    requireUseAllowed();
  }

  /**
   * Registers an owner-thread preflight that may reject a semantic replacement before publication.
   * The callback receives only backend-neutral semantic identities and runs inside the mutation
   * guard; it must not call back into this owner.
   *
   * @param preflight replacement preflight
   * @return idempotent owner-thread registration handle
   */
  public MutationPreflightRegistration registerMutationPreflight(MutationPreflight preflight) {
    Objects.requireNonNull(preflight, "preflight");
    requireUseAllowed();
    mutationPreflights.add(preflight);
    return new OwnerMutationPreflightRegistration(this, preflight, Thread.currentThread());
  }

  /**
   * Registers an owner-thread dependency that must release its resources before coordinated core
   * font close can begin.
   *
   * <p>The dependency is descriptive only: core close rejects before teardown while the returned
   * handle remains registered. This keeps backend state out of semantic identity while preventing
   * a caller from stranding native resources that still depend on core-owned font data.
   *
   * @param description stable description included in rejection diagnostics
   * @return idempotent owner-thread registration handle
   */
  public ResourceCloseDependencyRegistration registerResourceCloseDependency(
      String description) {
    Objects.requireNonNull(description, "description");
    if (description.isBlank()) {
      throw new IllegalArgumentException("Resource close dependency description must not be blank");
    }
    requireUseAllowed();
    ResourceCloseDependency dependency = new ResourceCloseDependency(description);
    resourceCloseDependencies.add(dependency);
    return new OwnerResourceCloseDependencyRegistration(
        this, dependency, Thread.currentThread());
  }

  /**
   * Opens a nestable owner-thread read/use scope.
   *
   * @param kind scope category
   * @return scope that must close on the owner thread
   */
  public ReadUseScope openReadUseScope(ReadUseKind kind) {
    Objects.requireNonNull(kind, "kind");
    requireUseAllowed();
    activeReadUseScopes++;
    return new OwnerReadUseScope(this, Thread.currentThread());
  }

  /**
   * Verifies that coordinated resource close may begin.
   *
   * @return {@code true} when resources still need teardown, or {@code false} after an earlier close
   * @throws IllegalStateException before installation, off the owner thread, during mutation, while
   *     a read/use scope is active, or while a dependent resource registration remains active
   */
  public boolean prepareResourceClose() {
    requireNoMutationInProgress();
    requireInstalledOwnerThread();
    if (lifecycle == Lifecycle.CLOSED) {
      return false;
    }
    if (activeReadUseScopes != 0) {
      throw new IllegalStateException("Semantic font owner cannot close during read/use scope");
    }
    if (!resourceCloseDependencies.isEmpty()) {
      throw new IllegalStateException(
          "Semantic font owner cannot close before dependent resources: "
              + resourceCloseDependencies.stream()
                  .map(ResourceCloseDependency::description)
                  .toList());
    }
    return true;
  }

  /**
   * Marks this owner closed after its lifecycle coordinator has released dependent resources. This
   * transition is idempotent on the owner thread and does not itself free font bytes or native
   * state.
   */
  public void completeCloseAfterResourceTeardown() {
    if (!prepareResourceClose()) {
      return;
    }
    snapshot = new Snapshot(snapshot.generation(), Map.of(), Map.of());
    lifecycle = Lifecycle.CLOSED;
  }

  private Mutation mutateOne(FontRequest request) {
    Snapshot current = snapshot;
    if (current.generation() == Long.MAX_VALUE) {
      return mutation(MutationOutcome.REJECTED, current);
    }
    return mutatePrepared(prepare(request), current);
  }

  private Mutation mutateLoad(FontLoadRequest request) {
    Snapshot current = snapshot;
    if (current.generation() == Long.MAX_VALUE) {
      return mutation(MutationOutcome.REJECTED, current);
    }
    return mutatePrepared(prepare(request), current);
  }

  private Mutation mutatePrepared(PreparedFont prepared, Snapshot current) {
    if (prepared == null) {
      return mutation(MutationOutcome.REJECTED, current);
    }

    Identity previous = current.identities().get(prepared.identity().key());
    if (prepared.identity().equals(previous)) {
      return mutation(MutationOutcome.UNCHANGED, current);
    }
    verifyReplacementAllowed(previous, prepared.identity());

    Map<FaceKey, Identity> replacement = new LinkedHashMap<>(current.identities());
    replacement.put(prepared.identity().key(), prepared.identity());
    Map<FaceKey, Font> descriptors = new LinkedHashMap<>(current.descriptors());
    if (prepared.descriptor() == null) {
      descriptors.remove(prepared.identity().key());
    } else {
      descriptors.put(prepared.identity().key(), prepared.descriptor());
    }
    Snapshot changed =
        new Snapshot(current.generation() + 1, replacement, descriptors);
    snapshot = changed;
    return mutation(MutationOutcome.CHANGED, changed);
  }

  private Mutation mutateBatch(List<FontRequest> requests) {
    Objects.requireNonNull(requests, "requests");
    Snapshot current = snapshot;
    List<FontRequest> requestSnapshot = List.copyOf(requests);
    if (!requestSnapshot.isEmpty() && current.generation() == Long.MAX_VALUE) {
      return mutation(MutationOutcome.REJECTED, current);
    }

    Map<FaceKey, Identity> replacement = new LinkedHashMap<>(current.identities());
    Map<FaceKey, Font> descriptors = new LinkedHashMap<>(current.descriptors());
    for (FontRequest request : requestSnapshot) {
      PreparedFont prepared = prepare(request);
      if (prepared == null) {
        return mutation(MutationOutcome.REJECTED, current);
      }
      Identity previous = replacement.get(prepared.identity().key());
      if (!prepared.identity().equals(previous)) {
        verifyReplacementAllowed(previous, prepared.identity());
      }
      replacement.put(prepared.identity().key(), prepared.identity());
      if (prepared.descriptor() == null) {
        descriptors.remove(prepared.identity().key());
      } else {
        descriptors.put(prepared.identity().key(), prepared.descriptor());
      }
    }

    if (replacement.equals(current.identities())) {
      return mutation(MutationOutcome.UNCHANGED, current);
    }
    Snapshot changed = new Snapshot(current.generation() + 1, replacement, descriptors);
    snapshot = changed;
    return mutation(MutationOutcome.CHANGED, changed);
  }

  private PreparedFont prepare(FontRequest request) {
    Objects.requireNonNull(request, "request");
    try {
      ByteBuffer loaded = request.loader().load();
      if (loaded == null) {
        return null;
      }
      ByteBuffer bytes = loaded.asReadOnlyBuffer();
      request.parser().parse(bytes.asReadOnlyBuffer());
      request.validator().validate(request, bytes.asReadOnlyBuffer());
      FaceKey key =
          new FaceKey(request.family(), request.style(), request.weight(), request.stretch());
      String locator = normalizeLocator(request.locator());
      if (!bytes.hasRemaining()) {
        return null;
      }
      return new PreparedFont(
          new Identity(key, locator, "sha256:" + sha256(bytes.asReadOnlyBuffer())),
          request.descriptor());
    } catch (RuntimeException exception) {
      return null;
    }
  }

  private PreparedFont prepare(FontLoadRequest request) {
    Objects.requireNonNull(request, "request");
    try {
      ByteBuffer loaded = request.loader().load();
      if (loaded == null) {
        return null;
      }
      ByteBuffer bytes = loaded.asReadOnlyBuffer();
      Font descriptor = request.parser().parse(bytes.asReadOnlyBuffer());
      if (descriptor == null || !bytes.hasRemaining()) {
        return null;
      }
      FaceKey key =
          new FaceKey(
              descriptor.fontFamily(),
              descriptor.style().name(),
              descriptor.weight().name(),
              descriptor.stretch().name());
      String locator = normalizeLocator(request.locator());
      return new PreparedFont(
          new Identity(key, locator, "sha256:" + sha256(bytes.asReadOnlyBuffer())),
          descriptor);
    } catch (RuntimeException exception) {
      return null;
    }
  }

  private void requireMutationAllowed() {
    requireNoMutationInProgress();
    requireActiveOwnerThread();
    if (activeReadUseScopes != 0) {
      throw new IllegalStateException("Semantic font mutation is forbidden during read/use scope");
    }
  }

  private void verifyReplacementAllowed(Identity previous, Identity replacement) {
    if (previous == null) {
      return;
    }
    for (MutationPreflight preflight : List.copyOf(mutationPreflights)) {
      preflight.beforeReplacement(previous, replacement);
    }
  }

  private void unregisterMutationPreflight(
      MutationPreflight preflight, Thread registrationThread) {
    requireUseAllowed();
    if (Thread.currentThread() != registrationThread) {
      throw new IllegalStateException(
          "Semantic mutation preflight must close on its registration thread");
    }
    mutationPreflights.remove(preflight);
  }

  private void unregisterResourceCloseDependency(
      ResourceCloseDependency dependency, Thread registrationThread) {
    requireUseAllowed();
    if (Thread.currentThread() != registrationThread) {
      throw new IllegalStateException(
          "Resource close dependency must close on its registration thread");
    }
    resourceCloseDependencies.remove(dependency);
  }

  private void requireUseAllowed() {
    requireNoMutationInProgress();
    requireActiveOwnerThread();
  }

  private void requireNoMutationInProgress() {
    if (mutationInProgress) {
      throw new IllegalStateException("Semantic font mutation is already in progress");
    }
  }

  private <T> T runMutation(Supplier<T> operation) {
    requireNoMutationInProgress();
    mutationInProgress = true;
    try {
      return operation.get();
    } finally {
      mutationInProgress = false;
    }
  }

  private void requireActiveOwnerThread() {
    if (lifecycle == Lifecycle.CLOSED) {
      throw new IllegalStateException("Semantic font owner is closed");
    }
    if (lifecycle != Lifecycle.ACTIVE) {
      throw new IllegalStateException("Semantic font owner is not installed");
    }
    if (Thread.currentThread() != ownerThread) {
      throw new IllegalStateException("Semantic font owner operation requires the install thread");
    }
  }

  private void requireInstalledOwnerThread() {
    if (lifecycle == Lifecycle.NEW || lifecycle == Lifecycle.INSTALLING) {
      throw new IllegalStateException("Semantic font owner is not installed");
    }
    if (Thread.currentThread() != ownerThread) {
      throw new IllegalStateException("Semantic font owner operation requires the install thread");
    }
  }

  private static Mutation mutation(MutationOutcome outcome, Snapshot snapshot) {
    return new Mutation(
        outcome, snapshot.generation(), List.copyOf(snapshot.identities().values()));
  }

  private static String normalize(String value, String field) {
    Objects.requireNonNull(value, field);
    String normalized = value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    if (normalized.isEmpty()) {
      throw new IllegalArgumentException(field + " must not be blank");
    }
    return normalized;
  }

  /**
   * Returns the canonical locator spelling used by semantic identities and compatibility caches.
   *
   * @param locator resource locator
   * @return trimmed, slash-normalized, space-encoded, and dot-segment-normalized locator
   */
  public static String normalizeLocator(String locator) {
    Objects.requireNonNull(locator, "locator");
    String candidate = locator.trim().replace('\\', '/').replace(" ", "%20");
    String normalized = URI.create(candidate).normalize().toString();
    if (normalized.isEmpty()) {
      throw new IllegalArgumentException("locator must not be blank");
    }
    return normalized;
  }

  private static String sha256(ByteBuffer bytes) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] chunk = new byte[Math.min(8192, Math.max(1, bytes.remaining()))];
      while (bytes.hasRemaining()) {
        int length = Math.min(bytes.remaining(), chunk.length);
        bytes.get(chunk, 0, length);
        digest.update(chunk, 0, length);
      }
      return HexFormat.of().formatHex(digest.digest());
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 is unavailable", exception);
    }
  }

  /**
   * Immutable normalized semantic face key.
   *
   * @param family normalized family
   * @param style normalized style
   * @param weight normalized weight
   * @param stretch normalized stretch
   */
  public record FaceKey(String family, String style, String weight, String stretch) {
    public FaceKey {
      family = normalize(family, "family");
      style = normalize(style, "style");
      weight = normalize(weight, "weight");
      stretch = normalize(stretch, "stretch");
    }
  }

  /**
   * Immutable renderer-neutral semantic identity.
   *
   * @param key normalized semantic face key
   * @param normalizedLocator normalized resource locator
   * @param byteRevision SHA-256 byte-content revision
   */
  public record Identity(FaceKey key, String normalizedLocator, String byteRevision) {
    public Identity {
      Objects.requireNonNull(key, "key");
      normalizedLocator = normalizeLocator(normalizedLocator);
      Objects.requireNonNull(byteRevision, "byteRevision");
      if (!byteRevision.matches("sha256:[0-9a-f]{64}")) {
        throw new IllegalArgumentException("byteRevision must contain a SHA-256 revision");
      }
    }
  }

  /**
   * Immutable observation of semantic identity content under one generation.
   *
   * @param generation observed generation
   * @param identities immutable identities published under that generation
   */
  public record Observation(long generation, List<Identity> identities) {
    public Observation {
      if (generation < 0) {
        throw new IllegalArgumentException("generation must be non-negative");
      }
      identities = List.copyOf(identities);
    }
  }

  /**
   * One staged semantic font request.
   *
   * @param family declared family
   * @param style declared style
   * @param weight declared weight
   * @param stretch declared stretch
   * @param locator resource locator
   * @param loader byte loading stage
   * @param parser font parsing stage
   * @param validator semantic validation stage
   * @param descriptor immutable public descriptor, or {@code null} for an owner-only request
   */
  public record FontRequest(
      String family,
      String style,
      String weight,
      String stretch,
      String locator,
      FontBytesLoader loader,
      FontBytesParser parser,
      FontBytesValidator validator,
      Font descriptor) {
    public FontRequest(
        String family,
        String style,
        String weight,
        String stretch,
        String locator,
        FontBytesLoader loader,
        FontBytesParser parser,
        FontBytesValidator validator) {
      this(family, style, weight, stretch, locator, loader, parser, validator, null);
    }

    public FontRequest {
      Objects.requireNonNull(loader, "loader");
      Objects.requireNonNull(parser, "parser");
      Objects.requireNonNull(validator, "validator");
    }

    /**
     * Creates a request from an existing immutable font descriptor.
     *
     * @param font immutable descriptor
     * @param loader byte loading stage
     * @param parser font parsing stage
     * @param validator semantic validation stage
     * @return staged semantic font request
     */
    public static FontRequest from(
        Font font, FontBytesLoader loader, FontBytesParser parser, FontBytesValidator validator) {
      Objects.requireNonNull(font, "font");
      return new FontRequest(
          font.fontFamily(),
          font.style().name(),
          font.weight().name(),
          font.stretch().name(),
          font.path(),
          loader,
          parser,
          validator,
          font);
    }
  }

  /**
   * One staged load whose descriptor traits are parsed from the loaded bytes.
   *
   * @param locator resource locator
   * @param loader byte loading stage
   * @param parser descriptor parsing stage
   */
  public record FontLoadRequest(
      String locator, FontBytesLoader loader, FontDescriptorParser parser) {
    public FontLoadRequest {
      Objects.requireNonNull(locator, "locator");
      Objects.requireNonNull(loader, "loader");
      Objects.requireNonNull(parser, "parser");
    }
  }

  /** Coordinates dependent resource publication inside one semantic mutation. */
  @FunctionalInterface
  public interface ResourcePublicationCoordinator {
    /** Publishes resources for the provisional semantic mutation or throws to roll it back. */
    void publish(Mutation mutation);
  }

  /** Backend-neutral callback that may reject a semantic same-key identity replacement. */
  @FunctionalInterface
  public interface MutationPreflight {
    /**
     * Checks one prepared replacement before the new identity or generation is published.
     *
     * @param previous currently published identity
     * @param replacement prepared replacement identity with the same face key
     */
    void beforeReplacement(Identity previous, Identity replacement);
  }

  /** Owner-thread handle for one registered semantic mutation preflight. */
  public interface MutationPreflightRegistration extends AutoCloseable {
    /** Removes the preflight; repeated close is a no-op. */
    @Override
    void close();
  }

  /** Owner-thread handle for one dependent-resource close guard. */
  public interface ResourceCloseDependencyRegistration extends AutoCloseable {
    /** Removes the dependency after its resources are released; repeated close is a no-op. */
    @Override
    void close();
  }

  /**
   * Result of one semantic transaction.
   *
   * @param outcome transaction outcome
   * @param generation resulting generation
   * @param identities immutable resulting identities
   */
  public record Mutation(MutationOutcome outcome, long generation, List<Identity> identities) {
    public Mutation {
      Objects.requireNonNull(outcome, "outcome");
      if (generation < 0) {
        throw new IllegalArgumentException("generation must be non-negative");
      }
      identities = List.copyOf(identities);
    }
  }

  /**
   * Successful installation result.
   *
   * @param ownerThread installed owner thread
   * @param bootstrap atomic bootstrap result
   */
  public record Installation(Thread ownerThread, Mutation bootstrap) {
    public Installation {
      Objects.requireNonNull(ownerThread, "ownerThread");
      Objects.requireNonNull(bootstrap, "bootstrap");
    }
  }

  /** Semantic transaction result kind. */
  public enum MutationOutcome {
    CHANGED,
    UNCHANGED,
    REJECTED
  }

  /** Owner-thread read/use scope category. */
  public enum ReadUseKind {
    MEASUREMENT,
    LAYOUT,
    RENDER
  }

  /** Loads bytes for one semantic font preparation. */
  @FunctionalInterface
  public interface FontBytesLoader {
    /** @return loaded bytes, or {@code null} when loading fails */
    ByteBuffer load();
  }

  /** Parses loaded font bytes and throws when they are unusable. */
  @FunctionalInterface
  public interface FontBytesParser {
    /** @param bytes read-only loaded bytes */
    void parse(ByteBuffer bytes);
  }

  /** Parses one immutable descriptor from read-only loaded bytes. */
  @FunctionalInterface
  public interface FontDescriptorParser {
    /** @return parsed immutable descriptor */
    Font parse(ByteBuffer bytes);
  }

  /** Performs semantic validation after bytes have parsed successfully. */
  @FunctionalInterface
  public interface FontBytesValidator {
    /**
     * @param request semantic request being validated
     * @param bytes read-only parsed bytes
     */
    void validate(FontRequest request, ByteBuffer bytes);
  }

  /** A nestable owner-thread read/use scope. */
  public interface ReadUseScope extends AutoCloseable {
    @Override
    void close();
  }

  private void closeScope(Thread openingThread) {
    requireActiveOwnerThread();
    if (Thread.currentThread() != openingThread) {
      throw new IllegalStateException("Read/use scope must close on its opening thread");
    }
    activeReadUseScopes--;
  }

  private record Snapshot(
      long generation, Map<FaceKey, Identity> identities, Map<FaceKey, Font> descriptors) {
    private Snapshot(long generation, Map<FaceKey, Identity> identities) {
      this(generation, identities, Map.of());
    }

    private Snapshot {
      identities = Collections.unmodifiableMap(new LinkedHashMap<>(identities));
      descriptors = Collections.unmodifiableMap(new LinkedHashMap<>(descriptors));
    }
  }

  private record PreparedFont(Identity identity, Font descriptor) {}

  private static final class ResourceCloseDependency {
    private final String description;

    private ResourceCloseDependency(String description) {
      this.description = description;
    }

    private String description() {
      return description;
    }
  }

  private static final class OwnerReadUseScope implements ReadUseScope {
    private final SemanticFontOwner owner;
    private final Thread openingThread;
    private boolean closed;

    private OwnerReadUseScope(SemanticFontOwner owner, Thread openingThread) {
      this.owner = owner;
      this.openingThread = openingThread;
    }

    @Override
    public void close() {
      if (closed) {
        return;
      }
      owner.closeScope(openingThread);
      closed = true;
    }
  }

  private static final class OwnerMutationPreflightRegistration
      implements MutationPreflightRegistration {
    private final SemanticFontOwner owner;
    private final MutationPreflight preflight;
    private final Thread registrationThread;
    private boolean closed;

    private OwnerMutationPreflightRegistration(
        SemanticFontOwner owner, MutationPreflight preflight, Thread registrationThread) {
      this.owner = owner;
      this.preflight = preflight;
      this.registrationThread = registrationThread;
    }

    @Override
    public void close() {
      if (closed) {
        return;
      }
      owner.unregisterMutationPreflight(preflight, registrationThread);
      closed = true;
    }
  }

  private static final class OwnerResourceCloseDependencyRegistration
      implements ResourceCloseDependencyRegistration {
    private final SemanticFontOwner owner;
    private final ResourceCloseDependency dependency;
    private final Thread registrationThread;
    private boolean closed;

    private OwnerResourceCloseDependencyRegistration(
        SemanticFontOwner owner,
        ResourceCloseDependency dependency,
        Thread registrationThread) {
      this.owner = owner;
      this.dependency = dependency;
      this.registrationThread = registrationThread;
    }

    @Override
    public void close() {
      if (closed) {
        return;
      }
      owner.unregisterResourceCloseDependency(dependency, registrationThread);
      closed = true;
    }
  }

  private enum Lifecycle {
    NEW,
    INSTALLING,
    ACTIVE,
    CLOSED
  }
}
