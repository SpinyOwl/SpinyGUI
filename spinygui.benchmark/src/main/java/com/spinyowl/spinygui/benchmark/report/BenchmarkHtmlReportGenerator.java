package com.spinyowl.spinygui.benchmark.report;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata.Artifact;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.benchmark.rendering.RenderingWorkloadSpecifications;
import com.spinyowl.spinygui.benchmark.rendering.StructuralValidationReport;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

/** Parses local benchmark JSON and renders its typed view through precompiled JTE templates. */
public final class BenchmarkHtmlReportGenerator {
  private static final double BUDGET_120_HZ_MICROS = 8_333;
  private static final RenderingWorkloadSpecifications.Specification RENDERING_SPECIFICATION =
      RenderingWorkloadSpecifications.CURRENT;
  private static final DateTimeFormatter RUN_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSSSSSSSS");
  private static final Pattern CPU_FILE = Pattern.compile("text-calculation-(\\d{8}-\\d{6}-\\d{9}(?:-\\d+)?)\\.json");
  private static final Pattern RENDERING_FILE = Pattern.compile("nanovg-text-(\\d{8}-\\d{6}-\\d{9}(?:-\\d+)?)\\.json");
  private static final Pattern DIAGNOSTIC_FILE = Pattern.compile("text-diagnostics-(\\d{8}-\\d{6}-\\d{9}(?:-\\d+)?)\\.json");
  private static final String MANIFEST_FILE = "report-manifest.json";
  private static final Gson REPORT_JSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

