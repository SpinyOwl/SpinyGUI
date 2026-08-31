package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.text;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.backend.renderer.Renderer;
import com.spinyowl.spinygui.core.clipboard.Clipboard;
import com.spinyowl.spinygui.core.input.impl.KeyboardLayoutImpl;
import com.spinyowl.spinygui.core.node.Frame;
import java.util.Map;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.junit.jupiter.api.Test;

class LwjglFrameServicesTest {
  @Test
  void composesAProductionPipelineAndOwnsFontLifecycle() {
    Frame frame = new Frame();
    frame.frameSize(640, 360);
    LwjglFrameServices services =
        new LwjglFrameServices(
            frame,
            new NoOpRenderer(),
            () -> 1d,
            new InMemoryClipboard(),
            new KeyboardLayoutImpl(Map.of()));
    try {
      frame.addChild(div(text("Wrapped services")));
      services.addStyleSheet("winframe { display: block; padding: 8px; }");

      var preparation = services.pipeline().prepareFrame(frame);

      assertSame(frame, services.frame());
      assertTrue(preparation.renderable());
      assertTrue(preparation.styleExecuted());
      assertTrue(preparation.layoutExecuted());
    } finally {
      services.close();
    }

    assertDoesNotThrow(services::close);
  }

  @Test
  void configurationRejectsInvalidWindowContracts() {
    assertThrows(
        IllegalArgumentException.class,
        () -> LwjglApplicationConfiguration.windowed(0, 360, "Invalid"));
    assertThrows(
        IllegalArgumentException.class,
        () -> LwjglApplicationConfiguration.windowed(640, 360, " "));
  }

  private static final class NoOpRenderer implements Renderer {
    @Override
    public void initialize() {}

    @Override
    public void render(
        long window, Vector2fc windowSize, Vector2ic bufferSize, Frame frame) {}

    @Override
    public void destroy() {}
  }

  private static final class InMemoryClipboard implements Clipboard {
    private String value;

    @Override
    public String getClipboardString() {
      return value;
    }

    @Override
    public void setClipboardString(String string) {
      value = string;
    }
  }
}
