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
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import java.util.List;
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
  private final NvgDebugRenderer debugRenderer;

  private boolean isVersionNew;
  private boolean debugMode;
  private Vector2f debugMousePosition;
  private long nanovgContext;

  public NvgRenderer(boolean antialiasingEnabled) {
    NvgFontRegistry fontRegistry = new NvgFontRegistry();
    this.antialiasingEnabled = antialiasingEnabled;
    this.elementRenderer = new NvgElementRenderer();
    this.textRenderer = new NvgTextRenderer(fontRegistry);
    this.borderRenderer = new NvgBorderRenderer();
    this.inputRenderer = new NvgInputRenderer(fontRegistry);
    this.textareaRenderer = new NvgTextareaRenderer(fontRegistry);
    this.debugRenderer = new NvgDebugRenderer();
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
      debugRenderer.render(frame, nanovgContext, debugMousePosition);
    }

    postRender();
  }

  private void renderLayoutTree(Frame layoutTree) {
    renderElement(layoutTree, layoutTree.layoutChildNodes());
  }

  private void renderElement(Node node, List<Node> children) {
    elementRenderer.render(node, nanovgContext);
    borderRenderer.render(node, nanovgContext);
    if (node instanceof InputElement input) {
      inputRenderer.render(input, nanovgContext);
    } else if (node instanceof TextareaElement textarea) {
      textareaRenderer.render(textarea, nanovgContext);
    }

    if (children != null) {
      children.forEach(this::renderLayoutNode);
    }
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
