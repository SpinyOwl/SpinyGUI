package com.spinyowl.spinygui.benchmark.report;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class BenchmarkHtmlReportGeneratorTest {
  @Test
  void generatesSelfContainedEscapedReportFromBothJsonFormats() {
    String html = BenchmarkHtmlReportGenerator.generate(cpuJson(), renderingJson());

    assertTrue(html.contains("measureLatin"));
    assertTrue(html.contains("100 fragments"));
    assertTrue(html.contains("1,000 fragments"));
    assertTrue(html.contains("&lt;GPU&gt;"));
    assertTrue(html.contains("Latency (us/op)"));
    assertTrue(html.contains("Logarithmic scale (base 10"));
    assertTrue(html.contains("120 Hz marker: 8,333 us"));
    assertTrue(html.contains("60 Hz marker: 16,667 us"));
    assertTrue(html.contains("class=\"budget-marker marker-120\""));
    assertTrue(html.contains("class=\"budget-marker marker-60\""));
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
    assertFalse(html.contains(":has("));
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
    assertTrue(compactHtml.contains(".row{grid-template-columns:minmax(0,1fr)auto;gap:6px}"));
    assertTrue(compactHtml.contains(".row>span:first-child{min-width:0;overflow-wrap:anywhere}"));
    assertTrue(compactHtml.contains(".row>span:last-child{min-width:0;max-width:45vw;overflow-wrap:anywhere;text-align:right}"));
    assertTrue(compactHtml.contains(".table-wrap{overflow-x:auto"));
    assertTrue(compactHtml.contains("table{min-width:640px}"));
    assertTrue(compactHtml.contains(".tooltip{position:fixed;z-index:4;inset:16px"));
    assertTrue(compactHtml.contains(".report-nav{position:sticky;top:0"));
    assertTrue(compactHtml.contains(".report-nav{margin-inline:-16px;padding:12px16px16px;flex-wrap:nowrap;overflow-x:auto}"));
    assertTrue(compactHtml.contains(".report-nava{flex:none;white-space:nowrap}"));
    assertTrue(compactHtml.contains("scroll-padding-top:72px"));

    String lowerCaseHtml = html.toLowerCase(Locale.ROOT);
    assertFalse(lowerCaseHtml.contains("http://"));
    assertFalse(lowerCaseHtml.contains("https://"));
    assertFalse(lowerCaseHtml.contains("//"));
    assertFalse(lowerCaseHtml.contains("<script"));
    assertFalse(lowerCaseHtml.contains("<link"));
    assertFalse(lowerCaseHtml.contains("src="));
    assertFalse(lowerCaseHtml.contains("url("));
  }

  @Test
  void loadsCompleteTimestampedPairsChronologicallyAndComputesChanges() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-runs");
    String first = "20260724-090000-000000000";
    String second = "20260724-100000-000000000";
    String collision = second + "-000001";
    writePair(archive, second, cpuJson().replace("12.5", "25.0"), renderingJson().replace("\"p99\":4", "\"p99\":8")
        .replace("\"budget120HzPercent\":4", "\"budget120HzPercent\":8"));
    writePair(archive, collision, cpuJson().replace("12.5", "20.0"), renderingJson().replace("\"p99\":4", "\"p99\":12")
        .replace("\"budget120HzPercent\":4", "\"budget120HzPercent\":12"));
    writePair(archive, first, cpuJson(), renderingJson());
    Files.writeString(archive.resolve("text-calculation-20260724-110000-000000000.json"), cpuJson());
    writePair(archive, "20260724-120000-000000000", "{not-json", renderingJson());
    writePair(archive, "20260724-130000-000000000", "[{\"benchmark\":\"missing-metrics\"}]", renderingJson());
    writePair(archive, "20261324-140000-000000000", cpuJson(), renderingJson());
    Files.writeString(archive.resolve("text-calculation-not-a-datetime.json"), cpuJson());
    Files.writeString(archive.resolve(".20260724-150000-000000000.benchmark-run.lock"), "stale");

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

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
    String html = BenchmarkHtmlReportGenerator.generateArchive(archive);
    assertTrue(html.contains("measureLatin"));
    assertTrue(html.contains("<section id=\"history\""));
    assertTrue(html.contains(collision));
    assertTrue(html.contains("CPU median/p95/p99"));
    assertTrue(html.contains("GPU median/p95/p99"));
    assertTrue(html.contains("-20.000%"));
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
