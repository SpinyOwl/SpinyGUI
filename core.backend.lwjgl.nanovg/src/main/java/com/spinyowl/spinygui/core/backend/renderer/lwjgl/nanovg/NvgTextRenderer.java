package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.createScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.resetScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRect;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRectStroke;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import com.spinyowl.spinygui.core.layout.LayoutNode;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Color;

public class NvgTextRenderer {

  public void initialize() {
    // to implement
  }

  public void render(LayoutNode layoutNode, long nanovg) {
    Text text = layoutNode.node().asText();
    //    if (visible(text) && visibleInParents(text)) {
    //      var style = text.calculatedStyle();
    //      var backgroundColor = style.backgroundColor();
    //      var borderRadius = getBorderRadius(text, style);
    var position = text.dimensions().paddingBoxPosition();

    var size = text.dimensions().paddingBoxSize();

    createScissor(nanovg, layoutNode);

    nvgSave(nanovg);
    drawRect(nanovg, position, size, Color.ROYALBLUE, 1);
    drawRectStroke(nanovg, position, size, Color.RED, 1);
    nvgRestore(nanovg);

    resetScissor(nanovg);
  }

  public void destroy() {
    // TODO: Implement
  }
}
