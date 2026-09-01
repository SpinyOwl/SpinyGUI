package com.spinyowl.spinygui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CAPS_LOCK;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_NUM_LOCK;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SUPER;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

import com.spinyowl.cbchain.impl.ChainCharCallback;
import com.spinyowl.cbchain.impl.ChainCursorEnterCallback;
import com.spinyowl.cbchain.impl.ChainCursorPosCallback;
import com.spinyowl.cbchain.impl.ChainKeyCallback;
import com.spinyowl.cbchain.impl.ChainMouseButtonCallback;
import com.spinyowl.cbchain.impl.ChainScrollCallback;
import com.spinyowl.cbchain.impl.ChainWindowSizeCallback;
import com.spinyowl.spinygui.core.FrameNavigator;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.GlfwSystemEventMapper;
import com.spinyowl.spinygui.core.event.processor.InputImpact;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.system.event.SystemCursorEnterEvent;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.system.event.SystemEvent;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.system.event.SystemMouseClickEvent;
import com.spinyowl.spinygui.core.system.event.SystemScrollEvent;
import com.spinyowl.spinygui.core.system.event.SystemWindowSizeEvent;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.system.input.SystemKeyMod;
import com.spinyowl.spinygui.core.system.input.SystemMouseButton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFWCursorEnterCallbackI;

class GlfwSystemEventBridgeTest {

  @Test
  void mapsEverySupportedCallbackAndGlfwValue() {
    RecordingProcessor processor = new RecordingProcessor();
    Frame frame = new Frame();
    GlfwSystemEventBridge.Chains chains = chains();
    GlfwSystemEventBridge bridge = GlfwSystemEventBridge.attached(
        new FrameNavigator(frame, 2), processor, GlfwSystemEventMapper.KeyPolicy.none(), chains);
    bridge.install(41L);

    chains.cursorPos().invoke(41L, 1.25, 2.5);
    chains.cursorEnter().invoke(41L, true);
    chains.windowSize().invoke(41L, 800, 600);
    chains.scroll().invoke(41L, -3.5, 4.5);
    int allMods = GLFW_MOD_SHIFT | GLFW_MOD_CONTROL | GLFW_MOD_ALT | GLFW_MOD_SUPER
        | GLFW_MOD_CAPS_LOCK | GLFW_MOD_NUM_LOCK;
    chains.mouseButton().invoke(41L, GLFW_MOUSE_BUTTON_1, GLFW_PRESS, allMods);
    chains.character().invoke(41L, 0x1F642);
    chains.key().invoke(41L, GLFW_KEY_A, 77, GLFW_REPEAT, allMods);

    assertEquals(7, processor.events.size());
    SystemCursorPosEvent cursor = (SystemCursorPosEvent) processor.events.get(0);
    assertEquals(1.25f, cursor.posX());
    assertEquals(2.5f, cursor.posY());
    assertTrue(((SystemCursorEnterEvent) processor.events.get(1)).entered());
    assertEquals(800, ((SystemWindowSizeEvent) processor.events.get(2)).width());
    assertEquals(4.5f, ((SystemScrollEvent) processor.events.get(3)).offsetY());
    SystemMouseClickEvent mouse = (SystemMouseClickEvent) processor.events.get(4);
    assertEquals(SystemMouseButton.MOUSE_BUTTON_1, mouse.button());
    assertEquals(SystemKeyAction.PRESS, mouse.action());
    assertEquals(SystemKeyMod.values().length, mouse.mods().size());
    assertEquals(0x1F642, ((SystemCharEvent) processor.events.get(5)).codepoint());
    SystemKeyEvent key = (SystemKeyEvent) processor.events.get(6);
    assertEquals(GLFW_KEY_A, key.keyCode());
    assertEquals(77, key.scancode());
    assertEquals(SystemKeyAction.REPEAT, key.action());
    processor.events.forEach(event -> assertSame(frame, event.frame()));
  }

  @Test
  void capturesCurrentFrameWithoutRetargetingQueuedEvents() {
    RecordingProcessor processor = new RecordingProcessor();
    Frame first = new Frame();
    Frame second = new Frame();
    FrameNavigator navigator = new FrameNavigator(first, 2);
    GlfwSystemEventBridge.Chains chains = chains();
    GlfwSystemEventBridge bridge = GlfwSystemEventBridge.attached(
        navigator, processor, GlfwSystemEventMapper.KeyPolicy.none(), chains);
    bridge.install(0L);

    chains.cursorPos().invoke(0L, 1, 1);
    navigator.navigate(second);
    chains.cursorPos().invoke(0L, 2, 2);

    assertSame(first, processor.events.get(0).frame());
    assertSame(second, processor.events.get(1).frame());
  }

  @Test
  void ignoresUnsupportedButtonAndActionsButStillRunsExplicitKeyPolicy() {
    RecordingProcessor processor = new RecordingProcessor();
    AtomicInteger policies = new AtomicInteger();
    GlfwSystemEventBridge.Chains chains = chains();
    GlfwSystemEventBridge bridge = GlfwSystemEventBridge.attached(
        new FrameNavigator(new Frame(), 1), processor,
        (window, key, action) -> policies.incrementAndGet(), chains);
    bridge.install(0L);

    chains.mouseButton().invoke(0L, 99, GLFW_PRESS, 0);
    chains.mouseButton().invoke(0L, GLFW_MOUSE_BUTTON_1, 99, 0);
    chains.key().invoke(0L, GLFW_KEY_A, 0, 99, 0);

    assertEquals(0, processor.events.size());
    assertEquals(1, policies.get());
  }

