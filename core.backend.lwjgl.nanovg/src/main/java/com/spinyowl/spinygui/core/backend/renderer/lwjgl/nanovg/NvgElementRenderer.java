package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.*;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRect;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;

public class NvgElementRenderer {

  public void render(Node node, long nanovg) {
    Element element = node.asElement();
    if (visible(element) /*&& visibleInParents(element)*/) {
      var style = element.resolvedStyle();
      var backgroundColor = style.backgroundColor();
      var borderRadius = getBorderRadius(element, style);

      var position = element.absolutePosition();
      var size = element.size();

      // render self
      createScissor(nanovg, node);
      nvgSave(nanovg);
      drawRect(nanovg, position, size, backgroundColor, borderRadius);
      nvgRestore(nanovg);
      resetScissor(nanovg);
    }
  }
}
