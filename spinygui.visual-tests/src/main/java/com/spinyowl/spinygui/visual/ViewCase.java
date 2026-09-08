package com.spinyowl.spinygui.visual;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.jsoup.Jsoup;

/** Immutable shared input; generated IDs allow comparison without editing the source demos. */
record ViewCase(String id, String xml, String css, int width, int height,
                String scrollId, double scrollX, double scrollY) {
  private static final String DEMOS =
      "spinygui.demo.complex/src/main/resources/com/spinyowl/spinygui/demo/";

  /** Loads all existing resource demos, plus focused fixtures and explicit scrolled states. */
  static List<ViewCase> load(Path root, String selection) throws IOException {
    var cases = new ArrayList<ViewCase>();
    String defaults = Files.readString(root.resolve("gui-test-html-app/comparison.css"));
    try (var files = Files.list(root.resolve(DEMOS))) {
      for (Path xml : files.filter(p -> p.toString().endsWith(".xml")).sorted().toList()) {
        String name = xml.getFileName().toString().replace(".xml", "");
        Path css = xml.resolveSibling(name + ".css");
        if (!Files.isRegularFile(css)) throw new IOException("Missing demo CSS: " + css);
        cases.add(create("demo-" + name, Files.readString(xml),
            defaults + "\n" + Files.readString(css), 960, 720, "", 0, 0));
      }
    }
    Path fixtures = root.resolve("spinygui.visual-tests/fixtures");
    for (String name : List.of("layout", "text", "borders", "clipping", "scrolling")) {
      String xml = Files.readString(fixtures.resolve(name + ".xml"));
      String css = defaults + "\n" + Files.readString(fixtures.resolve(name + ".css"));
      cases.add(create(name, xml, css, 480, 320, "", 0, 0));
      if (name.equals("scrolling")) {
        cases.add(create("scrolling-offset", xml, css, 480, 320, "scroller", 65, 90));
        cases.add(create("scrolling-clamped", xml, css, 480, 320, "scroller", 10000, 10000));
      }
    }
    if (selection.equals("all")) return List.copyOf(cases);
    var requested = new HashSet<>(List.of(selection.split(",", -1)));
    var selected = cases.stream().filter(c -> requested.remove(c.id())).toList();
    if (!requested.isEmpty() || selected.isEmpty()) {
      throw new IllegalArgumentException("Unknown/empty visualCases: " + requested);
    }
    return selected;
  }

  /** Uses the same HTML parsing rules as the native parser and preserves original explicit IDs. */
  static ViewCase create(String id, String xml, String css, int width, int height,
                         String scrollId, double scrollX, double scrollY) {
    var document = Jsoup.parse(xml);
    document.outputSettings().prettyPrint(false);
    var frames = document.getElementsByTag("winframe");
    if (frames.size() != 1) throw new IllegalArgumentException(id + " needs exactly one winframe");
    var frame = frames.first();
    var ids = new HashSet<String>();
    for (var element : frame.getAllElements()) {
      if (!element.id().isEmpty() && !ids.add(element.id())) {
        throw new IllegalArgumentException("Duplicate ID: " + element.id());
      }
    }
    int index = 0;
    for (var element : frame.getAllElements()) {
      if (element.id().isEmpty()) {
        String generated;
        do { generated = "visual-" + index++; } while (!ids.add(generated));
        element.attr("id", generated);
      }
    }
    if (!scrollId.isEmpty() && !ids.contains(scrollId)) {
      throw new IllegalArgumentException("Missing scroll target: " + scrollId);
    }
    // Fix the frame size explicitly in both engines. Animations use the initial static state.
    String controls = "\nwinframe { width: " + width + "px; height: " + height
        + "px; } * { font-family: Roboto !important; transition: none !important; animation: none !important; }";
    return new ViewCase(id, frame.outerHtml(), css + controls, width, height,
        scrollId, scrollX, scrollY);
  }
}
