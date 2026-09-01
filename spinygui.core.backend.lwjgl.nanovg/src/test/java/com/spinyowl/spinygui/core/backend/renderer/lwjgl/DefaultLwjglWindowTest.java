package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DefaultLwjglWindowTest {

  @Test
  void registrationCloseFailureDoesNotBlockRemainingOwnedCleanup() throws Exception {
    IllegalStateException registrationFailure = new IllegalStateException("registration");
    RecordingCleanup cleanup = new RecordingCleanup();
    DefaultLwjglWindow window = new DefaultLwjglWindow(
        LwjglApplicationConfiguration.windowed(100, 100, "test"), handle -> () -> { }, cleanup);
    set(window, "window", 42L);
    set(window, "glfwInitialized", true);
    set(window, "callbackRegistration",
        (LwjglCallbackInstaller.Registration) () -> { throw registrationFailure; });

    IllegalStateException thrown = assertThrows(IllegalStateException.class, window::close);

    assertEquals("registration", thrown.getMessage());
    assertEquals(1, thrown.getSuppressed().length);
    assertEquals(List.of("free", "current", "capabilities", "context", "destroy", "terminate"),
        cleanup.calls);
    window.close();
  }

  private static void set(Object target, String fieldName, Object value) throws Exception {
    Field field = DefaultLwjglWindow.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

  private static final class RecordingCleanup implements DefaultLwjglWindow.NativeCleanup {
    private final List<String> calls = new ArrayList<>();
    @Override public void freeCallbacks(long window) {
      calls.add("free");
      throw new IllegalStateException("free callbacks");
    }
    @Override public long currentContext() { calls.add("current"); return 42L; }
    @Override public void clearCapabilities() { calls.add("capabilities"); }
    @Override public void clearCurrentContext() { calls.add("context"); }
    @Override public void destroyWindow(long window) { calls.add("destroy"); }
    @Override public void terminateGlfw() { calls.add("terminate"); }
    @Override public void clearErrorCallback() { calls.add("error"); }
  }
}
