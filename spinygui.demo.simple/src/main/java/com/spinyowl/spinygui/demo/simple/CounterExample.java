package com.spinyowl.spinygui.demo.simple;

import static com.spinyowl.spinygui.core.input.KeyAction.CLICK;
import static com.spinyowl.spinygui.core.node.NodeBuilder.button;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.text;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.LwjglApplicationConfiguration;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.NvgLwjglApplication;
import com.spinyowl.spinygui.core.event.MouseClickEvent;
import com.spinyowl.spinygui.core.node.Text;
import java.util.Map;

/** Interactive example showing that source mutations automatically invalidate the real pipeline. */
public final class CounterExample extends NvgLwjglApplication {
  private int count;
  private Text value;

  private CounterExample() {
    super(LwjglApplicationConfiguration.windowed(640, 420, "SpinyGUI - Counter"));
  }

  public static void main(String[] args) {
    new CounterExample().run();
  }

  @Override
  protected void initializeGui() {
    value = text("0");
    var increment = button(Map.of("class", "increment"), text("Increase counter"));
    increment.addListener(
        MouseClickEvent.class,
        event -> {
          if (event.action() == CLICK) value.content(Integer.toString(++count));
        });

    frame()
        .addChild(
            div(
                Map.of("class", "panel"),
                div(Map.of("class", "label"), text("CURRENT VALUE")),
                div(Map.of("class", "value"), value),
                increment,
                div(
                    Map.of("class", "hint"),
                    text("Text.content(...) marks layout and paint dirty; no manual refresh call."))));

    addStyleSheet(
        """
        winframe {
          display: block;
          background-color: #f8fafc;
          padding: 52px;
        }
        .panel {
          display: block;
          width: 430px;
          padding: 28px;
          background-color: #0f172a;
          border-radius: 16px;
        }
        .label {
          display: block;
          color: #93c5fd;
          font-size: 13px;
          font-weight: bold;
        }
        .value {
          display: block;
          color: white;
          font-size: 64px;
          font-weight: bold;
          margin-top: 10px;
        }
        .increment {
          display: block;
          width: 220px;
          height: 44px;
          margin-top: 18px;
          background-color: #2563eb;
          color: white;
          border: 1px solid #60a5fa;
          border-radius: 8px;
        }
        .increment:hover {
          background-color: #1d4ed8;
        }
        .hint {
          display: block;
          color: #cbd5e1;
          font-size: 14px;
          margin-top: 18px;
        }
        """);
  }
}
