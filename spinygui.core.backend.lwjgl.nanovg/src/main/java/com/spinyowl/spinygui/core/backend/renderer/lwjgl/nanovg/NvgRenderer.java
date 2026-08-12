package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetInteger;
import com.spinyowl.spinygui.core.backend.renderer.Renderer;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.types.AffineTransform;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL30;

public class NvgRenderer implements Renderer {

  private final boolean antialiasingEnabled;
  private final AtomicBoolean initialized = new AtomicBoolean(false);
  private final NvgElementRenderer elementRenderer;
  private final NvgTextRenderer textRenderer;
  private final NvgBorderRenderer borderRenderer;
  private final NvgInputRenderer inputRenderer;
  private final NvgTextareaRenderer textareaRenderer;
  private final NvgScrollbarRenderer scrollbarRenderer;
  private final DiagnosticSession diagnostics;
  private final NvgTextCommandSink textCommands;
  private DebugRenderer debugRenderer;

  private boolean isVersionNew;
  private boolean debugMode;
  private Vector2f debugMousePosition;
  private long nanovgContext;
  private NvgTransformState.Factory transformStateFactory;
  private SubtreeContentState.Factory subtreeContentStateFactory;
  private SubtreeContentRenderer subtreeContentRenderer = this::renderSubtreeContent;

  public NvgRenderer(boolean antialiasingEnabled) {
    this(antialiasingEnabled, DiagnosticSession.disabled());
  }

  public NvgRenderer(boolean antialiasingEnabled, DiagnosticSession diagnostics) {
    NvgFontRegistry fontRegistry = new NvgFontRegistry();
    this.antialiasingEnabled = antialiasingEnabled;
    this.diagnostics = Objects.requireNonNull(diagnostics, "diagnostics");
    this.textCommands = new NanoVgTextCommandSink(fontRegistry, this.diagnostics);
    this.elementRenderer = new NvgElementRenderer(this.diagnostics);
    this.textRenderer = new NvgTextRenderer(textCommands, this.diagnostics);
    this.borderRenderer = new NvgBorderRenderer();
    this.inputRenderer = new NvgInputRenderer(textCommands, this.diagnostics);
    this.textareaRenderer = new NvgTextareaRenderer(textCommands, this.diagnostics);
    this.scrollbarRenderer = new NvgScrollbarRenderer(this.diagnostics);
    this.debugRenderer = new NvgDebugRenderer(this.diagnostics);
    this.transformStateFactory =
        (context, transform) -> NvgTransformState.apply(context, transform, textCommands);
    this.subtreeContentStateFactory =
        (context, element) -> NvgSubtreeContentState.apply(context, element, textCommands);
  }

  public NvgRenderer() {
    this(true);
  }

  public void initialize() {
    if (initialized.compareAndSet(false, true)) {
      isVersionNew =
          (glGetInteger(GL30.GL_MAJOR_VERSION) > 3)
              || glGetInteger(GL30.GL_MAJOR_VERSION) == 3
                  && glGetInteger(GL30.GL_MINOR_VERSION) >= 2;

      if (isVersionNew) {
        int flags =
            antialiasingEnabled
                ? NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_ANTIALIAS
                : NanoVGGL3.NVG_STENCIL_STROKES;
        nanovgContext = NanoVGGL3.nvgCreate(flags);
      } else {
        int flags =
            antialiasingEnabled
                ? NanoVGGL2.NVG_STENCIL_STROKES | NanoVGGL2.NVG_ANTIALIAS
                : NanoVGGL2.NVG_STENCIL_STROKES;
        nanovgContext = NanoVGGL2.nvgCreate(flags);
      }

    }
  }

  @Override
  public void render(long window, Vector2fc windowSize, Vector2ic frameBufferSize, Frame frame) {

    float pixelRatio = windowSize.x() / frameBufferSize.x();

    preRender(windowSize, pixelRatio);

    renderLayoutTree(frame);
    if (debugMode) {
      renderDebug(frame);
    }

    postRender();
  }

  void renderLayoutTree(Frame layoutTree) {
    renderElement(layoutTree, layoutTree.layoutChildNodes());
  }

  void renderDebug(Frame frame) {
    if (debugMode) {
      debugRenderer.render(frame, nanovgContext, debugMousePosition);
    }
  }

