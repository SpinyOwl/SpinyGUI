package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.createScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.getBorderRadius;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.resetScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRect;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import com.spinyowl.spinygui.core.layout.LayoutNode;
import com.spinyowl.spinygui.core.node.Element;

public class NvgElementRenderer {

  public void render(LayoutNode layoutNode, long nanovg) {
    Element element = layoutNode.node().asElement();
    if (visible(element) /*&& visibleInParents(element)*/) {
      var style = element.calculatedStyle();
      var backgroundColor = style.backgroundColor();
      var borderRadius = getBorderRadius(element, style);
      var position = element.dimensions().paddingBoxPosition();

      var size = element.dimensions().paddingBoxSize();

      // render self
      createScissor(nanovg, layoutNode);
      nvgSave(nanovg);
      drawRect(nanovg, position, size, backgroundColor, borderRadius);
      nvgRestore(nanovg);
      resetScissor(nanovg);
    }
  }
}
