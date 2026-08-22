package com.spinyowl.spinygui.core.node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import java.util.List;
import org.junit.jupiter.api.Test;

class FrameInvalidationTest {
  @Test
  void sourceMutationsAdvanceRevisionAndCascadeSafely() {
    Frame frame = cleanFrame();
    Element element = new Element("div");
    Text text = new Text("before");
    element.addChild(text);

    long revision = frame.revision();
    frame.addChild(element);
    assertTrue(frame.revision() > revision);
    assertTrue(frame.invalidation().styleDirty());

    clean(frame);
    revision = frame.revision();
    element.setAttribute("class", "selected");
    assertEquals(revision + 1, frame.revision());
    assertTrue(frame.invalidation().layoutDirty());
    element.setAttribute("class", "selected");
    assertEquals(revision + 1, frame.revision());

    clean(frame);
    text.content("after");
    assertTrue(frame.invalidation().layoutDirty());

    clean(frame);
    element.scrollTop(12);
    assertFalse(frame.invalidation().styleDirty());
    assertFalse(frame.invalidation().layoutDirty());
    assertTrue(frame.invalidation().transformDirty());

    clean(frame);
    frame.styleSheets().add(new StyleSheet(List.of(), List.of()));
    assertTrue(frame.invalidation().styleDirty());

    clean(frame);
    frame.frameSize(640, 480);
    assertTrue(frame.invalidation().layoutDirty());
    long sizedRevision = frame.revision();
    frame.frameSize(640, 480);
    assertEquals(sizedRevision, frame.revision());
  }

  @Test
  void frameSizeReadCannotMutateOwnedVector() {
    Frame frame = cleanFrame();
    frame.frameSize(100, 80);
    clean(frame);
    var snapshot = frame.frameSize();
    snapshot.set(200, 160);

    assertEquals(100, frame.frameSize().x);
    assertEquals(80, frame.frameSize().y);
    assertFalse(frame.invalidation().layoutDirty());
  }

  private static Frame cleanFrame() {
    Frame frame = new Frame();
    clean(frame);
    return frame;
  }

  private static void clean(Frame frame) {
    long revision = frame.revision();
    frame.completePreparation(revision, true, true, true);
    frame.markPainted(revision);
  }
}
