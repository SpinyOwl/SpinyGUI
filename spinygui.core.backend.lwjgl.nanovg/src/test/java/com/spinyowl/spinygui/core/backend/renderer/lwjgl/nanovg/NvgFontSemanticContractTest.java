package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NvgFontSemanticContractTest {
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
