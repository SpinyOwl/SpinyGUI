package com.spinyowl.spinygui.benchmark.cpu;

import com.spinyowl.spinygui.benchmark.TextWorkloads;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.impl.InlineFormattingContext;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.TextAlign;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.WordBreak;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/** Steady-state CPU benchmarks for text measurement, caret lookup, and inline layout. */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class TextCalculationBenchmark {
  private static final float FONT_SIZE = 16;
  private static final float LINE_HEIGHT = 1.2f;
  private static final float WRAP_WIDTH = 240;

  private FontServiceImpl fontService;
  private InlineFormattingContext inlineFormattingContext;
  private Element layoutParent;
  private List<Node> layoutNodes;
  private float endCaretOffset;

  @Setup(Level.Trial)
  public void setUp() {
    fontService = new FontServiceImpl(new FontStorageImpl(), false);
    warmFontData();
    endCaretOffset =
        fontService.measureText(TextWorkloads.LONG_SINGLE_FONT, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT)
            .width() - 1;
    setUpLayoutFixture();
  }

  @Benchmark
  public TextMetrics measureLatin() {
    return fontService.measureText(TextWorkloads.LATIN, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);
  }

  @Benchmark
  public TextMetrics measureWrappedParagraph() {
    return fontService.measureText(
        TextWorkloads.WRAPPED_PARAGRAPH,
        0,
        Font.DEFAULT,
        FONT_SIZE,
        LINE_HEIGHT,
        WRAP_WIDTH,
        true);
  }

  @Benchmark
  public TextMetrics measureMixedCjk() {
    return measureWithFallback(TextWorkloads.MIXED_CJK);
  }

  @Benchmark
  public TextMetrics measureSupplementaryUnicode() {
    return measureWithFallback(TextWorkloads.SUPPLEMENTARY_UNICODE);
  }

  @Benchmark
  public TextMetrics measureMissingGlyphs() {
    return measureWithFallback(TextWorkloads.MISSING_GLYPHS);
  }

  @Benchmark
  public TextMetrics measureLongSingleFont() {
    return fontService.measureText(
        TextWorkloads.LONG_SINGLE_FONT, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);
  }

  @Benchmark
  public TextCaretMetrics findCaretNearBeginning() {
    return fontService.getTextCaretMetrics(
        TextWorkloads.LONG_SINGLE_FONT, Font.DEFAULT, FONT_SIZE, 1);
  }

  @Benchmark
  public TextCaretMetrics findCaretNearEnd() {
    return fontService.getTextCaretMetrics(
        TextWorkloads.LONG_SINGLE_FONT, Font.DEFAULT, FONT_SIZE, endCaretOffset);
  }

  @Benchmark
  public float layoutTextDenseInlineContent() {
    return inlineFormattingContext.layout(layoutParent, layoutNodes, 0);
  }

  private TextMetrics measureWithFallback(String text) {
    return fontService.measureText(
        text,
        List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR),
        FONT_SIZE,
        LINE_HEIGHT);
  }

  private void warmFontData() {
    measureLatin();
    measureWrappedParagraph();
    measureMixedCjk();
    measureSupplementaryUnicode();
    measureMissingGlyphs();
  }

  private void setUpLayoutFixture() {
    layoutParent = NodeBuilder.div();
    style(layoutParent);
    layoutParent.box().contentSize(WRAP_WIDTH, 0);

    Text first = NodeBuilder.text(TextWorkloads.WRAPPED_PARAGRAPH);
    Text second = NodeBuilder.text(TextWorkloads.WRAPPED_PARAGRAPH);
    Text third = NodeBuilder.text(TextWorkloads.WRAPPED_PARAGRAPH);
    layoutParent.addChild(first);
    layoutParent.addChild(second);
    layoutParent.addChild(third);
    layoutNodes = List.of(first, second, third);
    inlineFormattingContext = new InlineFormattingContext(fontService);
  }

  private static void style(Element element) {
    ResolvedStyle style = element.resolvedStyle();
    style.display(Display.BLOCK);
    style.position(Position.STATIC);
    style.fontFamilies(List.of("Roboto"));
    style.fontStyle(FontStyle.NORMAL);
    style.fontWeight(FontWeight.NORMAL);
    style.fontSize(Length.pixel(FONT_SIZE));
    style.lineHeight(LINE_HEIGHT);
    style.color(Color.BLACK);
    style.whiteSpace(WhiteSpace.NORMAL);
    style.textAlign(TextAlign.LEFT);
    style.overflowWrap(OverflowWrap.NORMAL);
    style.wordBreak(WordBreak.NORMAL);
    style.tabSize(4);
  }
}
