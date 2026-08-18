package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.getBorderRadius;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.withPresentedOpacity;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRect;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import static org.lwjgl.nanovg.NanoVG.NVG_IMAGE_FLIPY;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgCreateImage;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillPaint;
import static org.lwjgl.nanovg.NanoVG.nvgImagePattern;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgRoundedRect;
import static org.lwjgl.nanovg.NanoVG.nvgRoundedRectVarying;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.system.MemoryStack.stackPush;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.style.types.Display;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL30;

public class NvgElementRenderer {
  private static final String IMG = "img";
  private static final String SRC = "src";
  private static final String TEXTURE_ID = "texture-id";
  private static final String FLIP_Y = "flip-y";

  private final DiagnosticSession diagnostics;
  private final Map<String, Integer> imageCache = new HashMap<>();

  public NvgElementRenderer() {
    this(DiagnosticSession.disabled());
  }

  NvgElementRenderer(DiagnosticSession diagnostics) {
    this.diagnostics = diagnostics;
  }

  public void render(Node node, long nanovg) {
    Element element = node.asElement();
    if (visible(element) /*&& visibleInParents(element)*/) {
      var style = element.resolvedStyle();
      var presentedStyle = element.presentedStyle();
      if ((Display.INLINE.equals(style.display()) || Display.INLINE_BLOCK.equals(style.display()))
          && !element.inlineFragments().isEmpty()) {
        diagnostics.increment(NvgDiagnosticCounter.SAVE_CALLS);
        nvgSave(nanovg);
        Vector2f offset = inlineFormattingOffset(element);
        element
            .inlineFragments()
            .forEach(
                fragment ->
                    drawRect(
                        nanovg,
                        new Vector2f(offset.x + fragment.x(), offset.y + fragment.y()),
                        new Vector2f(fragment.width(), fragment.height()),
                        withPresentedOpacity(presentedStyle.backgroundColor(), element)));
        diagnostics.increment(NvgDiagnosticCounter.RESTORE_CALLS);
        nvgRestore(nanovg);
        return;
      }
      var backgroundColor = withPresentedOpacity(presentedStyle.backgroundColor(), element);
      var borderRadius = getBorderRadius(element, style);

      var position = element.layoutAbsolutePosition();
      var size = element.size();

      // Always render the element rectangle first. For <img>, this is also the fallback when the
      // source cannot be resolved or loaded.
      diagnostics.increment(NvgDiagnosticCounter.SAVE_CALLS);
      nvgSave(nanovg);
      drawRect(nanovg, position, size, backgroundColor, borderRadius);
      if (IMG.equalsIgnoreCase(element.nodeName())) {
        renderImage(element, nanovg, position, size, borderRadius);
      }
      diagnostics.increment(NvgDiagnosticCounter.RESTORE_CALLS);
      nvgRestore(nanovg);
    }
  }

  private void renderImage(
      Element element, long nanovg, Vector2f position, Vector2f size, Vector4f borderRadius) {
    if (size.x <= 0 || size.y <= 0) {
      return;
    }

    int image = resolveImage(element, nanovg, size);
    if (image <= 0) {
      return;
    }

    try (var stack = stackPush()) {
      NVGPaint paint = NVGPaint.malloc(stack);
      nvgImagePattern(nanovg, position.x, position.y, size.x, size.y, 0f, image, 1f, paint);
      nvgBeginPath(nanovg);
      if (borderRadius != null && !borderRadius.equals(new Vector4f(0))) {
        if (borderRadius.x == borderRadius.y
            && borderRadius.x == borderRadius.z
            && borderRadius.x == borderRadius.w) {
          nvgRoundedRect(nanovg, position.x, position.y, size.x, size.y, borderRadius.x);
        } else {
          nvgRoundedRectVarying(
              nanovg,
              position.x,
              position.y,
              size.x,
              size.y,
              borderRadius.x,
              borderRadius.y,
              borderRadius.z,
              borderRadius.w);
        }
      } else {
        nvgRect(nanovg, position.x, position.y, size.x, size.y);
      }
      nvgFillPaint(nanovg, paint);
      nvgFill(nanovg);
    }
  }

  private int resolveImage(Element element, long nanovg, Vector2f size) {
    String textureId = attribute(element, TEXTURE_ID);
    if (textureId != null) {
      return resolveExternalTexture(element, nanovg, size, textureId);
    }

    String src = attribute(element, SRC);
    if (src == null) {
      return 0;
    }
    return imageCache.computeIfAbsent("src:" + src, ignored -> createImage(nanovg, src));
  }

  private int resolveExternalTexture(
      Element element, long nanovg, Vector2f size, String textureIdValue) {
    final int textureId;
    try {
      textureId = Integer.parseUnsignedInt(textureIdValue);
    } catch (NumberFormatException ignored) {
      return 0;
    }
    if (textureId == 0) {
      return 0;
    }

    int width = Math.max(1, Math.round(size.x));
    int height = Math.max(1, Math.round(size.y));
    boolean flipY = Boolean.parseBoolean(attribute(element, FLIP_Y));
    String key = "texture:" + textureId + ':' + width + 'x' + height + ':' + flipY;
    return imageCache.computeIfAbsent(
        key, ignored -> importExternalTexture(nanovg, textureId, width, height, flipY));
  }

  private int createImage(long nanovg, String src) {
    try (var stack = stackPush()) {
      return nvgCreateImage(nanovg, stack.UTF8(src), 0);
    } catch (RuntimeException ignored) {
      return 0;
    }
  }

  private int importExternalTexture(
      long nanovg, int textureId, int width, int height, boolean flipY) {
    int imageFlags = flipY ? NVG_IMAGE_FLIPY : 0;
    try {
      boolean gl3 =
          (GL30.glGetInteger(GL30.GL_MAJOR_VERSION) > 3)
              || GL30.glGetInteger(GL30.GL_MAJOR_VERSION) == 3
                  && GL30.glGetInteger(GL30.GL_MINOR_VERSION) >= 2;
      if (gl3) {
        return NanoVGGL3.nvglCreateImageFromHandle(
            nanovg, textureId, width, height, imageFlags | NanoVGGL3.NVG_IMAGE_NODELETE);
      }
      return NanoVGGL2.nvglCreateImageFromHandle(
          nanovg, textureId, width, height, imageFlags | NanoVGGL2.NVG_IMAGE_NODELETE);
    } catch (RuntimeException ignored) {
      return 0;
    }
  }

  private String attribute(Element element, String name) {
    String value = element.attributes().get(name);
    return value == null || value.isBlank() ? null : value.trim();
  }

  Vector2f inlineFormattingOffset(Element element) {
    Element parent = element.parent();
    while (parent != null && Display.INLINE.equals(parent.resolvedStyle().display())) {
      parent = parent.parent();
    }
    return parent == null
        ? new Vector2f()
        : parent.layoutAbsolutePosition();
  }
}
