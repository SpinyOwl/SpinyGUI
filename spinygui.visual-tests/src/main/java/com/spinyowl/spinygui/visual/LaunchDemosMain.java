package com.spinyowl.spinygui.visual;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** Launches a pair of visible shared-resource demos and owns their joint lifetime. */
public final class LaunchDemosMain {
  private LaunchDemosMain() {}

  /** Arguments: repository, session root, demo ID, optional automatic close time in seconds. */
  public static void main(String[] args) throws Exception {
    if (args.length != 4) throw new IllegalArgumentException("Expected repository, sessions, demo, seconds");
    Path root = Path.of(args[0]);
    ViewCase selected = select(ViewCase.load(root, "all"), args[2]);
    int seconds = Integer.parseInt(args[3]);
    if (seconds < 0) throw new IllegalArgumentException("demoSeconds must be nonnegative");
    Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    int width = Math.min(selected.width(), Math.max(100, screen.width / 2 - 48));
    int height = Math.min(selected.height(), Math.max(100, screen.height - 160));
    ViewCase view = ViewCase.create(selected.id(), selected.xml(), selected.css(), width, height,
        selected.scrollId(), selected.scrollX(), selected.scrollY());
    Files.createDirectories(Path.of(args[1]));
    Path session = Files.createTempDirectory(Path.of(args[1]), "session-").toAbsolutePath();
    Files.writeString(session.resolve("case.json"), new Gson().toJson(view));
    System.out.println("Launching " + view.id() + "; logs: " + session);
    try (var server = new PreviewServer(root, view);
         var playwright = Playwright.create();
         var context = playwright.chromium().launchPersistentContext(session.resolve("browser-profile"),
             new BrowserType.LaunchPersistentContextOptions().setHeadless(false)
                 .setViewportSize(null)
                 .setIgnoreDefaultArgs(List.of("--enable-automation", "about:blank"))
                 .setArgs(List.of("--app=" + server.url(), "--force-device-scale-factor=1")))) {
      var browser = context.browser();
      Page page = context.pages().getFirst();
      page.waitForFunction("() => window.visualReady !== undefined");
      page.evaluate("""
          () => Promise.race([window.visualReady,
            new Promise((_, reject) => setTimeout(() => reject(new Error('Demo readiness timed out')), 30000))])
          """);
      var cdp = context.newCDPSession(page);
      var windowId = cdp.send("Browser.getWindowForTarget").get("windowId");
      var bounds = new JsonObject();
      bounds.addProperty("left", screen.x + screen.width / 2 + 8);
      bounds.addProperty("top", screen.y + 24);
      // App mode retains only the OS window frame; size the content area to match native.
      int horizontalFrame = ((Number) page.evaluate("window.outerWidth - window.innerWidth")).intValue();
      int verticalFrame = ((Number) page.evaluate("window.outerHeight - window.innerHeight")).intValue();
      bounds.addProperty("width", width + Math.max(0, horizontalFrame));
      bounds.addProperty("height", height + Math.max(0, verticalFrame));
      bounds.addProperty("windowState", "normal");
      var parameters = new JsonObject();
      parameters.add("windowId", windowId);
      parameters.add("bounds", bounds);
      cdp.send("Browser.setWindowBounds", parameters);
      var query = new JsonObject();
      query.add("windowId", windowId);
      Files.writeString(session.resolve("browser-window.json"), cdp.send("Browser.getWindowBounds", query).toString());
      Process nativeApp = NativeProcess.start(NativeDemoMain.class, session,
          session.resolve("case.json").toString(), session.toString(),
          Integer.toString(screen.x + 16), Integer.toString(screen.y + 110));
      Thread shutdown = new Thread(() -> stop(nativeApp, session), "demo-cleanup");
      Runtime.getRuntime().addShutdownHook(shutdown);
      try {
        long readyDeadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30);
        while (!Files.exists(session.resolve("native-ready.json"))) {
          if (!nativeApp.isAlive()) throw new IOException("Native demo exited before first frame; see " + session.resolve("native.log"));
          if (System.nanoTime() > readyDeadline) throw new IOException("Native demo startup timed out");
          page.waitForTimeout(100);
        }
        System.out.println("Both demos are ready. Close either window (or press Esc in native) to end the pair.");
        long deadline = seconds == 0 ? Long.MAX_VALUE : System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds);
        while (nativeApp.isAlive() && !page.isClosed() && browser.isConnected() && System.nanoTime() < deadline) {
          page.waitForTimeout(100);
        }
        if (!nativeApp.isAlive() && nativeApp.exitValue() != 0) throw new IOException("Native demo failed; see " + session.resolve("native.log"));
      } catch (PlaywrightException failure) {
        if (!page.isClosed() && browser.isConnected()) throw failure;
      } finally {
        stop(nativeApp, session);
        Runtime.getRuntime().removeShutdownHook(shutdown);
      }
    }
  }

  /** Accepts resource stems and full comparison-case IDs; rejects multiple/unknown selections. */
  static ViewCase select(List<ViewCase> cases, String name) {
    String requested = name == null || name.isBlank() ? "button-demo" : name.trim();
    return cases.stream().filter(view -> view.id().equals(requested) || view.id().equals("demo-" + requested))
        .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown demo '" + requested
            + "'. Available: " + String.join(", ", cases.stream().map(ViewCase::id).toList())));
  }

  private static void stop(Process process, Path session) {
    try {
      Files.writeString(session.resolve("stop"), "close");
      if (!process.waitFor(5, TimeUnit.SECONDS)) process.destroyForcibly();
    } catch (IOException failure) {
      process.destroyForcibly();
    } catch (InterruptedException failure) {
      process.destroyForcibly();
      Thread.currentThread().interrupt();
    }
  }
}
