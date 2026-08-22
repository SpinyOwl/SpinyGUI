package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

/** One immutable visual fragment plus separately inspectable original-source provenance. */
public final class InlineFragment {

  private final Node node;
  private final String text;
  private final InlineSourceMapping sourceMapping;
  private final float x;
  private final float y;
  private final float width;
  private final float height;
  private final float baseline;
  private final Font font;
  private final float fontSize;
  private final Color color;
  private final List<ResolvedTextRun> runs;

  /**
   * Creates a fragment with intentional value semantics. Node identity and source provenance are
   * retained but are not part of visual equality.
   */
  @Builder
  public InlineFragment(
      Node node,
      String text,
      InlineSourceMapping sourceMapping,
      float x,
      float y,
      float width,
      float height,
      float baseline,
      Font font,
      float fontSize,
      Color color,
      List<ResolvedTextRun> runs) {
    this.node = node;
    this.text = text;
    this.sourceMapping = sourceMapping == null ? InlineSourceMapping.unmapped() : sourceMapping;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.baseline = baseline;
    this.font = font;
    this.fontSize = fontSize;
    this.color = color;
    this.runs = runs == null ? List.of() : List.copyOf(runs);
    if (!this.sourceMapping.matchesRenderedOutput(text, this.runs)) {
      throw new IllegalArgumentException(
          "Inline source mapping does not match the fragment's rendered output");
    }
  }

  public Node node() {
    return node;
  }

  public String text() {
    return text;
  }

  public InlineSourceMapping sourceMapping() {
    return sourceMapping;
  }

  public float x() {
    return x;
  }

  public float y() {
    return y;
  }

  public float width() {
    return width;
  }

  public float height() {
    return height;
  }

  public float baseline() {
    return baseline;
  }

  public Font font() {
    return font;
  }

  public float fontSize() {
    return fontSize;
  }

  public Color color() {
    return color;
  }

  public List<ResolvedTextRun> runs() {
    return runs;
  }

  public boolean textFragment() {
    return text != null && !text.isEmpty();
  }

  public InlineFragment translate(float dx, float dy) {
    return InlineFragment.builder()
        .node(node)
        .text(text)
        .sourceMapping(sourceMapping)
        .x(x + dx)
        .y(y + dy)
        .width(width)
        .height(height)
        .baseline(baseline + dy)
        .font(font)
        .fontSize(fontSize)
        .color(color)
        .runs(runs)
        .build();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (!(other instanceof InlineFragment fragment)) return false;
    return Float.compare(x, fragment.x) == 0
        && Float.compare(y, fragment.y) == 0
        && Float.compare(width, fragment.width) == 0
        && Float.compare(height, fragment.height) == 0
        && Float.compare(baseline, fragment.baseline) == 0
        && Float.compare(fontSize, fragment.fontSize) == 0
        && Objects.equals(text, fragment.text)
        && Objects.equals(font, fragment.font)
        && Objects.equals(color, fragment.color)
        && runs.equals(fragment.runs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, x, y, width, height, baseline, font, fontSize, color, runs);
  }

  @Override
  public String toString() {
    return "InlineFragment[text=%s, x=%s, y=%s, width=%s, height=%s, baseline=%s, font=%s, fontSize=%s, color=%s, runs=%s]"
        .formatted(text, x, y, width, height, baseline, font, fontSize, color, runs);
  }
}
