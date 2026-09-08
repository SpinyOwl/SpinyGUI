package com.spinyowl.spinygui.visual;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.style.types.Display;
import java.util.ArrayList;
import java.util.List;

/** Viewport border-box bounds in CSS pixels; client/scroll metrics follow DOM semantics. */
record Geometry(String id, double x, double y, double width, double height,
                double clientWidth, double clientHeight, double scrollWidth, double scrollHeight,
                double scrollLeft, double scrollTop) {
  /** Collects hidden nodes too, with zero geometry like getBoundingClientRect. */
  static List<Geometry> collect(Element root) {
    var result = new ArrayList<Geometry>();
    collect(root, false, result);
    return result;
  }

  private static void collect(Element element, boolean hidden, List<Geometry> result) {
    hidden |= element.resolvedStyle().display() == Display.NONE;
    String id = element.getAttribute("id");
    if (id != null && !id.isEmpty()) {
      if (hidden) {
        result.add(new Geometry(id, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
      } else {
        var bounds = bounds(element);
        result.add(new Geometry(id, bounds[0], bounds[1], bounds[2], bounds[3],
            element.clientWidth(), element.clientHeight(), element.scrollWidth(),
            element.scrollHeight(), element.scrollLeft(), element.scrollTop()));
      }
    }
    for (Node child : element.childNodes()) {
      if (child instanceof Element nested) collect(nested, hidden, result);
    }
  }

  /** Applies local and ancestor transforms around their viewport border-box origins. */
  private static double[] bounds(Element element) {
    var pos = element.absolutePosition();
    var size = element.size();
    double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
    double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
    for (int corner = 0; corner < 4; corner++) {
      float x = pos.x + (corner % 2) * size.x;
      float y = pos.y + (corner / 2) * size.y;
      for (Element node = element; node != null; node = node.parent()) {
        var origin = node.absolutePosition();
        var point = node.presentationState().transform().apply(x - origin.x, y - origin.y);
        x = point.x() + origin.x;
        y = point.y() + origin.y;
      }
      minX = Math.min(minX, x); maxX = Math.max(maxX, x);
      minY = Math.min(minY, y); maxY = Math.max(maxY, y);
    }
    return new double[] {minX, minY, maxX - minX, maxY - minY};
  }

  double[] values() {
    return new double[] {x, y, width, height, clientWidth, clientHeight,
        scrollWidth, scrollHeight, scrollLeft, scrollTop};
  }
}
