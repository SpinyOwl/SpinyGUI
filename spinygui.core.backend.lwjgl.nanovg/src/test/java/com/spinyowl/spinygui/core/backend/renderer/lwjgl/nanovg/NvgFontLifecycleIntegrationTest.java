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

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.system.font.FontLoadingException;
import com.spinyowl.spinygui.core.system.font.FontResourceObservation;
import com.spinyowl.spinygui.core.system.font.FontSemanticObservation;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NvgFontLifecycleIntegrationTest {
  private static final long CONTEXT = 71L;

  @TempDir Path fontDirectory;

  private final List<NvgRenderer> renderers = new ArrayList<>();
  private FontServiceImpl fontService;

  @BeforeEach
  void installFontOwner() {
    fontService = NvgFontTestOwner.install();
  }

  @AfterEach
  void closeOwners() {
    for (int index = renderers.size() - 1; index >= 0; index--) {
      renderers.get(index).destroy();
    }
    fontService.close();
  }

  @DisplayName("P4 T4: mutations flow through resolution, measurement, face recording, and teardown")
  @Test
  void mutationsResolveMeasureRecordFacesAndExposeAggregateRetention() throws Exception {
    FontSemanticObservation initial = fontService.semanticObservation();
    Path addedPath = copyResourceFont("fonts/MaterialIcons-Regular.ttf", "added icons.ttf");
    Font added = fontService.loadFont(addedPath.toString());
    FontSemanticObservation afterAdd = fontService.semanticObservation();
    Font duplicate = fontService.loadFont(addedPath.toString());
    FontSemanticObservation afterDuplicate = fontService.semanticObservation();
    String missing = fontDirectory.resolve("missing font.ttf").toString();
    assertThrows(FontLoadingException.class, () -> fontService.loadFont(missing));
    FontSemanticObservation afterFailure = fontService.semanticObservation();

    Path replacementPath = copyResourceFont("fonts/Roboto-Regular.ttf", "replacement regular.ttf");
    Font replacement = fontService.loadFont(replacementPath.toString());
    FontSemanticObservation afterReplacement = fontService.semanticObservation();
    Files.write(replacementPath, new byte[] {0}, StandardOpenOption.APPEND);
    Font reloaded = fontService.loadFont(replacementPath.toString());
    FontSemanticObservation afterReload = fontService.semanticObservation();

    List<Font> resolved =
        fontService
            .fontChainResolver()
            .resolve(
                List.of(reloaded.fontFamily()),
                reloaded.style(),
                reloaded.weight(),
                reloaded.stretch());
    TextMetrics metrics = fontService.measureText("A", List.of(reloaded), 16, 1);
    TextLineMetrics line = metrics.lines().getFirst();
    RecordingContextApi contexts = new RecordingContextApi(CONTEXT);
    NvgRenderer renderer = renderer(contexts, (context, name, bytes) -> 401, new ArrayList<>());
    renderer.initialize();
    assertEquals("A", renderer.displayText(reloaded, "A"));
    NvgTextCommandRecorder recording =
        new NvgTextCommandRecorder(font -> renderer.fontFace(font, CONTEXT) != null);
    Text text = recordedText("A", line, reloaded);
    new NvgTextRenderer(recording).render(text, CONTEXT);

    FontSemanticObservation afterRender = fontService.semanticObservation();
    FontResourceObservation coreResources = fontService.resourceObservation();
    NvgFontResourceObservation backendResources = renderer.fontResourceObservation();
    FontGenerationKey m5Key = new FontGenerationKey("control-snapshot", afterRender);
    AggregateRetention m7Retention = new AggregateRetention(coreResources, backendResources);

    assertAll(
        () -> assertEquals(initial.generation() + 1, afterAdd.generation()),
        () -> assertEquals(initial.identities().size() + 1, afterAdd.identities().size()),
        () -> assertSame(added, duplicate),
        () -> assertEquals(afterAdd, afterDuplicate),
        () -> assertEquals(afterDuplicate, afterFailure),
        () -> assertEquals(afterFailure.generation() + 1, afterReplacement.generation()),
        () -> assertEquals(afterReplacement.generation() + 1, afterReload.generation()),
        () -> assertEquals(replacement, reloaded),
        () -> assertNotSame(replacement, reloaded),
        () -> assertSame(reloaded, resolved.getFirst()),
        () -> assertFalse(line.runs().isEmpty()),
        () -> assertEquals(reloaded, line.runs().getFirst().font()),
        () -> assertEquals(afterReload, afterRender),
        () -> assertEquals(afterRender, m5Key.semantic()),
        () -> assertEquals(afterRender.generation(), backendResources.semanticGeneration()),
        () -> assertEquals(1, backendResources.contextCount()),
        () -> assertEquals(1, backendResources.faceEntries()),
        () -> assertEquals(1, backendResources.bufferEntries()),
        () -> assertEquals(1, backendResources.fontInfoEntries()),
        () -> assertEquals(0, backendResources.retainedSubmittedViews()),
        () -> assertEquals(coreResources.ownerByteEntries(), coreResources.ownerStbInfoEntries()),
        () -> assertEquals(3, m7Retention.backendOwnedEntries()),
        () -> assertTrue(m7Retention.totalOwnedEntries() >= 3),
        () ->
            assertTrue(
                recording.commands().stream()
                    .anyMatch(command -> command instanceof NvgTextCommand.Face face && face.selected())),
        () ->
            assertTrue(
                recording.commands().stream().anyMatch(NvgTextCommand.Text.class::isInstance)));

    renderer.destroy();
    NvgFontResourceObservation released = registry(renderer).observation();
    fontService.clear();
    FontResourceObservation clearedCore = fontService.resourceObservation();
    assertAll(
        () -> assertEquals(1, contexts.deletions),
        () -> assertEquals(0, released.contextCount()),
        () -> assertEquals(0, released.faceEntries()),
        () -> assertEquals(0, released.bufferEntries()),
        () -> assertEquals(0, released.fontInfoEntries()),
        () -> assertEquals(0, clearedCore.ownerByteEntries()),
        () -> assertEquals(0, clearedCore.ownerStbInfoEntries()));
  }

  @DisplayName("P4 T4: repeated active-face reload rejects without old-buffer accumulation")
  @Test
  void repeatedActiveFaceReloadRejectsUntilContextDeletionWithoutAccumulation() throws Exception {
    Path fontPath = copyResourceFont("fonts/Roboto-Regular.ttf", "bounded reload regular.ttf");
    Font active = fontService.loadFont(fontPath.toString());
    RecordingContextApi firstContexts = new RecordingContextApi(CONTEXT);
    NvgRenderer first = renderer(firstContexts, (context, name, bytes) -> 501, new ArrayList<>());
    first.initialize();
    String activeFace = first.fontFace(active, CONTEXT);
    FontSemanticObservation semanticBefore = fontService.semanticObservation();
    FontResourceObservation coreBefore = fontService.resourceObservation();
    NvgFontResourceObservation backendBefore = first.fontResourceObservation();

    for (int revision = 0; revision < 8; revision++) {
      Files.write(fontPath, new byte[] {(byte) revision}, StandardOpenOption.APPEND);
      IllegalStateException rejection =
          assertThrows(IllegalStateException.class, () -> fontService.loadFont(fontPath.toString()));
      assertAll(
          () -> assertTrue(rejection.getMessage().contains("Destroy")),
          () -> assertEquals(semanticBefore, fontService.semanticObservation()),
          () -> assertEquals(coreBefore, fontService.resourceObservation()),
          () -> assertEquals(backendBefore, first.fontResourceObservation()),
          () -> assertEquals(activeFace, first.fontFace(active, CONTEXT)));
    }

    first.destroy();
    Font accepted = fontService.loadFont(fontPath.toString());
    FontSemanticObservation acceptedSemantic = fontService.semanticObservation();
    RecordingContextApi replacementContexts = new RecordingContextApi(CONTEXT + 1);
    NvgRenderer replacement =
        renderer(replacementContexts, (context, name, bytes) -> 502, new ArrayList<>());
    replacement.initialize();
    assertEquals("A", replacement.displayText(accepted, "A"));
    assertNotNull(replacement.fontFace(accepted, CONTEXT + 1));
    NvgFontResourceObservation replacementResources = replacement.fontResourceObservation();

    assertAll(
        () -> assertEquals(semanticBefore.generation() + 1, acceptedSemantic.generation()),
        () -> assertEquals(1, firstContexts.deletions),
        () -> assertEquals(1, replacementResources.contextCount()),
        () -> assertEquals(1, replacementResources.faceEntries()),
        () -> assertEquals(1, replacementResources.bufferEntries()),
        () -> assertEquals(1, replacementResources.fontInfoEntries()),
        () -> assertEquals(1, replacementResources.retainedSemanticIdentities().size()),
        () -> assertEquals(0, replacementResources.retainedSubmittedViews()));

    replacement.destroy();
    assertEquals(1, replacementContexts.deletions);
  }

  @DisplayName("P4 T4: thread, context, face failure, and lifecycle observations remain independent")
  @Test
  void threadContextFaceFailureAndLifecycleRemainIndependent() throws Exception {
    AtomicInteger faceAttempts = new AtomicInteger();
    List<NvgRenderer.LifecycleEvent> lifecycle = new ArrayList<>();
    RecordingContextApi contexts = new RecordingContextApi(CONTEXT);
    NvgRenderer renderer =
        renderer(
            contexts,
            (context, name, bytes) -> faceAttempts.incrementAndGet() == 1 ? -1 : 601,
            lifecycle);
    renderer.initialize();
    FontSemanticObservation semanticBefore = fontService.semanticObservation();
    FontResourceObservation coreBefore = fontService.resourceObservation();

    assertEquals("A", renderer.displayText(Font.ROBOTO_REGULAR, "A"));
    assertNull(renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT));
    NvgFontResourceObservation failedFace = renderer.fontResourceObservation();
    assertNotNull(renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT));
    NvgFontResourceObservation successfulFace = renderer.fontResourceObservation();
    IllegalStateException contextMismatch =
        assertThrows(
            IllegalStateException.class,
            () -> renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT + 1));
    AtomicReference<Throwable> offThreadLoad = new AtomicReference<>();
    AtomicReference<Throwable> offThreadMeasure = new AtomicReference<>();
    AtomicReference<Throwable> offThreadFace = new AtomicReference<>();
    Thread worker =
        new Thread(
            () -> {
              capture(offThreadLoad, () -> fontService.loadFont("fonts/MaterialIcons-Regular.ttf"));
              capture(
                  offThreadMeasure,
                  () -> fontService.measureText("A", List.of(Font.ROBOTO_REGULAR), 16, 1));
              capture(
                  offThreadFace,
                  () -> renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT));
            },
            "p4-t4-integrated-off-thread");
    worker.start();
    worker.join();

    assertAll(
        () -> assertEquals(1, failedFace.bufferEntries()),
        () -> assertEquals(0, failedFace.faceEntries()),
        () -> assertEquals(1, failedFace.retryableFaceFailures()),
        () -> assertEquals(1, failedFace.faceCreationFailures()),
        () -> assertEquals(1, successfulFace.bufferEntries()),
        () -> assertEquals(1, successfulFace.faceEntries()),
        () -> assertEquals(0, successfulFace.retryableFaceFailures()),
        () -> assertEquals(1, successfulFace.faceCreationFailures()),
        () -> assertEquals(2, faceAttempts.get()),
        () -> assertTrue(contextMismatch.getMessage().contains("context replacement or mismatch")),
        () -> assertInstanceOf(IllegalStateException.class, offThreadLoad.get()),
        () -> assertInstanceOf(IllegalStateException.class, offThreadMeasure.get()),
        () -> assertInstanceOf(IllegalStateException.class, offThreadFace.get()),
        () -> assertEquals(semanticBefore, fontService.semanticObservation()),
        () -> assertEquals(coreBefore, fontService.resourceObservation()));

    renderer.destroy();
    assertAll(
        () ->
            assertThrows(
                IllegalStateException.class,
                () -> renderer.fontFace(Font.ROBOTO_REGULAR, CONTEXT)),
        () -> assertEquals(NvgRenderer.State.DESTROYED, renderer.state()),
        () -> assertEquals(1, contexts.deletions),
        () -> assertTrue(lifecycle.indexOf(NvgRenderer.LifecycleEvent.CONTEXT_DELETED) >= 0),
        () ->
            assertTrue(
                lifecycle.indexOf(NvgRenderer.LifecycleEvent.CONTEXT_DELETED)
                    < lifecycle.indexOf(NvgRenderer.LifecycleEvent.FREE_BACKEND_STB_FONT_INFO)),
        () ->
            assertEquals(
                NvgRenderer.LifecycleEvent.SHARED_CORE_CLOSE_SAFE,
                lifecycle.getLast()));
  }

  private NvgRenderer renderer(
      RecordingContextApi contexts,
      NvgFontRegistry.FaceCreator faceCreator,
      List<NvgRenderer.LifecycleEvent> lifecycle) {
    NvgRenderer renderer =
        new NvgRenderer(
            true,
            DiagnosticSession.disabled(),
            contexts,
            faceCreator,
            (owner, registry) -> owner.registerMutationPreflight(registry::beforeReplacement),
            lifecycle::add,
            NvgFontRegistry.FontInfoAllocator.OWNED);
    renderers.add(renderer);
    return renderer;
  }

  private Text recordedText(String value, TextLineMetrics line, Font font) {
    Element parent = new Element("div");
    Text text = new Text(value);
    parent.addChild(text);
    text.inlineFragments(
        List.of(
            InlineFragment.builder()
                .text(value)
                .x(0)
                .baseline(line.baseline())
                .width(line.width())
                .height(line.height())
                .font(font)
                .fontSize(16)
                .color(Color.BLACK)
                .runs(line.runs())
                .build()));
    return text;
  }

  private Path copyResourceFont(String resource, String name) throws Exception {
    Path destination = fontDirectory.resolve(name);
    try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
      assertNotNull(stream);
      Files.copy(stream, destination);
    }
    return destination;
  }

  private static NvgFontRegistry registry(NvgRenderer renderer) throws Exception {
    Field registryField = NvgRenderer.class.getDeclaredField("fontRegistry");
    registryField.setAccessible(true);
    return (NvgFontRegistry) registryField.get(renderer);
  }

  private static void capture(AtomicReference<Throwable> target, ThrowingOperation operation) {
    try {
      operation.run();
    } catch (Throwable failure) {
      target.set(failure);
    }
  }

  private record FontGenerationKey(String consumer, FontSemanticObservation semantic) {}

  private record AggregateRetention(
      FontResourceObservation core, NvgFontResourceObservation backend) {
    int backendOwnedEntries() {
      return backend.faceEntries() + backend.bufferEntries() + backend.fontInfoEntries();
    }

    int totalOwnedEntries() {
      return core.ownerByteEntries() + core.ownerStbInfoEntries() + backendOwnedEntries();
    }
  }

  @FunctionalInterface
  private interface ThrowingOperation {
    void run() throws Exception;
  }

  private static final class RecordingContextApi implements NvgRenderer.ContextApi {
    private final long context;
    private int deletions;

    private RecordingContextApi(long context) {
      this.context = context;
    }

    @Override
    public NvgRenderer.ContextHandle create(boolean antialiasingEnabled) {
      return new NvgRenderer.ContextHandle(context, NvgRenderer.Profile.GL3);
    }

    @Override
    public void delete(NvgRenderer.ContextHandle deleted) {
      assertEquals(context, deleted.identity());
      deletions++;
    }
  }
}
