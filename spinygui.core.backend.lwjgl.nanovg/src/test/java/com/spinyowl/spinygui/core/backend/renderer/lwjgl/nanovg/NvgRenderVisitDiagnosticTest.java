package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import java.util.List;
import org.junit.jupiter.api.Test;

class NvgRenderVisitDiagnosticTest {
  @Test
  void layoutTraversalCountsEveryVisitedElementIncludingFrame() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));
    NvgRenderer renderer = new NvgRenderer(false, diagnostics);
    renderer.transformStateFactory((context, transform) -> () -> {});
    renderer.subtreeContentStateFactory((context, element) -> () -> {});
    renderer.subtreeContentRenderer((node, context) -> {});
    Frame frame = new Frame();
    Element parent = new Element("div");
    Element child = new Element("div");
    parent.addChild(child);
    frame.addChild(parent);
    frame.layoutChildNodes(List.of(parent));
    parent.layoutChildNodes(List.of(child));

    renderer.renderLayoutTree(frame);

    assertEquals(3, diagnostics.snapshot().value(NvgDiagnosticCounter.RENDER_NODE_VISITS));
  }
}
