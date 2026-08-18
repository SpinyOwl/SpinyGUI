package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;

/**
 * NanoVG renderer variant that can present externally owned OpenGL textures inside regular
 * SpinyGUI elements. Texture ownership stays with the caller.
 */
public final class ExternalTextureNvgRenderer extends NvgRenderer {
  private final NvgElementRenderer elements = new NvgElementRenderer();
  private final NvgBorderRenderer borders = new NvgBorderRenderer();
  private final NvgExternalTextureRenderer textures = new NvgExternalTextureRenderer();

  public ExternalTextureNvgRenderer() {
    super();
    subtreeContentRenderer(this::renderElementContent);
  }

  /** Bind an externally owned OpenGL texture to an element. */
  public void bindExternalTexture(Element element, int textureId, int width, int height) {
    textures.bind(element, textureId, width, height);
  }

  /** Remove an external texture binding without deleting the OpenGL texture. */
  public void clearExternalTexture(Element element) {
    textures.clear(element, contextIdentity());
  }

  private void renderElementContent(Node node, long context) {
    elements.render(node, context);
    if (node instanceof Element element) textures.render(element, context);
    borders.render(node, context);
  }

  @Override
  public void destroy() {
    textures.release(contextIdentity());
    super.destroy();
  }
}
