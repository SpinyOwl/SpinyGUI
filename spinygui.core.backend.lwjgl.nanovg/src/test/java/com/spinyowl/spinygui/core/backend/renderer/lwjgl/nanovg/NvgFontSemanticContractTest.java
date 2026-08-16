package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NvgFontSemanticContractTest {
  private static final String T3_TARGET_DISABLED =
      "T3 contract target: semantic owner/backend invalidation integration is not implemented yet";
  private static final long CONTEXT = 37L;

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

  @Test
  void currentRegistryKeepsFaceBuffersAndFontInfoBackendLocalAndUsesDuplicateViews()
      throws Exception {
    NvgFontRegistry first = new NvgFontRegistry();
    NvgFontRegistry second = new NvgFontRegistry();
    Method fontKey = NvgFontRegistry.class.getDeclaredMethod("fontKey", Font.class);
    fontKey.setAccessible(true);
    String key = (String) fontKey.invoke(first, Font.ROBOTO_REGULAR);
    Class<?> fontFace =
        Arrays.stream(NvgFontRegistry.class.getDeclaredClasses())
            .filter(type -> type.getSimpleName().equals("FontFace"))
            .findFirst()
            .orElseThrow();
    Class<?> resourceKey =
        Arrays.stream(NvgFontRegistry.class.getDeclaredClasses())
            .filter(type -> type.getSimpleName().equals("FontResourceKey"))
            .findFirst()
            .orElseThrow();
    String source = Files.readString(nvgFontRegistrySource());

    assertAll(
        () -> assertEquals(List.of(Font.class), List.of(fontKey.getParameterTypes())),
        () -> assertTrue(Modifier.isPrivate(fontKey.getModifiers())),
        () -> assertTrue(key.contains(Font.ROBOTO_REGULAR.path())),
        () ->
            assertEquals(
                Set.of("resourceKey", "name", "id"),
                Arrays.stream(fontFace.getRecordComponents())
                    .map(component -> component.getName())
                    .collect(java.util.stream.Collectors.toSet())),
        () ->
            assertEquals(
                Set.of("context", "faceKey", "semanticIdentity", "normalizedLocator"),
                Arrays.stream(resourceKey.getRecordComponents())
                    .map(component -> component.getName())
                    .collect(java.util.stream.Collectors.toSet())),
        () -> assertEquals(1, NvgFontResourceObservation.HARD_CONTEXT_LIMIT),
        () ->
            assertNotSame(
                backendMap(first, "loadedFontFaces"), backendMap(second, "loadedFontFaces")),
        () -> assertNotSame(backendMap(first, "fontBuffers"), backendMap(second, "fontBuffers")),
        () -> assertNotSame(backendMap(first, "fontInfos"), backendMap(second, "fontInfos")),
        () ->
            assertTrue(
                source.contains(
                    "faceCreator.create(nanovg, fontFace, fontBuffer.duplicate())")),
        () ->
            assertTrue(
                source.contains(
                    "(context, name, bytes) -> nvgCreateFontMem(context, name, bytes, false)")),
        () ->
            assertTrue(
                source.contains("stbtt_InitFont(fontInfo, fontBuffer.duplicate())")));
  }

  @Test
  void currentRegistryLoadsRealFontInfoFromPreservedPathContainingSpaces() throws Exception {
    Path directory = Files.createDirectories(fontDirectory.resolve("backend font path with spaces"));
    Path fontPath = directory.resolve("Roboto preserved spelling.ttf");
    try (var stream =
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("fonts/Roboto-Regular.ttf")) {
      assertNotNull(stream);
      Files.copy(stream, fontPath);
    }
    Font font =
        new Font(
            "Backend Spaced Path",
            FontStyle.NORMAL,
            FontStretch.NORMAL,
            FontWeight.REGULAR,
            fontPath.toString());
    NvgFontRegistry registry = new NvgFontRegistry();

    String displayed = registry.displayText(CONTEXT, font, "A");

    assertAll(
        () -> assertEquals("A", displayed),
        () -> assertEquals(1, registry.observation().bufferEntries()),
        () -> assertEquals(1, registry.observation().fontInfoEntries()),
        () -> assertEquals(fontPath.toString(), font.path()));
  }

  @Test
  void currentFaceSelectionFailureAndRetryDoNotMutateCoreRegistry() throws Exception {
    List<Font> coreBefore = Font.fonts();
    NvgFontRegistry registry = new NvgFontRegistry();
    Font missing =
        new Font(
            "M3 T3 Missing",
            FontStyle.NORMAL,
            FontStretch.NORMAL,
            FontWeight.REGULAR,
            "fonts/m3-t3-missing.ttf");
    NvgTextCommandRecorder successfulSelection = new NvgTextCommandRecorder(font -> true);
    NvgTextCommandRecorder failedSelection = new NvgTextCommandRecorder(font -> false);

    boolean selected =
        successfulSelection.selectFace(
            CONTEXT, NvgTextCommand.TextPath.NORMAL, Font.ROBOTO_REGULAR);
    boolean reused =
        successfulSelection.selectFace(
            CONTEXT, NvgTextCommand.TextPath.NORMAL, Font.ROBOTO_REGULAR);
    boolean failed =
        failedSelection.selectFace(CONTEXT, NvgTextCommand.TextPath.NORMAL, Font.ROBOTO_REGULAR);
    boolean failedRetry =
        failedSelection.selectFace(CONTEXT, NvgTextCommand.TextPath.NORMAL, Font.ROBOTO_REGULAR);
    String missingAttempt = registry.fontFace(missing, CONTEXT);
    IllegalStateException contextMismatch =
        assertThrows(
            IllegalStateException.class,
            () -> registry.fontFace(missing, CONTEXT + 1));

    assertAll(
        () -> assertTrue(selected),
        () -> assertTrue(reused),
        () -> assertFalse(failed),
        () -> assertFalse(failedRetry),
        () -> assertNull(missingAttempt),
        () -> assertTrue(contextMismatch.getMessage().contains("different context")),
        () -> assertTrue(backendMap(registry, "loadedFontFaces").isEmpty()),
        () -> assertTrue(backendMap(registry, "fontBuffers").isEmpty()),
        () -> assertTrue(backendMap(registry, "fontInfos").isEmpty()),
        () -> assertEquals(coreBefore, Font.fonts()));
  }

  @DisplayName("P4 T1: face creation rejects before renderer initialization")
  @Test
  void p4TargetFaceCreationRejectsBeforeOwnerInstallation() {
    NvgFaceThreadTarget target = p4Target();

    assertThrows(
        IllegalStateException.class, () -> target.fontFace(Font.ROBOTO_REGULAR, CONTEXT));
  }

  @DisplayName("P4 T1: face creation and reuse require the exact renderer UI thread")
  @Test
  void p4TargetFaceCreationAndReuseSucceedOnExactOwnerThread() {
    NvgFaceThreadTarget target = p4Target();
    Thread installationThread = Thread.currentThread();
    target.installOwner();

    String created = target.fontFace(Font.ROBOTO_REGULAR, CONTEXT);
    String reused = target.fontFace(Font.ROBOTO_REGULAR, CONTEXT);

    assertSame(installationThread, target.ownerThread());
    assertEquals(created, reused);
    target.closeOwner();
  }

  @DisplayName("P4 T1: off-thread face creation and reuse reject")
  @Test
  void p4TargetOffThreadFaceCreationAndReuseRejectWithoutOwnerMigration() throws Exception {
    NvgFaceThreadTarget target = p4Target();
    Thread installationThread = Thread.currentThread();
    target.installOwner();
    target.fontFace(Font.ROBOTO_REGULAR, CONTEXT);

    Throwable reuseFailure =
        captureWorkerFailure(() -> target.fontFace(Font.ROBOTO_REGULAR, CONTEXT));
    Throwable creationFailure =
        captureWorkerFailure(() -> target.fontFace(Font.ROBOTO_BOLD, CONTEXT));

    assertInstanceOf(IllegalStateException.class, reuseFailure);
    assertInstanceOf(IllegalStateException.class, creationFailure);
    assertSame(installationThread, target.ownerThread());
    target.closeOwner();
  }

  @DisplayName("P4 T1: face creation and reuse reject after renderer destroy")
  @Test
  void p4TargetFaceCreationAndReuseRejectAfterOwnerClose() {
    NvgFaceThreadTarget target = p4Target();
    target.installOwner();
    target.fontFace(Font.ROBOTO_REGULAR, CONTEXT);
    target.closeOwner();

    assertThrows(
        IllegalStateException.class, () -> target.fontFace(Font.ROBOTO_REGULAR, CONTEXT));
    assertThrows(IllegalStateException.class, () -> target.fontFace(Font.ROBOTO_BOLD, CONTEXT));
  }

  @Disabled(T3_TARGET_DISABLED)
  @DisplayName("T3 target: immutable core observations and context-local face state stay separate")
  @Test
  void t3TargetCoreObservationAndBackendFaceStateRemainSeparate() {
    SemanticBackendTarget target = t3Target();
    CoreObservation coreBefore = target.coreObservation();

    FaceAttempt created =
        target.createOrReuse(coreBefore, CONTEXT, FaceCreationOutcome.SUCCESS);
    BackendFaceState face = created.face();

    assertAll(
        () -> assertTrue(created.success()),
        () -> assertFalse(created.reused()),
        () -> assertEquals(coreBefore, target.coreObservation()),
        () ->
            assertThrows(
                UnsupportedOperationException.class, () -> coreBefore.resolvedChain().clear()),
        () -> assertNotNull(face),
        () -> assertEquals(coreBefore.identity(), face.identity()),
        () -> assertEquals(CONTEXT, face.context()),
        () -> assertNotNull(face.faceName()),
        () -> assertNotEquals(0, face.faceId()),
        () -> assertNotNull(face.stbInfo()),
        () -> assertNotSame(face.sourceBuffer(), face.submittedView()),
        () -> assertEquals(face.sourceBuffer().capacity(), face.submittedView().capacity()),
        () -> assertTrue(face.duplicateViewSharesStorage()),
        () ->
            assertEquals(
                List.of(
                    "normalizedFamily", "style", "weight", "stretch", "locator", "byteRevision"),
                Arrays.stream(CoreSemanticIdentity.class.getRecordComponents())
                    .map(component -> component.getName())
                    .toList()),
        () ->
            assertEquals(
                List.of("identity", "generation"),
                Arrays.stream(CoreConsumerKey.class.getRecordComponents())
                    .map(component -> component.getName())
                    .toList()),
        () ->
            assertEquals(
                new CoreConsumerKey(coreBefore.identity(), coreBefore.generation()),
                target.coreKey(coreBefore)));
  }

  @Disabled(T3_TARGET_DISABLED)
  @DisplayName("T3 target: face load, STB, and NanoVG failures/retries never advance core generation")
  @Test
  void t3TargetFaceCreationFailureAndRetryHaveZeroSemanticGenerationEffect() {
    for (FaceCreationOutcome failureOutcome : FaceCreationOutcome.failures()) {
      SemanticBackendTarget target = t3Target();
      CoreObservation coreBefore = target.coreObservation();

      FaceAttempt failure = target.createOrReuse(coreBefore, CONTEXT, failureOutcome);
      CoreObservation afterFailure = target.coreObservation();
      FaceAttempt retry =
          target.createOrReuse(coreBefore, CONTEXT, FaceCreationOutcome.SUCCESS);
      CoreObservation afterRetry = target.coreObservation();

      assertAll(
          () -> assertFalse(failure.success()),
          () -> assertNull(failure.face()),
          () -> assertEquals(0, target.failedAttemptRetainedFaceCount()),
          () -> assertTrue(retry.success()),
          () -> assertFalse(retry.reused()),
          () -> assertEquals(coreBefore, afterFailure),
          () -> assertEquals(coreBefore, afterRetry));
    }
  }

  @Disabled(T3_TARGET_DISABLED)
  @DisplayName("T3 target: unchanged identity reuses only within the same live context")
  @Test
  void t3TargetSameIdentityReuseIsBoundedByLiveContext() {
    SemanticBackendTarget target = t3Target();
    CoreObservation core = target.coreObservation();

    FaceAttempt first = target.createOrReuse(core, CONTEXT, FaceCreationOutcome.SUCCESS);
    FaceAttempt sameContext = target.createOrReuse(core, CONTEXT, FaceCreationOutcome.SUCCESS);
    FaceAttempt otherContext = target.createOrReuse(core, CONTEXT + 1, FaceCreationOutcome.SUCCESS);
    BackendRetentionState beforeRetirement = target.backendState();
    target.retireContext(CONTEXT);
    BackendRetentionState afterRetirement = target.backendState();
    FaceAttempt retiredContext = target.createOrReuse(core, CONTEXT, FaceCreationOutcome.SUCCESS);
    BackendRetentionState afterRecreation = target.backendState();
    BackendResourceKey retiredKey = new BackendResourceKey(core.identity(), CONTEXT);

    assertAll(
        () -> assertFalse(first.reused()),
        () -> assertTrue(sameContext.reused()),
        () -> assertSame(first.face(), sameContext.face()),
        () -> assertFalse(otherContext.reused()),
        () -> assertEquals(CONTEXT + 1, otherContext.face().context()),
        () -> assertTrue(beforeRetirement.faces().contains(first.face().key())),
        () -> assertTrue(beforeRetirement.buffers().contains(retiredKey)),
        () -> assertTrue(beforeRetirement.fontInfos().contains(retiredKey)),
        () -> assertTrue(beforeRetirement.contexts().contains(CONTEXT)),
        () -> assertFalse(afterRetirement.faces().contains(first.face().key())),
        () -> assertFalse(afterRetirement.buffers().contains(retiredKey)),
        () -> assertFalse(afterRetirement.fontInfos().contains(retiredKey)),
        () ->
            assertTrue(
                afterRetirement.faces().stream().noneMatch(face -> face.context() == CONTEXT)),
        () ->
            assertTrue(
                afterRetirement.buffers().stream()
                    .noneMatch(buffer -> buffer.context() == CONTEXT)),
        () ->
            assertTrue(
                afterRetirement.fontInfos().stream()
                    .noneMatch(info -> info.context() == CONTEXT)),
        () -> assertFalse(afterRetirement.contexts().contains(CONTEXT)),
        () -> assertEquals(Set.of(CONTEXT + 1), afterRetirement.contexts()),
        () -> assertEquals(Set.of(core.identity()), afterRetirement.retainedIdentities()),
        () -> assertTrue(afterRetirement.maximumRetainedKindCount() <= 1),
        () -> assertFalse(retiredContext.reused()),
        () -> assertNotSame(first.face(), retiredContext.face()),
        () -> assertTrue(afterRecreation.faces().contains(retiredContext.face().key())),
        () -> assertTrue(afterRecreation.buffers().contains(retiredKey)),
        () -> assertTrue(afterRecreation.fontInfos().contains(retiredKey)),
        () -> assertEquals(Set.of(CONTEXT, CONTEXT + 1), afterRecreation.contexts()),
        () -> assertTrue(afterRecreation.maximumRetainedKindCount() <= 2),
        () -> assertEquals(3, target.faceCreations()),
        () -> assertEquals(core, target.coreObservation()));
  }

  @Disabled(T3_TARGET_DISABLED)
  @DisplayName("T3 target: changed semantic identity emits invalidation before face recreation")
  @Test
  void t3TargetChangedIdentitySignalsInvalidationAndForcesRecreation() {
    SemanticBackendTarget target = t3Target();
    target.selectStrategy(P4ReloadStrategy.BOUNDED_CONTEXT_ROTATION, 2);
    CoreObservation original = target.coreObservation();
    FaceAttempt originalFace =
        target.createOrReuse(original, CONTEXT, FaceCreationOutcome.SUCCESS);
    BackendRetentionState beforeChange = target.backendState();

    CoreObservation changed = target.reviseCoreIdentity("sha256:changed");
    BackendInvalidationSignal signal = target.observeSemanticChange(original, changed);
    target.applyInvalidation(signal);
    BackendRetentionState afterInvalidation = target.backendState();
    FaceAttempt replacement =
        target.createOrReuse(changed, CONTEXT, FaceCreationOutcome.SUCCESS);
    BackendRetentionState afterRecreation = target.backendState();
    BackendResourceKey originalKey = new BackendResourceKey(original.identity(), CONTEXT);
    BackendResourceKey replacementKey = new BackendResourceKey(changed.identity(), CONTEXT);

    assertAll(
        () -> assertTrue(beforeChange.faces().contains(originalFace.face().key())),
        () -> assertTrue(beforeChange.buffers().contains(originalKey)),
        () -> assertTrue(beforeChange.fontInfos().contains(originalKey)),
        () -> assertEquals(original.generation() + 1, changed.generation()),
        () -> assertEquals(original.identity(), signal.retiredIdentity()),
        () -> assertEquals(changed.identity(), signal.replacementIdentity()),
        () -> assertTrue(signal.affectedContexts().contains(CONTEXT)),
        () -> assertFalse(afterInvalidation.faces().contains(originalFace.face().key())),
        () -> assertFalse(afterInvalidation.buffers().contains(originalKey)),
        () -> assertFalse(afterInvalidation.fontInfos().contains(originalKey)),
        () -> assertFalse(afterInvalidation.contexts().contains(CONTEXT)),
        () -> assertFalse(afterInvalidation.retainedIdentities().contains(original.identity())),
        () -> assertEquals(0, afterInvalidation.maximumRetainedKindCount()),
        () -> assertFalse(replacement.reused()),
        () -> assertNotSame(originalFace.face(), replacement.face()),
        () -> assertTrue(afterRecreation.faces().contains(replacement.face().key())),
        () -> assertTrue(afterRecreation.buffers().contains(replacementKey)),
        () -> assertTrue(afterRecreation.fontInfos().contains(replacementKey)),
        () -> assertEquals(Set.of(changed.identity()), afterRecreation.retainedIdentities()),
        () -> assertEquals(Set.of(CONTEXT), afterRecreation.contexts()),
        () -> assertTrue(afterRecreation.maximumRetainedKindCount() <= 1),
        () -> assertEquals(changed, target.coreObservation()));
  }

  @Disabled(T3_TARGET_DISABLED)
  @DisplayName("T3 target: P4 strategy is active-context rejection or bounded context rotation")
  @Test
  void t3TargetP4StrategiesRejectUnboundedRetention() {
    SemanticBackendTarget rejection = t3Target();
    CoreObservation active = rejection.coreObservation();
    rejection.selectStrategy(P4ReloadStrategy.ACTIVE_CONTEXT_REJECTION, 0);
    FaceAttempt activeFace =
        rejection.createOrReuse(active, CONTEXT, FaceCreationOutcome.SUCCESS);
    BackendRetentionState beforeRejection = rejection.backendState();
    BackendResourceKey activeKey = new BackendResourceKey(active.identity(), CONTEXT);

    assertThrows(
        IllegalStateException.class,
        () -> rejection.reviseCoreIdentity("sha256:rejected"));

    BackendRetentionState afterRejection = rejection.backendState();
    FaceAttempt retainedFace =
        rejection.createOrReuse(active, CONTEXT, FaceCreationOutcome.SUCCESS);
    assertAll(
        () -> assertEquals(active, rejection.coreObservation()),
        () -> assertEquals(active.generation(), rejection.coreObservation().generation()),
        () -> assertEquals(beforeRejection, afterRejection),
        () -> assertTrue(afterRejection.faces().contains(activeFace.face().key())),
        () -> assertTrue(afterRejection.buffers().contains(activeKey)),
        () -> assertTrue(afterRejection.fontInfos().contains(activeKey)),
        () -> assertEquals(Set.of(CONTEXT), afterRejection.contexts()),
        () -> assertSame(activeFace.face(), retainedFace.face()),
        () -> assertTrue(retainedFace.reused()));

    SemanticBackendTarget rotation = t3Target();
    rotation.selectStrategy(P4ReloadStrategy.BOUNDED_CONTEXT_ROTATION, 2);
    CoreObservation revision = rotation.coreObservation();
    for (int index = 0; index < 4; index++) {
      rotation.createOrReuse(revision, CONTEXT, FaceCreationOutcome.SUCCESS);
      CoreObservation next = rotation.reviseCoreIdentity("sha256:rotation-" + index);
      rotation.applyInvalidation(rotation.observeSemanticChange(revision, next));
      revision = next;
    }

    BackendRetentionState rotationState = rotation.backendState();
    assertAll(
        () -> assertTrue(rotation.retainedContextCount() <= 2),
        () -> assertTrue(rotationState.maximumRetainedKindCount() <= 2));
    assertThrows(
        IllegalArgumentException.class,
        () -> rotation.selectStrategy(P4ReloadStrategy.UNBOUNDED_RETENTION, Integer.MAX_VALUE));
  }

  @Disabled(T3_TARGET_DISABLED)
  @DisplayName("T3 target: M5 and M7 keys depend only on core identity and generation")
  @Test
  void t3TargetM5AndM7KeysIgnoreAllBackendFaceAndContextState() {
    SemanticBackendTarget target = t3Target();
    CoreObservation core = target.coreObservation();
    CoreConsumerKey m5Before = target.m5Key(core);
    CoreConsumerKey m7Before = target.m7Key(core);

    target.createOrReuse(core, CONTEXT, FaceCreationOutcome.SUCCESS);
    target.createOrReuse(core, CONTEXT + 1, FaceCreationOutcome.SUCCESS);

    assertAll(
        () -> assertEquals(m5Before, target.m5Key(core)),
        () -> assertEquals(m7Before, target.m7Key(core)),
        () -> assertEquals(target.coreKey(core), m5Before),
        () -> assertEquals(target.coreKey(core), m7Before));

    CoreObservation changed = target.reviseCoreIdentity("sha256:consumer-change");
    assertAll(
        () -> assertNotEquals(m5Before, target.m5Key(changed)),
        () -> assertNotEquals(m7Before, target.m7Key(changed)));
  }

  @SuppressWarnings("unchecked")
  private static Map<?, ?> backendMap(NvgFontRegistry registry, String fieldName)
      throws ReflectiveOperationException {
    Field field = NvgFontRegistry.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return (Map<?, ?>) field.get(registry);
  }

  private static Path nvgFontRegistrySource() {
    Path root = Path.of("").toAbsolutePath();
    while (root != null && !Files.exists(root.resolve("settings.gradle.kts"))) {
      root = root.getParent();
    }
    if (root == null) {
      throw new IllegalStateException("Could not locate the SpinyGUI repository root");
    }
    return root.resolve(
        "spinygui.core.backend.lwjgl.nanovg/src/main/java/"
            + "com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/NvgFontRegistry.java");
  }

  private SemanticBackendTarget t3Target() {
    throw new UnsupportedOperationException("T3 semantic/backend target adapter is unavailable");
  }

  private record CoreSemanticIdentity(
      String normalizedFamily,
      String style,
      String weight,
      String stretch,
      String locator,
      String byteRevision) {}

  private record CoreObservation(
      CoreSemanticIdentity identity,
      long generation,
      List<CoreSemanticIdentity> resolvedChain) {}

  private record CoreConsumerKey(CoreSemanticIdentity identity, long generation) {}

  private record BackendFaceState(
      CoreSemanticIdentity identity,
      long context,
      String faceName,
      int faceId,
      ByteBuffer sourceBuffer,
      ByteBuffer submittedView,
      boolean duplicateViewSharesStorage,
      Object stbInfo) {
    BackendFaceKey key() {
      return new BackendFaceKey(identity, context, faceName, faceId);
    }
  }

  private record BackendFaceKey(
      CoreSemanticIdentity identity, long context, String faceName, int faceId) {}

  private record BackendResourceKey(CoreSemanticIdentity identity, long context) {}

  private record BackendRetentionState(
      Set<BackendFaceKey> faces,
      Set<BackendResourceKey> buffers,
      Set<BackendResourceKey> fontInfos,
      Set<Long> contexts) {
    BackendRetentionState {
      faces = Set.copyOf(faces);
      buffers = Set.copyOf(buffers);
      fontInfos = Set.copyOf(fontInfos);
      contexts = Set.copyOf(contexts);
    }

    Set<CoreSemanticIdentity> retainedIdentities() {
      java.util.stream.Stream<CoreSemanticIdentity> faceIdentities =
          faces.stream().map(BackendFaceKey::identity);
      java.util.stream.Stream<CoreSemanticIdentity> bufferIdentities =
          buffers.stream().map(BackendResourceKey::identity);
      java.util.stream.Stream<CoreSemanticIdentity> infoIdentities =
          fontInfos.stream().map(BackendResourceKey::identity);
      return java.util.stream.Stream.concat(
              java.util.stream.Stream.concat(faceIdentities, bufferIdentities), infoIdentities)
          .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    int maximumRetainedKindCount() {
      return Math.max(
          Math.max(faces.size(), buffers.size()), Math.max(fontInfos.size(), contexts.size()));
    }
  }

  private record FaceAttempt(boolean success, boolean reused, BackendFaceState face) {}

  private record BackendInvalidationSignal(
      CoreSemanticIdentity retiredIdentity,
      CoreSemanticIdentity replacementIdentity,
      Set<Long> affectedContexts) {}

  private enum FaceCreationOutcome {
    SUCCESS,
    BUFFER_LOAD_FAILURE,
    STB_INFO_FAILURE,
    NANOVG_FACE_FAILURE;

    static List<FaceCreationOutcome> failures() {
      return List.of(BUFFER_LOAD_FAILURE, STB_INFO_FAILURE, NANOVG_FACE_FAILURE);
    }
  }

  private enum P4ReloadStrategy {
    ACTIVE_CONTEXT_REJECTION,
    BOUNDED_CONTEXT_ROTATION,
    UNBOUNDED_RETENTION
  }

  private interface SemanticBackendTarget {
    CoreObservation coreObservation();

    FaceAttempt createOrReuse(
        CoreObservation observation, long nanovgContext, FaceCreationOutcome outcome);

    int failedAttemptRetainedFaceCount();

    int faceCreations();

    void retireContext(long nanovgContext);

    CoreObservation reviseCoreIdentity(String byteRevision);

    BackendInvalidationSignal observeSemanticChange(
        CoreObservation previous, CoreObservation replacement);

    void applyInvalidation(BackendInvalidationSignal signal);

    void selectStrategy(P4ReloadStrategy strategy, int retainedContextBound);

    int retainedContextCount();

    BackendRetentionState backendState();

    CoreConsumerKey coreKey(CoreObservation observation);

    CoreConsumerKey m5Key(CoreObservation observation);

    CoreConsumerKey m7Key(CoreObservation observation);
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
            "m3-nanovg-font-contract-worker");
    worker.start();
    worker.join();
    return failure.get();
  }

  private NvgFaceThreadTarget p4Target() {
    NvgRenderer.ContextApi contexts =
        new NvgRenderer.ContextApi() {
          @Override
          public NvgRenderer.ContextHandle create(boolean antialiasingEnabled) {
            return new NvgRenderer.ContextHandle(CONTEXT, NvgRenderer.Profile.GL3);
          }

          @Override
          public void delete(NvgRenderer.ContextHandle context) {}
        };
    NvgRenderer renderer =
        new NvgRenderer(
            true,
            com.spinyowl.spinygui.core.diagnostic.DiagnosticSession.disabled(),
            contexts,
            (context, name, bytes) -> 7);
    return new NvgFaceThreadTarget() {
      @Override
      public void installOwner() {
        renderer.initialize();
      }

      @Override
      public Thread ownerThread() {
        return renderer.uiThread();
      }

      @Override
      public String fontFace(Font font, long nanovgContext) {
        return renderer.fontFace(font, nanovgContext);
      }

      @Override
      public void closeOwner() {
        renderer.destroy();
      }
    };
  }

  private interface NvgFaceThreadTarget {
    void installOwner();

    Thread ownerThread();

    String fontFace(Font font, long nanovgContext);

    void closeOwner();
  }
}
