package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import java.util.List;
import org.junit.jupiter.api.Test;

class NvgStructuralValidationTest {
  @Test
  void sourceBoundProductionRecordingAcceptsExactNormalText() {
    Frame frame = frame("correct");

    var evidence = NvgStructuralValidation.validate(frame, textMeasurer());

    assertEquals(List.of("correct"), evidence.submittedText());
    assertEquals(1, evidence.submittedTextByPath().get(NvgStructuralValidation.Path.NORMAL));
  }

  @Test
  void allWrongNonBlankCommandsFailTheStructuralGate() {
    Frame frame = frame("wrong-but-nonblank");

    assertThrows(
        IllegalStateException.class,
        () ->
            NvgStructuralValidation.validate(
                frame,
                textMeasurer(),
                new NvgStructuralValidation.Requirements(
                    "source-bound-scene",
                    List.of("expected"),
                    List.of(NvgStructuralValidation.Path.NORMAL),
                    false,
                    false,
                    false,
                    false)));
  }

  private static TextMeasurer textMeasurer() {
    return new FontServiceImpl(new FontStorageImpl(), true);
  }

  private static Frame frame(String renderedText) {
    Frame frame = new Frame();
    Element container = new Element("div");
    Text text = new Text("source");
    text.inlineFragments(
        List.of(
            InlineFragment.builder()
                .text(renderedText)
                .x(2)
                .baseline(12)
                .font(Font.ROBOTO_REGULAR)
                .fontSize(16)
                .color(Color.BLACK)
                .build()));
    frame.addChild(container);
    container.addChild(text);
    frame.layoutChildNodes(List.of(container));
    container.layoutChildNodes(List.of(text));
    container.offsetParent(frame);
    text.offsetParent(container);
    return frame;
  }
}
