package com.spinyowl.spinygui;

import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;

import com.spinyowl.cbchain.IChainCharCallback;
import com.spinyowl.cbchain.IChainCursorEnterCallback;
import com.spinyowl.cbchain.IChainCursorPosCallback;
import com.spinyowl.cbchain.IChainKeyCallback;
import com.spinyowl.cbchain.IChainMouseButtonCallback;
import com.spinyowl.cbchain.IChainScrollCallback;
import com.spinyowl.cbchain.IChainWindowSizeCallback;
import com.spinyowl.cbchain.impl.ChainCharCallback;
import com.spinyowl.cbchain.impl.ChainCursorEnterCallback;
import com.spinyowl.cbchain.impl.ChainCursorPosCallback;
import com.spinyowl.cbchain.impl.ChainKeyCallback;
import com.spinyowl.cbchain.impl.ChainMouseButtonCallback;
import com.spinyowl.cbchain.impl.ChainScrollCallback;
import com.spinyowl.cbchain.impl.ChainWindowSizeCallback;
import com.spinyowl.spinygui.core.FrameNavigator;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.GlfwSystemEventMapper;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.LwjglCallbackInstaller;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import java.util.Objects;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

/**
 * cbchain-aware GLFW event bridge with explicit native-callback ownership.
 *
 * <p>Owned mode is exclusive to a fresh host-owned window. Attached mode never installs, unsets,
 * frees, or otherwise owns the caller's native callbacks; it only adds and removes exact handlers.
 */
public final class GlfwSystemEventBridge implements LwjglCallbackInstaller, AutoCloseable {
  /** Chains retained for the bridge lifetime and never cleared wholesale. */
  private final Chains chains;
  /** Stable mapper callbacks added to and removed from each chain by identity. */
  private final GlfwSystemEventMapper.Callbacks callbacks;
  /** Exclusive native registrar, or null for attach-only embedded mode. */
  private final NativeRegistrar nativeRegistrar;
  /** Native registration created once by owned mode and closed once with the bridge. */
  private Registration nativeRegistration;
  /** True after handlers have been attached and until idempotent close. */
  private boolean installed;

  private GlfwSystemEventBridge(
      FrameNavigator navigator,
      SystemEventProcessor systemEvents,
      GlfwSystemEventMapper.KeyPolicy keyPolicy,
      Chains chains,
      NativeRegistrar nativeRegistrar) {
    this.chains = Objects.requireNonNull(chains, "chains");
    this.nativeRegistrar = nativeRegistrar;
    callbacks = new GlfwSystemEventMapper(navigator, systemEvents, keyPolicy).callbacks();
  }

  /** Creates exclusive native-chain ownership for a fresh host-owned GLFW window. */
  public static GlfwSystemEventBridge owned(
      FrameNavigator navigator,
      SystemEventProcessor systemEvents,
      GlfwSystemEventMapper.KeyPolicy keyPolicy) {
    return new GlfwSystemEventBridge(
        navigator, systemEvents, keyPolicy,
        new Chains(new ChainCursorPosCallback(), new ChainCursorEnterCallback(),
            new ChainWindowSizeCallback(), new ChainScrollCallback(),
            new ChainMouseButtonCallback(), new ChainCharCallback(), new ChainKeyCallback()),
        GlfwSystemEventBridge::installNative);
  }

  /**
   * Creates attach-only embedded participation in caller-owned, already-installed chains.
   * Closing the bridge removes only its seven exact handlers.
   */
  public static GlfwSystemEventBridge attached(
      FrameNavigator navigator,
      SystemEventProcessor systemEvents,
      GlfwSystemEventMapper.KeyPolicy keyPolicy,
      Chains callerOwnedChains) {
    return new GlfwSystemEventBridge(
        navigator, systemEvents, keyPolicy, callerOwnedChains, null);
  }

  static GlfwSystemEventBridge owned(
      FrameNavigator navigator,
      SystemEventProcessor systemEvents,
      GlfwSystemEventMapper.KeyPolicy keyPolicy,
      Chains chains,
      NativeRegistrar nativeRegistrar) {
    return new GlfwSystemEventBridge(
        navigator, systemEvents, keyPolicy, chains,
        Objects.requireNonNull(nativeRegistrar, "nativeRegistrar"));
  }

  /** Attaches handlers and, only in exclusive owned mode, installs the native chains. */
  @Override
  public synchronized Registration install(long window) {
    if (installed) throw new IllegalStateException("Bridge is already installed");
    addHandlers();
    try {
      if (nativeRegistrar != null) nativeRegistration = nativeRegistrar.install(window, chains);
      installed = true;
      return this::close;
    } catch (RuntimeException failure) {
      removeHandlers(failure);
      throw failure;
    }
  }

  /** Removes bridge handlers and only bridge-owned native registrations; never terminates GLFW. */
  @Override
  public synchronized void close() {
    if (!installed) return;
    RuntimeException failure = null;
    try {
      if (nativeRegistration != null) nativeRegistration.close();
    } catch (RuntimeException cleanupFailure) {
      failure = cleanupFailure;
    } finally {
      nativeRegistration = null;
      failure = removeHandlers(failure);
      installed = false;
    }
    if (failure != null) throw failure;
  }

