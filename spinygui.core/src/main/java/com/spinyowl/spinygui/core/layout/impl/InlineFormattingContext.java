package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.setBorders;
import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.setPadding;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getFloatLengthOptional;
import static com.spinyowl.spinygui.core.style.types.Display.INLINE;
import static com.spinyowl.spinygui.core.style.types.Display.INLINE_BLOCK;
import static com.spinyowl.spinygui.core.style.types.OverflowWrap.ANYWHERE;
import static com.spinyowl.spinygui.core.style.types.OverflowWrap.BREAK_WORD;
import static com.spinyowl.spinygui.core.style.types.TextAlign.CENTER;
import static com.spinyowl.spinygui.core.style.types.TextAlign.RIGHT;
import static com.spinyowl.spinygui.core.style.types.WhiteSpace.NOWRAP;
import static com.spinyowl.spinygui.core.style.types.WhiteSpace.PRE;
import static com.spinyowl.spinygui.core.style.types.WhiteSpace.PRE_WRAP;
import static com.spinyowl.spinygui.core.style.types.WordBreak.BREAK_ALL;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.layout.LineBox;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.WordBreak;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InlineFormattingContext {
  @NonNull private final TextMeasurer textMeasurer;
  private InlineBlockLayout inlineBlockLayout;

  void inlineBlockLayout(InlineBlockLayout inlineBlockLayout) {
    this.inlineBlockLayout = inlineBlockLayout;
  }

  public float layout(@NonNull Element parent, @NonNull List<Node> nodes, float startY) {
    if (nodes.isEmpty()) {
      return 0;
    }

    float contentX = parent.box().border().left() + parent.box().padding().left();
    float contentY = parent.box().border().top() + parent.box().padding().top() + startY;
    float availableWidth = Math.max(0, parent.box().content().width());
    List<InlineUnit> units = new ArrayList<>();
    Map<Text, List<InlineFragment>> textFragments = new IdentityHashMap<>();
    Map<Element, List<InlineFragment>> elementFragments = new IdentityHashMap<>();

    nodes.forEach(node -> collectUnits(node, units, textFragments, elementFragments, parent));
    List<LineBox> lines = buildLines(parent, units, contentX, contentY, availableWidth);
    alignLines(parent, lines, contentX, availableWidth);

    for (LineBox line : lines) {
      for (InlineFragment fragment : line.fragments()) {
        if (fragment.node() instanceof Text text) {
          textFragments.computeIfAbsent(text, k -> new ArrayList<>()).add(fragment);
        }
      }
    }

    collectElementLineFragments(lines, elementFragments);

    textFragments.forEach(
        (text, fragments) -> {
          text.inlineFragments(fragments);
          applyUnionBox(text, fragments);
          if (!fragments.isEmpty()) {
            InlineFragment first = fragments.get(0);
            InlineFragment last = fragments.get(fragments.size() - 1);
            text.textStartX(first.x() - contentX);
            text.textStartY(first.y() - contentY);
            text.textEndX(last.x() + last.width() - contentX);
            text.textEndY(last.y() - contentY);
          }
        });

    for (Node node : nodes) {
      if (node instanceof Element element) {
        applyInlineElementFragments(element, textFragments, elementFragments);
      }
    }

    return lines.stream().map(line -> line.y() + line.height() - contentY).max(Float::compare).orElse(0f);
  }

  public boolean inlineNode(Node node) {
    if (node instanceof Text) {
      return true;
    }
    return node instanceof Element element && inlineDisplay(element);
  }

  private void collectUnits(
      Node node,
      List<InlineUnit> units,
      Map<Text, List<InlineFragment>> textFragments,
      Map<Element, List<InlineFragment>> elementFragments,
      Element inheritedParent) {
    if (node instanceof Text text) {
      text.inlineFragments(List.of());
      text.box().contentPosition(0, 0);
      text.box().contentSize(0, 0);
      units.addAll(textUnits(text, inheritedParent.resolvedStyle()));
      return;
    }

    if (!(node instanceof Element element)) {
      return;
    }

    ResolvedStyle style = element.resolvedStyle();
    element.inlineFragments(List.of());
    if (INLINE_BLOCK.equals(style.display())) {
      collectInlineBlockUnit(element, units, inheritedParent);
      return;
    }
    setBorders(style, element.box().border());
    setPadding(
        inheritedParent.box().content().width(),
        inheritedParent.box().content().height(),
        style,
        element.box().padding());
    setMargins(element, inheritedParent.box().content().width());

    addSpacer(units, element, style, element.box().margin().left(), false);
    addSpacer(
        units,
        element,
        style,
        element.box().border().left() + element.box().padding().left(),
        true);
    addVerticalStrut(units, element, style);

    for (Node child : element.childNodes()) {
      if (inlineNode(child)) {
        collectUnits(child, units, textFragments, elementFragments, element);
      }
    }

    addSpacer(
        units,
        element,
        style,
        element.box().padding().right() + element.box().border().right(),
        true);
    addSpacer(units, element, style, element.box().margin().right(), false);
  }

  private void collectInlineBlockUnit(
      Element element, List<InlineUnit> units, Element inheritedParent) {
    ResolvedStyle style = element.resolvedStyle();
    setBorders(style, element.box().border());
    setPadding(
        inheritedParent.box().content().width(),
        inheritedParent.box().content().height(),
        style,
        element.box().padding());
    if (inlineBlockLayout != null) {
      inlineBlockLayout.layout(element, inheritedParent);
    }
    setMargins(element, inheritedParent.box().content().width());
    units.add(inlineBlockUnit(element, style, inlineBlockMetrics(element)));
  }

  private InlineUnit inlineBlockUnit(
      Element element, ResolvedStyle style, InlineBlockMetrics metrics) {
    return new InlineUnit(element, style, null, false, false, false, metrics, 0, 0, true);
  }

  private void addSpacer(
      List<InlineUnit> units,
      Element element,
      ResolvedStyle style,
      float width,
      boolean elementBox) {
    if (width > 0) {
      units.add(new InlineUnit(element, style, null, false, false, true, width, 0, elementBox));
    }
  }

  private void addVerticalStrut(List<InlineUnit> units, Element element, ResolvedStyle style) {
    float verticalAdditions =
        element.box().border().top()
            + element.box().padding().top()
            + element.box().padding().bottom()
            + element.box().border().bottom();
    if (verticalAdditions > 0) {
      units.add(
          new InlineUnit(element, style, null, false, false, true, 0, verticalAdditions, true));
    }
  }

  private List<InlineUnit> textUnits(Text text, ResolvedStyle style) {
    String normalized = InlineWhitespace.normalize(text.content() == null ? "" : text.content(), style);
    var result = new ArrayList<InlineUnit>();
    WhiteSpace whiteSpace = style.whiteSpace();
    boolean preserveSpaces = PRE.equals(whiteSpace) || PRE_WRAP.equals(whiteSpace);
    int start = 0;
    for (int i = 0; i < normalized.length(); i++) {
      char c = normalized.charAt(i);
      if (c == '\n') {
        addTextUnit(text, style, normalized.substring(start, i), preserveSpaces, result);
        result.add(new InlineUnit(text, style, null, false, true, false, 0, 0, false));
        start = i + 1;
      } else if (!preserveSpaces && c == ' ') {
        addTextUnit(text, style, normalized.substring(start, i), false, result);
        result.add(new InlineUnit(text, style, " ", true, false, false, 0, 0, false));
        start = i + 1;
      }
    }
    addTextUnit(text, style, normalized.substring(start), preserveSpaces, result);
    return result;
  }

  private void addTextUnit(
      Text text, ResolvedStyle style, String value, boolean preserveSpaces, List<InlineUnit> units) {
    if (value.isEmpty()) {
      return;
    }
    if (preserveSpaces) {
      for (int i = 0; i < value.length(); i++) {
        units.add(
            new InlineUnit(
                text, style, String.valueOf(value.charAt(i)), false, false, false, 0, 0, false));
      }
    } else {
      units.add(new InlineUnit(text, style, value, false, false, false, 0, 0, false));
    }
  }

  private List<LineBox> buildLines(
      Element parent, List<InlineUnit> units, float contentX, float contentY, float availableWidth) {
    List<LineBox> lines = new ArrayList<>();
    LineBox line = newLine(contentX, contentY);
    float cursorX = contentX;

    for (InlineUnit unit : units) {
      if (unit.newline) {
        lines.add(closeLine(line));
        contentY += line.height();
        line = newLine(contentX, contentY);
        cursorX = contentX;
        continue;
      }
      if (unit.space && line.empty()) {
        continue;
      }

      float width = unit.width(textMeasurer);
      boolean breakIntoCharacters =
          unit.breakIntoCharacters(textMeasurer, availableWidth, cursorX, contentX);
      boolean wrap =
          !breakIntoCharacters
              && unit.wraps()
              && !line.empty()
              && cursorX + width > contentX + availableWidth;
      if (wrap) {
        trimTrailingSpace(line);
        lines.add(closeLine(line));
        contentY += line.height();
        line = newLine(contentX, contentY);
        cursorX = contentX;
        if (unit.space) {
          continue;
        }
      }

      if (breakIntoCharacters) {
        for (InlineUnit part : unit.breakIntoCharacters()) {
          if (!line.empty() && cursorX + part.width(textMeasurer) > contentX + availableWidth) {
            lines.add(closeLine(line));
            contentY += line.height();
            line = newLine(contentX, contentY);
            cursorX = contentX;
          }
          cursorX = addUnit(line, part, cursorX);
        }
      } else {
        cursorX = addUnit(line, unit, cursorX);
      }
    }

    trimTrailingSpace(line);
    if (!line.empty() || lines.isEmpty()) {
      lines.add(closeLine(line));
    }
    return lines;
  }

  private LineBox newLine(float x, float y) {
    var line = new LineBox();
    line.x(x);
    line.y(y);
    return line;
  }

  private float addUnit(LineBox line, InlineUnit unit, float cursorX) {
    float width = unit.width(textMeasurer);
    float height = unit.lineHeight(textMeasurer);
    float baseline = line.y() + unit.baseline(textMeasurer);
    line.addFragment(
        InlineFragment.builder()
            .node(unit.fragmentNode())
            .text(unit.text)
            .x(unit.fragmentX(cursorX))
            .y(line.y())
            .width(unit.fragmentWidth(width))
            .height(unit.fragmentHeight(height))
            .baseline(baseline)
            .font(unit.font)
            .fontSize(unit.fontSize)
            .color(unit.style.color())
            .build());
    return cursorX + width;
  }

  private LineBox closeLine(LineBox line) {
    if (line.height() == 0) {
      line.height(0);
      line.baseline(0);
      return line;
    }
    var adjusted = new LineBox();
    adjusted.x(line.x());
    adjusted.y(line.y());
    adjusted.height(line.height());
    adjusted.baseline(line.baseline());
    for (InlineFragment fragment : line.fragments()) {
      float dy = line.baseline() - (fragment.baseline() - line.y());
      adjusted.addFragment(fragment.translate(0, dy));
    }
    adjusted.width(line.width());
    adjusted.height(line.height());
    adjusted.baseline(line.baseline());
    return adjusted;
  }

  private void trimTrailingSpace(LineBox line) {
    List<InlineFragment> fragments = new ArrayList<>(line.fragments());
    if (!fragments.isEmpty()) {
      InlineFragment last = fragments.get(fragments.size() - 1);
      if (" ".equals(last.text())) {
        fragments.remove(fragments.size() - 1);
        line.removeLastFragment();
      }
    }
  }

  private void alignLines(Element parent, List<LineBox> lines, float contentX, float availableWidth) {
    for (LineBox line : lines) {
      float dx = 0;
      if (RIGHT.equals(parent.resolvedStyle().textAlign())) {
        dx = availableWidth - line.width();
      } else if (CENTER.equals(parent.resolvedStyle().textAlign())) {
        dx = (availableWidth - line.width()) / 2;
      }
      if (dx != 0) {
        float shift = dx;
        var fragments = new ArrayList<InlineFragment>();
        line.fragments().forEach(fragment -> fragments.add(fragment.translate(shift, 0)));
        line.x(contentX + dx);
        line.replaceFragments(fragments);
      } else {
        line.x(contentX);
      }
    }
  }

  private void applyUnionBox(Node node, List<InlineFragment> fragments) {
    if (fragments.isEmpty()) {
      node.box().contentSize(0, 0);
      return;
    }
    float minX = fragments.stream().map(InlineFragment::x).min(Comparator.naturalOrder()).orElse(0f);
    float minY = fragments.stream().map(InlineFragment::y).min(Comparator.naturalOrder()).orElse(0f);
    float maxX =
        fragments.stream()
            .map(fragment -> fragment.x() + fragment.width())
            .max(Comparator.naturalOrder())
            .orElse(minX);
    float maxY =
        fragments.stream()
            .map(fragment -> fragment.y() + fragment.height())
            .max(Comparator.naturalOrder())
            .orElse(minY);
    node.box().contentPosition(minX, minY);
    node.box().contentSize(maxX - minX, maxY - minY);
  }

  private void applyInlineElementFragments(
      Element element,
      Map<Text, List<InlineFragment>> textFragments,
      Map<Element, List<InlineFragment>> elementFragments) {
    if (INLINE_BLOCK.equals(element.resolvedStyle().display())) {
      element.inlineFragments(elementFragments.getOrDefault(element, List.of()));
      applyInlineBlockFragmentBox(element);
      return;
    }
    List<InlineFragment> fragments = new ArrayList<>();
    collectDescendantTextFragments(element, textFragments, fragments);
    fragments.addAll(elementFragments.getOrDefault(element, List.of()));
    element.inlineFragments(elementFragments.getOrDefault(element, List.of()));
    applyUnionBox(element, fragments);
    element.children().stream()
        .filter(child -> INLINE.equals(child.resolvedStyle().display()))
        .forEach(child -> applyInlineElementFragments(child, textFragments, elementFragments));
  }

  private void applyInlineBlockFragmentBox(Element element) {
    List<InlineFragment> fragments = element.inlineFragments();
    if (fragments.isEmpty()) {
      return;
    }
    InlineFragment fragment = fragments.get(0);
    element
        .box()
        .contentPosition(
            fragment.x() + element.box().border().left() + element.box().padding().left(),
            fragment.y() + element.box().border().top() + element.box().padding().top());
  }

  private void collectDescendantTextFragments(
      Element element,
      Map<Text, List<InlineFragment>> textFragments,
      List<InlineFragment> fragments) {
    for (Node child : element.childNodes()) {
      if (child instanceof Text text) {
        fragments.addAll(textFragments.getOrDefault(text, List.of()));
      } else if (child instanceof Element childElement) {
        collectDescendantTextFragments(childElement, textFragments, fragments);
      }
    }
  }

  private void collectElementLineFragments(
      List<LineBox> lines, Map<Element, List<InlineFragment>> elementFragments) {
    for (LineBox line : lines) {
      Map<Element, FragmentBounds> bounds = new IdentityHashMap<>();
      for (InlineFragment fragment : line.fragments()) {
        if (fragment.node() instanceof Text text) {
          collectTextAncestors(text, fragment, bounds);
        } else if (fragment.node() instanceof Element element && fragment.width() > 0) {
          addElementBounds(element, fragment, bounds);
        }
      }
      bounds.forEach(
          (element, fragmentBounds) ->
              elementFragments
                  .computeIfAbsent(element, ignored -> new ArrayList<>())
                  .add(fragmentBounds.toFragment(element)));
    }
  }

  private void collectTextAncestors(
      Text text, InlineFragment fragment, Map<Element, FragmentBounds> bounds) {
    Element current = text.parent();
    while (current != null && inlineDisplay(current)) {
      addElementBounds(current, fragment, bounds);
      current = current.parent();
    }
  }

  private void addElementBounds(
      Element element, InlineFragment fragment, Map<Element, FragmentBounds> bounds) {
    if (INLINE_BLOCK.equals(element.resolvedStyle().display())) {
      bounds.computeIfAbsent(element, ignored -> new FragmentBounds()).includeInlineBlock(fragment);
      return;
    }
    FragmentBounds fragmentBounds =
        bounds.computeIfAbsent(element, ignored -> new FragmentBounds());
    fragmentBounds.include(fragment, verticalExpansion(element));
  }

  private float verticalExpansion(Element element) {
    return element.box().border().top()
        + element.box().padding().top()
        + element.box().padding().bottom()
        + element.box().border().bottom();
  }

  private InlineBlockMetrics inlineBlockMetrics(Element element) {
    float borderWidth = element.box().borderBox().width();
    float borderHeight = element.box().borderBox().height();
    return new InlineBlockMetrics(
        element.box().marginBox().width(),
        element.box().marginBox().height(),
        borderWidth,
        borderHeight,
        element.box().margin().left(),
        element.box().margin().top(),
        inlineBlockBaseline(element, borderHeight));
  }

  private float inlineBlockBaseline(Element element, float fallback) {
    float contentOffset = element.box().border().top() + element.box().padding().top();
    return element
        .childNodes()
        .stream()
        .flatMap(child -> descendantTextFragments(child).stream())
        .map(fragment -> contentOffset + fragment.baseline())
        .max(Float::compare)
        .orElse(fallback);
  }

  private List<InlineFragment> descendantTextFragments(Node node) {
    if (node instanceof Text text) {
      return text.inlineFragments();
    }
    if (node instanceof Element element) {
      return element
          .childNodes()
          .stream()
          .flatMap(child -> descendantTextFragments(child).stream())
          .toList();
    }
    return List.of();
  }

  private boolean inlineDisplay(Element element) {
    return INLINE.equals(element.resolvedStyle().display())
        || INLINE_BLOCK.equals(element.resolvedStyle().display());
  }

  private Font findFont(ResolvedStyle style) {
    Set<String> fontFamilies = style.fontFamilies();
    FontStyle fontStyle = style.fontStyle();
    FontWeight fontWeight = style.fontWeight();
    Set<Font> fonts =
        fontFamilies.stream()
            .map(f -> Font.find(f, fontStyle, fontWeight))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    return fonts.stream().findFirst().orElse(Font.DEFAULT);
  }

  private void setMargins(Element element, float parentWidth) {
    ResolvedStyle style = element.resolvedStyle();
    element.box().margin().left(getFloatLengthOptional(style.marginLeft(), parentWidth).orElse(0f));
    element.box().margin().right(getFloatLengthOptional(style.marginRight(), parentWidth).orElse(0f));
    element.box().margin().top(0);
    element.box().margin().bottom(0);
  }

  private class InlineUnit {
    private final Node node;
    private final ResolvedStyle style;
    private final String text;
    private final boolean space;
    private final boolean newline;
    private final boolean spacer;
    private final InlineBlockMetrics inlineBlockMetrics;
    private final float spacerWidth;
    private final float extraHeight;
    private final boolean elementBox;
    private final Font font;
    private final float fontSize;
    private TextLineMetrics measurement;

    private InlineUnit(
        Node node,
        ResolvedStyle style,
        String text,
        boolean space,
        boolean newline,
        boolean spacer,
        float spacerWidth,
        float extraHeight,
        boolean elementBox) {
      this(node, style, text, space, newline, spacer, null, spacerWidth, extraHeight, elementBox);
    }

    private InlineUnit(
        Node node,
        ResolvedStyle style,
        String text,
        boolean space,
        boolean newline,
        boolean spacer,
        InlineBlockMetrics inlineBlockMetrics,
        float spacerWidth,
        float extraHeight,
        boolean elementBox) {
      this.node = node;
      this.style = style;
      this.text = text;
      this.space = space;
      this.newline = newline;
      this.spacer = spacer;
      this.inlineBlockMetrics = inlineBlockMetrics;
      this.spacerWidth = spacerWidth;
      this.extraHeight = extraHeight;
      this.elementBox = elementBox;
      this.font = findFont(style);
      Float measuredFontSize = StyleUtils.getFontSize(node);
      this.fontSize = measuredFontSize == null ? 16f : measuredFontSize;
    }

    float width(TextMeasurer measurer) {
      if (inlineBlockMetrics != null) {
        return inlineBlockMetrics.marginWidth();
      }
      if (spacer) {
        return spacerWidth;
      }
      return text == null || text.isEmpty() ? 0 : measurement(measurer).width();
    }

    TextLineMetrics measurement(TextMeasurer measurer) {
      if (measurement == null) {
        measurement = measurer.getTextLineMetrics(text == null ? "" : text, font, fontSize, style.lineHeight());
      }
      return measurement;
    }

    float lineHeight(TextMeasurer measurer) {
      if (inlineBlockMetrics != null) {
        return inlineBlockMetrics.marginHeight();
      }
      return measurement(measurer).height() + extraHeight;
    }

    float baseline(TextMeasurer measurer) {
      if (inlineBlockMetrics != null) {
        return inlineBlockMetrics.baseline() + inlineBlockMetrics.marginTop();
      }
      return measurement(measurer).baseline() + extraHeight / 2;
    }

    boolean wraps() {
      WhiteSpace whiteSpace = style.whiteSpace();
      return !NOWRAP.equals(whiteSpace) && !PRE.equals(whiteSpace);
    }

    boolean breakWord(TextMeasurer measurer, float availableWidth) {
      OverflowWrap overflowWrap = style.overflowWrap();
      WordBreak wordBreak = style.wordBreak();
      return (BREAK_WORD.equals(overflowWrap)
              || ANYWHERE.equals(overflowWrap)
              || WordBreak.BREAK_WORD.equals(wordBreak))
          && text != null
          && !space
          && wraps()
          && width(measurer) > availableWidth;
    }

    boolean breakIntoCharacters(
        TextMeasurer measurer, float availableWidth, float cursorX, float contentX) {
      return text != null
          && !space
          && wraps()
          && (BREAK_ALL.equals(style.wordBreak()) || breakWord(measurer, availableWidth))
          && cursorX + width(measurer) > contentX + availableWidth;
    }

    List<InlineUnit> breakIntoCharacters() {
      var parts = new ArrayList<InlineUnit>();
      for (int i = 0; i < text.length(); i++) {
        parts.add(
            new InlineUnit(
                node, style, String.valueOf(text.charAt(i)), false, false, false, 0, 0, false));
      }
      return parts;
    }

    Node fragmentNode() {
      return spacer && !elementBox ? null : node;
    }

    float fragmentX(float cursorX) {
      return inlineBlockMetrics == null ? cursorX : cursorX + inlineBlockMetrics.marginLeft();
    }

    float fragmentWidth(float width) {
      return inlineBlockMetrics == null ? width : inlineBlockMetrics.borderWidth();
    }

    float fragmentHeight(float height) {
      return inlineBlockMetrics == null ? height : inlineBlockMetrics.borderHeight();
    }
  }

  private static class FragmentBounds {
    private float minX = Float.MAX_VALUE;
    private float minY = Float.MAX_VALUE;
    private float maxX = -Float.MAX_VALUE;
    private float maxY = -Float.MAX_VALUE;

    void include(InlineFragment fragment, float verticalExpansion) {
      float yExpansion = verticalExpansion / 2;
      minX = Math.min(minX, fragment.x());
      minY = Math.min(minY, fragment.y() - yExpansion);
      maxX = Math.max(maxX, fragment.x() + fragment.width());
      maxY = Math.max(maxY, fragment.y() + fragment.height() + yExpansion);
    }

    void includeInlineBlock(InlineFragment fragment) {
      minX = Math.min(minX, fragment.x());
      minY = Math.min(minY, fragment.y());
      maxX = Math.max(maxX, fragment.x() + fragment.width());
      maxY = Math.max(maxY, fragment.y() + fragment.height());
    }

    InlineFragment toFragment(Element element) {
      return InlineFragment.builder()
          .node(element)
          .x(minX)
          .y(minY)
          .width(maxX - minX)
          .height(maxY - minY)
          .color(element.resolvedStyle().color())
          .build();
    }
  }

  private record InlineBlockMetrics(
      float marginWidth,
      float marginHeight,
      float borderWidth,
      float borderHeight,
      float marginLeft,
      float marginTop,
      float baseline) {}

  interface InlineBlockLayout {
    void layout(Element element, Element formattingParent);
  }
}
