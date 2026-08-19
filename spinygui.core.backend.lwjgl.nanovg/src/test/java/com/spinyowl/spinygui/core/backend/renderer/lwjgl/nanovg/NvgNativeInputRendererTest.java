package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_CHECKBOX;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_PASSWORD;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RADIO;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RANGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NvgNativeInputRendererTest {

  @BeforeEach
  void installFontOwner() {
    NvgFontTestOwner.install();
  }

  @Test
  void render_password_masksRuntimeValue() {
    RecordingTextSink textSink = new RecordingTextSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new NoOpStateSink(),
            (context, x, y, width, height) -> {},
            textSink,
            (context, x, y, height) -> {});
    renderer.textMeasurer(new NvgInputRendererTest.FixedTextMeasurer());
    InputElement input = input(TYPE_PASSWORD);
    input.value("secret");

    renderer.render(input, 3);

    assertEquals(List.of("\u2022\u2022\u2022\u2022\u2022\u2022"), textSink.values);
  }

  @Test
  void render_checkboxRadioAndRange_useDedicatedControlSinkWithoutTextMeasurer() {
    RecordingControlSink controlSink = new RecordingControlSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new NoOpStateSink(),
            (context, x, y, width, height) -> {},
            new RecordingTextSink(),
            (context, x, y, height) -> {},
            controlSink);

    InputElement checkbox = input(TYPE_CHECKBOX);
    checkbox.checked(true);
    renderer.render(checkbox, 1);

    InputElement radio = input(TYPE_RADIO);
    renderer.render(radio, 1);

    InputElement range = input(TYPE_RANGE);
    range.setAttribute("min", "0");
    range.setAttribute("max", "100");
    range.value("25");
    renderer.render(range, 1);

    assertEquals(List.of("checkbox:true", "radio:false", "range:0.25"), controlSink.calls);
  }

  private InputElement input(String type) {
    InputElement input = new InputElement();
    input.type(type);
    input.box().contentPosition(20, 30);
    input.box().contentSize(160, 20);
    input.resolvedStyle().fontFamilies(List.of(Font.DEFAULT.fontFamily()));
    input.resolvedStyle().fontSize(Length.pixel(16));
    input.resolvedStyle().lineHeight(1f);
    input.resolvedStyle().color(Color.BLACK);
    return input;
  }

  private static final class NoOpStateSink implements NvgInputRenderer.InputStateSink {
    @Override
    public void begin(
        long context, InputElement input, Vector2f contentPosition, Vector2f contentSize) {}

    @Override
    public void end(long context) {}
  }

  private static final class RecordingTextSink implements NvgInputRenderer.InputTextSink {
    private final List<String> values = new ArrayList<>();

    @Override
    public void drawText(
        long context,
        String text,
        Font font,
        List<ResolvedTextRun> runs,
        float fontSize,
        Color color,
        float x,
        float baseline) {
      values.add(text);
    }
  }

  private static final class RecordingControlSink implements NvgInputRenderer.InputControlSink {
    private final List<String> calls = new ArrayList<>();

    @Override
    public void drawCheckbox(
        long context, InputElement input, Vector2f position, Vector2f size) {
      calls.add("checkbox:" + input.checked());
    }

    @Override
    public void drawRadio(long context, InputElement input, Vector2f position, Vector2f size) {
      calls.add("radio:" + input.checked());
    }

    @Override
    public void drawRange(
        long context,
        InputElement input,
        Vector2f position,
        Vector2f size,
        double fraction) {
      calls.add("range:%.2f".formatted(fraction));
    }
  }
}