  private BenchmarkHtmlReportGenerator() {
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2 && args.length != 3) {
      throw new IllegalArgumentException(
          "Expected benchmark archive and HTML output paths, with an optional report-owned run ID");
    }
    Path archive = Path.of(args[0]);
    Path output = Path.of(args[1]);
    Files.createDirectories(output.getParent());
    ReportBundle bundle = args.length == 2 ? loadBundle(archive, null) : loadBundle(archive, args[2]);
    Files.writeString(output, render(bundle.view()));
    Files.writeString(output.resolveSibling(MANIFEST_FILE), manifestJson(bundle));
  }

  public static String generate(String cpuJson, String renderingJson) {
    BenchmarkReportView.ArchiveHealth health = new BenchmarkReportView.ArchiveHealth(1, 0, 0, List.of());
    BenchmarkReportView view = toView(parseCpu(cpuJson), parseRendering(renderingJson), "Current input",
        List.of(), List.of(), health);
    return render(view);
  }

  /** Loads complete archived pairs in chronological order and renders the newest pair as the current report. */
  public static String generateArchive(Path archive) throws IOException {
    return render(loadBundle(archive, null).view());
  }

  /** Generates the normalized, human-oriented archive manifest used beside the HTML report. */
  public static String generateManifest(Path archive) throws IOException {
    return manifestJson(loadBundle(archive, null));
  }

  /** Loads complete archived pairs in chronological order and selects the newest pair as current. */
  public static BenchmarkReportView loadArchive(Path archive) throws IOException {
    return loadBundle(archive, null).view();
  }

  /** Loads exactly the fresh report-owned pair; stale eligible archive pairs are not substituted. */
  public static BenchmarkReportView loadArchive(Path archive, String expectedRunIdentifier)
      throws IOException {
    return loadBundle(archive, expectedRunIdentifier).view();
  }

  private static String render(BenchmarkReportView view) {
    BenchmarkReportPage page = new BenchmarkReportPage(
        view,
        resource("chart.umd.min.js"),
        resource("benchmark-charts.js"),
        new GsonBuilder().serializeNulls().create().toJson(Map.of("chartPayloadVersion", 1, "charts", view.charts())));
    StringOutput output = new StringOutput();
    TemplateEngine.createPrecompiled(ContentType.Html).render("report.jte", page, output);
    return output.toString();
  }

  private static String manifestJson(ReportBundle bundle) {
    BenchmarkReportView view = bundle.view();
    ReportManifest manifest = new ReportManifest(
        1,
        bundle.currentTimestamp().toString(),
        view.currentRunIdentifier(),
        view.evidenceStatus(),
        view.buildStatus(),
        new ManifestSummary(
            view.structuralValidationStatus(),
            new ManifestMetric(view.slowestCpuName(), view.slowestCpuLatency(), "us/op"),
            new ManifestMetric(view.largestAllocationName(), view.largestAllocation(), "B/op"),
            new ManifestMetric("GPU p99", view.largestGpuP99(), "us"),
            view.largestGpuBudget120()),
        bundle.health());
    return REPORT_JSON.toJson(manifest) + System.lineSeparator();
  }

  private static String resource(String name) {
    try (InputStream stream = BenchmarkHtmlReportGenerator.class.getResourceAsStream(name)) {
      if (stream == null) throw new IllegalStateException("Missing benchmark report resource: " + name);
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new IllegalStateException("Missing benchmark report resource: " + name, exception);
    }
  }

  private static ReportBundle loadBundle(Path archive, String expectedRunIdentifier) throws IOException {
    ArchiveScan scan = scanArchive(archive);
    if (scan.runs().isEmpty()) {
      throw new IllegalArgumentException("Benchmark archive contains no complete valid run pairs");
    }
    ArchivedRun current = expectedRunIdentifier == null
        ? scan.runs().getLast()
        : scan.runs().stream()
            .filter(run -> run.identifier().equals(expectedRunIdentifier))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Benchmark archive contains no eligible complete pair for report-owned run ID: "
                    + expectedRunIdentifier));
    List<BenchmarkReportView.ChartTrend> trends = new ArrayList<>(cpuTrends(scan.runs()));
    trends.addAll(gpuTrends(scan.runs()));
    BenchmarkReportView view = toView(current.cpu(), current.rendering(), current.identifier(),
        history(scan.runs()), trends, scan.health());
    return new ReportBundle(view, current.timestamp(), scan.health());
  }

  private static ArchiveScan scanArchive(Path archive) throws IOException {
    if (!Files.isDirectory(archive)) {
      return new ArchiveScan(List.of(), new BenchmarkReportView.ArchiveHealth(0, 0, 0, List.of()));
    }
    Map<String, Path> cpuFiles = new HashMap<>();
    Map<String, Path> renderingFiles = new HashMap<>();
    Map<String, Path> diagnosticFiles = new HashMap<>();
    try (Stream<Path> files = Files.list(archive)) {
      files.filter(Files::isRegularFile).forEach(file -> {
        collectFile(file, CPU_FILE, cpuFiles);
        collectFile(file, RENDERING_FILE, renderingFiles);
        collectFile(file, DIAGNOSTIC_FILE, diagnosticFiles);
      });
    }
    List<ArchivedRun> runs = new ArrayList<>();
    List<BenchmarkReportView.ArchiveArtifact> artifacts = new ArrayList<>();
    Set<String> timingIdentifiers = new TreeSet<>();
    timingIdentifiers.addAll(cpuFiles.keySet());
    timingIdentifiers.addAll(renderingFiles.keySet());
    for (String identifierText : timingIdentifiers) {
      Path cpu = cpuFiles.get(identifierText);
      Path rendering = renderingFiles.get(identifierText);
      if (cpu == null) {
        artifacts.add(archiveArtifact(rendering, identifierText, "rendering", "excluded",
            "Missing CPU artifact with the same run ID"));
        continue;
      }
      if (rendering == null) {
        artifacts.add(archiveArtifact(cpu, identifierText, "cpu", "excluded",
            "Missing rendering artifact with the same run ID"));
        continue;
      }
      try {
        RunIdentifier identifier = parseIdentifier(identifierText);
        if (identifier == null) {
          artifacts.add(archiveArtifact(cpu, identifierText, "cpu", "excluded", "Invalid sortable run ID"));
          artifacts.add(archiveArtifact(rendering, identifierText, "rendering", "excluded", "Invalid sortable run ID"));
          continue;
        }
        List<CpuResult> cpuResults = parseCpu(Files.readString(cpu));
        RenderingResult renderingResult = parseRendering(Files.readString(rendering));
        if (!baselineEligiblePair(identifierText, cpuResults, renderingResult)) {
          artifacts.add(archiveArtifact(cpu, identifierText, "cpu", "excluded",
              "Pair is not eligible for timing/allocation history"));
          artifacts.add(archiveArtifact(rendering, identifierText, "rendering", "excluded",
              "Pair is not eligible for timing/allocation history"));
          continue;
        }
        runs.add(new ArchivedRun(identifierText, identifier.timestamp(), identifier.sequence(), cpuResults, renderingResult));
        artifacts.add(archiveArtifact(cpu, identifierText, "cpu", "included", "Eligible complete timing pair"));
        artifacts.add(archiveArtifact(rendering, identifierText, "rendering", "included", "Eligible complete timing pair"));
      } catch (RuntimeException failure) {
        String reason = "Invalid timing pair: " + failureReason(failure);
        artifacts.add(archiveArtifact(cpu, identifierText, "cpu", "excluded", reason));
        artifacts.add(archiveArtifact(rendering, identifierText, "rendering", "excluded", reason));
      }
    }
    for (Map.Entry<String, Path> diagnostic : diagnosticFiles.entrySet()) {
      artifacts.add(archiveArtifact(diagnostic.getValue(), diagnostic.getKey(), "diagnostics", "separate-evidence",
          "Counter diagnostics are not timing/allocation history"));
    }
    runs.sort(Comparator.comparing(ArchivedRun::timestamp).thenComparingInt(ArchivedRun::sequence));
    artifacts.sort(Comparator.comparing(BenchmarkReportView.ArchiveArtifact::fileName));
    int excludedTiming = (int) artifacts.stream()
        .filter(artifact -> artifact.status().equals("excluded") && !artifact.kind().equals("diagnostics"))
        .count();
    BenchmarkReportView.ArchiveHealth health = new BenchmarkReportView.ArchiveHealth(
        runs.size(), excludedTiming, diagnosticFiles.size(), List.copyOf(artifacts));
    return new ArchiveScan(List.copyOf(runs), health);
  }

  private static BenchmarkReportView.ArchiveArtifact archiveArtifact(Path file, String identifier,
      String kind, String status, String reason) {
    return new BenchmarkReportView.ArchiveArtifact(
        file.getFileName().toString(), identifier, kind, status, reason);
  }

  private static String failureReason(RuntimeException failure) {
    String message = failure.getMessage();
    return message == null || message.isBlank() ? failure.getClass().getSimpleName() : message;
  }

  private static void collectFile(Path file, Pattern pattern, Map<String, Path> target) {
    Matcher matcher = pattern.matcher(file.getFileName().toString());
    if (matcher.matches()) {
      try {
        if (parseIdentifier(matcher.group(1)) != null) target.put(matcher.group(1), file);
      } catch (RuntimeException ignored) {
        // Ignore files whose names are not valid sortable local datetime identifiers.
      }
    }
  }

  private static RunIdentifier parseIdentifier(String identifier) {
    int suffix = identifier.lastIndexOf('-');
    String datetime = suffix > 15 ? identifier.substring(0, suffix) : identifier;
    String sequence = suffix > 15 ? identifier.substring(suffix + 1) : "0";
    try {
      return new RunIdentifier(LocalDateTime.parse(datetime, RUN_ID_FORMAT), Integer.parseInt(sequence));
    } catch (RuntimeException ignored) {
      return null;
    }
  }

  private static List<CpuResult> parseCpu(String json) {
    JsonArray entries = JsonParser.parseString(json).getAsJsonArray();
    List<CpuResult> results = new ArrayList<>();
    for (JsonElement entry : entries) {
      JsonObject object = entry.getAsJsonObject();
      JsonObject primary = object.getAsJsonObject("primaryMetric");
      JsonObject metrics = object.getAsJsonObject("secondaryMetrics");
      results.add(
          new CpuResult(
              object.get("benchmark").getAsString().replaceFirst("^.*\\.", ""),
              primary.get("score").getAsDouble(),
              score(primary, "scoreError"),
              metrics.getAsJsonObject("gc.alloc.rate.norm").get("score").getAsDouble(),
              score(metrics.getAsJsonObject("gc.alloc.rate"), "score"),
              parameters(object),
              comparability(object),
              runMetadata(object)));
    }
    if (results.isEmpty()) throw new IllegalArgumentException("CPU report contains no benchmark results");
    return results;
  }

  private static RenderingResult parseRendering(String json) {
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    List<SceneResult> scenes = new ArrayList<>();
    JsonArray entries = root.getAsJsonArray("scenes");
    for (int index = 0; index < entries.size(); index++) {
      JsonElement entry = entries.get(index);
      JsonObject scene = entry.getAsJsonObject();
      WarmupMetadata warmup = warmupMetadata(scene);
      scenes.add(
          new SceneResult(
              index,
              scene.get("textFragmentCount").getAsInt(),
              scene.has("textNodeCount")
                  ? scene.get("textNodeCount").getAsInt()
                  : scene.get("textFragmentCount").getAsInt(),
              scene.get("textCodePointCount").getAsInt(),
              scene.get("resolvedGlyphCount").getAsInt(),
              scene.get("resolvedRunCount").getAsInt(),
              warmup,
              scene.get("measuredFrameCount").getAsInt(),
              latency(scene.getAsJsonObject("cpuSubmissionMicros")),
              latency(scene.getAsJsonObject("gpuCompleteMicros")),
              comparability(scene)));
    }
    if (scenes.isEmpty()) throw new IllegalArgumentException("Rendering report contains no scenes");
    return new RenderingResult(
        root.getAsJsonObject("environment"),
        StructuralValidationReport.fromJson(root.getAsJsonObject("structuralValidation")),
        scenes,
        runMetadata(root));
  }

  private static BenchmarkReportView toView(List<CpuResult> cpu, RenderingResult rendering, String currentRunIdentifier,
      List<BenchmarkReportView.HistoryRun> history, List<BenchmarkReportView.ChartTrend> trends,
      BenchmarkReportView.ArchiveHealth archiveHealth) {
    List<BenchmarkReportView.CpuRow> cpuRows = new ArrayList<>();
    List<BenchmarkReportView.CpuChartDatum> cpuChartData = new ArrayList<>();
    for (CpuResult result : cpu) {
      cpuRows.add(new BenchmarkReportView.CpuRow(
          result.displayLabel(), parameterMetadata(result.parameters()), comparability(result.comparability()),
          number(result.latency()), metric(result.error()), number(result.allocation()),
          metric(result.allocationRate())));
      cpuChartData.add(new BenchmarkReportView.CpuChartDatum(result.displayLabel(),
          finite("CPU latency: " + result.displayLabel(), result.latency()),
          finiteOrNull("CPU uncertainty: " + result.displayLabel(), result.error()),
          finite("CPU allocation: " + result.displayLabel(), result.allocation()),
          finiteOrNull("CPU allocation rate: " + result.displayLabel(), result.allocationRate())));
    }
    List<BenchmarkReportView.SceneRow> sceneRows = new ArrayList<>();
    List<BenchmarkReportView.RenderingChartDatum> renderingChartData = new ArrayList<>();
    for (SceneResult scene : rendering.scenes()) {
      String label = scene.displayLabel();
      sceneRows.add(new BenchmarkReportView.SceneRow(label, scene.evidence(),
          latencyText(scene.cpu()), latencyText(scene.gpu()), budgetText(scene.cpu()), budgetText(scene.gpu()),
          warmupText(scene.warmup()) + "; " + scene.measuredFrames() + " measured",
          comparability(scene.comparability())));
      renderingChartData.add(new BenchmarkReportView.RenderingChartDatum(label,
          finite("Rendering CPU median: " + label, scene.cpu().median()),
          finite("Rendering CPU p95: " + label, scene.cpu().p95()),
          finite("Rendering CPU p99: " + label, scene.cpu().p99()),
          finite("Rendering GPU median: " + label, scene.gpu().median()),
          finite("Rendering GPU p95: " + label, scene.gpu().p95()),
          finite("Rendering GPU p99: " + label, scene.gpu().p99())));
    }
    CpuResult slowest = cpu.stream().max(Comparator.comparingDouble(CpuResult::latency)).orElseThrow();
    CpuResult allocating = cpu.stream().max(Comparator.comparingDouble(CpuResult::allocation)).orElseThrow();
    SceneResult largestGpu = rendering.scenes().stream().max(Comparator.comparingDouble(scene -> scene.gpu().p99())).orElseThrow();
    List<BenchmarkReportView.EnvironmentValue> environment = new ArrayList<>();
    for (String key : List.of(
        "javaVersion", "javaVendor", "osName", "osVersion", "osArchitecture", "cpuModel",
        "glVendor", "glRenderer", "glDriverVersion", "glVersion")) {
      if (rendering.environment().has(key)) {
        environment.add(
            new BenchmarkReportView.EnvironmentValue(
                key, rendering.environment().get(key).getAsString()));
      }
    }
    List<BenchmarkReportView.MetadataValue> comparability = comparability(cpu, rendering);
    List<BenchmarkReportView.MetadataValue> implementation = implementation(cpu, rendering);
    String evidenceStatus = cpu.stream().allMatch(result -> result.comparability().available())
        && rendering.scenes().stream().allMatch(scene -> scene.comparability().available())
            ? "Complete identity and comparability metadata"
            : "Some results cannot be compared safely";
    String buildStatus = implementation.stream()
        .filter(entry -> entry.key().equals("implementationRevision"))
        .map(BenchmarkReportView.MetadataValue::value)
        .findFirst()
        .orElse("not reported");
    return new BenchmarkReportView(cpuRows, sceneRows, environment, comparability,
        implementation, evidenceStatus, buildStatus, archiveHealth,
        rendering.structuralValidation().status(), slowest.name(), number(slowest.latency()), allocating.name(), number(allocating.allocation()),
        fragmentCount(largestGpu.fragments()), number(largestGpu.gpu().p99()), percent(largestGpu.gpu().p99() / BUDGET_120_HZ_MICROS * 100),
        currentRunIdentifier, history, chartPayload(cpuChartData, renderingChartData, history, trends));
  }

  private static BenchmarkReportView.ChartPayload chartPayload(List<BenchmarkReportView.CpuChartDatum> cpu,
      List<BenchmarkReportView.RenderingChartDatum> rendering, List<BenchmarkReportView.HistoryRun> history,
      List<BenchmarkReportView.ChartTrend> trends) {
    List<String> historyRuns = history.stream().map(BenchmarkReportView.HistoryRun::identifier).toList();
    return new BenchmarkReportView.ChartPayload(cpu, rendering, historyRuns, trends);
  }

  private static List<BenchmarkReportView.ChartTrend> cpuTrends(List<ArchivedRun> runs) {
    Map<String, TrendSeries> values = new LinkedHashMap<>();
    for (int index = 0; index < runs.size(); index++) {
      ArchivedRun current = runs.get(index);
      Map<CpuResult, CpuResult> previous =
          index == 0 ? Map.of() : adjacentMatches(current.cpu(), runs.get(index - 1).cpu());
      for (CpuResult result : current.cpu()) {
        CpuResult prior = previous.get(result);
        values.compute(
                result.seriesKey(),
                (ignored, trend) ->
                    trend == null ? new TrendSeries(result.displayLabel()) : trend.withLabel(result.displayLabel()))
            .values()
            .add(
                new TrendValue(
                    index,
                    current.identifier(),
                    result.latency(),
                    change(
                        result.latency(),
                        prior == null ? null : prior.latency(),
                        result.comparability(),
                        prior == null ? null : prior.comparability())));
      }
    }
    return trendSeries(values, runs, "cpu", "CPU latency", "us/op");
  }

  private static List<BenchmarkReportView.ChartTrend> gpuTrends(List<ArchivedRun> runs) {
    Map<String, TrendSeries> values = new LinkedHashMap<>();
    for (int index = 0; index < runs.size(); index++) {
      ArchivedRun current = runs.get(index);
      Map<SceneResult, SceneResult> previous =
          index == 0
              ? Map.of()
              : adjacentMatches(current.rendering().scenes(), runs.get(index - 1).rendering().scenes());
      for (SceneResult scene : current.rendering().scenes()) {
        SceneResult prior = previous.get(scene);
        values.compute(
                scene.seriesKey(),
                (ignored, trend) ->
                    trend == null ? new TrendSeries(scene.displayLabel()) : trend.withLabel(scene.displayLabel()))
            .values()
            .add(
                new TrendValue(
                    index,
                    current.identifier(),
                    scene.gpu().p99(),
                    change(
                        scene.gpu().p99(),
                        prior == null ? null : prior.gpu().p99(),
                        scene.comparability(),
                        prior == null ? null : prior.comparability())));
      }
    }
    return trendSeries(values, runs, "gpu", "GPU p99", "us");
  }

  private static List<BenchmarkReportView.ChartTrend> trendSeries(Map<String, TrendSeries> values, List<ArchivedRun> runs,
      String prefix, String metric, String unit) {
    List<BenchmarkReportView.ChartTrend> series = new ArrayList<>();
    int seriesNumber = 1;
    for (TrendSeries trend : values.values()) {
      List<TrendValue> source = trend.values();
      double observedMinimum = source.stream().mapToDouble(TrendValue::value).min().orElse(0);
      double observedMaximum = source.stream().mapToDouble(TrendValue::value).max().orElse(0);
      double padding = observedMaximum == observedMinimum ? Math.max(Math.abs(observedMaximum) * .1, 1) : (observedMaximum - observedMinimum) * .1;
      double minimum = observedMinimum - padding;
      double maximum = observedMaximum + padding;
      List<Double> trendValues = new ArrayList<>();
      List<String> changes = new ArrayList<>();
      for (int index = 0; index < runs.size(); index++) {
        trendValues.add(null);
        changes.add(null);
      }
      for (TrendValue value : source) {
        trendValues.set(value.index(), finite(metric + ": " + trend.label(), value.value()));
        changes.set(value.index(), value.change());
      }
      String label = metric + ": " + trend.label();
      series.add(new BenchmarkReportView.ChartTrend(prefix + "-trend-" + seriesNumber++, label, unit,
          finite(label + " minimum", minimum), finite(label + " maximum", maximum), trendValues, changes));
    }
    return series;
  }

  private static List<BenchmarkReportView.HistoryRun> history(List<ArchivedRun> runs) {
    List<BenchmarkReportView.HistoryRun> history = new ArrayList<>();
    ArchivedRun previous = null;
    for (ArchivedRun current : runs) {
      Map<CpuResult, CpuResult> previousCpu =
          previous == null ? Map.of() : adjacentMatches(current.cpu(), previous.cpu());
      Map<SceneResult, SceneResult> previousScenes =
          previous == null
              ? Map.of()
              : adjacentMatches(current.rendering().scenes(), previous.rendering().scenes());
      List<BenchmarkReportView.CpuHistoryRow> cpuRows = new ArrayList<>();
      for (CpuResult result : current.cpu()) {
        CpuResult prior = previousCpu.get(result);
        cpuRows.add(new BenchmarkReportView.CpuHistoryRow(result.displayLabel(), parameterMetadata(result.parameters()),
            number(result.latency()), number(result.allocation()),
            change(result.latency(), prior == null ? null : prior.latency(), result.comparability(),
                prior == null ? null : prior.comparability()),
            change(result.allocation(), prior == null ? null : prior.allocation(), result.comparability(),
                prior == null ? null : prior.comparability())));
      }
      List<BenchmarkReportView.SceneHistoryRow> sceneRows = new ArrayList<>();
      for (SceneResult scene : current.rendering().scenes()) {
        SceneResult prior = previousScenes.get(scene);
        sceneRows.add(new BenchmarkReportView.SceneHistoryRow(
            scene.displayLabel(), scene.evidence(), latencyText(scene.cpu()),
            latencyText(scene.gpu()), latencyChange(scene.cpu(), prior == null ? null : prior.cpu(), scene.comparability(),
                prior == null ? null : prior.comparability()),
            latencyChange(scene.gpu(), prior == null ? null : prior.gpu(), scene.comparability(),
                prior == null ? null : prior.comparability()), percent(scene.cpu().budget120()),
            percent(scene.gpu().budget120()), change(scene.cpu().budget120(), prior == null ? null : prior.cpu().budget120(),
                scene.comparability(), prior == null ? null : prior.comparability()),
            change(scene.gpu().budget120(), prior == null ? null : prior.gpu().budget120(), scene.comparability(),
                prior == null ? null : prior.comparability())));
      }
      history.add(new BenchmarkReportView.HistoryRun(
          current.identifier(), comparability(current.cpu(), current.rendering()),
          implementation(current.cpu(), current.rendering()), cpuRows, sceneRows));
      previous = current;
    }
    return history;
  }

  private static <T extends ComparisonCandidate> Map<T, T> adjacentMatches(
      List<T> current, List<T> previous) {
    Map<T, T> matches = new IdentityHashMap<>();
    Set<T> matchedPrevious = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
    Map<String, List<T>> previousBySemanticId = new HashMap<>();
    for (T candidate : previous) {
      String semanticId = candidate.comparability().semanticIdOrNull();
      if (semanticId != null) {
        previousBySemanticId
            .computeIfAbsent(semanticId, ignored -> new ArrayList<>())
            .add(candidate);
      }
    }
    for (T candidate : current) {
      String semanticId = candidate.comparability().semanticIdOrNull();
      List<T> exact = semanticId == null ? List.of() : previousBySemanticId.getOrDefault(semanticId, List.of());
      if (exact.size() == 1 && matchedPrevious.add(exact.getFirst())) {
        matches.put(candidate, exact.getFirst());
      }
    }

    Map<String, List<T>> unmatchedCurrent = unmatchedByLogicalKey(current, matches.keySet());
    Map<String, List<T>> unmatchedPrevious = unmatchedByLogicalKey(previous, matchedPrevious);
    for (Map.Entry<String, List<T>> entry : unmatchedCurrent.entrySet()) {
      List<T> currentGroup = entry.getValue();
      List<T> previousGroup = unmatchedPrevious.getOrDefault(entry.getKey(), List.of());
      if (currentGroup.size() == 1
          && previousGroup.size() == 1
          && (!currentGroup.getFirst().comparability().available()
              || !previousGroup.getFirst().comparability().available())) {
        matches.put(currentGroup.getFirst(), previousGroup.getFirst());
      }
    }
    return matches;
  }

  private static <T extends ComparisonCandidate> Map<String, List<T>> unmatchedByLogicalKey(
      List<T> candidates, Set<T> matched) {
    Map<String, List<T>> byLogicalKey = new HashMap<>();
    for (T candidate : candidates) {
      if (!matched.contains(candidate)) {
        byLogicalKey
            .computeIfAbsent(candidate.logicalKey(), ignored -> new ArrayList<>())
            .add(candidate);
      }
    }
    return byLogicalKey;
  }

  private static List<BenchmarkReportView.MetadataValue> implementation(
      List<CpuResult> cpu, RenderingResult rendering) {
    Map<String, Set<String>> values = new TreeMap<>();
    for (CpuResult result : cpu) addImplementation(values, result.comparability());
    for (SceneResult scene : rendering.scenes()) addImplementation(values, scene.comparability());
    if (values.isEmpty()) {
      return List.of(new BenchmarkReportView.MetadataValue(
          "status", "not reported (legacy or invalid comparability metadata)"));
    }
    List<BenchmarkReportView.MetadataValue> result = new ArrayList<>();
    for (Map.Entry<String, Set<String>> entry : values.entrySet()) {
      result.add(new BenchmarkReportView.MetadataValue(entry.getKey(), String.join(" | ", entry.getValue())));
    }
    return List.copyOf(result);
  }

  private static List<BenchmarkReportView.MetadataValue> comparability(
      List<CpuResult> cpu, RenderingResult rendering) {
    Map<String, Set<String>> values = new TreeMap<>();
    for (CpuResult result : cpu) addComparability(values, result.comparability());
    for (SceneResult scene : rendering.scenes()) addComparability(values, scene.comparability());
    return metadata(values);
  }

  private static List<BenchmarkReportView.MetadataValue> comparability(
      ResultComparability comparability) {
    Map<String, Set<String>> values = new TreeMap<>();
    addComparability(values, comparability);
    return metadata(values);
  }

  private static void addComparability(
      Map<String, Set<String>> values, ResultComparability state) {
    if (state.metadata() == null) {
      addMetadata(values, "status", "not comparable: " + state.unavailableReason());
      return;
    }
    ComparabilityMetadata metadata = state.metadata();
    ComparabilityMetadata.Fingerprints fingerprints = metadata.fingerprints();
    addMetadata(values, "semanticId", metadata.semanticId());
    addMetadata(values, "evidenceMode", metadata.evidenceMode().json());
    addMetadata(values, "benchmarkVersion", metadata.benchmarkVersion());
    addMetadata(values, "workloadVersion", metadata.workloadVersion());
    addMetadata(values, "resultSchemaVersion", metadata.resultSchemaVersion());
    addMetadata(values, "behaviorContractVersion", metadata.behaviorContractVersion());
    addMetadata(values, "identityFingerprint", fingerprints.identity());
    addMetadata(values, "workloadFingerprint", fingerprints.workload());
    addMetadata(values, "environmentFingerprint", fingerprints.environment());
    addMetadata(values, "settingsFingerprint", fingerprints.settings());
    addMetadata(values, "requiredFingerprint", fingerprints.required());
  }

  private static void addMetadata(
      Map<String, Set<String>> values, String key, String value) {
    values.computeIfAbsent(key, ignored -> new TreeSet<>()).add(value);
  }

  private static List<BenchmarkReportView.MetadataValue> metadata(
      Map<String, ? extends Iterable<String>> values) {
    List<BenchmarkReportView.MetadataValue> result = new ArrayList<>();
    for (Map.Entry<String, ? extends Iterable<String>> entry : values.entrySet()) {
      List<String> entries = new ArrayList<>();
      entry.getValue().forEach(entries::add);
      result.add(new BenchmarkReportView.MetadataValue(entry.getKey(), String.join(" | ", entries)));
    }
    return List.copyOf(result);
  }

  private static List<BenchmarkReportView.MetadataValue> parameterMetadata(Map<String, String> values) {
    return values.entrySet().stream()
        .map(entry -> new BenchmarkReportView.MetadataValue(entry.getKey(), entry.getValue()))
        .toList();
  }

  private static Map<String, String> parameters(JsonObject result) {
    if (!result.has("params") || result.get("params").isJsonNull()) return Map.of();
    JsonElement element = result.get("params");
    if (!element.isJsonObject()) {
      throw new IllegalArgumentException("CPU benchmark params must be a JSON object");
    }
    Map<String, String> parameters = new TreeMap<>();
    for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
      JsonElement value = entry.getValue();
      if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
        throw new IllegalArgumentException("CPU benchmark param must be a JSON string: " + entry.getKey());
      }
      parameters.put(entry.getKey(), value.getAsString());
    }
    return Map.copyOf(parameters);
  }

  private static void addImplementation(Map<String, Set<String>> values, ResultComparability comparability) {
    if (comparability.metadata() == null) {
      values
          .computeIfAbsent("comparabilityStatus", ignored -> new TreeSet<>())
          .add("not comparable: " + comparability.unavailableReason());
      return;
    }
    ComparabilityMetadata.Implementation implementation = comparability.metadata().implementation();
    values.computeIfAbsent("buildRevision", ignored -> new TreeSet<>()).add(implementation.buildRevision());
    values.computeIfAbsent("commitRevision", ignored -> new TreeSet<>()).add(implementation.commitRevision());
    values.computeIfAbsent("implementationRevision", ignored -> new TreeSet<>())
        .add(implementation.implementationRevision());
  }

  private static ResultComparability comparability(JsonObject result) {
    if (!result.has("comparability") || result.get("comparability").isJsonNull()) {
      return new ResultComparability(null, "required comparability metadata missing");
    }
    try {
      return new ResultComparability(
          ComparabilityMetadata.fromJson(result.getAsJsonObject("comparability")), null);
    } catch (RuntimeException failure) {
      return new ResultComparability(null, "invalid comparability metadata: " + failure.getMessage());
    }
  }

  private static RunMetadataState runMetadata(JsonObject artifact) {
    if (!artifact.has("benchmarkRun") || artifact.get("benchmarkRun").isJsonNull()) {
      return new RunMetadataState(null, "benchmark run metadata missing");
    }
    try {
      return new RunMetadataState(
          BenchmarkRunMetadata.fromJson(artifact.getAsJsonObject("benchmarkRun")), null);
    } catch (RuntimeException failure) {
      return new RunMetadataState(null, "invalid benchmark run metadata: " + failure.getMessage());
    }
  }

  private static WarmupMetadata warmupMetadata(JsonObject scene) {
    boolean hasAlternating = scene.has("alternatingWarmupFrameCount");
    boolean hasValidation = scene.has("validationExposureCount");
    boolean hasPreMeasure = scene.has("preMeasureExposureCount");
    boolean hasAnyCorrected = hasAlternating || hasValidation || hasPreMeasure;
    boolean hasAllCorrected = hasAlternating && hasValidation && hasPreMeasure;
    if (hasAnyCorrected != hasAllCorrected || (hasAllCorrected && scene.has("warmupFrameCount"))) {
      throw new IllegalArgumentException("Rendering scene mixes incomplete warmup metadata schemas");
    }
    if (hasAllCorrected) {
      int alternating = scene.get("alternatingWarmupFrameCount").getAsInt();
      int validation = scene.get("validationExposureCount").getAsInt();
      int preMeasure = scene.get("preMeasureExposureCount").getAsInt();
      if (alternating < 0 || validation < 0 || preMeasure != alternating + validation) {
        throw new IllegalArgumentException("Rendering pre-measure exposure metadata is inconsistent");
      }
      return new WarmupMetadata(alternating, validation, preMeasure, true);
    }
    if (scene.has("warmupFrameCount")) {
      int legacy = scene.get("warmupFrameCount").getAsInt();
      return new WarmupMetadata(legacy, 0, legacy, false);
    }
    throw new IllegalArgumentException("Rendering scene contains no recognized warmup metadata");
  }

  private static boolean baselineEligiblePair(
      String identifier, List<CpuResult> cpu, RenderingResult rendering) {
    BenchmarkRunMetadata renderingRun = rendering.runMetadata().metadata();
    if (renderingRun == null
        || renderingRun.artifact() != Artifact.RENDERING
        || !renderingRun.baselineEligible()
        || !renderingRun.runId().equals(identifier)
        || rendering.scenes().isEmpty()
        || rendering.scenes().stream().anyMatch(scene -> !scene.warmup().corrected())
        || !internallyConsistentRenderingProfile(rendering.scenes())) {
      return false;
    }
    BenchmarkRunMetadata cpuRun = cpu.getFirst().runMetadata().metadata();
    if (cpuRun == null
        || cpuRun.artifact() != Artifact.CPU
        || !cpuRun.baselineEligible()
        || !cpuRun.runId().equals(identifier)
        || !cpuRun.runId().equals(renderingRun.runId())
        || cpuRun.evidenceMode() != renderingRun.evidenceMode()) {
      return false;
    }
    ComparabilityMetadata cpuPairMetadata = cpu.getFirst().comparability().metadata();
    ComparabilityMetadata renderingPairMetadata =
        rendering.scenes().getFirst().comparability().metadata();
    if (cpuPairMetadata == null || renderingPairMetadata == null) {
      return false;
    }
    boolean cpuConsistent =
        cpu.stream()
            .allMatch(
                result ->
                    cpuRun.equals(result.runMetadata().metadata())
                        && evidenceModeMatches(result.comparability(), cpuRun)
                        && result.comparability().metadata().environment().scope()
                            == ComparabilityMetadata.Scope.CPU
                        && result.comparability().metadata().environment()
                            .equals(cpuPairMetadata.environment())
                        && result.comparability().metadata().implementation()
                            .equals(cpuPairMetadata.implementation()));
    if (!cpuConsistent) return false;

    for (SceneResult scene : rendering.scenes()) {
      ComparabilityMetadata metadata = scene.comparability().metadata();
      if (!evidenceModeMatches(scene.comparability(), renderingRun)
          || metadata.environment().scope() != ComparabilityMetadata.Scope.RENDERING
          || !metadata.environment().equals(renderingPairMetadata.environment())
          || !metadata.implementation().equals(renderingPairMetadata.implementation())) {
        return false;
      }
    }
    if (usesCurrentProducerProfile(rendering.scenes())
        && !currentProducerProfileMatches(rendering.scenes())) {
      return false;
    }
    return applicableEnvironmentMatches(
            cpuPairMetadata.environment(), renderingPairMetadata.environment())
        && cpuPairMetadata.implementation().equals(renderingPairMetadata.implementation());
  }

  private static boolean evidenceModeMatches(
      ResultComparability comparability, BenchmarkRunMetadata runMetadata) {
    return comparability.metadata() != null
        && comparability.metadata().evidenceMode() == runMetadata.evidenceMode();
  }

  private static boolean internallyConsistentRenderingProfile(List<SceneResult> scenes) {
    Map<String, String> firstSettings = scenes.getFirst().comparability().metadata().benchmarkSettings();
    int alternatingFrames = 0;
    for (int index = 0; index < scenes.size(); index++) {
      SceneResult scene = scenes.get(index);
      ComparabilityMetadata metadata = scene.comparability().metadata();
      if (metadata == null || scene.logicalIndex() != index) return false;
      Map<String, String> settings = metadata.benchmarkSettings();
      WarmupMetadata warmup = scene.warmup();
      if (positiveSetting(settings, "measurement-order-index") != index + 1
          || positiveSetting(settings, "alternating-warmup-frames-scene")
              != warmup.alternatingFrames()
          || nonNegativeSetting(settings, "validation-exposures-scene")
              != warmup.validationExposures()
          || nonNegativeSetting(settings, "premeasure-exposures-scene")
              != warmup.preMeasureExposures()
          || positiveSetting(settings, "measured-frames") != scene.measuredFrames()
          || !commonRenderingSettingsMatch(firstSettings, settings)) {
        return false;
      }
      alternatingFrames += warmup.alternatingFrames();
    }
    return positiveSetting(firstSettings, "alternating-warmup-frames-pair") == alternatingFrames;
  }

  private static boolean usesCurrentProducerProfile(List<SceneResult> scenes) {
    if (scenes.size() != RENDERING_SPECIFICATION.measurementOrder().size()) return false;
    Set<String> expected = new java.util.HashSet<>();
    for (RenderingWorkloadSpecifications.SceneSpecification scene :
        RENDERING_SPECIFICATION.measurementOrder()) {
      expected.add(RENDERING_SPECIFICATION.identity(scene).semanticId());
    }
    Set<String> actual = new java.util.HashSet<>();
    for (SceneResult scene : scenes) actual.add(scene.comparability().metadata().semanticId());
    return actual.equals(expected);
  }

  private static boolean currentProducerProfileMatches(List<SceneResult> scenes) {
    for (int index = 0; index < scenes.size(); index++) {
      SceneResult scene = scenes.get(index);
      RenderingWorkloadSpecifications.SceneSpecification expected =
          RENDERING_SPECIFICATION.measurementOrder().get(index);
      if (!scene.comparability().metadata().semanticId()
              .equals(RENDERING_SPECIFICATION.identity(expected).semanticId())
          || !scene.comparability().metadata().benchmarkSettings()
              .equals(RENDERING_SPECIFICATION.executionSettings(expected))
          || scene.warmup().alternatingFrames()
              != RENDERING_SPECIFICATION.alternatingWarmupFrames(expected)
          || scene.warmup().validationExposures()
              != RENDERING_SPECIFICATION.validationExposures(expected)
          || scene.warmup().preMeasureExposures()
              != RENDERING_SPECIFICATION.preMeasureExposures(expected)
          || scene.measuredFrames() != RENDERING_SPECIFICATION.measuredFrames()) {
        return false;
      }
    }
    return true;
  }

  private static boolean commonRenderingSettingsMatch(
      Map<String, String> first, Map<String, String> current) {
    for (Map.Entry<String, String> entry : first.entrySet()) {
      if (!Set.of(
              "alternating-warmup-frames-scene",
              "measurement-order-index",
              "premeasure-exposures-scene",
              "validation-exposures-scene")
          .contains(entry.getKey())
          && !entry.getValue().equals(current.get(entry.getKey()))) {
        return false;
      }
    }
    return true;
  }

  private static int positiveSetting(Map<String, String> settings, String key) {
    int value = integerSetting(settings, key);
    if (value <= 0) throw new IllegalArgumentException("Rendering setting must be positive: " + key);
    return value;
  }

  private static int nonNegativeSetting(Map<String, String> settings, String key) {
    int value = integerSetting(settings, key);
    if (value < 0) throw new IllegalArgumentException("Rendering setting must be non-negative: " + key);
    return value;
  }

  private static int integerSetting(Map<String, String> settings, String key) {
    try {
      return Integer.parseInt(settings.get(key));
    } catch (RuntimeException failure) {
      throw new IllegalArgumentException("Rendering setting must be an integer: " + key, failure);
    }
  }

  private static boolean applicableEnvironmentMatches(
      ComparabilityMetadata.Environment cpu,
      ComparabilityMetadata.Environment rendering) {
    return cpu.scope() == ComparabilityMetadata.Scope.CPU
        && rendering.scope() == ComparabilityMetadata.Scope.RENDERING
        && cpu.jvmVendor().equals(rendering.jvmVendor())
        && cpu.jvmVersion().equals(rendering.jvmVersion())
        && cpu.osName().equals(rendering.osName())
        && cpu.osVersion().equals(rendering.osVersion())
        && cpu.osArchitecture().equals(rendering.osArchitecture())
        && cpu.cpuModel().equals(rendering.cpuModel());
  }

  private static String warmupText(WarmupMetadata warmup) {
    if (!warmup.corrected()) return warmup.alternatingFrames() + " warmup (legacy metadata)";
    return warmup.alternatingFrames()
        + " alternating warmup + "
        + warmup.validationExposures()
        + " synchronized validation = "
        + warmup.preMeasureExposures()
        + " pre-measure exposures";
  }

  private static String change(
      double value,
      Double previous,
      ResultComparability comparability,
      ResultComparability previousComparability) {
    if (previous == null) return "not available";
    ComparabilityMetadata.Comparison comparison = comparability.compare(previousComparability);
    if (!comparison.comparable()) return "not comparable: " + comparison.reason();
    if (previous == 0) return "not available";
    return signedChange(value, previous);
  }

  private static String signedChange(double value, double previous) {
    double difference = (value - previous) / previous * 100;
    return (difference >= 0 ? "+" : "") + percent(difference);
  }

  private static String latencyChange(
      Latency value,
      Latency previous,
      ResultComparability comparability,
      ResultComparability previousComparability) {
    if (previous == null) return "not available";
    ComparabilityMetadata.Comparison comparison = comparability.compare(previousComparability);
    if (!comparison.comparable()) return "not comparable: " + comparison.reason();
    if (previous.median() == 0 || previous.p95() == 0 || previous.p99() == 0) {
      return "not available";
    }
    return signedChange(value.median(), previous.median()) + " / "
        + signedChange(value.p95(), previous.p95()) + " / "
        + signedChange(value.p99(), previous.p99());
  }

  private static Latency latency(JsonObject value) {
    return new Latency(value.get("median").getAsDouble(), value.get("p95").getAsDouble(),
        value.get("p99").getAsDouble(), value.get("budget60HzPercent").getAsDouble(),
        value.get("budget120HzPercent").getAsDouble());
  }

  private static Double score(JsonObject value, String key) {
    return value != null && value.has(key) ? value.get(key).getAsDouble() : null;
  }

  private static String number(double value) {
    return String.format(Locale.ROOT, "%,.3f", value);
  }

  private static double finite(String metric, double value) {
    if (!Double.isFinite(value)) throw new IllegalArgumentException("Non-finite benchmark chart value: " + metric);
    return value;
  }

  private static Double finiteOrNull(String metric, Double value) {
    return value == null ? null : finite(metric, value);
  }

  private static String metric(Double value) {
    return value == null || !Double.isFinite(value) ? "not reported" : number(value);
  }

  private static String percent(double value) {
    return number(value) + "%";
  }

  private static String fragmentCount(int value) {
    return String.format(Locale.ROOT, "%,d", value);
  }

  private static String latencyText(Latency value) {
    return number(value.median()) + " / " + number(value.p95()) + " / " + number(value.p99());
  }

  private static String budgetText(Latency value) {
    return percent(value.budget60()) + " / " + percent(value.budget120());
  }

  private record CpuResult(
      String name,
      double latency,
      Double error,
      double allocation,
      Double allocationRate,
      Map<String, String> parameters,
      ResultComparability comparability,
      RunMetadataState runMetadata) implements ComparisonCandidate {
    String seriesKey() {
      return comparability.seriesKey("legacy-cpu:" + name + ":params=" + parameters);
    }

    String displayLabel() {
      return comparability.metadata() == null ? "Legacy CPU operation " + name : comparability.metadata().displayLabel();
    }

    @Override
    public String logicalKey() {
      return "cpu:" + name + ":" + parameters;
    }
  }

  private record RenderingResult(
      JsonObject environment,
      StructuralValidationReport structuralValidation,
      List<SceneResult> scenes,
      RunMetadataState runMetadata) {
  }

  private record SceneResult(
      int logicalIndex, int fragments, int nodes, int codePoints, int glyphs, int runs,
      WarmupMetadata warmup, int measuredFrames, Latency cpu, Latency gpu,
      ResultComparability comparability) implements ComparisonCandidate {
    String seriesKey() {
      return comparability.seriesKey("legacy-rendering:" + logicalIndex);
    }

    String displayLabel() {
      if (comparability.metadata() != null) return comparability.metadata().displayLabel();
      return "Legacy rendering scene " + (logicalIndex + 1);
    }

    List<BenchmarkReportView.MetadataValue> evidence() {
      return List.of(
          new BenchmarkReportView.MetadataValue("fragments", fragmentCount(fragments)),
          new BenchmarkReportView.MetadataValue("nodes", fragmentCount(nodes)),
          new BenchmarkReportView.MetadataValue("codePoints", fragmentCount(codePoints)),
          new BenchmarkReportView.MetadataValue("glyphs", fragmentCount(glyphs)),
          new BenchmarkReportView.MetadataValue("runs", fragmentCount(runs)));
    }

    @Override
    public String logicalKey() {
      return "rendering-scene:" + logicalIndex;
    }
  }

  private record WarmupMetadata(
      int alternatingFrames,
      int validationExposures,
      int preMeasureExposures,
      boolean corrected) {
  }

  private record Latency(double median, double p95, double p99, double budget60, double budget120) {
  }

  private record ArchiveScan(List<ArchivedRun> runs, BenchmarkReportView.ArchiveHealth health) {
  }

  private record ReportBundle(BenchmarkReportView view, LocalDateTime currentTimestamp,
      BenchmarkReportView.ArchiveHealth health) {
  }

  private record ReportManifest(int schemaVersion, String generatedAtLocal, String currentRunIdentifier,
      String evidenceStatus, String buildStatus, ManifestSummary currentSummary,
      BenchmarkReportView.ArchiveHealth archive) {
  }

  private record ManifestSummary(String structuralValidation, ManifestMetric slowestCpuOperation,
      ManifestMetric largestAllocation, ManifestMetric largestGpuP99, String largestGpuBudget120) {
  }

  private record ManifestMetric(String label, String value, String unit) {
  }

  private record ArchivedRun(String identifier, LocalDateTime timestamp, int sequence, List<CpuResult> cpu, RenderingResult rendering) {
  }

  private record RunIdentifier(LocalDateTime timestamp, int sequence) {
  }

  private record TrendValue(int index, String identifier, double value, String change) {
  }

  private record TrendSeries(String label, List<TrendValue> values) {
    TrendSeries(String label) {
      this(label, new ArrayList<>());
    }

    TrendSeries withLabel(String label) {
      return new TrendSeries(label, values);
    }
  }

  private interface ComparisonCandidate {
    String logicalKey();

    ResultComparability comparability();
  }

  private record ResultComparability(ComparabilityMetadata metadata, String unavailableReason) {
    ResultComparability {
      if ((metadata == null) == (unavailableReason == null)) {
        throw new IllegalArgumentException("Comparability metadata or an unavailable reason is required");
      }
    }

    String seriesKey(String legacyKey) {
      return metadata == null ? legacyKey : metadata.semanticId();
    }

    boolean available() {
      return metadata != null;
    }

    String semanticIdOrNull() {
      return metadata == null ? null : metadata.semanticId();
    }

    ComparabilityMetadata.Comparison compare(ResultComparability previous) {
      if (metadata == null) {
        return new ComparabilityMetadata.Comparison(false, List.of(unavailableReason));
      }
      if (previous == null || previous.metadata == null) {
        String reason = previous == null ? "previous comparability metadata missing" : previous.unavailableReason;
        return new ComparabilityMetadata.Comparison(false, List.of(reason));
      }
      return metadata.compare(previous.metadata);
    }
  }

  private record RunMetadataState(BenchmarkRunMetadata metadata, String unavailableReason) {
    RunMetadataState {
      if ((metadata == null) == (unavailableReason == null)) {
        throw new IllegalArgumentException("Run metadata or an unavailable reason is required");
      }
    }
  }
}
