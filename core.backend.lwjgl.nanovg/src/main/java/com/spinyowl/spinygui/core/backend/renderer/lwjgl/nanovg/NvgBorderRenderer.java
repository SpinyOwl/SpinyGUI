package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.createScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.resetScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRectStroke;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import org.joml.Vector2f;

public class NvgBorderRenderer {

  public void render(Node node, long nanovg) {
    Element element = node.asElement();

    createScissor(nanovg, node);
    var style = element.calculatedStyle();
    if (BorderStyle.NONE.equals(style.borderTopStyle())) return;
    float borderThickness = element.box().border().top();

    Vector2f position = element.absolutePosition().add(borderThickness / 2, borderThickness / 2);
    Vector2f size = element.size().sub(borderThickness, borderThickness);

    drawRectStroke(nanovg, position, size, style.borderTopColor(), borderThickness);
    resetScissor(nanovg);
  }
}
