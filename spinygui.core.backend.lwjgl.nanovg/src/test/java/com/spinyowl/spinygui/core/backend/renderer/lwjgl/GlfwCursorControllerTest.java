package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lwjgl.glfw.GLFW.GLFW_IBEAM_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZE_ALL_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZE_EW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZE_NESW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZE_NS_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZE_NWSE_CURSOR;

import com.spinyowl.spinygui.core.node.ButtonElement;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.style.types.CursorType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GlfwCursorControllerTest {
  @Test
  void update_cachesTheResolvedNativeCursorAndDestroysItOnClose() {
    Frame frame = new Frame();
    ButtonElement button = new ButtonElement();
    button.box().contentPosition(10, 10);
    button.box().contentSize(20, 20);
    frame.addChild(button);
    RecordingNativeApi nativeApi = new RecordingNativeApi();
    GlfwCursorController controller = new GlfwCursorController(frame, nativeApi);

    controller.update(7, 15, 15);
    controller.update(7, 15, 15);
    controller.close();

    assertEquals(List.of("create:221188", "set:7:1", "destroy:1"), nativeApi.calls);
  }

  @ParameterizedTest
  @CsvSource({
      "TEXT, " + GLFW_IBEAM_CURSOR,
      "MOVE, " + GLFW_RESIZE_ALL_CURSOR,
      "EW_RESIZE, " + GLFW_RESIZE_EW_CURSOR,
      "NS_RESIZE, " + GLFW_RESIZE_NS_CURSOR,
      "NWSE_RESIZE, " + GLFW_RESIZE_NWSE_CURSOR,
      "NESW_RESIZE, " + GLFW_RESIZE_NESW_CURSOR
  })
  void update_mapsEverySupportedNativeCursor(CursorType cursor, int expectedShape) {
    Frame frame = new Frame();
    Element target = new Element("div");
    target.resolvedStyle().cursor(cursor);
    target.box().contentPosition(10, 10);
    target.box().contentSize(20, 20);
    frame.addChild(target);
    RecordingNativeApi nativeApi = new RecordingNativeApi();
    GlfwCursorController controller = new GlfwCursorController(frame, nativeApi);

    controller.update(7, 15, 15);

    assertEquals(List.of("create:" + expectedShape, "set:7:1"), nativeApi.calls);
  }

  private static final class RecordingNativeApi implements GlfwCursorController.NativeApi {
    private final List<String> calls = new ArrayList<>();

    @Override
    public long create(int shape) {
      calls.add("create:" + shape);
      return 1;
    }

    @Override
    public void set(long window, long cursor) {
      calls.add("set:" + window + ":" + cursor);
    }

    @Override
    public void destroy(long cursor) {
      calls.add("destroy:" + cursor);
    }
  }
}
