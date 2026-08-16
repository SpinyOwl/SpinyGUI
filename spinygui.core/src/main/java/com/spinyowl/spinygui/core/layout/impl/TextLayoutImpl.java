package com.spinyowl.spinygui.core.layout.impl;

import com.google.common.collect.Iterables;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.TextLayout;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.layout.Box;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.util.Comparator;
import java.util.List;
import lombok.NonNull;

public class TextLayoutImpl implements TextLayout {
  @NonNull private final FontService fontService;
  @NonNull private final TextMeasurer textMeasurer;

  public TextLayoutImpl(@NonNull FontService fontService) {
    this(fontService, requireTextMeasurer(fontService));
  }

  public TextLayoutImpl(
      @NonNull FontService fontService, @NonNull TextMeasurer textMeasurer) {
    this.fontService = fontService;
    this.textMeasurer = textMeasurer;
  }

  public void layout(Text text, LayoutContext context) {
    if (text == null) return;

    Element parent = text.parent();
    if (parent == null) return;

    Float fontSize = StyleUtils.getFontSize(text);
    if (fontSize == null) return;

    // get text related styles.
    ResolvedStyle style = parent.resolvedStyle();
    List<String> fontFamilies = style.fontFamilies();
    FontStyle fontStyle = style.fontStyle();
    FontWeight fontWeight = style.fontWeight();

    Float lineHeight = style.lineHeight();

    // find appropriate font.
    List<Font> fontsToUse = findFonts(fontFamilies, fontStyle, fontWeight);

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
        textMeasurer.measureText(
             text.content(), startX, fontsToUse, fontSize, lineHeight, parentWidth, false);

    text.textStartX(startX);
    text.textStartY(startY);

    TextLineMetrics lastLine = Iterables.getLast(metrics.lines());
    float lastTextEndX = lastLine.width();
    float measuredLineHeight = metrics.lineHeight();
    float lastTextEndY = metrics.height() - measuredLineHeight; // top border of last text line.
    if (lastTextEndX >= parentWidth) {
      lastTextEndX = 0;
      lastTextEndY += measuredLineHeight;
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

  List<Font> findFonts(List<String> fontFamilies, FontStyle fontStyle, FontWeight fontWeight) {
    textMeasurer.diagnostics().increment(TextDiagnosticCounter.FONT_CHAIN_RESOLUTIONS);
    List<Font> fonts =
        fontService
            .fontChainResolver()
            .resolve(fontFamilies, fontStyle, fontWeight, FontStretch.NORMAL);
    List<Font> available = fonts.stream().filter(fontService::isFontAvailable).toList();
    return available.isEmpty() ? List.of(Font.DEFAULT) : available;
  }

  private static TextMeasurer requireTextMeasurer(FontService fontService) {
    if (fontService instanceof TextMeasurer textMeasurer) {
      return textMeasurer;
    }
    throw new IllegalArgumentException("FontService must also implement TextMeasurer");
  }
}
