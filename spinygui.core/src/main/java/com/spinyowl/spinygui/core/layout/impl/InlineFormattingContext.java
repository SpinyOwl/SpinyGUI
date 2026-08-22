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

import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.layout.InlineSourceMapping;
import com.spinyowl.spinygui.core.layout.LineBox;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.WordBreak;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.internal.RangeTextMeasurerAdapter;
import com.spinyowl.spinygui.core.system.cache.BoundedTextCache;
import com.spinyowl.spinygui.core.system.cache.TextCacheConfiguration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

public class InlineFormattingContext implements AutoCloseable {
  @NonNull private final TextMeasurer textMeasurer;
  private final TextCacheConfiguration cacheConfiguration;
  private boolean m7CacheEnabled;
  private PreparedInlineTextCache preparedTexts;
  private boolean closed;
  private BoundedTextCache.Stats lastFontChainStats = new BoundedTextCache.Stats(0, 0, 0, 0, 0, 0, 0);
  private BoundedTextCache.Stats lastMetricsStats = new BoundedTextCache.Stats(0, 0, 0, 0, 0, 0, 0);

  public InlineFormattingContext(@NonNull TextMeasurer textMeasurer) {
    this(textMeasurer, TextCacheConfiguration.disabled());
  }

  public InlineFormattingContext(
      @NonNull TextMeasurer textMeasurer, @NonNull TextCacheConfiguration cacheConfiguration) {
    this.textMeasurer = textMeasurer;
    this.cacheConfiguration = cacheConfiguration;
    this.m7CacheEnabled = cacheConfiguration.enabled();
    this.preparedTexts = new PreparedInlineTextCache(
        cacheConfiguration.enabled(), cacheConfiguration.preparedEntries(), cacheConfiguration.preparedWeight());
  }
  private InlineBlockLayout inlineBlockLayout;

  /** Selects the deterministic M7 cache mode for subsequently created layout passes. */
  public void m7CacheEnabled(boolean enabled) {
    if (closed) {
      throw new IllegalStateException("Inline formatting context is closed");
    }
    if (m7CacheEnabled != enabled) {
      preparedTexts.close();
      preparedTexts = new PreparedInlineTextCache(
          enabled, cacheConfiguration.preparedEntries(), cacheConfiguration.preparedWeight());
    }
    this.m7CacheEnabled = enabled;
  }

  /** Package-private M7 evidence seam for the context-owned prepared-value family. */
  public BoundedTextCache.Stats preparedTextCacheStats() {
    return preparedTexts.stats();
  }

  /** Package-private evidence seam for verifying the configured inline cache mode. */
  boolean m7CacheEnabled() {
    return m7CacheEnabled;
  }

  /** Latest pass-owned font-chain and metrics diagnostics, without retaining pass entries. */
  public Map<String, BoundedTextCache.Stats> fontCalculationCacheStats() {
    return Map.of("font-chain", lastFontChainStats, "metrics", lastMetricsStats);
  }

  void inlineBlockLayout(InlineBlockLayout inlineBlockLayout) {
    this.inlineBlockLayout = inlineBlockLayout;
  }

