package com.spinyowl.spinygui.benchmark.report;

import java.util.List;

/** Typed, presentation-ready data supplied to the precompiled local benchmark report templates. */
public record BenchmarkReportView(
    List<CpuRow> cpuRows, List<SceneRow> sceneRows, List<ChartRow> cpuChartRows, List<ChartRow> gpuChartRows,
    List<EnvironmentValue> environment, boolean pixelValidationPassed, String slowestCpuName, String slowestCpuLatency,
    String largestAllocationName, String largestAllocation, String largestGpuFragments, String largestGpuP99,
    String largestGpuBudget120, String currentRunIdentifier, List<HistoryRun> history, List<TrendSeries> trends,
    ChartPayload charts) {
  public record CpuRow(String name, String latency, String uncertainty, String allocation, String allocationRate,
      String latencyWidth, String allocationWidth) { }
  public record SceneRow(String fragments, String complexity, String cpuLatency, String gpuLatency, String cpuBudget,
      String gpuBudget, String samples) { }
  public record ChartRow(String label, String value, String width, String cssClass, String suffix) { }
  public record EnvironmentValue(String key, String value) { }
  public record HistoryRun(String identifier, List<CpuHistoryRow> cpuRows, List<SceneHistoryRow> sceneRows) { }
  public record CpuHistoryRow(String name, String latency, String allocation, String latencyChange, String allocationChange) { }
  public record SceneHistoryRow(String fragments, String cpuLatency, String gpuLatency, String cpuChange,
      String gpuChange, String cpuBudget120, String gpuBudget120, String cpuBudgetChange, String gpuBudgetChange) { }
  public record TrendSeries(String id, String label, String unit, String timelineFirstRun, String timelineLastRun,
      String minimum, String maximum, double numericMinimum, double numericMaximum, List<TrendSegment> segments,
      List<TrendPoint> points) { }
  public record TrendSegment(List<TrendPoint> points) { }
  public record TrendPoint(String runIdentifier, String x, String y, String value, double numericValue, String change,
      String accessibleLabel) { }
  public record ChartPayload(List<CpuChartDatum> cpu, List<RenderingChartDatum> rendering,
      List<String> historyRuns, List<ChartTrend> trends) { }
  public record CpuChartDatum(String label, double latency, double uncertainty,
      double allocation, double allocationRate) { }
  public record RenderingChartDatum(String label, double cpuMedian, double cpuP95, double cpuP99,
      double gpuMedian, double gpuP95, double gpuP99) { }
  public record ChartTrend(String id, String label, String unit, double minimum, double maximum,
      List<Double> values, List<String> changes) { }
}
