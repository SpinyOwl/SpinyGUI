package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.style.types.Color;
import org.junit.jupiter.api.Test;

class NvgTextStateTrackerTest {

  @Test
  void suppressesOnlyExactValuesInsideOneScope() {
    NvgTextStateTracker tracker = new NvgTextStateTracker();
    tracker.beginScope();

    assertTrue(tracker.selectFace(Font.DEFAULT));
    assertFalse(tracker.selectFace(Font.DEFAULT));
    assertTrue(tracker.fontSize(16f));
    assertFalse(tracker.fontSize(16f));
    assertTrue(tracker.fontSize(-0f));
    assertTrue(tracker.fontSize(+0f));
    assertTrue(tracker.color(Color.BLACK));
    assertFalse(tracker.color(Color.BLACK));
    assertTrue(tracker.alignment(65));
    assertFalse(tracker.alignment(65));
  }

  @Test
  void unknownMutationRestoreAndFrameResetForceReemission() {
    NvgTextStateTracker tracker = new NvgTextStateTracker();
    tracker.beginScope();
    tracker.selectFace(Font.DEFAULT);
    tracker.fontSize(16);
    tracker.color(Color.BLACK);
    tracker.alignment(65);

    tracker.invalidate();
    assertTrue(tracker.selectFace(Font.DEFAULT));
    assertTrue(tracker.fontSize(16));
    assertTrue(tracker.color(Color.BLACK));
    assertTrue(tracker.alignment(65));

    tracker.endScope();
    assertTrue(tracker.selectFace(Font.DEFAULT));
    assertTrue(tracker.selectFace(Font.DEFAULT));
    tracker.beginScope();
    assertTrue(tracker.selectFace(Font.DEFAULT));
  }
}
