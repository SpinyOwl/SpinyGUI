package com.spinyowl.spinygui.visual;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/** Explicit paired capture entry point. A comparison pass requires both metrics and pixels. */
public final class CompareViewsMain {
  private static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();

  private CompareViewsMain() {}

  /** Arguments: repository root, report root, and all or comma-separated case IDs. */
  public static void main(String[] args) throws Exception {
    if (args.length != 3) throw new IllegalArgumentException("Expected repository, report directory, cases");
    Path root = Path.of(args[0]).toAbsolutePath();
    Path output = Path.of(args[1]).toAbsolutePath();
    Files.createDirectories(output);
    // Unique run folders prevent a failed capture from reusing any previous run's evidence.
    Path run = Files.createTempDirectory(output, "run-");
    var outcomes = new ArrayList<Outcome>();
    try {
      var cases = ViewCase.load(root, args[2]);
      try (Playwright playwright = Playwright.create();
           Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
               .setHeadless(true).setIgnoreDefaultArgs(List.of("--hide-scrollbars")))) {
        for (ViewCase view : cases) {
          Path directory = run.resolve(view.id());
          Files.createDirectories(directory);
          Files.writeString(directory.resolve("case.json"), JSON.toJson(view));
          try {
            captureBrowser(root, view, browser, directory);
            captureNative(directory);
            var expected = readGeometry(directory.resolve("browser-geometry.json"));
            var actual = readGeometry(directory.resolve("native-geometry.json"));
            var geometry = ViewComparison.geometry(expected, actual);
            var pixels = ViewComparison.pixels(
                ImageIO.read(directory.resolve("browser.png").toFile()),
                ImageIO.read(directory.resolve("native.png").toFile()));
            if (pixels.diff() != null) ImageIO.write(pixels.diff(), "png", directory.resolve("diff.png").toFile());
            outcomes.add(new Outcome(view.id(), pixels.passed() && geometry.isEmpty() ? "passed" : "mismatch",
                pixels.detail(), pixels.fraction(), pixels.passed(), geometry.isEmpty(), geometry));
          } catch (Exception failure) {
            if (failure instanceof InterruptedException) Thread.currentThread().interrupt();
            outcomes.add(new Outcome(view.id(), "capture-error", failure.toString(), null, null, null, List.of()));
          }
          Files.writeString(directory.resolve("result.json"), JSON.toJson(outcomes.getLast()));
          System.out.println(view.id() + ": " + outcomes.getLast().status());
          if (Thread.currentThread().isInterrupted()) break;
        }
      }
    } catch (Exception failure) {
      outcomes.add(new Outcome("setup", "setup-error", failure.toString(), null, null, null, List.of()));
    }
    writeReport(output, run, outcomes);
    System.out.println("Comparison report: " + output.resolve("index.html"));
    if (outcomes.isEmpty() || outcomes.stream().anyMatch(result -> !result.status().equals("passed"))) {
      throw new IllegalStateException("View comparison failed; inspect the report and per-case native.log");
    }
  }

  private static void captureBrowser(Path root, ViewCase view, Browser browser, Path output) throws IOException {
    try (var server = new PreviewServer(root, view);
         var context = browser.newContext(new Browser.NewContextOptions()
             .setViewportSize(view.width(), view.height()).setDeviceScaleFactor(1)
             .setLocale("en-US").setTimezoneId("UTC"))) {
      Page page = context.newPage();
      page.setDefaultTimeout(30000);
      page.setDefaultNavigationTimeout(30000);
      var errors = new ArrayList<String>();
      page.onPageError(errors::add);
      page.navigate(server.url());
      page.waitForFunction("() => window.visualReady !== undefined");
      Object geometry = page.evaluate("""
          () => Promise.race([window.visualReady,
            new Promise((_, reject) => setTimeout(() => reject(new Error('Capture readiness timed out')), 30000))])
          """);
      if (!errors.isEmpty()) throw new IllegalStateException("Browser errors: " + errors);
      Files.writeString(output.resolve("browser-geometry.json"), JSON.toJson(geometry));
      page.screenshot(new Page.ScreenshotOptions().setPath(output.resolve("browser.png")));
      Files.writeString(output.resolve("browser-environment.json"), JSON.toJson(Map.of(
          "version", browser.version(), "playwright", "1.58.0", "pixelRatio", 1,
          "width", view.width(), "height", view.height(), "locale", "en-US",
          "captureDefaults", "shared-css-v2-visible-scrollbars",
          "arguments", browser.newBrowserCDPSession().send("Browser.getBrowserCommandLine"))));
    }
  }

  private static void captureNative(Path output) throws Exception {
    Process process = NativeProcess.start(NativeCaptureMain.class, output,
        output.resolve("case.json").toString(), output.toString());
    try {
      if (!process.waitFor(60, TimeUnit.SECONDS)) throw new IOException("Native capture timed out after 60s");
      if (process.exitValue() != 0) throw new IOException("Native capture exited " + process.exitValue() + "; see native.log");
    } finally {
      if (process.isAlive()) {
        process.descendants().forEach(ProcessHandle::destroyForcibly);
        process.destroyForcibly();
        process.waitFor(5, TimeUnit.SECONDS);
      }
    }
  }

  private static List<Geometry> readGeometry(Path path) throws IOException {
    Geometry[] values = JSON.fromJson(Files.readString(path), Geometry[].class);
    if (values == null) throw new IOException("Missing geometry array: " + path);
    return Arrays.asList(values);
  }

  private static void writeReport(Path output, Path run, List<Outcome> outcomes) throws IOException {
    Files.writeString(run.resolve("summary.json"), JSON.toJson(Map.of(
        "time", Instant.now().toString(), "geometryToleranceCssPixels", ViewComparison.GEOMETRY_TOLERANCE,
        "channelTolerance", ViewComparison.CHANNEL_TOLERANCE,
        "maxDifferentPixelFraction", ViewComparison.MAX_DIFFERENT_PIXEL_FRACTION, "cases", outcomes)));
    var html = new StringBuilder("<!doctype html><meta charset='utf-8'><title>SpinyGUI comparison</title>"
        + "<style>body{font:16px system-ui;margin:24px;background:#f8fafc;color:#172033}"
        + ".images{display:flex;gap:12px;overflow:auto}figure{margin:0}img{max-width:440px;border:1px solid #aaa}"
        + "section{background:white;padding:20px;margin:16px 0}pre{white-space:pre-wrap}</style>"
        + "<h1>SpinyGUI / Chromium comparison</h1><p>Geometry tolerance: 1 CSS px. "
        + "RGBA channel tolerance: 16/255. At most 0.5% of pixels may exceed it. Both checks must pass.</p>");
    html.append("<p><a href='").append(run.getFileName()).append("/summary.json'>Run metadata and results</a></p>");
    for (Outcome outcome : outcomes) {
      String base = run.getFileName() + "/" + outcome.id();
      html.append("<section><h2>").append(escape(outcome.id())).append(" — ")
          .append(escape(outcome.status())).append("</h2><p>").append(escape(outcome.detail())).append("</p>");
      if (outcome.pixelsPassed() != null) {
        html.append("<p>Pixels: ").append(outcome.pixelsPassed() ? "passed" : "failed")
            .append(" (above-tolerance pixels: ")
            .append(String.format(Locale.ROOT, "%.2f%%", outcome.differentPixelFraction() * 100))
            .append("). Geometry: ").append(outcome.geometryPassed() ? "passed" : "failed").append(".</p>");
      }
      if (!outcome.id().equals("setup")) {
        html.append("<div class='images'>");
        for (String name : List.of("browser", "native", "diff")) {
          if (Files.isRegularFile(run.resolve(outcome.id()).resolve(name + ".png"))) {
            html.append("<figure><figcaption>").append(name).append("</figcaption><a href='")
                .append(base).append('/').append(name).append(".png'><img src='")
                .append(base).append('/').append(name).append(".png'></a></figure>");
          }
        }
        html.append("</div><p>");
        for (String file : List.of("case.json", "result.json", "browser-geometry.json", "native-geometry.json",
            "browser-environment.json", "native-environment.json", "native.log")) {
          if (Files.isRegularFile(run.resolve(outcome.id()).resolve(file))) {
            html.append("<a href='").append(base).append('/').append(file).append("'>").append(file).append("</a> · ");
          }
        }
        html.append("</p>");
      }
      html.append("<details><summary>Geometry differences (").append(outcome.geometry().size())
          .append(")</summary><pre>").append(escape(String.join("\n", outcome.geometry())))
          .append("</pre></details></section>");
    }
    Files.writeString(output.resolve("index.html"), html.toString());
  }

  private static String escape(String text) {
    return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
  }

  private record Outcome(String id, String status, String detail, Double differentPixelFraction,
                         Boolean pixelsPassed, Boolean geometryPassed, List<String> geometry) {}
}