  public float layout(@NonNull Element parent, @NonNull List<Node> nodes, float startY) {
    if (closed) {
      throw new IllegalStateException("Inline formatting context is closed");
    }
    if (nodes.isEmpty()) {
      return 0;
    }

    Pass pass = new Pass();
    try {
      float contentX = parent.box().border().left() + parent.box().padding().left();
      float contentY = parent.box().border().top() + parent.box().padding().top() + startY;
      float availableWidth = Math.max(0, parent.box().content().width());
      List<InlineUnit> units = new ArrayList<>();
      Map<Text, List<InlineFragment>> textFragments = new IdentityHashMap<>();
      Map<Element, List<InlineFragment>> elementFragments = new IdentityHashMap<>();

      nodes.forEach(
          node -> collectUnits(node, units, textFragments, elementFragments, parent, pass));
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

      return lines.stream()
          .map(line -> line.y() + line.height() - contentY)
          .max(Float::compare)
          .orElse(0f);
    } finally {
      pass.close();
    }
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
      Element inheritedParent,
      Pass pass) {
    if (node instanceof Text text) {
      text.inlineFragments(List.of());
      text.box().contentPosition(0, 0);
      text.box().contentSize(0, 0);
      units.addAll(textUnits(text, inheritedParent.resolvedStyle(), pass));
      return;
    }

    if (!(node instanceof Element element)) {
      return;
    }

    ResolvedStyle style = element.resolvedStyle();
    element.inlineFragments(List.of());
    if (INLINE_BLOCK.equals(style.display())) {
      collectInlineBlockUnit(element, units, inheritedParent, pass);
      return;
    }
    setBorders(style, element.box().border());
    setPadding(
        inheritedParent.box().content().width(),
        inheritedParent.box().content().height(),
        style,
        element.box().padding());
    setMargins(element, inheritedParent.box().content().width());

    addSpacer(units, element, style, element.box().margin().left(), false, pass);
    addSpacer(
        units,
        element,
        style,
        element.box().border().left() + element.box().padding().left(),
        true,
        pass);
    addVerticalStrut(units, element, style, pass);

    for (Node child : element.childNodes()) {
      if (inlineNode(child)) {
        collectUnits(child, units, textFragments, elementFragments, element, pass);
      }
    }

    addSpacer(
        units,
        element,
        style,
        element.box().padding().right() + element.box().border().right(),
        true,
        pass);
    addSpacer(units, element, style, element.box().margin().right(), false, pass);
  }

  private void collectInlineBlockUnit(
      Element element, List<InlineUnit> units, Element inheritedParent, Pass pass) {
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
    units.add(inlineBlockUnit(element, style, inlineBlockMetrics(element), pass));
  }

  private InlineUnit inlineBlockUnit(
      Element element, ResolvedStyle style, InlineBlockMetrics metrics, Pass pass) {
    return new InlineUnit(element, style, null, null, false, false, metrics, 0, 0, true, pass);
  }

  private void addSpacer(
      List<InlineUnit> units,
      Element element,
      ResolvedStyle style,
      float width,
      boolean elementBox,
      Pass pass) {
    if (width > 0) {
      units.add(
          new InlineUnit(
              element, style, null, null, false, true, null, width, 0, elementBox, pass));
    }
  }

  private void addVerticalStrut(
      List<InlineUnit> units, Element element, ResolvedStyle style, Pass pass) {
    float verticalAdditions =
        element.box().border().top()
            + element.box().padding().top()
            + element.box().padding().bottom()
            + element.box().border().bottom();
    if (verticalAdditions > 0) {
      units.add(
          new InlineUnit(
              element, style, null, null, false, true, null, 0, verticalAdditions, true, pass));
    }
  }

  private List<InlineUnit> textUnits(Text text, ResolvedStyle style, Pass pass) {
    PreparedInlineText prepared =
        pass.prepare(text.content() == null ? "" : text.content(), style, textMeasurer.diagnostics());
    var result = new ArrayList<InlineUnit>();
    for (PreparedInlineText.Unit unit : prepared.units()) {
      result.add(
          new InlineUnit(
              text,
              style,
              prepared,
              unit,
              unit.kind() == PreparedInlineText.UnitKind.COLLAPSIBLE_SPACE,
              false,
              null,
              0,
              0,
              false,
              pass));
    }
    return result;
  }