  @Test
  void attachedCloseRemovesOnlyBridgeHandlersAndLeavesCallerChainsUsable() {
    RecordingProcessor processor = new RecordingProcessor();
    GlfwSystemEventBridge.Chains chains = chains();
    AtomicInteger callerEvents = new AtomicInteger();
    chains.cursorPos().add((window, x, y) -> callerEvents.incrementAndGet());
    chains.key().add((window, key, scancode, action, mods) -> callerEvents.incrementAndGet());
    GlfwSystemEventBridge bridge = GlfwSystemEventBridge.attached(
        new FrameNavigator(new Frame(), 1), processor, GlfwSystemEventMapper.KeyPolicy.none(), chains);
    bridge.install(999L);

    assertEquals(2, chains.cursorPos().size());
    bridge.close();
    bridge.close();
    assertEquals(1, chains.cursorPos().size());
    assertEquals(1, chains.key().size());
    chains.cursorPos().invoke(999L, 1, 2);
    chains.key().invoke(999L, GLFW_KEY_A, 0, GLFW_RELEASE, 0);
    assertEquals(2, callerEvents.get());
    assertEquals(0, processor.events.size());
  }

  @Test
  void rollsBackEarlierHandlersWhenAChainRejectsAttachment() {
    GlfwSystemEventBridge.Chains chains = new GlfwSystemEventBridge.Chains(
        new ChainCursorPosCallback(), new ChainCursorEnterCallback() {
          @Override public boolean add(GLFWCursorEnterCallbackI callback) {
            throw new IllegalStateException("attach failed");
          }
        }, new ChainWindowSizeCallback(), new ChainScrollCallback(),
        new ChainMouseButtonCallback(), new ChainCharCallback(), new ChainKeyCallback());
    GlfwSystemEventBridge bridge = GlfwSystemEventBridge.attached(
        new FrameNavigator(new Frame(), 1), new RecordingProcessor(),
        GlfwSystemEventMapper.KeyPolicy.none(), chains);

    assertThrows(IllegalStateException.class, () -> bridge.install(0L));

    assertEquals(0, chains.cursorPos().size());
    assertEquals(0, chains.cursorEnter().size());
  }

  @Test
  void keyPolicyNavigationDoesNotRetargetCapturedKeyEvent() {
    RecordingProcessor processor = new RecordingProcessor();
    Frame first = new Frame();
    Frame second = new Frame();
    FrameNavigator navigator = new FrameNavigator(first, 2);
    GlfwSystemEventBridge.Chains chains = chains();
    GlfwSystemEventBridge bridge = GlfwSystemEventBridge.attached(
        navigator, processor, (window, key, action) -> navigator.navigate(second), chains);
    bridge.install(0L);

    chains.key().invoke(0L, GLFW_KEY_A, 11, GLFW_PRESS, 0);

    assertSame(first, processor.events.get(0).frame());
    assertSame(second, navigator.currentFrame());
  }

  @Test
  void fakeOwnedRegistrarInstallsAndClosesExactlyOnce() {
    AtomicInteger installs = new AtomicInteger();
    AtomicInteger closes = new AtomicInteger();
    GlfwSystemEventBridge.Chains chains = chains();
    GlfwSystemEventBridge bridge = GlfwSystemEventBridge.owned(
        new FrameNavigator(new Frame(), 1), new RecordingProcessor(),
        GlfwSystemEventMapper.KeyPolicy.none(), chains,
        (window, installedChains) -> {
          assertSame(chains, installedChains);
          installs.incrementAndGet();
          return closes::incrementAndGet;
        });

    bridge.install(55L);
    bridge.close();
    bridge.close();

    assertEquals(1, installs.get());
    assertEquals(1, closes.get());
    assertEquals(0, chains.cursorPos().size());
  }

  @Test
  void nativeInstallFailureRemovesAllAttachedHandlers() {
    GlfwSystemEventBridge.Chains chains = chains();
    GlfwSystemEventBridge bridge = GlfwSystemEventBridge.owned(
        new FrameNavigator(new Frame(), 1), new RecordingProcessor(),
        GlfwSystemEventMapper.KeyPolicy.none(), chains,
        (window, installedChains) -> { throw new IllegalStateException("native failed"); });

    assertThrows(IllegalStateException.class, () -> bridge.install(55L));

    assertEquals(0, chains.cursorPos().size());
    assertEquals(0, chains.cursorEnter().size());
    assertEquals(0, chains.windowSize().size());
    assertEquals(0, chains.scroll().size());
    assertEquals(0, chains.mouseButton().size());
    assertEquals(0, chains.character().size());
    assertEquals(0, chains.key().size());
  }

  private static GlfwSystemEventBridge.Chains chains() {
    return new GlfwSystemEventBridge.Chains(
        new ChainCursorPosCallback(), new ChainCursorEnterCallback(),
        new ChainWindowSizeCallback(), new ChainScrollCallback(),
        new ChainMouseButtonCallback(), new ChainCharCallback(), new ChainKeyCallback());
  }

  private static final class RecordingProcessor implements SystemEventProcessor {
    private final List<SystemEvent> events = new ArrayList<>();
    @Override public InputImpact processEvents() { return InputImpact.NO_IMPACT; }
    @Override public void push(SystemEvent event) { events.add(event); }
    @Override public boolean hasEvents() { return !events.isEmpty(); }
  }
}
