package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.createScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.getBorderRadius;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.resetScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRect;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.style.types.Display;
import org.joml.Vector2f;

public class NvgElementRenderer {

  public void render(Node node, long nanovg) {
    Element element = node.asElement();
    if (visible(element) /*&& visibleInParents(element)*/) {
      var style = element.resolvedStyle();
      if ((Display.INLINE.equals(style.display()) || Display.INLINE_BLOCK.equals(style.display()))
          && !element.inlineFragments().isEmpty()) {
        createScissor(nanovg, node);
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
                        style.backgroundColor()));
        nvgRestore(nanovg);
        resetScissor(nanovg);
        return;
      }
      var backgroundColor = style.backgroundColor();
      var borderRadius = getBorderRadius(element, style);

      var position = element.layoutAbsolutePosition();
      var size = element.size();

      // render self
      createScissor(nanovg, node);
      nvgSave(nanovg);
      drawRect(nanovg, position, size, backgroundColor, borderRadius);
      nvgRestore(nanovg);
      resetScissor(nanovg);
    }
  }

  Vector2f inlineFormattingOffset(Element element) {
    Element parent = element.parent();
    while (parent != null && Display.INLINE.equals(parent.resolvedStyle().display())) {
      parent = parent.parent();
    }
    return parent == null
        ? new Vector2f()
        : parent.layoutAbsolutePosition().sub(parent.scrollLeft(), parent.scrollTop());
  }
}
