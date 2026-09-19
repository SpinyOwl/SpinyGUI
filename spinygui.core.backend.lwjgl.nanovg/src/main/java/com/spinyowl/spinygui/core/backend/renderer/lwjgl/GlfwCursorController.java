package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import static org.lwjgl.glfw.GLFW.GLFW_ARROW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_HAND_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_IBEAM_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZE_ALL_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZE_EW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZE_NESW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZE_NS_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZE_NWSE_CURSOR;
import com.spinyowl.spinygui.core.cursor.CursorResolver;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.style.types.CursorType;
import java.util.EnumMap;
import java.util.Map;
import org.joml.Vector2f;

/** Owns cached GLFW standard cursor handles for one window and applies the resolved pointer shape. */
public final class GlfwCursorController implements AutoCloseable {
  private static final NativeApi SYSTEM_API = new NativeApi() {
    @Override public long create(int shape) { return org.lwjgl.glfw.GLFW.glfwCreateStandardCursor(shape); }
    @Override public void set(long window, long cursor) { org.lwjgl.glfw.GLFW.glfwSetCursor(window, cursor); }
    @Override public void destroy(long cursor) { org.lwjgl.glfw.GLFW.glfwDestroyCursor(cursor); }
  };
  private final Frame frame;
  private final NativeApi nativeApi;
  private final Map<CursorType, Long> cursors = new EnumMap<>(CursorType.class);
  private CursorType current;

  public GlfwCursorController(Frame frame) {
    this(frame, SYSTEM_API);
  }

  GlfwCursorController(Frame frame, NativeApi nativeApi) {
    this.frame = frame;
    this.nativeApi = nativeApi;
  }

  /** Applies the cursor appropriate for the supplied window-local pointer position. */
  public void update(long window, double x, double y) {
    CursorType next = CursorResolver.resolve(frame, new Vector2f((float) x, (float) y));
    if (next.equals(current)) return;
    current = next;
    nativeApi.set(window, cursor(next));
  }

  private long cursor(CursorType type) {
    return cursors.computeIfAbsent(type, key -> nativeApi.create(shape(key)));
  }

  private static int shape(CursorType type) {
    return switch (type) {
      case TEXT -> GLFW_IBEAM_CURSOR;
      case POINTER -> GLFW_HAND_CURSOR;
      case EW_RESIZE -> GLFW_RESIZE_EW_CURSOR;
      case NS_RESIZE -> GLFW_RESIZE_NS_CURSOR;
      case NWSE_RESIZE -> GLFW_RESIZE_NWSE_CURSOR;
      case NESW_RESIZE -> GLFW_RESIZE_NESW_CURSOR;
      case MOVE -> GLFW_RESIZE_ALL_CURSOR;
      case AUTO, DEFAULT -> GLFW_ARROW_CURSOR;
    };
  }

  @Override
  public void close() {
    for (long cursor : cursors.values()) nativeApi.destroy(cursor);
    cursors.clear();
    current = null;
  }

  interface NativeApi {
    long create(int shape);
    void set(long window, long cursor);
    void destroy(long cursor);
  }
}