  private List<LineBox> buildLines(
      Element parent,
      List<InlineUnit> units,
      float contentX,
      float contentY,
      float availableWidth) {
    List<LineBox> lines = new ArrayList<>();
    LineBox line = newLine(contentX, contentY);
    float cursorX = contentX;

    for (InlineUnit unit : units) {
      if (unit.newline()) {
        lines.add(closeLine(line));
        contentY += line.height();
        line = newLine(contentX, contentY);
        cursorX = contentX;
        continue;
      }
      if (unit.space && line.empty()) {
        continue;
      }

      if (unit.preservedOutputSequence()) {
        for (int index = unit.rangeStart(); index < unit.rangeEnd(); ) {
          textMeasurer
              .diagnostics()
              .increment(TextDiagnosticCounter.INLINE_RANGE_CODE_POINT_VISITS);
          int next = unit.nextCodePointBoundary(index);
          float partWidth = unit.width(index, next);
          if (unit.wraps()
              && !line.empty()
              && cursorX + partWidth > contentX + availableWidth) {
            lines.add(closeLine(line));
            contentY += line.height();
            line = newLine(contentX, contentY);
            cursorX = contentX;
          }
          cursorX = addUnit(line, unit, index, next, cursorX);
          index = next;
        }
        continue;
      }

      float width = unit.width();
      boolean breakIntoCharacters =
          unit.breakIntoCharacters(availableWidth, cursorX, contentX);
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
        for (int index = unit.rangeStart(); index < unit.rangeEnd(); ) {
          textMeasurer
              .diagnostics()
              .increment(TextDiagnosticCounter.INLINE_RANGE_CODE_POINT_VISITS);
          int next = unit.nextCodePointBoundary(index);
          if (!line.empty()
              && cursorX + unit.width(index, next) > contentX + availableWidth) {
            lines.add(closeLine(line));
            contentY += line.height();
            line = newLine(contentX, contentY);
            cursorX = contentX;
          }
          cursorX = addUnit(line, unit, index, next, cursorX);
          index = next;
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
    return addUnit(line, unit, unit.rangeStart(), unit.rangeEnd(), cursorX);
  }

  private float addUnit(
      LineBox line, InlineUnit unit, int rangeStart, int rangeEnd, float cursorX) {
    float width = unit.width(rangeStart, rangeEnd);
    float height = unit.lineHeight(rangeStart, rangeEnd);
    float baseline = line.y() + unit.baseline(rangeStart, rangeEnd);
    String durableText = unit.durableText(rangeStart, rangeEnd);
    FragmentResolution resolution =
        unit.fragmentResolution(durableText, rangeStart, rangeEnd);
    if (durableText == null) {
      textMeasurer.diagnostics().increment(TextDiagnosticCounter.INLINE_NULL_TEXT_FRAGMENTS);
    } else {
      textMeasurer.diagnostics().increment(TextDiagnosticCounter.INLINE_DURABLE_TEXT_STRINGS);
    }
    line.addFragment(
        InlineFragment.builder()
            .node(unit.fragmentNode())
            .text(durableText)
            .sourceMapping(resolution.sourceMapping())
            .x(unit.fragmentX(cursorX))
            .y(line.y())
            .width(unit.fragmentWidth(width))
            .height(unit.fragmentHeight(height))
            .baseline(baseline)
            .font(unit.fonts.isEmpty() ? Font.DEFAULT : unit.fonts.get(0))
            .fontSize(unit.fontSize)
            .color(unit.style.color())
            .runs(resolution.runs())
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
          (element, fragmentBounds) -> {
            textMeasurer.diagnostics().increment(TextDiagnosticCounter.INLINE_NULL_TEXT_FRAGMENTS);
            elementFragments
                .computeIfAbsent(element, ignored -> new ArrayList<>())
                .add(fragmentBounds.toFragment(element));
          });
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
    private final PreparedInlineText prepared;
    private final PreparedInlineText.Unit range;
    private final boolean space;
    private final boolean spacer;
    private final InlineBlockMetrics inlineBlockMetrics;
    private final float spacerWidth;
    private final float extraHeight;
    private final boolean elementBox;
    private final TypographyKey typographyKey;
    private final List<Font> fonts;
    private final float fontSize;
    private final Pass pass;
    private TextLineMetrics measurement;

    private InlineUnit(
        Node node,
        ResolvedStyle style,
        PreparedInlineText prepared,
        PreparedInlineText.Unit range,
        boolean space,
        boolean spacer,
        InlineBlockMetrics inlineBlockMetrics,
        float spacerWidth,
        float extraHeight,
        boolean elementBox,
        Pass pass) {
      this.node = node;
      this.style = style;
      this.prepared = prepared;
      this.range = range;
      this.space = space;
      this.spacer = spacer;
      this.inlineBlockMetrics = inlineBlockMetrics;
      this.spacerWidth = spacerWidth;
      this.extraHeight = extraHeight;
      this.elementBox = elementBox;
      this.pass = pass;
      this.typographyKey = pass.typographyKey(style);
      this.fonts = pass.fonts(style, typographyKey);
      Float measuredFontSize = StyleUtils.getFontSize(node);
      this.fontSize = measuredFontSize == null ? 16f : measuredFontSize;
    }

    boolean newline() {
      return range != null && range.kind() == PreparedInlineText.UnitKind.FORCED_BREAK;
    }

    int rangeStart() {
      return range == null ? 0 : range.preparedStart();
    }

    int rangeEnd() {
      return range == null ? 0 : range.preparedEnd();
    }

    int nextCodePointBoundary(int start) {
      return start + Character.charCount(prepared.text().codePointAt(start));
    }

    boolean preservedOutputSequence() {
      WhiteSpace whiteSpace = style.whiteSpace();
      return range != null
          && (PRE.equals(whiteSpace) || PRE_WRAP.equals(whiteSpace))
          && !newline();
    }

    float width() {
      return width(rangeStart(), rangeEnd());
    }

    float width(int start, int end) {
      if (inlineBlockMetrics != null) {
        return inlineBlockMetrics.marginWidth();
      }
      if (spacer) {
        return spacerWidth;
      }
      return range == null || newline() ? 0 : measurement(start, end).width();
    }

    TextLineMetrics measurement(int start, int end) {
      if (range != null && (start != range.preparedStart() || end != range.preparedEnd())) {
        return pass.measure(
            prepared, start, end, typographyKey, fonts, fontSize, style.lineHeight());
      }
      if (measurement == null) {
        measurement =
            pass.measure(
                prepared, start, end, typographyKey, fonts, fontSize, style.lineHeight());
      }
      return measurement;
    }

    float lineHeight(int start, int end) {
      if (inlineBlockMetrics != null) {
        return inlineBlockMetrics.marginHeight();
      }
      return measurement(start, end).height() + extraHeight;
    }

    float baseline(int start, int end) {
      if (inlineBlockMetrics != null) {
        return inlineBlockMetrics.baseline() + inlineBlockMetrics.marginTop();
      }
      return measurement(start, end).baseline() + extraHeight / 2;
    }

    boolean wraps() {
      WhiteSpace whiteSpace = style.whiteSpace();
      return !NOWRAP.equals(whiteSpace) && !PRE.equals(whiteSpace);
    }

    boolean breakWord(float availableWidth) {
      OverflowWrap overflowWrap = style.overflowWrap();
      WordBreak wordBreak = style.wordBreak();
      return (BREAK_WORD.equals(overflowWrap)
              || ANYWHERE.equals(overflowWrap)
              || WordBreak.BREAK_WORD.equals(wordBreak))
          && range != null
          && !space
          && wraps()
          && width() > availableWidth;
    }

    boolean breakIntoCharacters(float availableWidth, float cursorX, float contentX) {
      return range != null
          && !space
          && wraps()
          && (BREAK_ALL.equals(style.wordBreak()) || breakWord(availableWidth))
          && cursorX + width() > contentX + availableWidth;
    }

    String durableText(int start, int end) {
      if (range == null || newline()) {
        return null;
      }
      return prepared.text().substring(start, end);
    }

    FragmentResolution fragmentResolution(String durableText, int start, int end) {
      if (range == null || newline()) {
        return new FragmentResolution(List.of(), InlineSourceMapping.unmapped());
      }
      List<ResolvedTextRun> measuredRuns = measurement(start, end).runs();
      if (measuredRuns.isEmpty()) {
        return new FragmentResolution(
            List.of(), prepared.sourceMappingForPreparedRange(durableText, start, end));
      }

      boolean replacementAware =
          measuredRuns.stream()
              .flatMap(run -> run.glyphs().stream())
              .anyMatch(
                  glyph ->
                      glyph.replacement()
                          || glyph.sourceCodePoint() != glyph.renderedCodePoint());
      int renderedLength =
          replacementAware
              ? measuredRuns.stream()
                  .flatMap(run -> run.glyphs().stream())
                  .mapToInt(glyph -> Character.charCount(glyph.renderedCodePoint()))
                  .sum()
              : 0;
      int[] sourceStarts = replacementAware ? new int[renderedLength] : null;
      int[] sourceEnds = replacementAware ? new int[renderedLength] : null;
      StringBuilder renderedText =
          replacementAware ? new StringBuilder(renderedLength) : null;
      List<ResolvedTextRun> localRuns = new ArrayList<>(measuredRuns.size());
      int localOffset = 0;
      for (ResolvedTextRun run : measuredRuns) {
        int runStart = localOffset;
        List<ResolvedGlyph> localGlyphs = new ArrayList<>(run.glyphs().size());
        for (ResolvedGlyph glyph : run.glyphs()) {
          int glyphStart = localOffset;
          int glyphEnd = glyphStart + Character.charCount(glyph.renderedCodePoint());
          if (replacementAware) {
            int originalStart =
                prepared.sourceStartForPreparedRange(glyph.sourceStart(), glyph.sourceEnd());
            int originalEnd =
                prepared.sourceEndForPreparedRange(glyph.sourceStart(), glyph.sourceEnd());
            for (int index = glyphStart; index < glyphEnd; index++) {
              sourceStarts[index] = originalStart;
              sourceEnds[index] = originalEnd;
            }
            renderedText.appendCodePoint(glyph.renderedCodePoint());
          }
          localGlyphs.add(
              new ResolvedGlyph(
                  glyphStart,
                  glyphEnd,
                  glyph.sourceCodePoint(),
                  glyph.renderedCodePoint(),
                  glyph.font(),
                  glyph.replacement()));
          localOffset = glyphEnd;
        }
        localRuns.add(
            new ResolvedTextRun(runStart, localOffset, run.font(), localGlyphs, run.advance()));
      }

      InlineSourceMapping mapping =
          replacementAware
              ? InlineSourceMapping.forRenderedText(
                  prepared.source(), renderedText.toString(), sourceStarts, sourceEnds)
              : prepared.sourceMappingForPreparedRange(durableText, start, end);
      return new FragmentResolution(localRuns, mapping);
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

  private record FragmentResolution(
      List<ResolvedTextRun> runs, InlineSourceMapping sourceMapping) {}

  private final class Pass implements AutoCloseable {
    private final boolean cacheEnabled = m7CacheEnabled;
    private final long generation = Font.semanticOwner().generation();
    private final BoundedTextCache<TypographyKey, List<Font>> fontChains =
        new BoundedTextCache<>(
            cacheConfiguration.fontChainEntries(),
            cacheConfiguration.fontChainWeight(),
            value -> Math.max(1, value.size() * 32),
            cacheEnabled);
    private final BoundedTextCache<MeasurementKey, TextLineMetrics> measurements =
        new BoundedTextCache<>(
            cacheConfiguration.metricsEntries(),
            cacheConfiguration.metricsWeight(),
            ignored -> 256,
            cacheEnabled);
    private boolean closed;

    PreparedInlineText prepare(String source, ResolvedStyle style, DiagnosticSession diagnostics) {
      requireActiveGeneration();
      return InlineFormattingContext.this.preparedTexts.getOrPrepare(source, style, diagnostics);
    }

    TypographyKey typographyKey(ResolvedStyle style) {
      if (!cacheEnabled) {
        return null;
      }
      return new TypographyKey(
          List.copyOf(style.fontFamilies()),
          style.fontStyle(),
          style.fontWeight(),
          FontStretch.NORMAL);
    }

    List<Font> fonts(ResolvedStyle style, TypographyKey key) {
      requireActiveGeneration();
      if (!cacheEnabled) {
        textMeasurer.diagnostics().increment(TextDiagnosticCounter.FONT_CHAIN_RESOLUTIONS);
        return Font.semanticOwner()
            .resolver()
            .resolve(
                style.fontFamilies(),
                style.fontStyle(),
                style.fontWeight(),
                FontStretch.NORMAL);
      }
      List<Font> cached = fontChains.get(key);
      if (cached != null) {
        return cached;
      }
      textMeasurer.diagnostics().increment(TextDiagnosticCounter.FONT_CHAIN_RESOLUTIONS);
      List<Font> resolved =
          List.copyOf(
              Font.semanticOwner()
                  .resolver()
                  .resolve(key.families(), key.fontStyle(), key.fontWeight(), key.fontStretch()));
      fontChains.put(key, resolved);
      return resolved;
    }

    TextLineMetrics measure(
        PreparedInlineText prepared,
        int start,
        int end,
        TypographyKey typographyKey,
        List<Font> fonts,
        float fontSize,
        float lineHeight) {
      requireActiveGeneration();
      String source = prepared == null ? "" : prepared.text();
      if (!cacheEnabled) {
        return measureRange(source, start, end, fonts, fontSize, lineHeight);
      }
      MeasurementKey key =
          new MeasurementKey(
              source,
              start,
              end,
              typographyKey,
              fonts,
              fontSize,
              lineHeight,
              generation);
      TextLineMetrics cached = measurements.get(key);
      if (cached != null) {
        textMeasurer.diagnostics().increment(TextDiagnosticCounter.INLINE_MEASUREMENT_REUSES);
        return cached;
      }
      TextLineMetrics measured = measureRange(source, start, end, fonts, fontSize, lineHeight);
      measurements.put(key, measured);
      return measured;
    }

    private TextLineMetrics measureRange(
        String source,
        int start,
        int end,
        List<Font> fonts,
        float fontSize,
        float lineHeight) {
      TextMetrics metrics =
          RangeTextMeasurerAdapter.measureRange(
              textMeasurer,
              source,
              start,
              end,
              0,
              fonts,
              fontSize,
              lineHeight,
              Float.MAX_VALUE,
              false);
      TextLineMetrics measured = metrics.lines().get(0);
      textMeasurer.diagnostics().increment(TextDiagnosticCounter.INLINE_MEASUREMENT_RANGE_CALLS);
      return measured;
    }

    private void requireActiveGeneration() {
      if (closed) {
        throw new IllegalStateException("Inline layout pass is already closed");
      }
      if (Font.semanticOwner().generation() != generation) {
        throw new IllegalStateException("Font registry mutated during an inline layout pass");
      }
    }

    @Override
    public void close() {
      if (closed) {
        return;
      }
      boolean generationChanged = Font.semanticOwner().generation() != generation;
      lastFontChainStats = fontChains.stats();
      lastMetricsStats = measurements.stats();
      measurements.clear();
      fontChains.clear();
      measurements.close();
      fontChains.close();
      closed = true;
      textMeasurer.diagnostics().increment(TextDiagnosticCounter.INLINE_PASS_CLEANUPS);
      if (generationChanged) {
        throw new IllegalStateException("Font registry mutated during an inline layout pass");
      }
    }
  }

  private record TypographyKey(
      List<String> families,
      FontStyle fontStyle,
      FontWeight fontWeight,
      FontStretch fontStretch) {}

  private record MeasurementKey(
      String source,
      int start,
      int end,
      TypographyKey typographyKey,
      List<Font> fonts,
      float fontSize,
      float lineHeight,
      long generation) {}

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

  /** Releases context-owned prepared values; subsequent layout use is rejected. */
  @Override
  public void close() {
    if (!closed) {
      preparedTexts.close();
      closed = true;
    }
  }
}
