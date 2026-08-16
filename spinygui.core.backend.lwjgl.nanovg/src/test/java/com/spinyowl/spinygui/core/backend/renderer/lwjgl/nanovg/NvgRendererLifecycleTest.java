package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.FontResourceObservation;
import com.spinyowl.spinygui.core.system.font.FontSemanticObservation;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lwjgl.system.MemoryUtil;

class NvgRendererLifecycleTest {
  private static final long CONTEXT = 41L;

  @TempDir Path fontDirectory;

  private FontServiceImpl fontService;

  @BeforeEach
  void installFontOwner() {
    fontService = NvgFontTestOwner.install();
  }

  @AfterEach
  void closeFontOwner() {
    fontService.close();
  }

  @DisplayName("P4 T1: renderer state, context identity, repeat calls, and use-after-destroy")
  @Test
  void rendererTransitionsAreExplicitAndContextReplacementIsRejected() {
    RecordingContextApi contexts = new RecordingContextApi(CONTEXT);
    NvgRenderer renderer = renderer(contexts);

    assertAll(
        () -> assertEquals(NvgRenderer.State.NEW, renderer.state()),
        () -> assertThrows(IllegalStateException.class, () -> renderer.render(0, null, null, null)),
        () ->
            assertThrows(
                IllegalStateException.class,
                () -> renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT)));

