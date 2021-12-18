package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.getBorderRadius;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.inScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRect;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visibleInParents;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import com.spinyowl.spinygui.core.node.Element;

public class NvgElementRenderer {

  public NvgElementRenderer() {}

  public void render(Element element, long nanovg) {
    if (visible(element) && visibleInParents(element)) {
      var style = element.calculatedStyle();
      var backgroundColor = style.backgroundColor();
      var borderRadius = getBorderRadius(element, style);
      var position =
          element
              .absolutePosition()
              .add(element.dimensions().border().left(), element.dimensions().border().top());

      var size = element.dimensions().paddingBoxSize();

      // render self
      inScissor(
          nanovg,
          element,
          () -> {
            nvgSave(nanovg);
            drawRect(nanovg, position, size, backgroundColor, borderRadius);
            nvgRestore(nanovg);
          });
    }
  }
}
