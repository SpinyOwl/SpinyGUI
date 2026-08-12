package com.spinyowl.spinygui.benchmark.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata.EvidenceMode;
import com.spinyowl.spinygui.benchmark.rendering.RenderingWorkloadSpecifications;
import com.spinyowl.spinygui.benchmark.rendering.RenderingBoundaryScenes;
import com.spinyowl.spinygui.benchmark.rendering.StructuralValidationReport;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
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
    assertTrue(html.contains("Rendering small"));
    assertTrue(html.contains("Rendering large"));
    assertTrue(html.contains("<b>fragments:</b> 100"));
    assertTrue(html.contains("<b>fragments:</b> 1,000"));
    assertTrue(html.contains("&lt;GPU&gt;"));
    assertTrue(html.contains("Latency (us/op)"));
    assertTrue(count(html, "id=\"cpu-latency-chart\"") == 1);
    assertTrue(count(html, "id=\"cpu-allocation-chart\"") == 1);
    assertTrue(count(html, "id=\"cpu-rendering-chart\"") == 1);
    assertTrue(count(html, "id=\"gpu-rendering-chart\"") == 1);
    assertTrue(count(html, "role=\"img\"") == 4);
    assertTrue(count(html, "class=\"chart-fallback\"") == 4);
    assertTrue(html.contains("class=\"chart-scroll\""));
    assertTrue(html.contains("class=\"chart-frame\""));
    assertTrue(html.contains("id=\"cpu-data\""));
    assertTrue(html.contains("id=\"rendering-data\""));
    assertTrue(html.contains("<span class=\"summary-value\">40.000 us</span>"));
    assertTrue(html.contains("0.480% of the 120 Hz budget"));
    assertTrue(html.contains("<a class=\"skip-link\" href=\"#overview\">Skip to report content</a>"));
    assertTrue(html.contains("<nav class=\"report-nav\" aria-label=\"Benchmark report sections\">"));
    assertTrue(html.contains("href=\"#overview\" aria-current=\"location\""));
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
    assertTrue(
        html.contains(
            "30 alternating warmup + 1 synchronized validation = 31 pre-measure exposures; 200 measured"));
    assertTrue(
        html.contains(
            "30 alternating warmup + 0 synchronized validation = 30 pre-measure exposures; 200 measured"));
    assertTrue(html.contains("Structural validation: passed (4 approved scenes,"));
    assertTrue(html.contains("class=\"summary-grid\""));
    assertTrue(html.contains("Technical evidence and fingerprints"));
    assertTrue(html.contains("Archive status: 1 eligible runs, 0 excluded timing artifacts"));
    String compactHtml = html.replaceAll("\\s+", "");
    String chartGuidance = "<p class=\"chart-guidance\">How to read: Lower values are better.</p>";
    assertEquals(4, count(html, chartGuidance));
    for (String caption : List.of("CPU operation latency (us/op)", "Normalized CPU allocation (B/op)",
        "CPU submission latency by rendering scene", "GPU-complete latency by rendering scene")) {
      assertTrue(html.contains("<figcaption>" + caption + "</figcaption>" + chartGuidance));
    }
    assertTrue(compactHtml.contains(".chart-guidance{color:#aebed0;margin:008px}"));
    assertTrue(compactHtml.contains("@media(max-width:700px)"));
    assertTrue(compactHtml.contains(".chart-frame{position:relative;min-width:820px;height:480px}"));
    assertTrue(compactHtml.contains(".chart-scroll{max-width:100%;overflow-x:auto;overscroll-behavior-inline:contain}"));
    assertTrue(compactHtml.contains(".table-wrap{max-width:100%;overflow-x:auto"));
    assertTrue(compactHtml.contains(".pill{display:inline-block;box-sizing:border-box;max-width:100%;overflow-wrap:anywhere"));
    assertTrue(compactHtml.contains(".metadata-griddd{min-width:0;margin:0;overflow-wrap:anywhere}"));
    assertTrue(compactHtml.contains("table{min-width:640px}"));
    assertTrue(compactHtml.contains(".tooltip{position:fixed;z-index:4;inset:16px"));
    assertTrue(compactHtml.contains(".report-nav{position:sticky;top:0"));
    assertTrue(compactHtml.contains(".report-nav{margin-inline:-16px;padding:12px16px10px;flex-wrap:nowrap;overflow-x:auto;scrollbar-width:thin}"));
    assertTrue(compactHtml.contains(".report-nava{flex:none;white-space:nowrap}"));
    assertTrue(html.contains("<div class=\"empty-state\"><h3>No trend yet</h3>"));
    assertFalse(html.contains("id=\"trend-select\""));
    assertFalse(html.contains("<canvas id=\"history-chart\" role=\"img\""));
    assertTrue(html.contains("id=\"history-data\""));
    assertTrue(html.contains("<caption>Precise CPU benchmark results</caption>"));
    assertTrue(html.contains("<thead>"));
    assertTrue(html.contains("scope=\"col\""));
    assertTrue(html.contains("scope=\"row\""));
    assertFalse(html.contains("<svg"));
    assertFalse(html.contains(":has("));
    assertFalse(html.contains("cx=\""));
    assertFalse(html.contains("<polyline"));
    assertTrue(compactHtml.contains(".trend-select:focus-visible"));
    assertTrue(compactHtml.contains("scroll-padding-top:76px"));
    assertFalse(compactHtml.contains("scroll-margin-top:"));

    assertTrue(html.contains("Chart.js v4.5.1"));
    assertTrue(html.contains("@kurkle/color v0.3.2"));
    assertTrue(html.contains("<script id=\"benchmark-chart-data\" type=\"application/json\">"));
    assertTrue(html.contains("data.chartPayloadVersion"));
    assertTrue(html.contains("indexAxis:'y'"));
    assertTrue(html.contains("type:'logarithmic'"));
    assertTrue(html.contains("max:16667"));
    assertTrue(html.contains("8333"));
    assertTrue(html.contains("budgetMarkers"));
    assertTrue(html.contains("[[8333, '120 Hz'], [16667, '60 Hz']]"));
    assertTrue(html.contains("valueLabels"));
    assertTrue(html.contains("updateActiveNavigation"));
    assertTrue(html.contains("window.requestAnimationFrame"));
    assertTrue(html.contains("Number.isInteger(Math.log10(Number(value)))"));
    assertTrue(html.contains("spanGaps:false"));
    assertTrue(html.contains("return change && /^[+-]/.test(change) ? colors.blue : 'transparent'"));
    assertTrue(html.contains("function chartOptions(xScale, xTitle, yTitle)"));
    assertTrue(html.contains("x:{...xScale, title:{display:true, text:xTitle, color:colors.text}, ticks:{color:colors.muted}, grid:{color:colors.grid}}"));
    assertTrue(html.contains("y:{title:{display:true, text:yTitle, color:colors.text}, ticks:{color:colors.muted}, grid:{color:colors.grid}}"));
    assertTrue(html.contains("cpuConfig('Latency (us/op)', 'CPU operation', 'Latency (us/op)'"));
    assertTrue(html.contains("cpuConfig('Allocation (B/op)', 'CPU operation', 'Allocation (B/op)'"));
    assertEquals(2, count(html, "renderingConfig('Latency (us)', 'Rendering scene'"));
    assertTrue(html.contains("return trend.id.startsWith('cpu-') ? 'CPU latency (us/op)' : 'GPU p99 latency (us)';"));
    assertTrue(html.contains("x:{title:{display:true, text:'Benchmark run', color:colors.text}, ticks:{color:colors.muted},grid:{color:colors.grid}}"));
    assertTrue(html.contains("y:{min:trend.minimum,max:trend.maximum,title:{display:true, text:historyMetricTitle(trend), color:colors.text},ticks:{color:colors.muted},grid:{color:colors.grid}}"));
    assertTrue(html.contains("historyChart.data.datasets[0].data = trend.values"));
    assertInOrder(html, "function activateTrend(trendId)",
        "historyChart.options.scales.y.title.text = historyMetricTitle(trend);", "historyChart.update();");
    assertFalse(html.contains("class=\"track\""));
    assertFalse(html.contains("class=\"budget-marker"));
    assertTrue(count(html, "<script") == 3);
    assertFalse(html.contains("<script src="));
    assertFalse(html.contains("<link rel=\"stylesheet\""));
    assertFalse(html.contains("sourceMappingURL"));

    String noArchiveHtml = BenchmarkHtmlReportGenerator.generate(cpuJson(), renderingJson());
    assertFalse(noArchiveHtml.contains("id=\"history-chart\""));
    assertTrue(noArchiveHtml.contains("No trend yet"));
    assertFalse(noArchiveHtml.contains("id=\"trend-select\""));
  }

  @Test
  void reportsArchiveHealthAndWritesNormalizedManifest(@TempDir Path archive) throws IOException {
    String included = "20260724-090000-000000000";
    String cpuOnly = "20260724-100000-000000000";
    String diagnostics = "20260724-110000-000000000";
    writePair(archive, included, cpuJson(), renderingJson());
    Files.writeString(
        archive.resolve("text-calculation-" + cpuOnly + ".json"),
        withRunMetadata(cpuJson(), cpuOnly, "cpu", "paired-report", timedMode()));
    Files.writeString(archive.resolve("text-diagnostics-" + diagnostics + ".json"), "{}");

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);
    String html = BenchmarkHtmlReportGenerator.generateArchive(archive);
    JsonObject manifest = JsonParser.parseString(BenchmarkHtmlReportGenerator.generateManifest(archive)).getAsJsonObject();

    assertEquals(1, view.archiveHealth().eligibleRunCount());
    assertEquals(1, view.archiveHealth().excludedTimingArtifactCount());
    assertEquals(1, view.archiveHealth().diagnosticArtifactCount());
    assertEquals(4, view.archiveHealth().artifacts().size());
    assertTrue(html.contains("Missing rendering artifact with the same run ID"));
    assertTrue(html.contains("Counter diagnostics are not timing/allocation history"));
    assertEquals(1, manifest.get("schemaVersion").getAsInt());
    assertEquals(included, manifest.get("currentRunIdentifier").getAsString());
    assertTrue(manifest.has("generatedAtLocal"));
    assertTrue(manifest.has("currentSummary"));
    assertEquals(4, manifest.getAsJsonObject("archive").getAsJsonArray("artifacts").size());

    Path output = archive.resolve("report.html");
    BenchmarkHtmlReportGenerator.main(new String[] {archive.toString(), output.toString(), included});
    assertTrue(Files.readString(output).contains("SpinyGUI Local Benchmark Report"));
    assertTrue(Files.exists(archive.resolve("report-manifest.json")));
    assertEquals(included, JsonParser.parseString(Files.readString(archive.resolve("report-manifest.json")))
        .getAsJsonObject().get("currentRunIdentifier").getAsString());
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
  void allWrongNonBlackStructuralJsonIsNeverReportedPassed() {
    JsonObject rendering = JsonParser.parseString(renderingJson()).getAsJsonObject();
    JsonObject firstBoundary =
        rendering
            .getAsJsonObject("structuralValidation")
            .getAsJsonArray("scenes")
            .get(0)
            .getAsJsonObject();
    JsonArray wrong = new JsonArray();
    wrong.add("all wrong but non-black");
    firstBoundary.add("submittedText", wrong);

    assertThrows(
        IllegalArgumentException.class,
        () -> BenchmarkHtmlReportGenerator.generate(cpuJson(), rendering.toString()));
  }

  @Test
  void structuralReportRejectsUnknownMissingWrongTypeOrderAndCommandProofDrift() {
    List<java.util.function.Consumer<JsonObject>> mutations =
        List.of(
            root -> root.addProperty("unknown", true),
            root -> root.remove("validatorVersion"),
            root -> root.addProperty("synchronizedExposureScene", 1),
            root -> {
              JsonArray scenes = root.getAsJsonArray("scenes");
              JsonElement first = scenes.remove(0);
              scenes.add(first);
            },
            root ->
                root.getAsJsonArray("scenes")
                    .get(0)
                    .getAsJsonObject()
                    .addProperty("commandCount", 999));
    for (var mutation : mutations) {
      JsonObject rendering = JsonParser.parseString(renderingJson()).getAsJsonObject();
      mutation.accept(rendering.getAsJsonObject("structuralValidation"));
      assertThrows(
          IllegalArgumentException.class,
          () -> BenchmarkHtmlReportGenerator.generate(cpuJson(), rendering.toString()));
    }
  }

  @Test
  void archiveExcludesStructurallyInvalidRenderingPair() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-invalid-structural-report");
    String accepted = "20260724-090000-000000000";
    String rejected = "20260724-100000-000000000";
    writePair(archive, accepted, cpuJson(), renderingJson());
    JsonObject invalid = JsonParser.parseString(renderingJson()).getAsJsonObject();
    invalid
        .getAsJsonObject("structuralValidation")
        .getAsJsonArray("scenes")
        .get(0)
        .getAsJsonObject()
        .addProperty("commandDigestSha256", "sha256:" + "0".repeat(64));
    writePair(archive, rejected, cpuJson(), invalid.toString());

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    assertEquals(1, view.history().size());
    assertEquals(accepted, view.history().getFirst().identifier());
    assertTrue(Files.exists(archive.resolve("nanovg-text-" + rejected + ".json")));
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
    assertTrue(html.contains("<select class=\"trend-select\" id=\"trend-select\">"));
    assertTrue(html.contains("<option value=\"cpu-trend-1\" selected>CPU latency: measure&lt;Latin&gt;</option>"));
    assertTrue(html.contains("<canvas id=\"history-chart\" role=\"img\""));
    assertFalse(html.contains("<svg"));
  }

  @Test
  void distinguishesDuplicateFragmentsAndSplitsMissingSeriesAtGlobalTimelineGaps() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-trend-gaps");
    String first = "20260724-090000-000000000";
    String middle = "20260724-100000-000000000";
    String last = "20260724-110000-000000000";
    String duplicateFragments = renderingJson().replace("\"textFragmentCount\":1000", "\"textFragmentCount\":100");
    writePair(archive, first, cpuJson(), duplicateFragments);
    writePair(archive, middle, cpuJson().replace("measureLatin", "otherOperation")
        .replace("cpu-measure-latin", "cpu-other-operation"), duplicateFragments);
    writePair(archive, last, cpuJson(), duplicateFragments);

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    BenchmarkReportView.ChartTrend missingCpu = chartTrend(view, "CPU latency: measureLatin");
    assertTrue(view.charts().trends().stream()
        .filter(series -> series.label().startsWith("GPU p99: Rendering ")).count() == 2);
    assertTrue(missingCpu.values().equals(java.util.Arrays.asList(12.5, null, 12.5)));
    assertTrue(missingCpu.changes().equals(java.util.Arrays.asList("not available", null, "not available")));
  }

  @Test
  void keepsBoundaryGapsAndShowsAnInsufficientHistoryStateForOneRun() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-trend-boundaries");
    String first = "20260724-090000-000000000";
    String middle = "20260724-100000-000000000";
    String last = "20260724-110000-000000000";
    writePair(archive, first, cpuJson().replace("measureLatin", "otherOperation")
        .replace("cpu-measure-latin", "cpu-other-operation"), renderingJson());
    writePair(archive, middle, cpuJson(), renderingJson());
    writePair(archive, last, cpuJson().replace("measureLatin", "otherOperation")
        .replace("cpu-measure-latin", "cpu-other-operation"), renderingJson());
    BenchmarkReportView.ChartTrend boundaryOnly = chartTrend(BenchmarkHtmlReportGenerator.loadArchive(archive), "CPU latency: measureLatin");
    assertTrue(boundaryOnly.values().equals(java.util.Arrays.asList(null, 12.5, null)));

    Path singleRunArchive = Files.createTempDirectory("benchmark-single-trend");
    writePair(singleRunArchive, first, cpuJson(), renderingJson());
    BenchmarkReportView.ChartTrend single = chartTrend(BenchmarkHtmlReportGenerator.loadArchive(singleRunArchive), "CPU latency: measureLatin");
    assertTrue(single.values().equals(List.of(12.5)));
    String html = BenchmarkHtmlReportGenerator.generateArchive(singleRunArchive);
    assertFalse(html.contains("id=\"trend-select\""));
    assertFalse(html.contains("id=\"history-chart\""));
    assertTrue(html.contains("<h3>No trend yet</h3>"));
    assertTrue(html.contains("Generate one more comparable CPU/rendering pair"));
  }

  @Test
  void suppressesSignedDeltasAndExplainsFingerprintAndLegacyMetadataMismatches() throws IOException {
    String first = "20260724-090000-000000000";
    String second = "20260724-100000-000000000";
    List<FingerprintMismatch> mismatches = List.of(
        new FingerprintMismatch("identity",
            cpuJson().replace("\"behaviorContractVersion\":\"behavior-1\"", "\"behaviorContractVersion\":\"behavior-2\""),
            renderingJson(),
            "not comparable: identity.behavior-contract-version differs"),
        new FingerprintMismatch("workload",
            cpuJson().replace("sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "sha256:dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"),
            renderingJson(),
            "not comparable: workload.workload-content-sha256 differs"),
        new FingerprintMismatch("environment",
            cpuJson().replace("\"jvmVersion\":\"25.0.1\"", "\"jvmVersion\":\"26\""),
            withRenderingComparabilityValue(
                renderingJson(), -1, "environment", "jvmVersion", "26"),
            "not comparable: environment.jvm-version differs"),
        new FingerprintMismatch("settings",
            cpuJson().replace("\"warmup-iterations\":\"3\"", "\"warmup-iterations\":\"4\""),
            renderingJson(),
            "not comparable: settings.warmup-iterations differs"));
    for (FingerprintMismatch fixture : mismatches) {
      Path mismatchArchive = Files.createTempDirectory("benchmark-" + fixture.name() + "-mismatch");
      writePair(mismatchArchive, first, cpuJson(), renderingJson());
      writePair(
          mismatchArchive,
          second,
          fixture.cpuJson().replace("12.5", "25.0"),
          fixture.renderingJson());

      BenchmarkReportView mismatch = BenchmarkHtmlReportGenerator.loadArchive(mismatchArchive);
      String mismatchChange = mismatch.history().getLast().cpuRows().getFirst().latencyChange();
      assertEquals(fixture.reason(), mismatchChange);
      assertEquals(mismatchChange, chartTrend(mismatch, "CPU latency: measureLatin").changes().getLast());
      assertEquals("25.000", mismatch.history().getLast().cpuRows().getFirst().latency());
      assertFalse(mismatchChange.startsWith("+") || mismatchChange.startsWith("-"));
    }

    String legacyHtml =
        BenchmarkHtmlReportGenerator.generate(legacyCpuJson(), renderingWithoutComparability());
    assertTrue(legacyHtml.contains("measureLatin"));
    assertTrue(legacyHtml.contains("12.500"));
    assertTrue(
        legacyHtml.contains("not comparable: required comparability metadata missing"));
  }

  @Test
  void changedImplementationRevisionRemainsComparableAndIsReportedPerRun() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-implementation-revision");
    String first = "20260724-090000-000000000";
    String second = "20260724-100000-000000000";
    writePair(archive, first, cpuJson(), renderingJson());
    writePair(archive, second, changedImplementation(cpuJson()).replace("12.5", "25.0"),
        changedImplementation(renderingJson()));

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    assertEquals("+100.000%", view.history().getLast().cpuRows().getFirst().latencyChange());
    assertEquals(1, view.charts().trends().stream()
        .filter(series -> series.label().equals("CPU latency: measureLatin")).count());
    assertTrue(view.history().getFirst().implementation().stream()
        .anyMatch(entry -> entry.key().equals("implementationRevision") && entry.value().equals("impl-1")));
    assertTrue(view.history().getLast().implementation().stream()
        .anyMatch(entry -> entry.key().equals("implementationRevision") && entry.value().equals("impl-2")));
    assertTrue(view.history().getLast().implementation().stream()
        .anyMatch(entry -> entry.key().equals("commitRevision") && entry.value().equals("commit-2")));
    String html = BenchmarkHtmlReportGenerator.generateArchive(archive);
    assertTrue(html.contains("impl-1"));
    assertTrue(html.contains("impl-2"));
    assertTrue(html.contains("semanticId"));
    assertTrue(html.contains("evidenceMode"));
    assertTrue(html.contains("requiredFingerprint"));
    assertTrue(html.contains("buildRevision"));
    assertTrue(html.contains("commitRevision"));
  }

  @Test
  void presentationOnlyCpuLabelChangeKeepsTheSemanticSeriesAndShowsTheDeclaredLabel()
      throws IOException {
    Path archive = Files.createTempDirectory("benchmark-cpu-display-label");
    String first = "20260724-090000-000000000";
    String second = "20260724-100000-000000000";
    writePair(archive, first, cpuJson(), renderingJson());
    writePair(
        archive,
        second,
        cpuJson().replace("measureLatin", "CPU Latin declared label").replace("12.5", "25.0"),
        renderingJson());

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    assertEquals("CPU Latin declared label", view.cpuRows().getFirst().name());
    assertEquals("CPU Latin declared label", view.history().getLast().cpuRows().getFirst().name());
    assertEquals("+100.000%", view.history().getLast().cpuRows().getFirst().latencyChange());
    assertEquals(1, view.charts().trends().stream()
        .filter(series -> series.label().equals("CPU latency: CPU Latin declared label")).count());
  }

  @Test
  void sameBenchmarkNameUsesSemanticIdentityForDistinctParameterizedSeries() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-parameterized-series");
    String first = "20260724-090000-000000000";
    String second = "20260724-100000-000000000";
    String seriesA = cpuJson();
    String seriesB = cpuJson().replace("cpu-measure-latin", "cpu-measure-latin-parameter-b")
        .replace("12.5", "30.0");
    writePair(archive, first, combineCpu(seriesA, seriesB), renderingJson());
    writePair(archive, second,
        changedImplementation(combineCpu(seriesA.replace("12.5", "25.0"), seriesB.replace("30.0", "15.0"))),
        changedImplementation(renderingJson()));

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);
    List<BenchmarkReportView.ChartTrend> cpuTrends = view.charts().trends().stream()
        .filter(series -> series.label().equals("CPU latency: measureLatin")).toList();

    assertEquals(2, cpuTrends.size());
    assertTrue(cpuTrends.stream().anyMatch(series -> series.changes().equals(List.of("not available", "+100.000%"))));
    assertTrue(cpuTrends.stream().anyMatch(series -> series.changes().equals(List.of("not available", "-50.000%"))));
  }

  @Test
  void displaysParametersSeparatelyFromExactSemanticSeriesIdentity() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-displayed-parameters");
    String first = "20260724-090000-000000000";
    String second = "20260724-100000-000000000";
    String seriesA = withCpuParams(cpuJson(), "size", "small");
    String seriesB = withCpuParams(
        cpuJson().replace("cpu-measure-latin", "cpu-measure-latin-large").replace("12.5", "30.0"),
        "size",
        "large");
    writePair(archive, first, combineCpu(seriesA, seriesB), renderingJson());
    writePair(
        archive,
        second,
        combineCpu(seriesA.replace("12.5", "25.0"), seriesB.replace("30.0", "15.0")),
        renderingJson());

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);
    String html = BenchmarkHtmlReportGenerator.generateArchive(archive);

    assertEquals("small", view.cpuRows().getFirst().parameters().getFirst().value());
    assertEquals("large", view.cpuRows().get(1).parameters().getFirst().value());
    assertTrue(view.cpuRows().getFirst().comparability().stream()
        .anyMatch(entry -> entry.key().equals("semanticId")
            && entry.value().equals("cpu-measure-latin")));
    assertTrue(html.contains("<b>size:</b> small"));
    assertTrue(html.contains("<b>size:</b> large"));
    assertEquals(2, view.charts().trends().stream()
        .filter(series -> series.label().equals("CPU latency: measureLatin")).count());
  }

  @Test
  void workloadVersionChangeIsNonComparableWhileRevisionOnlyChangeIsComparable()
      throws IOException {
    Path archive = Files.createTempDirectory("benchmark-workload-version-mismatch");
    String first = "20260724-090000-000000000";
    String second = "20260724-100000-000000000";
    writePair(archive, first, cpuJson(), renderingJson());
    writePair(
        archive,
        second,
        changedImplementation(cpuJson())
            .replace("\"workloadVersion\":\"workload-1\"", "\"workloadVersion\":\"workload-2\"")
            .replace("12.5", "25.0"),
        changedImplementation(renderingJson()));

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    assertEquals(
        "not comparable: identity.workload-version differs",
        view.history().getLast().cpuRows().getFirst().latencyChange());
    assertFalse(view.history().getLast().cpuRows().getFirst().latencyChange().startsWith("+"));
  }

  @Test
  void methodologyExplainsComparabilityEvidenceAndLocalLimitations() {
    String html = BenchmarkHtmlReportGenerator.generate(cpuJson(), renderingJson());

    for (String text : List.of(
        "Diagnostics-enabled counter runs are separate evidence",
        "required fingerprints qualify every signed delta",
        "Implementation revisions are traceability metadata only",
        "describe observed structure and never identify a series",
        "hardware-, operating-system-, JVM-, GPU-, and driver-sensitive")) {
      assertTrue(html.contains(text));
    }
    assertTrue(html.contains("identityFingerprint"));
    assertTrue(html.contains("workloadFingerprint"));
    assertTrue(html.contains("environmentFingerprint"));
    assertTrue(html.contains("settingsFingerprint"));
  }

  @Test
  void changedObservedRenderingCountsStayInOneComparableSemanticSeries() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-output-evidence");
    String first = "20260724-090000-000000000";
    String second = "20260724-100000-000000000";
    writePair(archive, first, cpuJson(), renderingJson());
    writePair(archive, second, cpuJson(), renderingJson()
        .replace("\"resolvedGlyphCount\":20", "\"resolvedGlyphCount\":21")
        .replace("\"resolvedRunCount\":2", "\"resolvedRunCount\":3")
        .replace("\"p99\":4,", "\"p99\":8,"));

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);
    BenchmarkReportView.ChartTrend smallScene = chartTrend(view, "GPU p99: Rendering small");

    assertEquals(List.of(4.0, 8.0), smallScene.values());
    assertEquals(List.of("not available", "+100.000%"), smallScene.changes());
    assertEquals("Rendering small", view.sceneRows().getFirst().name());
    assertEquals("21", evidence(view.sceneRows().getFirst().evidence(), "glyphs"));
    assertEquals("3", evidence(view.sceneRows().getFirst().evidence(), "runs"));
    assertEquals("20", evidence(view.history().getFirst().sceneRows().getFirst().evidence(), "glyphs"));
    assertEquals("2", evidence(view.history().getFirst().sceneRows().getFirst().evidence(), "runs"));
    assertEquals("21", evidence(view.history().getLast().sceneRows().getFirst().evidence(), "glyphs"));
    assertEquals("3", evidence(view.history().getLast().sceneRows().getFirst().evidence(), "runs"));
    assertEquals(
        view.history().getFirst().comparability(),
        view.history().getLast().comparability());
    assertEquals("Rendering small", view.charts().rendering().getFirst().label());
    assertEquals(2, view.charts().trends().stream()
        .filter(series -> series.label().startsWith("GPU p99:")).count());
    String html = BenchmarkHtmlReportGenerator.generateArchive(archive);
    assertTrue(html.contains("<b>glyphs:</b> 20"));
    assertTrue(html.contains("<b>glyphs:</b> 21"));
    assertTrue(html.contains("<b>runs:</b> 2"));
    assertTrue(html.contains("<b>runs:</b> 3"));
  }

  @Test
  void suppressesRenderingTripletDeltaWhenAnyPriorPercentileIsZero() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-zero-rendering-percentile");
    String first = "20260724-090000-000000000";
    String second = "20260724-100000-000000000";
    writePair(
        archive,
        first,
        cpuJson(),
        renderingJson().replace(
            "\"cpuSubmissionMicros\":{\"median\":1,\"p95\":2,\"p99\":3",
            "\"cpuSubmissionMicros\":{\"median\":1,\"p95\":0,\"p99\":3"));
    writePair(archive, second, cpuJson(), renderingJson());

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    assertEquals("not available", view.history().getLast().sceneRows().getFirst().cpuChange());
    assertFalse(view.history().getLast().sceneRows().getFirst().cpuChange().contains("Infinity"));
  }

  @Test
  void archiveExcludesEveryCpuResultWithMissingOrInvalidComparability() throws IOException {
    List<RawVariant> variants =
        List.of(
            new RawVariant("missing", legacyCpuJson()),
            new RawVariant("invalid", invalidCpuJson()),
            new RawVariant("one-of-many-missing", combineCpu(cpuJson(), legacyCpuJson())),
            new RawVariant("one-of-many-invalid", combineCpu(cpuJson(), invalidCpuJson())));
    for (RawVariant variant : variants) {
      Path archive = Files.createTempDirectory("benchmark-cpu-" + variant.name());
      String accepted = "20260724-090000-000000000";
      String rejected = "20260724-100000-000000000";
      writePair(archive, accepted, cpuJson(), renderingJson());
      writePair(archive, rejected, variant.json(), renderingJson());

      BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

      assertEquals(accepted, view.currentRunIdentifier());
      assertEquals(List.of(accepted), view.charts().historyRuns());
      assertEquals(1, view.history().size());
      assertTrue(Files.exists(archive.resolve("text-calculation-" + rejected + ".json")));
    }

    String invalidHtml =
        BenchmarkHtmlReportGenerator.generate(invalidCpuJson(), invalidRenderingJson());
    assertTrue(invalidHtml.contains("measureLatin"));
    assertTrue(invalidHtml.contains("12.500"));
    assertTrue(
        invalidHtml.contains(
            "not comparable: invalid comparability metadata: Missing required comparability field: cpuModel"));
  }

  @Test
  void archiveExcludesEveryRenderingSceneWithMissingOrInvalidComparability() throws IOException {
    List<RawVariant> variants =
        List.of(
            new RawVariant("one-missing", renderingWithSceneComparabilityRemoved(0)),
            new RawVariant("one-invalid", renderingWithSceneComparabilityInvalid(1)),
            new RawVariant("all-missing", renderingWithoutComparability()),
            new RawVariant("all-invalid", invalidRenderingJson()));
    for (RawVariant variant : variants) {
      Path archive = Files.createTempDirectory("benchmark-rendering-" + variant.name());
      String accepted = "20260724-090000-000000000";
      String rejected = "20260724-100000-000000000";
      writePair(archive, accepted, cpuJson(), renderingJson());
      writePair(archive, rejected, cpuJson(), variant.json());

      BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

      assertEquals(accepted, view.currentRunIdentifier());
      assertEquals(List.of(accepted), view.charts().historyRuns());
      assertEquals(1, view.history().size());
      assertTrue(Files.exists(archive.resolve("nanovg-text-" + rejected + ".json")));
    }
  }

  @Test
  void archiveRequiresEveryComparabilityEvidenceModeToMatchRunMetadata() throws IOException {
    List<ArtifactPair> variants =
        List.of(
            new ArtifactPair(
                cpuJson().replace(timedMode(), counterMode()), renderingJson()),
            new ArtifactPair(
                cpuJson(), renderingJson().replace(timedMode(), counterMode())));
    for (int index = 0; index < variants.size(); index++) {
      Path archive = Files.createTempDirectory("benchmark-mode-mismatch-" + index);
      String accepted = "20260724-090000-000000000";
      String rejected = "20260724-100000-000000000";
      writePair(archive, accepted, cpuJson(), renderingJson());
      writePair(
          archive,
          rejected,
          variants.get(index).cpu(),
          variants.get(index).rendering(),
          "paired-report",
          timedMode());

      BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

      assertEquals(accepted, view.currentRunIdentifier());
      assertEquals(List.of(accepted), view.charts().historyRuns());
    }
  }

  @Test
  void distinctValidSemanticIdsNeverUseLogicalFallbackForCpu() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-valid-semantic-separation");
    writePair(
        archive,
        "20260724-090000-000000000",
        cpuJson(),
        renderingJson());
    writePair(
        archive,
        "20260724-100000-000000000",
        cpuJson().replace("cpu-measure-latin", "cpu-measure-latin-parameter-b"),
        renderingJson());

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    assertEquals("not available", view.history().getLast().cpuRows().getFirst().latencyChange());
  }

  @Test
  void excludesIncompleteUnpairedCounterAndLegacyWarmupArtifactsFromBaselineSelection()
      throws IOException {
    Path archive = Files.createTempDirectory("benchmark-ineligible-pairs");
    String accepted = "20260724-090000-000000000";
    String incomplete = "20260724-100000-000000000";
    String unpaired = "20260724-110000-000000000";
    String counter = "20260724-120000-000000000";
    String legacyWarmup = "20260724-130000-000000000";
    String mismatchedPair = "20260724-140000-000000000";
    writePair(archive, accepted, cpuJson(), renderingJson());
    Files.writeString(
        archive.resolve("text-calculation-" + incomplete + ".json"),
        withRunMetadata(cpuJson(), incomplete, "cpu", "paired-report", timedMode()));
    writePair(
        archive,
        unpaired,
        cpuJson(),
        renderingJson(),
        "unpaired-investigation",
        timedMode());
    writePair(
        archive,
        counter,
        cpuJson().replace(timedMode(), counterMode()),
        renderingJson().replace(timedMode(), counterMode()),
        "paired-report",
        counterMode());
    writePair(
        archive,
        legacyWarmup,
        cpuJson(),
        legacyWarmupJson(),
        "paired-report",
        timedMode());
    Files.writeString(
        archive.resolve("text-calculation-" + mismatchedPair + ".json"),
        withRunMetadata(cpuJson(), mismatchedPair, "cpu", "paired-report", timedMode()));
    Files.writeString(
        archive.resolve("nanovg-text-" + mismatchedPair + ".json"),
        withRunMetadata(
            renderingJson(), accepted, "rendering", "paired-report", timedMode()));

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    assertEquals(accepted, view.currentRunIdentifier());
    assertEquals(List.of(accepted), view.charts().historyRuns());
    assertEquals(1, view.history().size());
    assertTrue(Files.exists(archive.resolve("text-calculation-" + incomplete + ".json")));
    assertTrue(Files.exists(archive.resolve("nanovg-text-" + legacyWarmup + ".json")));
    assertTrue(
        BenchmarkHtmlReportGenerator.generate(cpuJson(), legacyWarmupJson())
            .contains("60 warmup (legacy metadata); 200 measured"));
  }

  @Test
  void completeSharedPairLifecycleSelectsExactlyOneReportRunAndLeavesOtherArtifactsDiagnosable()
      throws IOException {
    Path archive = Files.createTempDirectory("benchmark-paired-lifecycle");
    String paired = "20260812-120000-000000000";
    String incomplete = "20260812-120100-000000000";
    String standalone = "20260812-120200-000000000";
    writePair(archive, paired, cpuJson(), renderingJson());
    Files.writeString(
        archive.resolve("text-calculation-" + incomplete + ".json"),
        withRunMetadata(cpuJson(), incomplete, "cpu", "paired-report", timedMode()));
    writePair(
        archive,
        standalone,
        cpuJson(),
        renderingJson(),
        "unpaired-investigation",
        timedMode());
    Files.writeString(archive.resolve("." + paired + ".benchmark-run.lock"), "stale");

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    assertEquals(paired, view.currentRunIdentifier());
    assertEquals(List.of(paired), view.charts().historyRuns());
    assertTrue(Files.exists(archive.resolve("text-calculation-" + incomplete + ".json")));
    assertTrue(Files.exists(archive.resolve("nanovg-text-" + standalone + ".json")));
    assertTrue(Files.exists(archive.resolve("." + paired + ".benchmark-run.lock")));
  }

  @Test
  void reportOwnedRunSelectionNeverSubstitutesAnOlderEligiblePair() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-report-owned-run");
    String stale = "20260812-120000-000000000";
    String fresh = "20260812-120100-000000000";
    writePair(archive, stale, cpuJson(), renderingJson());

    IllegalArgumentException missing =
        assertThrows(
            IllegalArgumentException.class,
            () -> BenchmarkHtmlReportGenerator.loadArchive(archive, fresh));

    assertTrue(missing.getMessage().contains(fresh));
    writePair(archive, fresh, cpuJson(), renderingJson());

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive, fresh);

    assertEquals(fresh, view.currentRunIdentifier());
    assertEquals(List.of(stale, fresh), view.charts().historyRuns());
  }

  @Test
  void archiveRejectsWarmupCountsThatMismatchOrRewriteTheApprovedSourceProfile()
      throws IOException {
    List<RawVariant> variants =
        List.of(
            new RawVariant(
                "report-settings-mismatch",
                renderingWithWarmupProfile(29, 1, 30, 30, 0, 30, false)),
            new RawVariant(
                "swapped-in-report-and-settings",
                renderingWithWarmupProfile(30, 0, 30, 30, 1, 31, true)),
            new RawVariant(
                "arbitrary-internally-consistent",
                renderingWithWarmupProfile(29, 2, 31, 29, 0, 29, true)));
    for (RawVariant variant : variants) {
      Path archive = Files.createTempDirectory("benchmark-warmup-" + variant.name());
      String accepted = "20260724-090000-000000000";
      String rejected = "20260724-100000-000000000";
      writePair(archive, accepted, cpuJson(), renderingJson());
      writePair(archive, rejected, cpuJson(), variant.json());

      BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

      assertEquals(accepted, view.currentRunIdentifier());
      assertEquals(List.of(accepted), view.charts().historyRuns());
      assertEquals(1, view.history().size());
      assertTrue(Files.exists(archive.resolve("nanovg-text-" + rejected + ".json")));
    }
  }

  @Test
  void archiveRetainsSelfDescribingE5RenderingProfileEvolutionAsASeparateSeries()
      throws IOException {
    Path archive = Files.createTempDirectory("benchmark-e5-rendering-profile-evolution");
    String first = "20260724-090000-000000000";
    String evolved = "20260724-100000-000000000";
    writePair(archive, first, cpuJson(), renderingJson());
    String evolvedRendering = evolvedRenderingProfile(renderingJson());
    writePair(archive, evolved, cpuJson(), evolvedRendering);

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    assertEquals(List.of(first, evolved), view.charts().historyRuns());
    assertEquals(evolved, view.currentRunIdentifier());
    assertEquals("Rendering small evolved", view.sceneRows().getFirst().name());
    assertEquals("not available", view.history().getLast().sceneRows().getFirst().gpuChange());
    assertTrue(view.charts().trends().stream()
        .anyMatch(series -> series.label().equals("GPU p99: Rendering small evolved")));
  }

  @Test
  void archiveRejectsE5RenderingProfileWithInternallyInconsistentPairWarmup() throws IOException {
    Path archive = Files.createTempDirectory("benchmark-e5-rendering-profile-inconsistent");
    String accepted = "20260724-090000-000000000";
    String rejected = "20260724-100000-000000000";
    writePair(archive, accepted, cpuJson(), renderingJson());
    JsonObject inconsistent = JsonParser.parseString(evolvedRenderingProfile(renderingJson())).getAsJsonObject();
    inconsistent.getAsJsonArray("scenes").get(0).getAsJsonObject().getAsJsonObject("comparability")
        .getAsJsonObject("benchmarkSettings").addProperty("alternating-warmup-frames-pair", "999");
    writePair(archive, rejected, cpuJson(), inconsistent.toString());

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    assertEquals(accepted, view.currentRunIdentifier());
    assertEquals(List.of(accepted), view.charts().historyRuns());
  }

  @Test
  void archiveRejectsFullSceneArrayReorderWithRewrittenPositionalMetadata()
      throws IOException {
    Path archive = Files.createTempDirectory("benchmark-rendering-full-array-reorder");
    String accepted = "20260724-090000-000000000";
    String rejected = "20260724-100000-000000000";
    writePair(archive, accepted, cpuJson(), renderingJson());
    writePair(
        archive,
        rejected,
        cpuJson(),
        renderingWithReorderedScenesAndRewrittenPositionalMetadata());

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    assertEquals(accepted, view.currentRunIdentifier());
    assertEquals(List.of(accepted), view.charts().historyRuns());
    assertTrue(Files.exists(archive.resolve("nanovg-text-" + rejected + ".json")));
  }

  @Test
  void archiveRequiresRawMeasuredFramesToAgreeWithFingerprintedSourceSettings()
      throws IOException {
    Path archive = Files.createTempDirectory("benchmark-rendering-measured-frames");
    String accepted = "20260724-090000-000000000";
    String rejected = "20260724-100000-000000000";
    writePair(archive, accepted, cpuJson(), renderingJson());
    writePair(
        archive,
        rejected,
        cpuJson(),
        renderingJson().replaceFirst("\"measuredFrameCount\":200", "\"measuredFrameCount\":199"));

    BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

    assertEquals(accepted, view.currentRunIdentifier());
    assertEquals(List.of(accepted), view.charts().historyRuns());
  }

  @Test
  void archiveRequiresPairWideEnvironmentAndImplementationConsistency()
      throws IOException {
    List<ArtifactPair> variants =
        List.of(
            new ArtifactPair(
                combineCpu(
                    cpuJson(),
                    withCpuComparabilityValue(cpuJson(), "environment", "jvmVersion", "26")),
                renderingJson()),
            new ArtifactPair(
                cpuJson(),
                withRenderingComparabilityValue(
                    renderingJson(), 1, "environment", "glRenderer", "Other renderer")),
            new ArtifactPair(
                cpuJson(),
                withRenderingComparabilityValue(
                    renderingJson(), -1, "environment", "osVersion", "2")),
            new ArtifactPair(
                combineCpu(
                    cpuJson(),
                    withCpuComparabilityValue(
                        cpuJson(), "implementation", "buildRevision", "build-2")),
                renderingJson()),
            new ArtifactPair(
                cpuJson(),
                withRenderingComparabilityValue(
                    renderingJson(), 1, "implementation", "commitRevision", "commit-2")),
            new ArtifactPair(
                cpuJson(),
                withRenderingComparabilityValue(
                    renderingJson(), -1, "implementation", "implementationRevision", "impl-2")));
    for (int index = 0; index < variants.size(); index++) {
      Path archive = Files.createTempDirectory("benchmark-pair-consistency-" + index);
      String accepted = "20260724-090000-000000000";
      String rejected = "20260724-100000-000000000";
      writePair(archive, accepted, cpuJson(), renderingJson());
      writePair(
          archive,
          rejected,
          variants.get(index).cpu(),
          variants.get(index).rendering());

      BenchmarkReportView view = BenchmarkHtmlReportGenerator.loadArchive(archive);

      assertEquals(accepted, view.currentRunIdentifier());
      assertEquals(List.of(accepted), view.charts().historyRuns());
    }
  }

  private static void writePair(Path archive, String identifier, String cpu, String rendering) throws IOException {
    writePair(archive, identifier, cpu, rendering, "paired-report", timedMode());
  }

  private static void writePair(
      Path archive,
      String identifier,
      String cpu,
      String rendering,
      String pairing,
      String evidenceMode)
      throws IOException {
    Files.writeString(
        archive.resolve("text-calculation-" + identifier + ".json"),
        withRunMetadata(cpu, identifier, "cpu", pairing, evidenceMode));
    Files.writeString(
        archive.resolve("nanovg-text-" + identifier + ".json"),
        withRunMetadata(rendering, identifier, "rendering", pairing, evidenceMode));
  }

  private static void assertTooltipReferences(String html) {
    List<String> tooltipIds = List.of("help-latency", "help-uncertainty", "help-allocation",
        "help-percentiles", "help-frame-budgets", "help-scene-complexity", "help-cpu-budget",
        "help-gpu-budget", "help-samples", "help-structural-validation");
    assertTrue(count(html, "class=\"tooltip\"") == tooltipIds.size());
    for (String id : tooltipIds) {
      assertTrue(count(html, "id=\"" + id + "\"") == 1);
      assertTrue(count(html, "aria-describedby=\"" + id + "\"") == 1);
    }
  }

  private static int count(String value, String token) {
    return value.split(java.util.regex.Pattern.quote(token), -1).length - 1;
  }

  private static void assertInOrder(String value, String... tokens) {
    int previous = -1;
    for (String token : tokens) {
      int current = value.indexOf(token, previous + 1);
      assertTrue(current > previous, () -> "Expected token in order: " + token);
      previous = current;
    }
  }

  private static BenchmarkReportView.ChartTrend chartTrend(BenchmarkReportView view, String label) {
    return view.charts().trends().stream().filter(series -> series.label().equals(label)).findFirst().orElseThrow();
  }

  private static String evidence(
      List<BenchmarkReportView.MetadataValue> evidence, String key) {
    return evidence.stream()
        .filter(value -> value.key().equals(key))
        .map(BenchmarkReportView.MetadataValue::value)
        .findFirst()
        .orElseThrow();
  }

  private static String cpuJson() {
    return """
        [{"benchmark":"example.measureLatin","primaryMetric":{"score":12.5,"scoreError":0.5},"secondaryMetrics":{"gc.alloc.rate.norm":{"score":42},"gc.alloc.rate":{"score":7}},"comparability":%s}]
        """.formatted(cpuComparability("cpu-measure-latin", "measureLatin", "impl-1"));
  }

  private static String legacyCpuJson() {
    return """
        [{"benchmark":"example.measureLatin","primaryMetric":{"score":12.5,"scoreError":0.5},"secondaryMetrics":{"gc.alloc.rate.norm":{"score":42},"gc.alloc.rate":{"score":7}}}]
        """;
  }

  private static String invalidCpuJson() {
    JsonArray report = JsonParser.parseString(cpuJson()).getAsJsonArray();
    report.get(0).getAsJsonObject().getAsJsonObject("comparability")
        .getAsJsonObject("environment").remove("cpuModel");
    return report.toString();
  }

  private static String renderingJson() {
    return """
        {"environment":{"javaVersion":"25","javaVendor":"Vendor","osName":"OS","osVersion":"1","osArchitecture":"x64","glVendor":"<GPU>","glRenderer":"Renderer","glVersion":"4"},"structuralValidation":%s,"scenes":[
        {"textFragmentCount":100,"textCodePointCount":20,"resolvedGlyphCount":20,"resolvedRunCount":2,"alternatingWarmupFrameCount":30,"validationExposureCount":1,"preMeasureExposureCount":31,"measuredFrameCount":200,"cpuSubmissionMicros":{"median":1,"p95":2,"p99":3,"budget60HzPercent":1,"budget120HzPercent":2},"gpuCompleteMicros":{"median":2,"p95":3,"p99":4,"budget60HzPercent":2,"budget120HzPercent":4},"comparability":%s},
        {"textFragmentCount":1000,"textCodePointCount":200,"resolvedGlyphCount":200,"resolvedRunCount":20,"alternatingWarmupFrameCount":30,"validationExposureCount":0,"preMeasureExposureCount":30,"measuredFrameCount":200,"cpuSubmissionMicros":{"median":10,"p95":20,"p99":30,"budget60HzPercent":10,"budget120HzPercent":20},"gpuCompleteMicros":{"median":20,"p95":30,"p99":40,"budget60HzPercent":20,"budget120HzPercent":40},"comparability":%s}]}
        """.formatted(
            structuralValidationJson(),
            renderingComparability(0, "impl-1"),
            renderingComparability(1, "impl-1"));
  }

  private static String structuralValidationJson() {
    var fontService =
        RenderingWorkloadSpecifications.CURRENT.createFontService(DiagnosticSession.disabled());
    return StructuralValidationReport.create(
            RenderingBoundaryScenes.synchronizedSmallFixtureEvidence(fontService),
            RenderingBoundaryScenes.validateAll(fontService))
        .toJson()
        .toString();
  }

  private static String renderingComparability(int sceneIndex, String implementationRevision) {
    ComparabilityMetadata.Environment environment =
        new ComparabilityMetadata.Environment(
            ComparabilityMetadata.Scope.RENDERING,
            "Vendor",
            "25.0.1",
            "OS",
            "1",
            "x64",
            "CPU model",
            "GPU vendor",
            "Renderer",
            "driver-1",
            "4.6");
    ComparabilityMetadata.Implementation implementation =
        new ComparabilityMetadata.Implementation(
            implementationRevision, "build-1", "commit-1");
    var specification = RenderingWorkloadSpecifications.CURRENT;
    var scene = specification.measurementOrder().get(sceneIndex);
    return specification
        .comparability(
            scene,
            EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED,
            environment,
            implementation)
        .toJson()
        .toString();
  }

  private static String renderingWithoutComparability() {
    JsonObject report = JsonParser.parseString(renderingJson()).getAsJsonObject();
    for (var scene : report.getAsJsonArray("scenes")) {
      scene.getAsJsonObject().remove("comparability");
    }
    return report.toString();
  }

  private static String renderingWithSceneComparabilityRemoved(int sceneIndex) {
    JsonObject report = JsonParser.parseString(renderingJson()).getAsJsonObject();
    report.getAsJsonArray("scenes").get(sceneIndex).getAsJsonObject().remove("comparability");
    return report.toString();
  }

  private static String renderingWithSceneComparabilityInvalid(int sceneIndex) {
    JsonObject report = JsonParser.parseString(renderingJson()).getAsJsonObject();
    report
        .getAsJsonArray("scenes")
        .get(sceneIndex)
        .getAsJsonObject()
        .getAsJsonObject("comparability")
        .getAsJsonObject("environment")
        .remove("cpuModel");
    return report.toString();
  }

  private static String invalidRenderingJson() {
    JsonObject report = JsonParser.parseString(renderingJson()).getAsJsonObject();
    for (var scene : report.getAsJsonArray("scenes")) {
      scene.getAsJsonObject().getAsJsonObject("comparability")
          .getAsJsonObject("environment").remove("cpuModel");
    }
    return report.toString();
  }

  private static String renderingWithWarmupProfile(
      int smallAlternating,
      int smallValidation,
      int smallPreMeasure,
      int largeAlternating,
      int largeValidation,
      int largePreMeasure,
      boolean updateComparabilitySettings) {
    JsonObject report = JsonParser.parseString(renderingJson()).getAsJsonObject();
    setWarmupProfile(
        report.getAsJsonArray("scenes").get(0).getAsJsonObject(),
        smallAlternating,
        smallValidation,
        smallPreMeasure,
        updateComparabilitySettings);
    setWarmupProfile(
        report.getAsJsonArray("scenes").get(1).getAsJsonObject(),
        largeAlternating,
        largeValidation,
        largePreMeasure,
        updateComparabilitySettings);
    return report.toString();
  }

  private static String evolvedRenderingProfile(String json) {
    JsonObject report = JsonParser.parseString(json).getAsJsonObject();
    for (int index = 0; index < report.getAsJsonArray("scenes").size(); index++) {
      JsonObject scene = report.getAsJsonArray("scenes").get(index).getAsJsonObject();
      JsonObject comparability = scene.getAsJsonObject("comparability");
      comparability.addProperty("workloadVersion", "workload-evolved");
      comparability.addProperty("behaviorContractVersion", "behavior-evolved");
      comparability.addProperty(
          "semanticId", comparability.get("semanticId").getAsString() + "-evolved");
      comparability.addProperty(
          "displayLabel", "Rendering " + (index == 0 ? "small evolved" : "large evolved"));
      JsonObject settings = comparability.getAsJsonObject("benchmarkSettings");
      settings.addProperty("alternating-warmup-frames-pair", "40");
      settings.addProperty("alternating-warmup-frames-scene", "20");
      settings.addProperty("premeasure-exposures-scene", Integer.toString(20 + (index == 0 ? 1 : 0)));
      scene.addProperty("alternatingWarmupFrameCount", 20);
      scene.addProperty("preMeasureExposureCount", 20 + (index == 0 ? 1 : 0));
    }
    return report.toString();
  }

  private static void setWarmupProfile(
      JsonObject scene,
      int alternating,
      int validation,
      int preMeasure,
      boolean updateComparabilitySettings) {
    scene.addProperty("alternatingWarmupFrameCount", alternating);
    scene.addProperty("validationExposureCount", validation);
    scene.addProperty("preMeasureExposureCount", preMeasure);
    if (updateComparabilitySettings) {
      JsonObject settings = scene.getAsJsonObject("comparability").getAsJsonObject("benchmarkSettings");
      settings.addProperty("alternating-warmup-frames-scene", Integer.toString(alternating));
      settings.addProperty("validation-exposures-scene", Integer.toString(validation));
      settings.addProperty("premeasure-exposures-scene", Integer.toString(preMeasure));
    }
  }

  private static String renderingWithReorderedScenesAndRewrittenPositionalMetadata() {
    JsonObject report = JsonParser.parseString(renderingJson()).getAsJsonObject();
    JsonArray original = report.getAsJsonArray("scenes");
    JsonObject small = original.get(0).getAsJsonObject().deepCopy();
    JsonObject large = original.get(1).getAsJsonObject().deepCopy();
    JsonArray reordered = new JsonArray();
    reordered.add(large);
    reordered.add(small);
    report.add("scenes", reordered);

    rewritePositionalRenderingMetadata(large, 1, 1, 31);
    rewritePositionalRenderingMetadata(small, 2, 0, 30);
    return report.toString();
  }

  private static void rewritePositionalRenderingMetadata(
      JsonObject scene, int orderIndex, int validationExposures, int preMeasureExposures) {
    setWarmupProfile(scene, 30, validationExposures, preMeasureExposures, true);
    scene
        .getAsJsonObject("comparability")
        .getAsJsonObject("benchmarkSettings")
        .addProperty("measurement-order-index", Integer.toString(orderIndex));
  }

  private static String withCpuComparabilityValue(
      String json, String group, String field, String value) {
    JsonArray report = JsonParser.parseString(json).getAsJsonArray();
    report
        .get(0)
        .getAsJsonObject()
        .getAsJsonObject("comparability")
        .getAsJsonObject(group)
        .addProperty(field, value);
    return report.toString();
  }

  private static String withRenderingComparabilityValue(
      String json, int sceneIndex, String group, String field, String value) {
    JsonObject report = JsonParser.parseString(json).getAsJsonObject();
    JsonArray scenes = report.getAsJsonArray("scenes");
    int start = sceneIndex < 0 ? 0 : sceneIndex;
    int end = sceneIndex < 0 ? scenes.size() : sceneIndex + 1;
    for (int index = start; index < end; index++) {
      scenes
          .get(index)
          .getAsJsonObject()
          .getAsJsonObject("comparability")
          .getAsJsonObject(group)
          .addProperty(field, value);
    }
    return report.toString();
  }

  private static String cpuComparability(
      String semanticId, String label, String implementationRevision) {
    String settings =
        "\"benchmark-mode\":\"average-time\",\"forks\":\"2\",\"measurement-batch-size\":\"1\",\"measurement-iterations\":\"5\",\"measurement-time\":\"PT0.5S\",\"native-access\":\"all-unnamed\",\"output-time-unit\":\"microseconds\",\"profiler\":\"gc\",\"state-scope\":\"benchmark\",\"threads\":\"1\",\"warmup-batch-size\":\"1\",\"warmup-forks\":\"0\",\"warmup-iterations\":\"3\",\"warmup-time\":\"PT0.5S\"";
    return """
        {"fingerprintSchemaVersion":2,"benchmarkVersion":"benchmark-1","workloadVersion":"workload-1","resultSchemaVersion":"result-schema-2","behaviorContractVersion":"behavior-1","evidenceMode":"timed-allocation-diagnostics-disabled","semanticId":"%s","displayLabel":"%s","workloadContentSha256":"sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa","workloadShapeSha256":"sha256:bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb","fontInputsSha256":"sha256:cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc","environment":{"scope":"cpu","jvmVendor":"Vendor","jvmVersion":"25.0.1","osName":"OS","osVersion":"1","osArchitecture":"x64","cpuModel":"CPU model"},"benchmarkSettings":{%s},"implementation":{"implementationRevision":"%s","buildRevision":"build-1","commitRevision":"commit-1"}}
        """.formatted(semanticId, label, settings, implementationRevision).trim();
  }

  private static String legacyWarmupJson() {
    return renderingJson()
        .replace(
            "\"alternatingWarmupFrameCount\":30,\"validationExposureCount\":1,\"preMeasureExposureCount\":31",
            "\"warmupFrameCount\":60")
        .replace(
            "\"alternatingWarmupFrameCount\":30,\"validationExposureCount\":0,\"preMeasureExposureCount\":30",
            "\"warmupFrameCount\":60");
  }

  private static String withRunMetadata(
      String json, String runId, String artifact, String pairing, String evidenceMode) {
    JsonObject metadata =
        JsonParser.parseString(
                """
                {"schemaVersion":1,"runId":"%s","artifact":"%s","pairing":"%s","evidenceMode":"%s"}
                """.formatted(runId, artifact, pairing, evidenceMode))
            .getAsJsonObject();
    com.google.gson.JsonElement root;
    try {
      root = JsonParser.parseString(json);
    } catch (com.google.gson.JsonParseException failure) {
      return json;
    }
    if (artifact.equals("cpu")) {
      for (var entry : root.getAsJsonArray()) {
        entry.getAsJsonObject().add("benchmarkRun", metadata.deepCopy());
      }
    } else {
      root.getAsJsonObject().add("benchmarkRun", metadata);
    }
    return root.toString();
  }

  private static String timedMode() {
    return "timed-allocation-diagnostics-disabled";
  }

  private static String counterMode() {
    return "counter-only-diagnostics-enabled";
  }

  private static String changedImplementation(String json) {
    return json.replace("impl-1", "impl-2").replace("build-1", "build-2").replace("commit-1", "commit-2");
  }

  private static String withCpuParams(String json, String key, String value) {
    JsonArray report = JsonParser.parseString(json).getAsJsonArray();
    JsonObject params = new JsonObject();
    params.addProperty(key, value);
    report.get(0).getAsJsonObject().add("params", params);
    return report.toString();
  }

  private static String combineCpu(String first, String second) {
    return "[" + first.trim().substring(1, first.trim().length() - 1) + ","
        + second.trim().substring(1, second.trim().length() - 1) + "]";
  }

  private record FingerprintMismatch(
      String name, String cpuJson, String renderingJson, String reason) { }

  private record RawVariant(String name, String json) { }

  private record ArtifactPair(String cpu, String rendering) { }
}
