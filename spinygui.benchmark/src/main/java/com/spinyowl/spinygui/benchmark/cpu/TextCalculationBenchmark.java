package com.spinyowl.spinygui.benchmark.cpu;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.layout.impl.InlineFormattingContext;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.BenchmarkParams;

/** Steady-state CPU benchmarks for text measurement, caret lookup, and inline layout. */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Threads(1)
@Fork(value = 2, warmups = 0)
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS, batchSize = 1)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS, batchSize = 1)
public class TextCalculationBenchmark {
  private FontServiceImpl fontService;
  private InlineFormattingContext inlineFormattingContext;
  private Element layoutParent;
  private List<Node> layoutNodes;
  private float endCaretOffset;
  private CpuWorkloadSpecifications.BenchmarkDispatch dispatch;

  @Setup(Level.Trial)
  public void setUp(BenchmarkParams benchmarkParams) {
    dispatch = CpuWorkloadSpecifications.dispatchForBenchmark(benchmarkParams.getBenchmark());
    fontService =
        CpuWorkloadSpecifications.TRIAL_SETUP.createFontService(DiagnosticSession.disabled());
    CpuWorkloadSpecifications.TRIAL_SETUP.warmFonts(fontService);
    endCaretOffset =
        CpuWorkloadSpecifications.TRIAL_SETUP.preparedEndCaret().preparedOffset(fontService);
    setUpLayoutFixture();
  }

  @Benchmark
  public TextMetrics measureLatin() {
    return dispatchMeasurement();
  }

  @Benchmark
  public TextMetrics measureWrappedParagraph() {
    return dispatchMeasurement();
  }

  @Benchmark
  public TextMetrics measureMixedCjk() {
    return dispatchMeasurement();
  }

  @Benchmark
  public TextMetrics measureSupplementaryUnicode() {
    return dispatchMeasurement();
  }

  @Benchmark
  public TextMetrics measureMissingGlyphs() {
    return dispatchMeasurement();
  }

  @Benchmark
  public TextMetrics measureLongSingleFont() {
    return dispatchMeasurement();
  }

  @Benchmark
  public TextCaretMetrics findCaretNearBeginning() {
    var specification = dispatch.caret();
    return specification.findCaret(fontService, specification.preparedOffset(fontService));
  }

  @Benchmark
  public TextCaretMetrics findCaretNearEnd() {
    return dispatch.caret().findCaret(fontService, endCaretOffset);
  }

  @Benchmark
  public float layoutTextDenseInlineContent() {
    return dispatch.inlineLayout().layout(inlineFormattingContext, layoutParent, layoutNodes);
  }

  private TextMetrics dispatchMeasurement() {
    return dispatch.measurement().measure(fontService);
  }

  private void setUpLayoutFixture() {
    var specification = CpuWorkloadSpecifications.TRIAL_SETUP.preparedInlineLayout();
    layoutParent = NodeBuilder.div();
    specification.style().apply(layoutParent);
    layoutParent.box().contentSize(
        specification.containerWidthPx(), specification.containerHeightPx());

    List<Node> nodes = new ArrayList<>(specification.textNodeCount());
    for (int index = 0; index < specification.textNodeCount(); index++) {
      Text text = NodeBuilder.text(specification.text());
      layoutParent.addChild(text);
      nodes.add(text);
    }
    layoutNodes = List.copyOf(nodes);
    inlineFormattingContext = new InlineFormattingContext(fontService);
  }
}
