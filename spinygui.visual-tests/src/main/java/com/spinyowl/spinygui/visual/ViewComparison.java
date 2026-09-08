package com.spinyowl.spinygui.visual;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Independent geometry and pixel checks. Tolerances are fixed, recorded, and never auto-adjusted. */
final class ViewComparison {
  static final double GEOMETRY_TOLERANCE = 1d;
  static final int CHANNEL_TOLERANCE = 16;
  static final double MAX_DIFFERENT_PIXEL_FRACTION = 0.005;
  private static final List<String> FIELDS = List.of("x", "y", "width", "height", "clientWidth",
      "clientHeight", "scrollWidth", "scrollHeight", "scrollLeft", "scrollTop");

  private ViewComparison() {}

  /** A mismatch is retained for every missing, duplicate, non-finite, or out-of-tolerance value. */
  static List<String> geometry(List<Geometry> expected, List<Geometry> actual) {
    var failures = new ArrayList<String>();
    Map<String, Geometry> browser = index(expected, "browser", failures);
    Map<String, Geometry> nativeView = index(actual, "native", failures);
    if (browser.isEmpty() || nativeView.isEmpty()) failures.add("Empty geometry capture");
    for (var entry : browser.entrySet()) {
      Geometry other = nativeView.remove(entry.getKey());
      if (other == null) {
        failures.add("Missing native element: " + entry.getKey());
        continue;
      }
      double[] wanted = entry.getValue().values(), got = other.values();
      for (int i = 0; i < wanted.length; i++) {
        if (!Double.isFinite(wanted[i]) || !Double.isFinite(got[i])
            || Math.abs(wanted[i] - got[i]) > GEOMETRY_TOLERANCE) {
          failures.add(entry.getKey() + "." + FIELDS.get(i)
              + ": browser=" + wanted[i] + ", native=" + got[i]);
        }
      }
    }
    nativeView.keySet().forEach(id -> failures.add("Unexpected native element: " + id));
    return List.copyOf(failures);
  }

  private static Map<String, Geometry> index(List<Geometry> input, String source, List<String> failures) {
    var indexed = new LinkedHashMap<String, Geometry>();
    for (Geometry geometry : input) {
      if (geometry.id() == null || geometry.id().isBlank()) failures.add(source + " geometry has no ID");
      if (indexed.put(geometry.id(), geometry) != null) failures.add(source + " duplicate ID: " + geometry.id());
    }
    return indexed;
  }

  /** Includes alpha; size mismatches fail before pixel indexing. Diff shows failures in magenta. */
  static PixelResult pixels(BufferedImage expected, BufferedImage actual) {
    if (expected == null || actual == null) throw new IllegalArgumentException("Missing/invalid PNG capture");
    if (expected.getWidth() != actual.getWidth() || expected.getHeight() != actual.getHeight()) {
      return new PixelResult(false, -1, 1d, "Image dimensions differ", null);
    }
    var diff = new BufferedImage(expected.getWidth(), expected.getHeight(), BufferedImage.TYPE_INT_RGB);
    long different = 0;
    for (int y = 0; y < expected.getHeight(); y++) {
      for (int x = 0; x < expected.getWidth(); x++) {
        int a = expected.getRGB(x, y), b = actual.getRGB(x, y);
        boolean mismatch = false;
        for (int shift = 0; shift <= 24; shift += 8) {
          mismatch |= Math.abs(((a >>> shift) & 255) - ((b >>> shift) & 255)) > CHANNEL_TOLERANCE;
        }
        if (mismatch) different++;
        int gray = ((((a >>> 16) & 255) + ((a >>> 8) & 255) + (a & 255)) / 3 + 255) / 2;
        diff.setRGB(x, y, mismatch ? 0xff00ff : gray * 0x010101);
      }
    }
    double fraction = different / ((double) expected.getWidth() * expected.getHeight());
    return new PixelResult(fraction <= MAX_DIFFERENT_PIXEL_FRACTION, different, fraction,
        different + " pixels exceed channel tolerance " + CHANNEL_TOLERANCE, diff);
  }

  record PixelResult(boolean passed, long differentPixels, double fraction, String detail,
                     BufferedImage diff) {}
}
