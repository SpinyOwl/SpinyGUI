package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.nanovg.NanoVG.NVG_IMAGE_FLIPY;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgDeleteImage;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillPaint;
import static org.lwjgl.nanovg.NanoVG.nvgImagePattern;
import static org.lwjgl.nanovg.NanoVG.nvgRect;

import com.spinyowl.spinygui.core.node.Element;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

/** Draws externally owned OpenGL textures inside ordinary SpinyGUI elements. */
final class NvgExternalTextureRenderer {
  private final Map<Element, TextureBinding> bindings = new IdentityHashMap<>();
  private final Map<Element, ImageBinding> images = new IdentityHashMap<>();

  void bind(Element element, int textureId, int width, int height) {
    Objects.requireNonNull(element, "element");
    if (textureId <= 0) throw new IllegalArgumentException("textureId must be positive");
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("texture dimensions must be positive");
    }
    bindings.put(element, new TextureBinding(textureId, width, height));
  }

  void clear(Element element, long context) {
    bindings.remove(element);
    ImageBinding image = images.remove(element);
    if (image != null && context != 0) nvgDeleteImage(context, image.imageId());
  }

  void render(Element element, long context) {
    TextureBinding binding = bindings.get(element);
    if (binding == null) return;

    ImageBinding image = images.get(element);
    if (image == null || !image.texture().equals(binding)) {
      if (image != null) nvgDeleteImage(context, image.imageId());
      int imageId = createImage(context, binding);
      if (imageId <= 0) {
        throw new IllegalStateException("NanoVG failed to wrap external OpenGL texture");
      }
      image = new ImageBinding(binding, imageId);
      images.put(element, image);
    }

    var position = element.layoutAbsolutePosition();
    var size = element.size();
    if (size.x <= 0 || size.y <= 0) return;

    try (MemoryStack stack = MemoryStack.stackPush()) {
      NVGPaint paint = NVGPaint.malloc(stack);
      nvgImagePattern(
          context,
          position.x,
          position.y,
          size.x,
          size.y,
          0f,
          image.imageId(),
          1f,
          paint);
      nvgBeginPath(context);
      nvgRect(context, position.x, position.y, size.x, size.y);
      nvgFillPaint(context, paint);
      nvgFill(context);
    }
  }

  void release(long context) {
    if (context != 0) {
      images.values().forEach(image -> nvgDeleteImage(context, image.imageId()));
    }
    images.clear();
    bindings.clear();
  }

  private int createImage(long context, TextureBinding binding) {
    if (GL.getCapabilities().OpenGL30) {
      int flags = org.lwjgl.nanovg.NanoVGGL3.NVG_IMAGE_NODELETE | NVG_IMAGE_FLIPY;
      return org.lwjgl.nanovg.NanoVGGL3.nvglCreateImageFromHandle(
          context, binding.textureId(), binding.width(), binding.height(), flags);
    }
    int flags = org.lwjgl.nanovg.NanoVGGL2.NVG_IMAGE_NODELETE | NVG_IMAGE_FLIPY;
    return org.lwjgl.nanovg.NanoVGGL2.nvglCreateImageFromHandle(
        context, binding.textureId(), binding.width(), binding.height(), flags);
  }

  private record TextureBinding(int textureId, int width, int height) {}
  private record ImageBinding(TextureBinding texture, int imageId) {}
}
