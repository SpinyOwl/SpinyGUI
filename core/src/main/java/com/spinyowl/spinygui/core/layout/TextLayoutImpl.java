package com.spinyowl.spinygui.core.layout;

import com.google.common.collect.Iterables;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.layout.Box;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.tree.LayoutContext;
import com.spinyowl.spinygui.core.system.tree.LayoutNode;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TextLayoutImpl implements TextLayout {
  @NonNull private final FontService fontService;

  public void layout(LayoutNode layoutNode, LayoutContext context) {
    if (!(layoutNode.node() instanceof Text text)) return;

    Element parent = text.parent();
    if (parent == null) return;

    Float fontSize = StyleUtils.getFontSize(text);
    if (fontSize == null) return;

    // get text related styles.
    ResolvedStyle style = parent.resolvedStyle();
    Set<String> fontFamilies = style.fontFamilies();
    FontStyle fontStyle = style.fontStyle();
    FontWeight fontWeight = style.fontWeight();

    Float lineHeight = style.lineHeight();

    // find appropriate font.
    Font fontToUse = findFont(fontFamilies, fontStyle, fontWeight);

    // get width of parent node.
    Box parentBox = parent.box();
    Rect parentContent = parentBox.content();
    float parentWidth = parentContent.width();

    // top left by default until `text-align` property not implemented.
    float startX = 0;
    float startY = 0;

    startX = context.lastTextEndX() != null ? context.lastTextEndX() : startX;

    if (context.lastTextEndY() != null) {
      startY = context.lastTextEndY();
    } else if (context.previousNode() != null) {
      startY =
          context.previousNode().box().borderBox().height()
              + context.previousNode().box().borderBox().y();
    }

    TextMetrics metrics =
        fontService.getTextMetrics(
            text.content(), startX, fontToUse, fontSize, lineHeight, parentWidth, false);

    text.textStartX(startX);
    text.textStartY(startY);

    TextLineMetrics lastLine = Iterables.getLast(metrics.lines());
    float lastTextEndX = lastLine.width();
    float fullLineHeight = metrics.fullLineHeight();
    float lastTextEndY = metrics.height() - fullLineHeight; // top border of last text line.
    if (lastTextEndX >= parentWidth) {
      lastTextEndX = 0;
      lastTextEndY += fullLineHeight;
    }

    text.textEndX(lastTextEndX);
    text.textEndY(lastTextEndY);

    float contentX = parentBox.border().left() + parentBox.padding().left();
    float contentY = parentBox.border().top() + parentBox.padding().top();

    text.box().contentPosition(contentX, contentY + startY);

    float maxTextWidth =
        metrics.lines().stream()
            .map(TextLineMetrics::width)
            .max(Comparator.naturalOrder())
            .orElse(0f);

    text.box().contentSize(maxTextWidth, metrics.height());

    context.lastTextEndX(lastTextEndX);
    context.lastTextEndY(lastTextEndY);
    context.lastBlockBottomY(text.box().borderBox().y() + text.box().borderBox().height());
  }

  private Font findFont(Set<String> fontFamilies, FontStyle fontStyle, FontWeight fontWeight) {
    Set<Font> fonts =
        fontFamilies.stream()
            .map(f -> Font.getFonts(f, fontStyle, fontWeight))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    return fonts.stream().filter(fontService::isFontAvailable).findFirst().orElse(Font.DEFAULT);
  }
}