  private void renderElement(Node node, List<Node> children) {
    Element element = node.asElement();
    try (var ignored =
        transformStateFactory.apply(
            nanovgContext, transformAroundBorderBox(element))) {
      subtreeContentRenderer.render(node, nanovgContext);

      if (children != null) {
        try (var contentState = subtreeContentStateFactory.apply(nanovgContext, element)) {
          children.forEach(this::renderLayoutNode);
        }
      }
      scrollbarRenderer.render(element, nanovgContext);
    }
  }

  private AffineTransform transformAroundBorderBox(Element element) {
    Vector2f position = element.layoutAbsolutePosition();
    return AffineTransform.translation(position.x, position.y)
        .multiply(element.presentationState().transform())
        .multiply(AffineTransform.translation(-position.x, -position.y));
  }

  void transformStateFactory(NvgTransformState.Factory transformStateFactory) {
    this.transformStateFactory = transformStateFactory;
  }

  void subtreeContentRenderer(SubtreeContentRenderer subtreeContentRenderer) {
    this.subtreeContentRenderer = subtreeContentRenderer;
  }

  void subtreeContentStateFactory(SubtreeContentState.Factory subtreeContentStateFactory) {
    this.subtreeContentStateFactory = subtreeContentStateFactory;
  }

  void debugRenderer(DebugRenderer debugRenderer) {
    this.debugRenderer = debugRenderer;
  }

  private void renderSubtreeContent(Node node, long context) {
    elementRenderer.render(node, context);
    borderRenderer.render(node, context);
    if (node instanceof InputElement input) {
      inputRenderer.render(input, context);
    } else if (node instanceof TextareaElement textarea) {
      textareaRenderer.render(textarea, context);
    }
  }

  @FunctionalInterface
  interface SubtreeContentRenderer {
    void render(Node node, long context);
  }

  @FunctionalInterface
  interface SubtreeContentState extends AutoCloseable {
    @Override
    void close();

    interface Factory {
      SubtreeContentState apply(long context, Element element);
    }
  }

  @FunctionalInterface
  interface DebugRenderer {
    void render(Frame frame, long context, Vector2fc mousePosition);

    default void textMeasurer(TextMeasurer textMeasurer) {}
  }

  private void renderLayoutNode(Node node) {
    if (node instanceof Element) {
      renderElement(node, node.layoutChildNodes());
    } else if (node instanceof Text) {
      textRenderer.render(node, nanovgContext);
    }
  }

  private void postRender() {

    nvgEndFrame(nanovgContext);

    glDisable(GL_BLEND);
    glEnable(GL_DEPTH_TEST);

    //    imageReferenceManager.removeOldImages(nvgContext);
    //    context.getContextData().remove(NVG_CONTEXT);
    //    context.getContextData().remove(IMAGE_REFERENCE_MANAGER);
  }

  private void preRender(Vector2fc windowSize, float pixelRatio) {
    //    loadFontsToNvg();
    //    context.getContextData().put(NVG_CONTEXT, nvgContext);

    glDisable(GL_DEPTH_TEST);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    nvgBeginFrame(nanovgContext, windowSize.x(), windowSize.y(), pixelRatio);
  }

  public void destroy() {
    if (isVersionNew) {
      NanoVGGL3.nnvgDelete(nanovgContext);
    } else {
      NanoVGGL2.nnvgDelete(nanovgContext);
    }
    //
    // RendererProvider.getInstance().getComponentRenderers().forEach(ComponentRenderer::destroy);
    //    imageReferenceManager.destroy();}
  }

  public boolean debugMode() {
    return debugMode;
  }

  public void debugMode(boolean debugMode) {
    this.debugMode = debugMode;
  }

  public void toggleDebugMode() {
    debugMode(!debugMode);
  }

  public void debugMousePosition(Vector2fc debugMousePosition) {
    this.debugMousePosition =
        debugMousePosition == null ? null : new Vector2f(debugMousePosition);
  }

  public void textMeasurer(TextMeasurer textMeasurer) {
    inputRenderer.textMeasurer(textMeasurer);
    textareaRenderer.textMeasurer(textMeasurer);
    debugRenderer.textMeasurer(textMeasurer);
  }
}
