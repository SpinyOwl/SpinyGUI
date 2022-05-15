package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.createScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.resetScissor;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.nanovg.NanoVG.nvgTextBox;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.CalculatedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import java.util.Set;

public class NvgTextRenderer {

  public void initialize() {
    // to implement
  }

  public void render(Node node, long nanovg) {
    Text text = node.asText();
    //    if (visible(text) && visibleInParents(text)) {
    //      var style = text.calculatedStyle();
    //      var backgroundColor = style.backgroundColor();
    //      var borderRadius = getBorderRadius(text, style);
    var position = text.dimensions().paddingBoxPosition();

    var size = text.dimensions().paddingBoxSize();

    Element parent = text.parent();
    if (parent == null) return;

    Float fontSize = StyleUtils.getFontSize(text);
    if (fontSize == null) return;

    // get text related styles.
    CalculatedStyle style = parent.calculatedStyle();
    Set<String> fontFamilies = style.fontFamilies();
    FontStyle fontStyle = style.fontStyle();
    FontWeight fontWeight = style.fontWeight();

    Float lineHeight = style.lineHeight();

    // find appropriate font.
//    Font fontToUse = findFont(fontFamilies, fontStyle, fontWeight);

    createScissor(nanovg, node);

//    nvgSave(nanovg);
    //    drawRect(nanovg, position, size, Color.ROYALBLUE, 1);
    //    drawRectStroke(nanovg, position, size, Color.RED, 1);
//    nvgFontSize(nanovg, StyleUtils.getFontSize(node));
//    nvgFontFace(nanovg, node.parent().calculatedStyle().font);
//    nvgTextAlign(nanovg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
//    nvgTextBox(nanovg, position.x, position.y, size.x, text.content());
//    nvgRestore(nanovg);

    resetScissor(nanovg);
  }

  public void destroy() {
    // TODO: Implement
  }
}
