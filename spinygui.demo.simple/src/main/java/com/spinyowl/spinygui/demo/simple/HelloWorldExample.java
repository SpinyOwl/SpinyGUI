package com.spinyowl.spinygui.demo.simple;

import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.text;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.LwjglApplicationConfiguration;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.NvgLwjglApplication;
import java.util.Map;

/** Minimal windowed example using the default service, pipeline, renderer, and GLFW wrappers. */
public final class HelloWorldExample extends NvgLwjglApplication {

  private HelloWorldExample() {
    super(LwjglApplicationConfiguration.windowed(720, 420, "SpinyGUI - Hello world"));
  }

  public static void main(String[] args) {
    new HelloWorldExample().run();
  }

  @Override
  protected void initializeGui() {
    frame()
        .addChild(
            div(
                Map.of("class", "card"),
                div(Map.of("class", "eyebrow"), text("SPINYGUI SIMPLE DEMO")),
                div(Map.of("class", "title"), text("Hello from the wrapped frame loop")),
                div(
                    Map.of("class", "copy"),
                    text(
                        "The application base owns GLFW, NanoVG, input, style, transitions, "
                            + "layout, transforms, rendering, and teardown."))));

    addStyleSheet(
        """
        winframe {
          display: block;
          background-color: #eef2ff;
          padding: 48px;
        }
        .card {
          display: block;
          width: 560px;
          padding: 30px;
          background-color: white;
          border: 1px solid #c7d2fe;
          border-radius: 14px;
        }
        .eyebrow {
          display: block;
          color: #4f46e5;
          font-size: 13px;
          font-weight: bold;
        }
        .title {
          display: block;
          color: #172554;
          font-size: 30px;
          font-weight: bold;
          margin-top: 14px;
        }
        .copy {
          display: block;
          color: #475569;
          font-size: 17px;
          line-height: 1.45;
          margin-top: 18px;
        }
        """);
  }
}
