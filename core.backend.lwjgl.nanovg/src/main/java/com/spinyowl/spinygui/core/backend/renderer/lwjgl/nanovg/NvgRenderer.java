package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetInteger;

import com.spinyowl.spinygui.core.backend.renderer.Renderer;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.LayoutNode;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.util.IOUtil;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL30;

@Slf4j
public class NvgRenderer implements Renderer {

  private final boolean antialiasingEnabled;
  private final AtomicBoolean initialized = new AtomicBoolean(false);
  private final NvgElementRenderer elementRenderer;
  private final NvgTextRenderer textRenderer;
  private final NvgBorderRenderer borderRenderer;

  private final Map<String, Font> loadedFonts = new HashMap<>();
  private final Map<String, ByteBuffer> fontData = new HashMap<>();

  private boolean isVersionNew;
  private long nanovgContext;

  public NvgRenderer(boolean antialiasingEnabled) {
    this.antialiasingEnabled = antialiasingEnabled;
    this.elementRenderer = new NvgElementRenderer();
    this.textRenderer = new NvgTextRenderer();
    this.borderRenderer = new NvgBorderRenderer();
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
  public void render(
      long window, Vector2fc windowSize, Vector2ic frameBufferSize, LayoutNode root) {

    float pixelRatio = windowSize.x() / frameBufferSize.x();

    preRender(windowSize, pixelRatio);

    renderLayoutTree(root);

    postRender();
  }

  private void renderLayoutTree(LayoutNode layoutTree) {
    renderLayoutNode(layoutTree);
  }

  private void renderNode(Node node, List<LayoutNode> children) {
    elementRenderer.render(node, nanovgContext);
    borderRenderer.render(node, nanovgContext);

    if (children != null) {
      children.forEach(this::renderLayoutNode);
    }
  }

  private void renderLayoutNode(LayoutNode layoutNode) {
    Node node = layoutNode.node();
    if (node instanceof Element) {
      renderNode(node, layoutNode.children());
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
    loadFontsToNvg();

    glDisable(GL_DEPTH_TEST);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    nvgBeginFrame(nanovgContext, windowSize.x(), windowSize.y(), pixelRatio);
  }

  private void loadFontsToNvg() {
    Font.getFonts().forEach(this::loadFont);
  }

  private void loadFont(Font font) {
    String fontName = font.path();
    if (loadedFonts.containsKey(fontName)) {
      return;
    }
    ByteBuffer byteBuffer = IOUtil.resourceAsByteBuffer(fontName);
    if (byteBuffer == null) {
      log.error("Failed to load font: {}", fontName);
      return;
    }
    nvgCreateFontMem(nanovgContext, fontName, byteBuffer, false);
    fontData.put(fontName, byteBuffer);
    loadedFonts.put(fontName, font);
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
}
