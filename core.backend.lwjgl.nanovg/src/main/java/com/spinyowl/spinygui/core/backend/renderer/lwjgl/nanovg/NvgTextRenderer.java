package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.createScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.resetScissor;
import static org.lwjgl.nanovg.NanoVG.*;

import com.google.common.collect.ImmutableList;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgColorUtil;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.util.Set;
import org.lwjgl.nanovg.NVGColor;

public class NvgTextRenderer {

  public void render(Node node, long nanovg) {
    Text text = node.asText();
    var position = text.absolutePosition();
    var size = text.size();

    Element parent = text.parent();
    if (parent == null) return;

    ResolvedStyle style = parent.resolvedStyle();

    Float fontSize = StyleUtils.getFontSize(text);
    Set<String> fontFamilies = style.fontFamilies();
    FontStyle fontStyle = style.fontStyle();
    FontWeight fontWeight = style.fontWeight();
    Float lineHeight = style.lineHeight();

    if (fontSize == null) return;
    Font font =
        Font.findFonts(fontFamilies, fontStyle, fontWeight).stream()
            .findFirst()
            .orElse(Font.DEFAULT);

    createScissor(nanovg, node);

    nvgSave(nanovg);
    //    drawRect(nanovg, position, size, Color.ROYALBLUE, 1);
    //    drawRectStroke(nanovg, position, size, Color.RED, 1);

    nvgFontSize(nanovg, fontSize);
    nvgFontFace(nanovg, font.path());
    try (NVGColor color = NvgColorUtil.create(Color.BLACK)) {
      TextMetrics metrics = text.metrics();
      ImmutableList<TextLineMetrics> lines = metrics.lines();
      nvgFillColor(nanovg, color);
      for (int i = 0; i < lines.size(); i++) {
        TextLineMetrics line = lines.get(i);
        nvgBeginPath(nanovg);
        nvgTextAlign(nanovg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgText(
            nanovg,
            position.x(),
            position.y() + fontSize * (lineHeight - 1f) + i * metrics.fullLineHeight(),
            line.characters().toString().trim().replaceAll("\\s+", " "));
      }
    }

    nvgRestore(nanovg);

    resetScissor(nanovg);
  }
}
