package com.spinyowl.spinygui.benchmark.report;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
  private static final double BUDGET_60_HZ_MICROS = 16_667;
  private static final double LOGARITHMIC_MIN_WIDTH_PERCENT = 5;
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
    BenchmarkReportView view = toView(parseCpu(cpuJson), parseRendering(renderingJson), "Current input", List.of());
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
    return toView(current.cpu(), current.rendering(), current.identifier(), history(runs));
  }

  private static String render(BenchmarkReportView view) {
    StringOutput output = new StringOutput();
    TemplateEngine.createPrecompiled(ContentType.Html).render("report.jte", view, output);
    return output.toString();
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
      scenes.add(new SceneResult(scene.get("textFragmentCount").getAsInt(), scene.get("textCodePointCount").getAsInt(),
          scene.get("resolvedGlyphCount").getAsInt(), scene.get("resolvedRunCount").getAsInt(),
          scene.get("warmupFrameCount").getAsInt(), scene.get("measuredFrameCount").getAsInt(),
          latency(scene.getAsJsonObject("cpuSubmissionMicros")), latency(scene.getAsJsonObject("gpuCompleteMicros"))));
    }
    if (scenes.isEmpty()) throw new IllegalArgumentException("Rendering report contains no scenes");
    return new RenderingResult(root.getAsJsonObject("environment"), root.get("pixelValidationPassed").getAsBoolean(), scenes);
  }

  private static BenchmarkReportView toView(List<CpuResult> cpu, RenderingResult rendering, String currentRunIdentifier,
      List<BenchmarkReportView.HistoryRun> history) {
    List<BenchmarkReportView.CpuRow> cpuRows = new ArrayList<>();
    double latencyMin = cpu.stream().mapToDouble(CpuResult::latency).filter(value -> value > 0).min().orElse(1);
    double latencyMax = cpu.stream().mapToDouble(CpuResult::latency).max().orElse(1);
    double allocationMin = cpu.stream().mapToDouble(CpuResult::allocation).filter(value -> value > 0).min().orElse(1);
    double allocationMax = cpu.stream().mapToDouble(CpuResult::allocation).max().orElse(1);
    for (CpuResult result : cpu) {
      cpuRows.add(new BenchmarkReportView.CpuRow(
          result.name(), number(result.latency()), metric(result.error()), number(result.allocation()),
          metric(result.allocationRate()), number(logarithmicWidth(result.latency(), latencyMin, latencyMax)),
          number(logarithmicWidth(result.allocation(), allocationMin, allocationMax))));
    }
    List<BenchmarkReportView.SceneRow> sceneRows = new ArrayList<>();
    List<BenchmarkReportView.ChartRow> cpuChartRows = new ArrayList<>();
    List<BenchmarkReportView.ChartRow> gpuChartRows = new ArrayList<>();
    for (SceneResult scene : rendering.scenes()) {
      String fragments = fragmentCount(scene.fragments()) + " fragments";
      sceneRows.add(new BenchmarkReportView.SceneRow(fragments, scene.codePoints() + " code points; " + scene.glyphs() + " glyphs; " + scene.runs() + " runs",
          latencyText(scene.cpu()), latencyText(scene.gpu()), budgetText(scene.cpu()), budgetText(scene.gpu()),
          scene.warmupFrames() + " warmup; " + scene.measuredFrames() + " measured"));
      addChartRows(cpuChartRows, fragments, scene.cpu(), "");
      addChartRows(gpuChartRows, fragments, scene.gpu(), "gpu");
    }
    CpuResult slowest = cpu.stream().max(Comparator.comparingDouble(CpuResult::latency)).orElseThrow();
    CpuResult allocating = cpu.stream().max(Comparator.comparingDouble(CpuResult::allocation)).orElseThrow();
    SceneResult largestGpu = rendering.scenes().stream().max(Comparator.comparingDouble(scene -> scene.gpu().p99())).orElseThrow();
    List<BenchmarkReportView.EnvironmentValue> environment = new ArrayList<>();
    for (String key : List.of(
        "javaVersion", "javaVendor", "osName", "osVersion", "osArchitecture", "glVendor", "glRenderer", "glVersion")) {
      environment.add(new BenchmarkReportView.EnvironmentValue(key, rendering.environment().get(key).getAsString()));
    }
    return new BenchmarkReportView(cpuRows, sceneRows, cpuChartRows, gpuChartRows, environment,
        rendering.pixelValidationPassed(), slowest.name(), number(slowest.latency()), allocating.name(), number(allocating.allocation()),
        fragmentCount(largestGpu.fragments()), number(largestGpu.gpu().p99()), percent(largestGpu.gpu().p99() / BUDGET_120_HZ_MICROS * 100),
        currentRunIdentifier, history);
  }

  private static List<BenchmarkReportView.HistoryRun> history(List<ArchivedRun> runs) {
    List<BenchmarkReportView.HistoryRun> history = new ArrayList<>();
    ArchivedRun previous = null;
    for (ArchivedRun current : runs) {
      Map<String, CpuResult> previousCpu = previous == null ? Map.of() : cpuByName(previous.cpu());
      Map<Integer, SceneResult> previousScenes = previous == null ? Map.of() : scenesByFragments(previous.rendering());
      List<BenchmarkReportView.CpuHistoryRow> cpuRows = new ArrayList<>();
      for (CpuResult result : current.cpu()) {
        CpuResult prior = previousCpu.get(result.name());
        cpuRows.add(new BenchmarkReportView.CpuHistoryRow(result.name(), number(result.latency()), number(result.allocation()),
            change(result.latency(), prior == null ? null : prior.latency()), change(result.allocation(), prior == null ? null : prior.allocation())));
      }
      List<BenchmarkReportView.SceneHistoryRow> sceneRows = new ArrayList<>();
      for (SceneResult scene : current.rendering().scenes()) {
        SceneResult prior = previousScenes.get(scene.fragments());
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

  private static Map<Integer, SceneResult> scenesByFragments(RenderingResult rendering) {
    Map<Integer, SceneResult> values = new HashMap<>();
    for (SceneResult row : rendering.scenes()) values.put(row.fragments(), row);
    return values;
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

  private static void addChartRows(List<BenchmarkReportView.ChartRow> rows, String fragments, Latency latency, String cssClass) {
    double maximum = BUDGET_60_HZ_MICROS;
    rows.add(chartRow(fragments + " median", latency.median(), maximum, cssClass));
    rows.add(chartRow(fragments + " p95", latency.p95(), maximum, cssClass));
    rows.add(chartRow(fragments + " p99", latency.p99(), maximum, cssClass));
  }

  private static BenchmarkReportView.ChartRow chartRow(String label, double value, double maximum, String cssClass) {
    return new BenchmarkReportView.ChartRow(label, number(value), number(Math.min(100, value / maximum * 100)),
        cssClass + (value > maximum ? " over-budget" : ""), value > maximum ? " (over 60 Hz scale)" : "");
  }

  private static Latency latency(JsonObject value) {
    return new Latency(value.get("median").getAsDouble(), value.get("p95").getAsDouble(),
        value.get("p99").getAsDouble(), value.get("budget60HzPercent").getAsDouble(),
        value.get("budget120HzPercent").getAsDouble());
  }

  private static Double score(JsonObject value, String key) {
    return value != null && value.has(key) ? value.get(key).getAsDouble() : null;
  }

  private static double logarithmicWidth(double value, double min, double max) {
    if (value <= 0) return 0;
    if (max <= min) return 100;
    return LOGARITHMIC_MIN_WIDTH_PERCENT + (100 - LOGARITHMIC_MIN_WIDTH_PERCENT)
        * (Math.log10(value) - Math.log10(min)) / (Math.log10(max) - Math.log10(min));
  }

  private static String number(double value) {
    return String.format(Locale.ROOT, "%,.3f", value);
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
      int fragments, int codePoints, int glyphs, int runs, int warmupFrames, int measuredFrames, Latency cpu, Latency gpu) {
  }

  private record Latency(double median, double p95, double p99, double budget60, double budget120) {
  }

  private record ArchivedRun(String identifier, LocalDateTime timestamp, int sequence, List<CpuResult> cpu, RenderingResult rendering) {
  }

  private record RunIdentifier(LocalDateTime timestamp, int sequence) {
  }
}
