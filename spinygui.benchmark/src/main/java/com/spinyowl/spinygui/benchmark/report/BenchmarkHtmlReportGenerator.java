package com.spinyowl.spinygui.benchmark.report;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.GsonBuilder;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

/** Parses local benchmark JSON and renders its typed view through precompiled JTE templates. */
public final class BenchmarkHtmlReportGenerator {
  private static final double BUDGET_120_HZ_MICROS = 8_333;
  private static final DateTimeFormatter RUN_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSSSSSSSS");
  private static final Pattern CPU_FILE = Pattern.compile("text-calculation-(\\d{8}-\\d{6}-\\d{9}(?:-\\d+)?)\\.json");
  private static final Pattern RENDERING_FILE = Pattern.compile("nanovg-text-(\\d{8}-\\d{6}-\\d{9}(?:-\\d+)?)\\.json");

  private BenchmarkHtmlReportGenerator() {
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      throw new IllegalArgumentException("Expected benchmark archive and HTML output paths");
    }
    Path output = Path.of(args[1]);
    Files.createDirectories(output.getParent());
    Files.writeString(output, generateArchive(Path.of(args[0])));
  }

  public static String generate(String cpuJson, String renderingJson) {
    BenchmarkReportView view = toView(parseCpu(cpuJson), parseRendering(renderingJson), "Current input", List.of(), List.of());
    return render(view);
  }

  /** Loads complete archived pairs in chronological order and renders the newest pair as the current report. */
  public static String generateArchive(Path archive) throws IOException {
    return render(loadArchive(archive));
  }

  /** Loads complete archived pairs in chronological order and selects the newest pair as current. */
  public static BenchmarkReportView loadArchive(Path archive) throws IOException {
    List<ArchivedRun> runs = findCompleteRuns(archive);
    if (runs.isEmpty()) throw new IllegalArgumentException("Benchmark archive contains no complete valid run pairs");
    ArchivedRun current = runs.getLast();
    List<BenchmarkReportView.ChartTrend> trends = new ArrayList<>(cpuTrends(runs));
    trends.addAll(gpuTrends(runs));
    return toView(current.cpu(), current.rendering(), current.identifier(), history(runs), trends);
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

  private static String resource(String name) {
    try (InputStream stream = BenchmarkHtmlReportGenerator.class.getResourceAsStream(name)) {
      if (stream == null) throw new IllegalStateException("Missing benchmark report resource: " + name);
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new IllegalStateException("Missing benchmark report resource: " + name, exception);
    }
  }

  private static List<ArchivedRun> findCompleteRuns(Path archive) throws IOException {
    if (!Files.isDirectory(archive)) return List.of();
    Map<String, Path> cpuFiles = new HashMap<>();
    Map<String, Path> renderingFiles = new HashMap<>();
    try (Stream<Path> files = Files.list(archive)) {
      files.filter(Files::isRegularFile).forEach(file -> collectFile(file, CPU_FILE, cpuFiles));
    }
    try (Stream<Path> files = Files.list(archive)) {
      files.filter(Files::isRegularFile).forEach(file -> collectFile(file, RENDERING_FILE, renderingFiles));
    }
    List<ArchivedRun> runs = new ArrayList<>();
    for (Map.Entry<String, Path> cpu : cpuFiles.entrySet()) {
      Path rendering = renderingFiles.get(cpu.getKey());
      if (rendering == null) continue;
      try {
        RunIdentifier identifier = parseIdentifier(cpu.getKey());
        if (identifier == null) continue;
        runs.add(new ArchivedRun(cpu.getKey(), identifier.timestamp(), identifier.sequence(),
            parseCpu(Files.readString(cpu.getValue())), parseRendering(Files.readString(rendering))));
      } catch (RuntimeException ignored) {
        // Ignore malformed pairs so a partial local run cannot prevent report regeneration.
      }
    }
    runs.sort(Comparator.comparing(ArchivedRun::timestamp).thenComparingInt(ArchivedRun::sequence));
    return runs;
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
      results.add(new CpuResult(object.get("benchmark").getAsString().replaceFirst("^.*\\.", ""),
          primary.get("score").getAsDouble(), score(primary, "scoreError"),
          metrics.getAsJsonObject("gc.alloc.rate.norm").get("score").getAsDouble(),
          score(metrics.getAsJsonObject("gc.alloc.rate"), "score")));
    }
    if (results.isEmpty()) throw new IllegalArgumentException("CPU report contains no benchmark results");
    return results;
  }

  private static RenderingResult parseRendering(String json) {
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    List<SceneResult> scenes = new ArrayList<>();
    for (JsonElement entry : root.getAsJsonArray("scenes")) {
      JsonObject scene = entry.getAsJsonObject();
      scenes.add(new SceneResult(scene.get("textFragmentCount").getAsInt(), scene.has("textNodeCount") ? scene.get("textNodeCount").getAsInt() : scene.get("textFragmentCount").getAsInt(), scene.get("textCodePointCount").getAsInt(),
          scene.get("resolvedGlyphCount").getAsInt(), scene.get("resolvedRunCount").getAsInt(),
          scene.get("warmupFrameCount").getAsInt(), scene.get("measuredFrameCount").getAsInt(),
          latency(scene.getAsJsonObject("cpuSubmissionMicros")), latency(scene.getAsJsonObject("gpuCompleteMicros"))));
    }
    if (scenes.isEmpty()) throw new IllegalArgumentException("Rendering report contains no scenes");
    return new RenderingResult(root.getAsJsonObject("environment"), root.get("pixelValidationPassed").getAsBoolean(), scenes);
  }

  private static BenchmarkReportView toView(List<CpuResult> cpu, RenderingResult rendering, String currentRunIdentifier,
      List<BenchmarkReportView.HistoryRun> history, List<BenchmarkReportView.ChartTrend> trends) {
    List<BenchmarkReportView.CpuRow> cpuRows = new ArrayList<>();
    List<BenchmarkReportView.CpuChartDatum> cpuChartData = new ArrayList<>();
    for (CpuResult result : cpu) {
      cpuRows.add(new BenchmarkReportView.CpuRow(
          result.name(), number(result.latency()), metric(result.error()), number(result.allocation()),
          metric(result.allocationRate())));
      cpuChartData.add(new BenchmarkReportView.CpuChartDatum(result.name(),
          finite("CPU latency: " + result.name(), result.latency()),
          finiteOrNull("CPU uncertainty: " + result.name(), result.error()),
          finite("CPU allocation: " + result.name(), result.allocation()),
          finiteOrNull("CPU allocation rate: " + result.name(), result.allocationRate())));
    }
    List<BenchmarkReportView.SceneRow> sceneRows = new ArrayList<>();
    List<BenchmarkReportView.RenderingChartDatum> renderingChartData = new ArrayList<>();
    for (SceneResult scene : rendering.scenes()) {
      String fragments = fragmentCount(scene.fragments()) + " fragments";
      sceneRows.add(new BenchmarkReportView.SceneRow(fragments, scene.codePoints() + " code points; " + scene.glyphs() + " glyphs; " + scene.runs() + " runs",
          latencyText(scene.cpu()), latencyText(scene.gpu()), budgetText(scene.cpu()), budgetText(scene.gpu()),
          scene.warmupFrames() + " warmup; " + scene.measuredFrames() + " measured"));
      renderingChartData.add(new BenchmarkReportView.RenderingChartDatum(fragments,
          finite("Rendering CPU median: " + fragments, scene.cpu().median()),
          finite("Rendering CPU p95: " + fragments, scene.cpu().p95()),
          finite("Rendering CPU p99: " + fragments, scene.cpu().p99()),
          finite("Rendering GPU median: " + fragments, scene.gpu().median()),
          finite("Rendering GPU p95: " + fragments, scene.gpu().p95()),
          finite("Rendering GPU p99: " + fragments, scene.gpu().p99())));
    }
    CpuResult slowest = cpu.stream().max(Comparator.comparingDouble(CpuResult::latency)).orElseThrow();
    CpuResult allocating = cpu.stream().max(Comparator.comparingDouble(CpuResult::allocation)).orElseThrow();
    SceneResult largestGpu = rendering.scenes().stream().max(Comparator.comparingDouble(scene -> scene.gpu().p99())).orElseThrow();
    List<BenchmarkReportView.EnvironmentValue> environment = new ArrayList<>();
    for (String key : List.of(
        "javaVersion", "javaVendor", "osName", "osVersion", "osArchitecture", "glVendor", "glRenderer", "glVersion")) {
      environment.add(new BenchmarkReportView.EnvironmentValue(key, rendering.environment().get(key).getAsString()));
    }
    return new BenchmarkReportView(cpuRows, sceneRows, environment,
        rendering.pixelValidationPassed(), slowest.name(), number(slowest.latency()), allocating.name(), number(allocating.allocation()),
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
    Map<String, List<TrendValue>> values = new LinkedHashMap<>();
    for (int index = 0; index < runs.size(); index++) {
      ArchivedRun current = runs.get(index);
      Map<String, CpuResult> previous = index == 0 ? Map.of() : cpuByName(runs.get(index - 1).cpu());
      for (CpuResult result : current.cpu()) {
        CpuResult prior = previous.get(result.name());
        values.computeIfAbsent(result.name(), ignored -> new ArrayList<>()).add(
            new TrendValue(index, current.identifier(), result.latency(), change(result.latency(), prior == null ? null : prior.latency())));
      }
    }
    return trendSeries(values, runs, "cpu", "CPU latency", "us/op");
  }

  private static List<BenchmarkReportView.ChartTrend> gpuTrends(List<ArchivedRun> runs) {
    Map<String, List<TrendValue>> values = new LinkedHashMap<>();
    for (int index = 0; index < runs.size(); index++) {
      ArchivedRun current = runs.get(index);
      Map<SceneIdentity, SceneResult> previous = index == 0 ? Map.of() : scenesByIdentity(runs.get(index - 1).rendering());
      for (SceneResult scene : current.rendering().scenes()) {
        SceneResult prior = previous.get(scene.identity());
        String sceneLabel = sceneLabel(scene);
        values.computeIfAbsent(sceneLabel, ignored -> new ArrayList<>()).add(new TrendValue(index, current.identifier(), scene.gpu().p99(),
            change(scene.gpu().p99(), prior == null ? null : prior.gpu().p99())));
      }
    }
    return trendSeries(values, runs, "gpu", "GPU p99", "us");
  }

  private static List<BenchmarkReportView.ChartTrend> trendSeries(Map<String, List<TrendValue>> values, List<ArchivedRun> runs,
      String prefix, String metric, String unit) {
    List<BenchmarkReportView.ChartTrend> series = new ArrayList<>();
    int seriesNumber = 1;
    for (Map.Entry<String, List<TrendValue>> entry : values.entrySet()) {
      List<TrendValue> source = entry.getValue();
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
        trendValues.set(value.index(), finite(metric + ": " + entry.getKey(), value.value()));
        changes.set(value.index(), value.change());
      }
      String label = metric + ": " + entry.getKey();
      series.add(new BenchmarkReportView.ChartTrend(prefix + "-trend-" + seriesNumber++, label, unit,
          finite(label + " minimum", minimum), finite(label + " maximum", maximum), trendValues, changes));
    }
    return series;
  }

  private static List<BenchmarkReportView.HistoryRun> history(List<ArchivedRun> runs) {
    List<BenchmarkReportView.HistoryRun> history = new ArrayList<>();
    ArchivedRun previous = null;
    for (ArchivedRun current : runs) {
      Map<String, CpuResult> previousCpu = previous == null ? Map.of() : cpuByName(previous.cpu());
      Map<SceneIdentity, SceneResult> previousScenes = previous == null ? Map.of() : scenesByIdentity(previous.rendering());
      List<BenchmarkReportView.CpuHistoryRow> cpuRows = new ArrayList<>();
      for (CpuResult result : current.cpu()) {
        CpuResult prior = previousCpu.get(result.name());
        cpuRows.add(new BenchmarkReportView.CpuHistoryRow(result.name(), number(result.latency()), number(result.allocation()),
            change(result.latency(), prior == null ? null : prior.latency()), change(result.allocation(), prior == null ? null : prior.allocation())));
      }
      List<BenchmarkReportView.SceneHistoryRow> sceneRows = new ArrayList<>();
      for (SceneResult scene : current.rendering().scenes()) {
        SceneResult prior = previousScenes.get(scene.identity());
        sceneRows.add(new BenchmarkReportView.SceneHistoryRow(fragmentCount(scene.fragments()), latencyText(scene.cpu()),
            latencyText(scene.gpu()), latencyChange(scene.cpu(), prior == null ? null : prior.cpu()),
            latencyChange(scene.gpu(), prior == null ? null : prior.gpu()), percent(scene.cpu().budget120()),
            percent(scene.gpu().budget120()), change(scene.cpu().budget120(), prior == null ? null : prior.cpu().budget120()),
            change(scene.gpu().budget120(), prior == null ? null : prior.gpu().budget120())));
      }
      history.add(new BenchmarkReportView.HistoryRun(current.identifier(), cpuRows, sceneRows));
      previous = current;
    }
    return history;
  }

  private static Map<String, CpuResult> cpuByName(List<CpuResult> rows) {
    Map<String, CpuResult> values = new HashMap<>();
    for (CpuResult row : rows) values.put(row.name(), row);
    return values;
  }

  private static Map<SceneIdentity, SceneResult> scenesByIdentity(RenderingResult rendering) {
    Map<SceneIdentity, SceneResult> values = new HashMap<>();
    for (SceneResult row : rendering.scenes()) values.put(row.identity(), row);
    return values;
  }

  private static String sceneLabel(SceneResult scene) {
    return fragmentCount(scene.fragments()) + " fragments; " + scene.nodes() + " nodes; " + scene.codePoints()
        + " code points; " + scene.glyphs() + " glyphs; " + scene.runs() + " runs";
  }

  private static String change(double value, Double previous) {
    if (previous == null || previous == 0) return "not available";
    double difference = (value - previous) / previous * 100;
    return (difference >= 0 ? "+" : "") + percent(difference);
  }

  private static String latencyChange(Latency value, Latency previous) {
    return change(value.median(), previous == null ? null : previous.median()) + " / "
        + change(value.p95(), previous == null ? null : previous.p95()) + " / "
        + change(value.p99(), previous == null ? null : previous.p99());
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

  private record CpuResult(String name, double latency, Double error, double allocation, Double allocationRate) {
  }

  private record RenderingResult(JsonObject environment, boolean pixelValidationPassed, List<SceneResult> scenes) {
  }

  private record SceneResult(
      int fragments, int nodes, int codePoints, int glyphs, int runs, int warmupFrames, int measuredFrames, Latency cpu, Latency gpu) {
    SceneIdentity identity() { return new SceneIdentity(fragments, nodes, codePoints, glyphs, runs); }
  }

  private record SceneIdentity(int fragments, int nodes, int codePoints, int glyphs, int runs) { }

  private record Latency(double median, double p95, double p99, double budget60, double budget120) {
  }

  private record ArchivedRun(String identifier, LocalDateTime timestamp, int sequence, List<CpuResult> cpu, RenderingResult rendering) {
  }

  private record RunIdentifier(LocalDateTime timestamp, int sequence) {
  }

  private record TrendValue(int index, String identifier, double value, String change) {
  }
}
