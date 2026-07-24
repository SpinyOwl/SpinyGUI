package com.spinyowl.spinygui.benchmark.report;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BenchmarkHtmlReportGeneratorTest {
  @Test
  void generatesSelfContainedEscapedReportFromBothJsonFormats(@TempDir Path archive) throws IOException {
    writePair(archive, "20260724-090000-000000000", cpuJson(), renderingJson());
    String html = BenchmarkHtmlReportGenerator.generateArchive(archive);

    assertTrue(html.contains("measureLatin"));
    assertTrue(html.contains("100 fragments"));
    assertTrue(html.contains("1,000 fragments"));
    assertTrue(html.contains("&lt;GPU&gt;"));
    assertTrue(html.contains("Latency (us/op)"));
    assertTrue(count(html, "id=\"cpu-latency-chart\"") == 1);
    assertTrue(count(html, "id=\"cpu-allocation-chart\"") == 1);
    assertTrue(count(html, "id=\"cpu-rendering-chart\"") == 1);
    assertTrue(count(html, "id=\"gpu-rendering-chart\"") == 1);
    assertTrue(count(html, "role=\"img\"") == 5);
    assertTrue(count(html, "class=\"chart-fallback\"") == 5);
    assertTrue(html.contains("class=\"chart-scroll\""));
    assertTrue(html.contains("class=\"chart-frame\""));
    assertTrue(html.contains("id=\"cpu-data\""));
    assertTrue(html.contains("id=\"rendering-data\""));
    assertTrue(html.contains("40.000 us; 0.480% of the 120 Hz budget"));
    assertTrue(html.contains("<a class=\"skip-link\" href=\"#overview\">Skip to report content</a>"));
    assertTrue(html.contains("<nav class=\"report-nav\" aria-label=\"Benchmark report sections\">"));
    assertTrue(html.contains("href=\"#overview\""));
    assertTrue(html.contains("href=\"#cpu\""));
    assertTrue(html.contains("href=\"#rendering\""));
    assertTrue(html.contains("href=\"#history\""));
    assertTrue(html.contains("href=\"#methodology\""));
    assertTrue(html.contains("<section id=\"overview\""));
    assertTrue(html.contains("<section id=\"cpu\""));
    assertTrue(html.contains("<section id=\"rendering\""));
    assertTrue(html.contains("<section id=\"history\""));
    assertTrue(html.contains("<section id=\"methodology\""));
    assertFalse(html.contains("tab-control"));
    assertFalse(html.contains("tab-panel"));
    assertFalse(html.contains("#tab-overview"));
    assertTrue(html.contains("role=\"tooltip\""));
    assertTooltipReferences(html);
    assertTrue(html.contains("&#9432;"));
    assertTrue(html.contains("Latency is average microseconds per operation"));
    assertTrue(html.contains("JMH score error"));
    assertTrue(html.contains("B/op; 7.000 MB/sec"));
    assertTrue(html.contains("60 warmup; 200 measured"));
    assertTrue(html.contains("Pixel validation: <b>passed</b>"));
    String compactHtml = html.replaceAll("\\s+", "");
    assertTrue(compactHtml.contains("@media(max-width:700px)"));
    assertTrue(compactHtml.contains(".chart-frame{position:relative;min-width:820px;height:480px}"));
    assertTrue(compactHtml.contains(".chart-scroll{overflow-x:auto;overscroll-behavior-inline:contain}"));
    assertTrue(compactHtml.contains(".table-wrap{overflow-x:auto"));
    assertTrue(compactHtml.contains("table{min-width:640px}"));
    assertTrue(compactHtml.contains(".tooltip{position:fixed;z-index:4;inset:16px"));
    assertTrue(compactHtml.contains(".report-nav{position:sticky;top:0"));
    assertTrue(compactHtml.contains(".report-nav{margin-inline:-16px;padding:12px16px16px;flex-wrap:nowrap;overflow-x:auto}"));
    assertTrue(compactHtml.contains(".report-nava{flex:none;white-space:nowrap}"));
    assertTrue(html.contains("<div class=\"trend-controls\" role=\"toolbar\" aria-label=\"Trend metric selector\">"));
    assertTrue(html.contains("<button class=\"trend-option\" type=\"button\" data-trend-id=\"cpu-trend-1\" aria-pressed=\"true\">"));
    assertTrue(html.contains("<canvas id=\"history-chart\" role=\"img\""));
    assertTrue(html.contains("id=\"history-data\""));
    assertTrue(count(html, "aria-pressed=\"true\"") == 1);
    assertFalse(html.contains("type=\"radio\""));
    assertFalse(html.contains("class=\"trend-panel\""));
    assertFalse(html.contains("<svg"));
    assertFalse(html.contains(":has("));
    assertFalse(html.contains("cx=\""));
    assertFalse(html.contains("<polyline"));
    assertTrue(compactHtml.contains(".trend-controls{display:flex;flex-wrap:wrap;gap:4px"));
    assertTrue(compactHtml.contains(".trend-option[aria-pressed=true]"));
    assertTrue(compactHtml.contains(".trend-option:focus-visible"));
    assertTrue(compactHtml.contains("scroll-padding-top:72px"));

    assertTrue(html.contains("Chart.js v4.5.1"));
    assertTrue(html.contains("@kurkle/color v0.3.2"));
    assertTrue(html.contains("<script id=\"benchmark-chart-data\" type=\"application/json\">"));
    assertTrue(html.contains("data.chartPayloadVersion"));
    assertTrue(html.contains("indexAxis:'y'"));
    assertTrue(html.contains("type:'logarithmic'"));
    assertTrue(html.contains("max:16667"));
    assertTrue(html.contains("8333"));
    assertTrue(html.contains("budgetMarkers"));
    assertTrue(html.contains("spanGaps:false"));
    assertTrue(html.contains("aria-pressed"));
    assertTrue(html.contains("historyChart.data.datasets[0].data = trend.values"));
    assertTrue(html.contains("historyChart.update()"));
    assertFalse(html.contains("class=\"track\""));
    assertFalse(html.contains("class=\"budget-marker"));
    assertTrue(count(html, "<script") == 3);
    assertFalse(html.contains("<script src="));
    assertFalse(html.contains("<link rel=\"stylesheet\""));
    assertFalse(html.contains("sourceMappingURL"));

    String noArchiveHtml = BenchmarkHtmlReportGenerator.generate(cpuJson(), renderingJson());
    assertTrue(noArchiveHtml.contains("id=\"history-chart\""));
    assertTrue(noArchiveHtml.contains("Interactive chart unavailable. <a href=\"#history-data\""));
    assertFalse(noArchiveHtml.contains("<button class=\"trend-option\" type=\"button\" data-trend-id="));
  }

  @Test
  void rejectsNonFiniteChartValues() {
    IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
        () -> BenchmarkHtmlReportGenerator.generate(cpuJson().replace("12.5", "1e309"), renderingJson()));
    assertTrue(failure.getMessage().contains("Non-finite benchmark chart value"));
  }

  @Test
  void preservesMissingOptionalCpuMetricsInChartPayloadAndRawTable(@TempDir Path archive) throws IOException {
    String cpuWithoutOptionalMetrics = cpuJson().replace(",\"scoreError\":0.5", "")
        .replace(",\"gc.alloc.rate\":{\"score\":7}", "");
    writePair(archive, "20260724-090000-000000000", cpuWithoutOptionalMetrics, renderingJson());

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    assertTrue(view.charts().cpu().getFirst().uncertainty() == null);
    assertTrue(view.charts().cpu().getFirst().allocationRate() == null);
    String html = BenchmarkHtmlReportGenerator.generateArchive(archive);
    assertTrue(html.contains("not reported"));
    assertTrue(html.contains("\"uncertainty\":null"));
    assertTrue(html.contains("\"allocationRate\":null"));
  }

  @Test
  void escapesScriptTerminationAttemptsInChartPayload() {
    String html = BenchmarkHtmlReportGenerator.generate(cpuJson().replace("measureLatin", "measure</script><script>alert(1)</script>"), renderingJson());

    assertTrue(html.contains("measure\\u003c/script\\u003e\\u003cscript\\u003ealert(1)\\u003c/script\\u003e"));
    assertFalse(html.contains("measure</script><script>alert(1)</script>"));
  }

  @Test
  void rejectsNonFiniteOptionalChartValues() {
    IllegalArgumentException uncertaintyFailure = assertThrows(IllegalArgumentException.class,
        () -> BenchmarkHtmlReportGenerator.generate(cpuJson().replace("0.5", "1e309"), renderingJson()));
    assertTrue(uncertaintyFailure.getMessage().contains("Non-finite benchmark chart value"));

    IllegalArgumentException allocationRateFailure = assertThrows(IllegalArgumentException.class,
        () -> BenchmarkHtmlReportGenerator.generate(cpuJson().replace("\"score\":7", "\"score\":1e309"), renderingJson()));
    assertTrue(allocationRateFailure.getMessage().contains("Non-finite benchmark chart value"));
  }

  @Test
  void loadsCompleteTimestampedPairsChronologicallyAndComputesChanges() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-runs");
    String first = "20260724-090000-000000000";
    String second = "20260724-100000-000000000";
    String collision = second + "-000001";
    String escapedCpu = cpuJson().replace("measureLatin", "measure<Latin>");
    writePair(archive, second, escapedCpu.replace("12.5", "25.0"), renderingJson().replace("\"p99\":4,", "\"p99\":8,")
        .replace("\"budget120HzPercent\":4", "\"budget120HzPercent\":8"));
    writePair(archive, collision, escapedCpu.replace("12.5", "20.0"), renderingJson().replace("\"p99\":4,", "\"p99\":12,")
        .replace("\"budget120HzPercent\":4", "\"budget120HzPercent\":12"));
    writePair(archive, first, escapedCpu, renderingJson());
    Files.writeString(archive.resolve("text-calculation-20260724-110000-000000000.json"), cpuJson());
    writePair(archive, "20260724-120000-000000000", "{not-json", renderingJson());
    writePair(archive, "20260724-130000-000000000", "[{\"benchmark\":\"missing-metrics\"}]", renderingJson());
    writePair(archive, "20261324-140000-000000000", cpuJson(), renderingJson());
    Files.writeString(archive.resolve("text-calculation-not-a-datetime.json"), cpuJson());
    Files.writeString(archive.resolve(".20260724-150000-000000000.benchmark-run.lock"), "stale");

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    BenchmarkReportView.ChartPayload charts = view.charts();
    assertTrue(charts.cpu().getFirst().label().equals("measure<Latin>"));
    assertTrue(charts.cpu().getFirst().latency() == 20.0);
    assertTrue(charts.cpu().getFirst().uncertainty() == 0.5);
    assertTrue(charts.cpu().getFirst().allocation() == 42.0);
    assertTrue(charts.cpu().getFirst().allocationRate() == 7.0);
    assertTrue(charts.rendering().getFirst().cpuMedian() == 1.0);
    assertTrue(charts.rendering().getFirst().cpuP95() == 2.0);
    assertTrue(charts.rendering().getFirst().cpuP99() == 3.0);
    assertTrue(charts.rendering().getFirst().gpuMedian() == 2.0);
    assertTrue(charts.rendering().getFirst().gpuP95() == 3.0);
    assertTrue(charts.rendering().getFirst().gpuP99() == 12.0);
    assertTrue(view.cpuRows().getFirst().latency().equals("20.000"));
    assertTrue(view.history().size() == 3);
    assertTrue(view.history().getFirst().identifier().equals(first));
    assertTrue(view.history().get(1).identifier().equals(second));
    assertTrue(view.history().getLast().identifier().equals(collision));
    assertTrue(view.history().getFirst().cpuRows().getFirst().latencyChange().equals("not available"));
    assertTrue(view.history().get(1).cpuRows().getFirst().latencyChange().equals("+100.000%"));
    assertTrue(view.history().getLast().cpuRows().getFirst().latencyChange().equals("-20.000%"));
    assertTrue(view.history().getLast().cpuRows().getFirst().allocationChange().equals("+0.000%"));
    assertTrue(view.history().getLast().sceneRows().getFirst().gpuChange().contains("+0.000% / +0.000% / +50.000%"));
    BenchmarkReportView.ChartTrend cpuTrend = chartTrend(view, "CPU latency: measure<Latin>");
    assertTrue(charts.historyRuns().equals(List.of(first, second, collision)));
    assertTrue(cpuTrend.values().equals(List.of(12.5, 25.0, 20.0)));
    assertTrue(cpuTrend.changes().equals(List.of("not available", "+100.000%", "-20.000%")));
    assertTrue(cpuTrend.minimum() == 11.25);
    assertTrue(cpuTrend.maximum() == 26.25);
    String html = BenchmarkHtmlReportGenerator.generateArchive(archive);
    assertTrue(html.contains("measure&lt;Latin&gt;"));
    assertTrue(html.contains("<section id=\"history\""));
    assertTrue(html.contains(collision));
    assertTrue(html.contains("CPU median/p95/p99"));
    assertTrue(html.contains("GPU median/p95/p99"));
    assertTrue(html.contains("-20.000%"));
    assertTrue(html.contains("CPU latency: measure&lt;Latin&gt;"));
    assertTrue(html.contains("data-trend-id=\"cpu-trend-1\""));
    assertFalse(html.contains("<svg"));
    assertFalse(html.contains("type=\"radio\""));
  }

  @Test
  void distinguishesDuplicateFragmentsAndSplitsMissingSeriesAtGlobalTimelineGaps() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-trend-gaps");
    String first = "20260724-090000-000000000";
    String middle = "20260724-100000-000000000";
    String last = "20260724-110000-000000000";
    String duplicateFragments = renderingJson().replace("\"textFragmentCount\":1000", "\"textFragmentCount\":100");
    writePair(archive, first, cpuJson(), duplicateFragments);
    writePair(archive, middle, cpuJson().replace("measureLatin", "otherOperation"), duplicateFragments);
    writePair(archive, last, cpuJson(), duplicateFragments);

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    BenchmarkReportView.ChartTrend missingCpu = chartTrend(view, "CPU latency: measureLatin");
    assertTrue(view.charts().trends().stream().filter(series -> series.label().startsWith("GPU p99: 100 fragments")).count() == 2);
    assertTrue(missingCpu.values().equals(java.util.Arrays.asList(12.5, null, 12.5)));
    assertTrue(missingCpu.changes().equals(java.util.Arrays.asList("not available", null, "not available")));
  }

  @Test
  void keepsBoundaryGapsAndSingleRunChartsOnTheGlobalTimeline() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-trend-boundaries");
    String first = "20260724-090000-000000000";
    String middle = "20260724-100000-000000000";
    String last = "20260724-110000-000000000";
    writePair(archive, first, cpuJson().replace("measureLatin", "otherOperation"), renderingJson());
    writePair(archive, middle, cpuJson(), renderingJson());
    writePair(archive, last, cpuJson().replace("measureLatin", "otherOperation"), renderingJson());
    BenchmarkReportView.ChartTrend boundaryOnly = chartTrend(BenchmarkHtmlReportGenerator.loadArchive(archive), "CPU latency: measureLatin");
    assertTrue(boundaryOnly.values().equals(java.util.Arrays.asList(null, 12.5, null)));

    Path singleRunArchive = Files.createTempDirectory("benchmark-single-trend");
    writePair(singleRunArchive, first, cpuJson(), renderingJson());
    BenchmarkReportView.ChartTrend single = chartTrend(BenchmarkHtmlReportGenerator.loadArchive(singleRunArchive), "CPU latency: measureLatin");
    assertTrue(single.values().equals(List.of(12.5)));
    String html = BenchmarkHtmlReportGenerator.generateArchive(singleRunArchive);
    assertTrue(html.contains("data-trend-id=\"cpu-trend-1\""));
    assertFalse(html.contains("One matching run"));
  }

  private static void writePair(Path archive, String identifier, String cpu, String rendering) throws IOException {
    Files.writeString(archive.resolve("text-calculation-" + identifier + ".json"), cpu);
    Files.writeString(archive.resolve("nanovg-text-" + identifier + ".json"), rendering);
  }

  private static void assertTooltipReferences(String html) {
    List<String> tooltipIds = List.of("help-latency", "help-uncertainty", "help-allocation",
        "help-percentiles", "help-frame-budgets", "help-scene-complexity", "help-cpu-budget",
        "help-gpu-budget", "help-samples", "help-pixel-validation");
    assertTrue(count(html, "class=\"tooltip\"") == tooltipIds.size());
    for (String id : tooltipIds) {
      assertTrue(count(html, "id=\"" + id + "\"") == 1);
      assertTrue(count(html, "aria-describedby=\"" + id + "\"") == 1);
    }
  }

  private static int count(String value, String token) {
    return value.split(java.util.regex.Pattern.quote(token), -1).length - 1;
  }

  private static BenchmarkReportView.ChartTrend chartTrend(BenchmarkReportView view, String label) {
    return view.charts().trends().stream().filter(series -> series.label().equals(label)).findFirst().orElseThrow();
  }

  private static String cpuJson() {
    return """
        [{"benchmark":"example.measureLatin","primaryMetric":{"score":12.5,"scoreError":0.5},"secondaryMetrics":{"gc.alloc.rate.norm":{"score":42},"gc.alloc.rate":{"score":7}}}]
        """;
  }

  private static String renderingJson() {
    return """
        {"environment":{"javaVersion":"25","javaVendor":"Vendor","osName":"OS","osVersion":"1","osArchitecture":"x64","glVendor":"<GPU>","glRenderer":"Renderer","glVersion":"4"},"pixelValidationPassed":true,"scenes":[
        {"textFragmentCount":100,"textCodePointCount":20,"resolvedGlyphCount":20,"resolvedRunCount":2,"warmupFrameCount":60,"measuredFrameCount":200,"cpuSubmissionMicros":{"median":1,"p95":2,"p99":3,"budget60HzPercent":1,"budget120HzPercent":2},"gpuCompleteMicros":{"median":2,"p95":3,"p99":4,"budget60HzPercent":2,"budget120HzPercent":4}},
        {"textFragmentCount":1000,"textCodePointCount":200,"resolvedGlyphCount":200,"resolvedRunCount":20,"warmupFrameCount":60,"measuredFrameCount":200,"cpuSubmissionMicros":{"median":10,"p95":20,"p99":30,"budget60HzPercent":10,"budget120HzPercent":20},"gpuCompleteMicros":{"median":20,"p95":30,"p99":40,"budget60HzPercent":20,"budget120HzPercent":40}}]}
        """;
  }
}