  private void addHandlers() {
    boolean cursorPos = false;
    boolean cursorEnter = false;
    boolean windowSize = false;
    boolean scroll = false;
    boolean mouseButton = false;
    boolean character = false;
    try {
      cursorPos = requireAdded(chains.cursorPos().add(callbacks.cursorPos()));
      cursorEnter = requireAdded(chains.cursorEnter().add(callbacks.cursorEnter()));
      windowSize = requireAdded(chains.windowSize().add(callbacks.windowSize()));
      scroll = requireAdded(chains.scroll().add(callbacks.scroll()));
      mouseButton = requireAdded(chains.mouseButton().add(callbacks.mouseButton()));
      character = requireAdded(chains.character().add(callbacks.character()));
      requireAdded(chains.key().add(callbacks.key()));
    } catch (RuntimeException failure) {
      if (character) remove(failure, () -> chains.character().remove(callbacks.character()));
      if (mouseButton) remove(failure, () -> chains.mouseButton().remove(callbacks.mouseButton()));
      if (scroll) remove(failure, () -> chains.scroll().remove(callbacks.scroll()));
      if (windowSize) remove(failure, () -> chains.windowSize().remove(callbacks.windowSize()));
      if (cursorEnter) remove(failure, () -> chains.cursorEnter().remove(callbacks.cursorEnter()));
      if (cursorPos) remove(failure, () -> chains.cursorPos().remove(callbacks.cursorPos()));
      throw failure;
    }
  }

  private static boolean requireAdded(boolean added) {
    if (!added) throw new IllegalStateException("Callback chain rejected bridge handler");
    return true;
  }

  private RuntimeException removeHandlers(RuntimeException failure) {
    failure = remove(failure, () -> chains.key().remove(callbacks.key()));
    failure = remove(failure, () -> chains.character().remove(callbacks.character()));
    failure = remove(failure, () -> chains.mouseButton().remove(callbacks.mouseButton()));
    failure = remove(failure, () -> chains.scroll().remove(callbacks.scroll()));
    failure = remove(failure, () -> chains.windowSize().remove(callbacks.windowSize()));
    failure = remove(failure, () -> chains.cursorEnter().remove(callbacks.cursorEnter()));
    return remove(failure, () -> chains.cursorPos().remove(callbacks.cursorPos()));
  }

  private static RuntimeException remove(RuntimeException failure, Runnable removal) {
    try {
      removal.run();
      return failure;
    } catch (RuntimeException removalFailure) {
      if (failure == null) return removalFailure;
      failure.addSuppressed(removalFailure);
      return failure;
    }
  }

  private static Registration installNative(long window, Chains chains) {
    try {
      glfwSetCursorPosCallback(window, chains.cursorPos());
      glfwSetCursorEnterCallback(window, chains.cursorEnter());
      glfwSetWindowSizeCallback(window, chains.windowSize());
      glfwSetScrollCallback(window, chains.scroll());
      glfwSetMouseButtonCallback(window, chains.mouseButton());
      glfwSetCharCallback(window, chains.character());
      glfwSetKeyCallback(window, chains.key());
      return () -> unsetNative(window);
    } catch (RuntimeException failure) {
      unsetNative(window);
      throw failure;
    }
  }

  private static void unsetNative(long window) {
    free(glfwSetKeyCallback(window, null));
    free(glfwSetCharCallback(window, null));
    free(glfwSetMouseButtonCallback(window, null));
    free(glfwSetScrollCallback(window, null));
    free(glfwSetWindowSizeCallback(window, null));
    free(glfwSetCursorEnterCallback(window, null));
    free(glfwSetCursorPosCallback(window, null));
  }

  private static void free(org.lwjgl.system.Callback callback) {
    if (callback != null) callback.free();
  }

  /** Testable owned-mode boundary; implementations must rollback partial native installation. */
  @FunctionalInterface
  interface NativeRegistrar {
    /** Installs all owned chains and returns their exact teardown registration. */
    Registration install(long window, Chains chains);
  }

  /** Caller-owned or bridge-owned callback chains for the seven supported event types. */
  public record Chains(
      IChainCursorPosCallback cursorPos,
      IChainCursorEnterCallback cursorEnter,
      IChainWindowSizeCallback windowSize,
      IChainScrollCallback scroll,
      IChainMouseButtonCallback mouseButton,
      IChainCharCallback character,
      IChainKeyCallback key) {
    /** Validates every chain because partial attachment cannot provide parity. */
    public Chains {
      Objects.requireNonNull(cursorPos, "cursorPos");
      Objects.requireNonNull(cursorEnter, "cursorEnter");
      Objects.requireNonNull(windowSize, "windowSize");
      Objects.requireNonNull(scroll, "scroll");
      Objects.requireNonNull(mouseButton, "mouseButton");
      Objects.requireNonNull(character, "character");
      Objects.requireNonNull(key, "key");
    }
  }
}
