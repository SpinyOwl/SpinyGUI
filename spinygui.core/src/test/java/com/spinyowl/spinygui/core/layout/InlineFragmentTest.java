package com.spinyowl.spinygui.core.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class InlineFragmentTest {

  @Test
  void constructorFreezesRunsAndRejectsMismatchedRenderedMapping() {
    List<ResolvedTextRun> mutableRuns = new ArrayList<>();
    InlineFragment fragment =
        new InlineFragment(
            new Text("a"),
            "a",
            mapping("a", 0),
            1,
            2,
            3,
            4,
            5,
            Font.DEFAULT,
            16,
            Color.BLACK,
            mutableRuns);
    mutableRuns.add(run("a"));

    assertEquals(List.of(), fragment.runs());
    assertThrows(
        IllegalArgumentException.class,
        () -> InlineFragment.builder().text("b").sourceMapping(mapping("a", 0)).build());
  }

  @Test
  void visualEqualityExcludesNodeAndProvenanceButIncludesVisualFields() {
    InlineFragment first = fragment(new Text("a"), mapping("a", 0), List.of(run("a")));
    InlineFragment second = fragment(new Text("a"), mapping("a", 2), List.of(run("a")));
    InlineFragment visuallyDifferent =
        InlineFragment.builder()
            .node(second.node())
            .text("a")
            .sourceMapping(second.sourceMapping())
            .x(9)
            .font(Font.DEFAULT)
            .fontSize(16)
            .color(Color.BLACK)
            .runs(List.of(run("a")))
            .build();

    assertNotSame(first.node(), second.node());
    assertNotEquals(first.sourceMapping(), second.sourceMapping());
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    assertNotEquals(first, visuallyDifferent);
  }

  @Test
  void translatePreservesImmutableProvenanceAndRuns() {
    InlineFragment fragment = fragment(new Text("a"), mapping("a", 0), List.of(run("a")));

    InlineFragment translated = fragment.translate(3, 5);

    assertSame(fragment.sourceMapping(), translated.sourceMapping());
    assertSame(fragment.runs(), translated.runs());
    assertEquals(fragment.x() + 3, translated.x());
    assertEquals(fragment.baseline() + 5, translated.baseline());
  }

  private static InlineFragment fragment(
      Text node, InlineSourceMapping mapping, List<ResolvedTextRun> runs) {
    return InlineFragment.builder()
        .node(node)
        .text("a")
        .sourceMapping(mapping)
        .x(1)
        .y(2)
        .width(3)
        .height(4)
        .baseline(5)
        .font(Font.DEFAULT)
        .fontSize(16)
        .color(Color.BLACK)
        .runs(runs)
        .build();
  }

  private static InlineSourceMapping mapping(String text, int sourceStart) {
    String source = "x".repeat(sourceStart) + text;
    return InlineSourceMapping.forRenderedText(
        source, text, new int[] {sourceStart}, new int[] {sourceStart + 1});
  }

  private static ResolvedTextRun run(String text) {
    int codePoint = text.codePointAt(0);
    int length = Character.charCount(codePoint);
    ResolvedGlyph glyph =
        new ResolvedGlyph(0, length, codePoint, codePoint, Font.DEFAULT, false);
    return new ResolvedTextRun(0, length, Font.DEFAULT, List.of(glyph), 1);
  }
}
