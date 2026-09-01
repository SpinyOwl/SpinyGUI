package com.spinyowl.spinygui.demo.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import org.junit.jupiter.api.Test;

class EmbeddedCallbackCoexistenceSmokeTest {

  @Test
  void protocolShowsBothKeyPathsBeforeDetachAndOnlyCallerKeyPathAfterward() {
    EmbeddedCallbackCoexistenceSmoke.Protocol protocol =
        new EmbeddedCallbackCoexistenceSmoke.Protocol();

    protocol.onCallerKey(GLFW_KEY_C, GLFW_PRESS);
    protocol.onBridgeKey(GLFW_KEY_C, GLFW_PRESS);
    assertEquals(1, protocol.callerCount());
    assertEquals(1, protocol.bridgeCount());
    assertTrue(protocol.title().startsWith("KEY ATTACHED | C: caller=1 bridge=1"));

    protocol.onCallerKey(GLFW_KEY_D, GLFW_PRESS);
    assertTrue(protocol.takeDetachRequest());
    assertFalse(protocol.takeDetachRequest());
    protocol.markDetached();

    protocol.onCallerKey(GLFW_KEY_C, GLFW_PRESS);
    assertEquals(2, protocol.callerCount());
    assertEquals(1, protocol.bridgeCount());
    assertTrue(protocol.title().startsWith("KEY DETACHED | C: caller=2 bridge=1"));

    protocol.onCallerKey(GLFW_KEY_ESCAPE, GLFW_RELEASE);
    assertFalse(protocol.closeRequested());
    protocol.onCallerKey(GLFW_KEY_ESCAPE, GLFW_PRESS);
    assertTrue(protocol.closeRequested());
  }
}
