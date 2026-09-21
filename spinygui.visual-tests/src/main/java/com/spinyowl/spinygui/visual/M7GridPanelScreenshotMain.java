package com.spinyowl.spinygui.visual;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/** Captures the M7 Grid demo with the standard native pipeline and checks panel pixels locally. */
public final class M7GridPanelScreenshotMain {
  private static final int TEAL = 0xff0f766e;
  private static final int BUTTON = 0xff99f6e4;
  private static final int CLIPPED_SCROLL_CONTENT = 0xfffef3c7;

  private M7GridPanelScreenshotMain() {}

  /** Arguments: repository root and capture output directory. */
  public static void main(String[] args) throws Exception {
    if (args.length != 2) throw new IllegalArgumentException("Expected repository root and output directory");
    Path root = Path.of(args[0]).toAbsolutePath();
    Path output = Path.of(args[1]).toAbsolutePath();
    Files.createDirectories(output);
    ViewCase source = ViewCase.load(root, "demo-grid-style-demo").getFirst();
    ViewCase view = ViewCase.create(source.id(), source.xml(), source.css(), 720, 560,
        source.scrollId(), source.scrollX(), source.scrollY());
    Path input = output.resolve("case.json");
    Files.writeString(input, new Gson().toJson(view));
    capture(input, output);
    Evidence evidence = verify(output.resolve("native.png"), output.resolve("native-geometry.json"));
    Files.writeString(output.resolve("m7-panel-assertion.json"), new Gson().toJson(evidence));
    System.out.println("M7 Grid panel screenshot assertion passed: " + output.resolve("native.png"));
  }

  private static void capture(Path input, Path output) throws IOException, InterruptedException {
    Process process = NativeProcess.start(
        NativeCaptureMain.class, output, input.toString(), output.toString());
    if (!process.waitFor(60, TimeUnit.SECONDS)) {
      process.destroyForcibly();
      throw new IOException("M7 Grid native capture timed out after 60s");
    }
    if (process.exitValue() != 0) {
      throw new IOException("M7 Grid native capture exited " + process.exitValue()
          + "; see " + output.resolve("native.log"));
    }
  }

  private static Evidence verify(Path screenshot, Path geometryFile) throws IOException {
    BufferedImage image = ImageIO.read(screenshot.toFile());
    if (image == null) throw new IOException("Missing or invalid native PNG: " + screenshot);
    Map<String, Bounds> geometry = readGeometry(geometryFile);
    Bounds panel = require(geometry, "m7-grid-panel");
    Bounds scroll = require(geometry, "m7-scroll");
    Bounds checkbox = require(geometry, "m7-check");
    Bounds button = require(geometry, "m7-apply");

    ColorBounds teal = colorBounds(image, TEAL);
    if (teal.count < 500 || teal.minX > panel.left() + 2 || teal.maxX < panel.right() - 3
        || teal.minY > panel.top() + 2 || teal.maxY < panel.bottom() - 3) {
      throw new IllegalStateException("M7 panel border extent is missing from native screenshot");
    }
    long buttonPixels = colorCount(image, BUTTON, button);
    if (buttonPixels < 20) {
      throw new IllegalStateException("M7 Apply button is missing from native screenshot");
    }
    long checkboxPixels = colorCount(image, TEAL, checkbox);
    if (checkboxPixels < 4) {
      throw new IllegalStateException("M7 checkbox is missing from native screenshot");
    }
    ColorBounds scrollContent = colorBounds(image, CLIPPED_SCROLL_CONTENT);
    if (scrollContent.count < 100 || !scroll.contains(scrollContent)) {
      throw new IllegalStateException("M7 scroll content is not visibly clipped to its Grid cell");
    }
    return new Evidence(panel, scroll, checkbox, button, teal.count, buttonPixels, checkboxPixels,
        scrollContent.count);
  }

  private static Map<String, Bounds> readGeometry(Path source) throws IOException {
    JsonArray items = JsonParser.parseString(Files.readString(source)).getAsJsonArray();
    var values = new java.util.LinkedHashMap<String, Bounds>();
    for (var item : items) {
      JsonObject geometry = item.getAsJsonObject();
      values.put(geometry.get("id").getAsString(), new Bounds(
          geometry.get("x").getAsDouble(), geometry.get("y").getAsDouble(),
          geometry.get("width").getAsDouble(), geometry.get("height").getAsDouble()));
    }
    return Map.copyOf(values);
  }

  private static Bounds require(Map<String, Bounds> geometry, String id) {
    Bounds bounds = geometry.get(id);
    if (bounds == null || bounds.width <= 0 || bounds.height <= 0) {
      throw new IllegalStateException("Missing visible M7 element in native geometry: " + id);
    }
    return bounds;
  }

  private static ColorBounds colorBounds(BufferedImage image, int color) {
    ColorBounds bounds = new ColorBounds(image.getWidth(), image.getHeight());
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if (image.getRGB(x, y) == color) bounds.include(x, y);
      }
    }
    return bounds;
  }

  private static long colorCount(BufferedImage image, int color, Bounds bounds) {
    long count = 0;
    for (int y = bounds.top(); y <= bounds.bottom(); y++) {
      for (int x = bounds.left(); x <= bounds.right(); x++) {
        if (x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight()
            && image.getRGB(x, y) == color) count++;
      }
    }
    return count;
  }

  private record Bounds(double x, double y, double width, double height) {
    int left() { return (int) Math.floor(x); }
    int top() { return (int) Math.floor(y); }
    int right() { return (int) Math.ceil(x + width); }
    int bottom() { return (int) Math.ceil(y + height); }
    boolean contains(ColorBounds colors) {
      return colors.minX >= left() && colors.maxX <= right() && colors.minY >= top()
          && colors.maxY <= bottom();
    }
  }

  private static final class ColorBounds {
    private int minX;
    private int minY;
    private int maxX = -1;
    private int maxY = -1;
    private long count;

    private ColorBounds(int width, int height) {
      minX = width;
      minY = height;
    }

    private void include(int x, int y) {
      minX = Math.min(minX, x);
      minY = Math.min(minY, y);
      maxX = Math.max(maxX, x);
      maxY = Math.max(maxY, y);
      count++;
    }
  }

  private record Evidence(Bounds panel, Bounds scroll, Bounds checkbox, Bounds button,
                          long tealPixels, long buttonPixels, long checkboxPixels,
                          long clippedScrollContentPixels) {}
}