    renderer.initialize();
    String face = renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT);

    assertAll(
        () -> assertEquals(NvgRenderer.State.INITIALIZED, renderer.state()),
        () -> assertSame(Thread.currentThread(), renderer.uiThread()),
        () -> assertEquals(CONTEXT, renderer.contextIdentity()),
        () -> assertNotNull(face),
        () -> assertEquals(face, renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT)),
        () -> assertThrows(IllegalStateException.class, renderer::initialize),
        () -> assertEquals(1, contexts.creations),
        () ->
            assertThrows(
                IllegalStateException.class,
                () -> renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT + 1)));

    renderer.destroy();
    renderer.destroy();

    assertAll(
        () -> assertEquals(NvgRenderer.State.DESTROYED, renderer.state()),
        () -> assertEquals(0, renderer.contextIdentity()),
        () -> assertEquals(1, contexts.deletions),
        () -> assertThrows(IllegalStateException.class, renderer::initialize),
        () -> assertThrows(IllegalStateException.class, () -> renderer.render(0, null, null, null)),
        () ->
            assertThrows(
                IllegalStateException.class,
                () -> renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT)));

    RecordingContextApi neverInitializedContexts = new RecordingContextApi(CONTEXT + 1);
    NvgRenderer neverInitialized = renderer(neverInitializedContexts);
    neverInitialized.destroy();
    neverInitialized.destroy();
    assertAll(
        () -> assertEquals(NvgRenderer.State.DESTROYED, neverInitialized.state()),
        () -> assertEquals(0, neverInitializedContexts.creations),
        () -> assertEquals(0, neverInitializedContexts.deletions));
  }

  @DisplayName("P4 T1: every renderer operation is confined to its UI thread")
  @Test
  void initializedOperationsRejectOffThreadWithoutStateOrContextMigration() throws Exception {
    RecordingContextApi newContexts = new RecordingContextApi(CONTEXT + 1);
    NvgRenderer newRenderer = renderer(newContexts);
    Throwable newInitializeFailure = captureWorkerFailure(newRenderer::initialize);

    RecordingContextApi contexts = new RecordingContextApi(CONTEXT);
    NvgRenderer renderer = renderer(contexts);
    renderer.initialize();
    renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT);

    Throwable initializeFailure = captureWorkerFailure(renderer::initialize);
    Throwable renderFailure =
        captureWorkerFailure(() -> renderer.render(0, null, null, null));
    Throwable faceFailure =
        captureWorkerFailure(() -> renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT));
    Throwable destroyFailure = captureWorkerFailure(renderer::destroy);

    assertAll(
        () -> assertInstanceOf(IllegalStateException.class, newInitializeFailure),
        () -> assertEquals(NvgRenderer.State.NEW, newRenderer.state()),
        () -> assertEquals(0, newContexts.creations),
        () -> assertInstanceOf(IllegalStateException.class, initializeFailure),
        () -> assertInstanceOf(IllegalStateException.class, renderFailure),
        () -> assertInstanceOf(IllegalStateException.class, faceFailure),
        () -> assertInstanceOf(IllegalStateException.class, destroyFailure),
        () -> assertEquals(NvgRenderer.State.INITIALIZED, renderer.state()),
        () -> assertSame(Thread.currentThread(), renderer.uiThread()),
        () -> assertEquals(CONTEXT, renderer.contextIdentity()),
        () -> assertEquals(1, contexts.creations),
        () -> assertEquals(0, contexts.deletions));

    newRenderer.destroy();
    renderer.destroy();
  }

  @DisplayName("P4 T1: initializing and destroying reject reentrant renderer use")
  @Test
  void transitionalStatesAreObservableAndRejectReentrantOperations() {
    RecordingContextApi contexts = new RecordingContextApi(CONTEXT);
    NvgRenderer renderer = renderer(contexts);
    contexts.onCreate =
        () ->
            assertAll(
                () -> assertEquals(NvgRenderer.State.INITIALIZING, renderer.state()),
                () -> assertThrows(IllegalStateException.class, renderer::initialize),
                () ->
                    assertThrows(
                        IllegalStateException.class,
                        () -> renderer.render(0, null, null, null)),
                () ->
                    assertThrows(
                        IllegalStateException.class,
                        () -> renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT)),
                () -> assertThrows(IllegalStateException.class, renderer::destroy));
    contexts.onDelete =
        () ->
            assertAll(
                () -> assertEquals(NvgRenderer.State.DESTROYING, renderer.state()),
                () -> assertThrows(IllegalStateException.class, renderer::initialize),
                () ->
                    assertThrows(
                        IllegalStateException.class,
                        () -> renderer.render(0, null, null, null)),
                () ->
                    assertThrows(
                        IllegalStateException.class,
                        () -> renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT)),
                () -> assertThrows(IllegalStateException.class, renderer::destroy));

    renderer.initialize();
    renderer.destroy();

    assertEquals(NvgRenderer.State.DESTROYED, renderer.state());
  }

  @DisplayName("P4 T1: post-create initialization failure rolls context back and cannot retry")
  @Test
  void partialInitializationFailureTransitionsToFailedAndRollsBackContext() {
    RecordingContextApi contexts = new RecordingContextApi(CONTEXT);
    NvgRenderer renderer =
        new NvgRenderer(
            true,
            DiagnosticSession.disabled(),
            contexts,
            (context, name, bytes) -> 7,
            (owner, registry) -> {
              throw new IllegalStateException("preflight binding failed");
            });

    IllegalStateException failure = assertThrows(IllegalStateException.class, renderer::initialize);

    assertAll(
        () -> assertEquals("preflight binding failed", failure.getMessage()),
        () -> assertEquals(NvgRenderer.State.FAILED, renderer.state()),
        () -> assertEquals(0, renderer.contextIdentity()),
        () -> assertEquals(1, contexts.creations),
        () -> assertEquals(1, contexts.deletions),
        () -> assertThrows(IllegalStateException.class, renderer::initialize),
        () ->
            assertThrows(
                IllegalStateException.class,
                () -> renderer.render(0, null, null, null)),
        () ->
            assertThrows(
                IllegalStateException.class,
                () -> renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT)));

    renderer.destroy();
    assertEquals(NvgRenderer.State.DESTROYED, renderer.state());
    assertEquals(1, contexts.deletions);
  }

  @DisplayName("P4 T1: active face rejects semantic replacement until renderer destruction")
  @Test
  void activeFaceRejectsReplacementBeforeCorePublicationUntilRendererIsDestroyed()
      throws Exception {
    Path initial = copyResourceFont("fonts/Roboto-Regular.ttf", "active initial.ttf");
    Font active = fontService.loadFont(initial.toString());
    RecordingContextApi firstContexts = new RecordingContextApi(CONTEXT);
    NvgRenderer first = renderer(firstContexts);
    first.initialize();
    first.fontFace(active, CONTEXT);
    FontSemanticObservation semanticBefore = fontService.semanticObservation();
    FontResourceObservation resourcesBefore = fontService.resourceObservation();
    Path replacement = copyResourceFont("fonts/Roboto-Regular.ttf", "active replacement.ttf");

    IllegalStateException rejection =
        assertThrows(
            IllegalStateException.class,
            () -> fontService.loadFont(replacement.toString()));

    assertAll(
        () -> assertTrue(rejection.getMessage().contains("Destroy")),
        () -> assertEquals(semanticBefore, fontService.semanticObservation()),
        () -> assertEquals(resourcesBefore, fontService.resourceObservation()),
        () -> assertEquals(NvgRenderer.State.INITIALIZED, first.state()));

    first.destroy();
    Font accepted = fontService.loadFont(replacement.toString());

    assertAll(
        () -> assertEquals(semanticBefore.generation() + 1, fontService.semanticObservation().generation()),
        () -> assertEquals(replacement.toString(), accepted.path()),
        () ->
            assertThrows(
                IllegalStateException.class,
                () -> first.fontFace(accepted, CONTEXT)));

    RecordingContextApi replacementContexts = new RecordingContextApi(CONTEXT + 1);
    NvgRenderer replacementRenderer = renderer(replacementContexts);
    replacementRenderer.initialize();
    assertNotNull(replacementRenderer.fontFace(accepted, CONTEXT + 1));
    replacementRenderer.destroy();
  }

  @DisplayName("P4 T2: faces, backing bytes, and submitted views belong to one context")
  @Test
  void fontResourcesAreContextOwnedAndCannotReuseAcrossRenderers() throws Exception {
    long generationBefore = fontService.semanticObservation().generation();
    RecordingContextApi firstContexts = new RecordingContextApi(CONTEXT);
    AtomicReference<ByteBuffer> firstSubmittedView = new AtomicReference<>();
    AtomicInteger firstCreations = new AtomicInteger();
    NvgRenderer first =
        renderer(
            firstContexts,
            (context, name, bytes) -> {
              assertEquals(CONTEXT, context);
              firstSubmittedView.set(bytes);
              return 100 + firstCreations.incrementAndGet();
            });
    first.initialize();

    String firstFace = first.fontFace(Font.ROBOTO_REGULAR, CONTEXT);
    String reusedFace = first.fontFace(Font.ROBOTO_REGULAR, CONTEXT);
    NvgFontResourceObservation firstObservation = first.fontResourceObservation();
    ByteBuffer firstOwnedBuffer = onlyOwnedBuffer(first);

    RecordingContextApi secondContexts = new RecordingContextApi(CONTEXT + 1);
    AtomicInteger secondCreations = new AtomicInteger();
    NvgRenderer second =
        renderer(
            secondContexts,
            (context, name, bytes) -> {
              assertEquals(CONTEXT + 1, context);
              return 200 + secondCreations.incrementAndGet();
            });
    second.initialize();
    String secondFace = second.fontFace(Font.ROBOTO_REGULAR, CONTEXT + 1);
    NvgFontResourceObservation secondObservation = second.fontResourceObservation();

    assertAll(
        () -> assertEquals(firstFace, reusedFace),
        () -> assertNotEquals(firstFace, secondFace),
        () -> assertEquals(1, firstCreations.get()),
        () -> assertEquals(1, secondCreations.get()),
        () -> assertEquals(CONTEXT, firstObservation.contextIdentity()),
        () -> assertEquals(CONTEXT + 1, secondObservation.contextIdentity()),
        () -> assertEquals(1, firstObservation.contextCount()),
        () -> assertEquals(1, firstObservation.faceEntries()),
        () -> assertEquals(1, firstObservation.bufferEntries()),
        () -> assertEquals(0, firstObservation.retainedSubmittedViews()),
        () ->
            assertThrows(
                UnsupportedOperationException.class,
                () -> firstObservation.retainedSemanticIdentities().clear()),
        () -> assertNotSame(firstOwnedBuffer, firstSubmittedView.get()),
        () ->
            assertEquals(
                MemoryUtil.memAddress(firstOwnedBuffer),
                MemoryUtil.memAddress(firstSubmittedView.get())),
        () ->
            assertThrows(
                IllegalStateException.class,
                () -> first.fontFace(Font.ROBOTO_REGULAR, CONTEXT + 1)),
        () -> assertEquals(generationBefore, fontService.semanticObservation().generation()));

    first.destroy();
    second.destroy();
  }

  @DisplayName("P4 T2: failed face creation retains one bounded retry state")
  @Test
  void failedFaceCreationIsBoundedAndSuccessfulRetryPublishesOnce() {
    long generationBefore = fontService.semanticObservation().generation();
    RecordingContextApi contexts = new RecordingContextApi(CONTEXT);
    AtomicInteger attempts = new AtomicInteger();
    NvgRenderer renderer =
        renderer(
            contexts,
            (context, name, bytes) -> {
              int attempt = attempts.incrementAndGet();
              if (attempt == 1) {
                return -1;
              }
              if (attempt == 2) {
                throw new IllegalStateException("injected face creation failure");
              }
              if (attempt == 3) {
                throw new AssertionError("injected face creation error");
              }
              return 77;
            });
    renderer.initialize();

    assertNull(renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT));
    NvgFontResourceObservation firstFailure = renderer.fontResourceObservation();
    IllegalStateException injectedFailure =
        assertThrows(
            IllegalStateException.class,
            () -> renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT));
    NvgFontResourceObservation secondFailure = renderer.fontResourceObservation();
    AssertionError injectedError =
        assertThrows(
            AssertionError.class,
            () -> renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT));
    NvgFontResourceObservation thirdFailure = renderer.fontResourceObservation();
    String accepted = renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT);
    NvgFontResourceObservation afterRetry = renderer.fontResourceObservation();

    assertAll(
        () -> assertEquals(1, firstFailure.bufferEntries()),
        () -> assertEquals(0, firstFailure.faceEntries()),
        () -> assertEquals(1, firstFailure.retryableFaceFailures()),
        () -> assertEquals(1, firstFailure.faceCreationFailures()),
        () -> assertEquals(1, secondFailure.bufferEntries()),
        () -> assertEquals(1, secondFailure.retryableFaceFailures()),
        () -> assertEquals(2, secondFailure.faceCreationFailures()),
        () -> assertEquals("injected face creation failure", injectedFailure.getMessage()),
        () -> assertEquals(1, thirdFailure.bufferEntries()),
        () -> assertEquals(1, thirdFailure.retryableFaceFailures()),
        () -> assertEquals(3, thirdFailure.faceCreationFailures()),
        () -> assertEquals("injected face creation error", injectedError.getMessage()),
        () -> assertNotNull(accepted),
        () -> assertEquals(1, afterRetry.bufferEntries()),
        () -> assertEquals(1, afterRetry.faceEntries()),
        () -> assertEquals(0, afterRetry.retryableFaceFailures()),
        () -> assertEquals(3, afterRetry.faceCreationFailures()),
        () -> assertEquals(4, attempts.get()),
        () -> assertEquals(generationBefore, fontService.semanticObservation().generation()));

    renderer.destroy();
  }

  @DisplayName("P4 T2: semantic change retires unused old-version backend entries")
  @Test
  void semanticObservationKeepsOnlyCurrentUnusedBackendIdentity() throws Exception {
    Path initial = copyResourceFont("fonts/Roboto-Regular.ttf", "observed initial.ttf");
    Font firstFont = fontService.loadFont(initial.toString());
    RecordingContextApi contexts = new RecordingContextApi(CONTEXT);
    NvgRenderer renderer = renderer(contexts);
    renderer.initialize();
    assertEquals("A", renderer.displayText(firstFont, "A"));
    NvgFontResourceObservation before = renderer.fontResourceObservation();

    Path replacement = copyResourceFont("fonts/Roboto-Regular.ttf", "observed replacement.ttf");
    Font replacementFont = fontService.loadFont(replacement.toString());
    assertEquals("A", renderer.displayText(replacementFont, "A"));
    NvgFontResourceObservation after = renderer.fontResourceObservation();
    IllegalStateException staleDescriptor =
        assertThrows(IllegalStateException.class, () -> renderer.displayText(firstFont, "A"));
    NvgFontResourceObservation afterStaleRejection = renderer.fontResourceObservation();
    var replacementIdentity =
        Font.semanticOwner().observation().identities().stream()
            .filter(identity -> identity.normalizedLocator().contains("observed%20replacement.ttf"))
            .findFirst()
            .orElseThrow();

    assertAll(
        () -> assertEquals(before.semanticGeneration() + 1, after.semanticGeneration()),
        () -> assertEquals(1, before.bufferEntries()),
        () -> assertEquals(1, before.fontInfoEntries()),
        () -> assertEquals(1, after.bufferEntries()),
        () -> assertEquals(1, after.fontInfoEntries()),
        () -> assertEquals(java.util.Set.of(replacementIdentity), after.retainedSemanticIdentities()),
        () -> assertEquals(0, after.faceEntries()),
        () -> assertEquals(0, after.retryableFaceFailures()),
        () -> assertTrue(staleDescriptor.getMessage().contains("stale")),
        () -> assertEquals(after, afterStaleRejection));

    renderer.destroy();
  }

  @DisplayName("P4 T2: installed identity replaces unused compatibility retention")
  @Test
  void unregisteredCompatibilityStateYieldsToInstalledSemanticIdentity() throws Exception {
    Path fontPath = copyResourceFont("fonts/Roboto-Regular.ttf", "compatibility identity.ttf");
    Font compatibilityDescriptor = fontService.loadFont(fontPath.toString());
    fontService.clear();
    RecordingContextApi contexts = new RecordingContextApi(CONTEXT);
    NvgRenderer renderer = renderer(contexts);
    renderer.initialize();

    assertEquals("A", renderer.displayText(compatibilityDescriptor, "A"));
    NvgFontResourceObservation beforeRegistration = renderer.fontResourceObservation();
    Font registered = fontService.loadFont(fontPath.toString());
    assertEquals("A", renderer.displayText(registered, "A"));
    NvgFontResourceObservation afterRegistration = renderer.fontResourceObservation();
    var installedIdentity = Font.semanticOwner().observation().identities().getFirst();

    assertAll(
        () -> assertEquals(1, beforeRegistration.bufferEntries()),
        () -> assertEquals(1, beforeRegistration.fontInfoEntries()),
        () -> assertTrue(beforeRegistration.retainedSemanticIdentities().isEmpty()),
        () -> assertEquals(1, afterRegistration.bufferEntries()),
        () -> assertEquals(1, afterRegistration.fontInfoEntries()),
        () ->
            assertEquals(
                java.util.Set.of(installedIdentity),
                afterRegistration.retainedSemanticIdentities()));

    renderer.destroy();
  }

  private NvgRenderer renderer(RecordingContextApi contexts) {
    return renderer(contexts, (context, name, bytes) -> 7);
  }

  private NvgRenderer renderer(
      RecordingContextApi contexts, NvgFontRegistry.FaceCreator faceCreator) {
    return new NvgRenderer(
        true,
        DiagnosticSession.disabled(),
        contexts,
        faceCreator);
  }

  @SuppressWarnings("unchecked")
  private static ByteBuffer onlyOwnedBuffer(NvgRenderer renderer) throws Exception {
    Field registryField = NvgRenderer.class.getDeclaredField("fontRegistry");
    registryField.setAccessible(true);
    NvgFontRegistry registry = (NvgFontRegistry) registryField.get(renderer);
    Field buffersField = NvgFontRegistry.class.getDeclaredField("fontBuffers");
    buffersField.setAccessible(true);
    Map<?, ByteBuffer> buffers = (Map<?, ByteBuffer>) buffersField.get(registry);
    return buffers.values().iterator().next();
  }

  private Path copyResourceFont(String resource, String name) throws Exception {
    Path destination = fontDirectory.resolve(name);
    try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
      assertNotNull(stream);
      Files.copy(stream, destination);
    }
    return destination;
  }

  private static Throwable captureWorkerFailure(Runnable operation) throws InterruptedException {
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread worker =
        new Thread(
            () -> {
              try {
                operation.run();
              } catch (Throwable throwable) {
                failure.set(throwable);
              }
            },
            "nvg-renderer-lifecycle-worker");
    worker.start();
    worker.join();
    return failure.get();
  }

  private static final class RecordingContextApi implements NvgRenderer.ContextApi {
    private final long context;
    private int creations;
    private int deletions;
    private Runnable onCreate = () -> {};
    private Runnable onDelete = () -> {};

    private RecordingContextApi(long context) {
      this.context = context;
    }

    @Override
    public NvgRenderer.ContextHandle create(boolean antialiasingEnabled) {
      creations++;
      onCreate.run();
      return new NvgRenderer.ContextHandle(context, NvgRenderer.Profile.GL3);
    }

    @Override
    public void delete(NvgRenderer.ContextHandle deleted) {
      assertEquals(context, deleted.identity());
      onDelete.run();
      deletions++;
    }
  }
}
