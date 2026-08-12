package com.spinyowl.spinygui.core.diagnostic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import org.junit.jupiter.api.Test;

class FrameDiagnosticCounterTest {
  @Test
  void frameDiagnosticsCaptureStructuralReadsAndMutationWithoutChangingDefaultBehavior() {
    Frame frame = new Frame();
    DiagnosticSession session = DiagnosticSession.enabled(java.util.List.of(FrameDiagnosticCounter.values()));
    frame.diagnostics(session);

    Element child = new Element("div");
    child.setAttribute("id", "child");
    frame.addChild(child);
    frame.childNodes();
    frame.children();
    child.box().contentPosition(4, 8);
    child.layoutAbsolutePosition();
    child.size();
    assertEquals(child, frame.getElementById("child"));

    DiagnosticSnapshot snapshot = session.snapshot();
    assertTrue(snapshot.value(FrameDiagnosticCounter.CHILD_NODE_VIEW_READS) > 0);
    assertTrue(snapshot.value(FrameDiagnosticCounter.ELEMENT_VIEW_READS) > 0);
    assertTrue(snapshot.value(FrameDiagnosticCounter.GEOMETRY_POSITION_READS) > 0);
    assertTrue(snapshot.value(FrameDiagnosticCounter.GEOMETRY_SIZE_READS) > 0);
    assertTrue(snapshot.value(FrameDiagnosticCounter.LOOKUP_NODE_VISITS) > 0);
    assertEquals(1, snapshot.value(FrameDiagnosticCounter.MUTATION_ATTACHMENTS));
  }

  @Test
  void disabledFrameDiagnosticsRemainNoOp() {
    Frame frame = new Frame();
    Element child = new Element("div");
    frame.addChild(child);

    assertEquals(DiagnosticSession.disabled(), frame.diagnostics());
    assertEquals(0, DiagnosticSession.disabled().snapshot().values().size());
  }
}
