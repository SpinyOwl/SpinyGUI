package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

/** Installs one owned set of GLFW window callbacks and returns its teardown handle. */
@FunctionalInterface
public interface LwjglCallbackInstaller {

  /**
   * Installs callbacks for {@code window}.
   *
   * @param window initialized GLFW window handle
   * @return non-null idempotent registration owned by the caller
   */
  Registration install(long window);

  /** Owns only the callback registrations created by one installer invocation. */
  @FunctionalInterface
  interface Registration extends AutoCloseable {
    /** Detaches or frees only callbacks owned by this registration. */
    @Override
    void close();
  }
}
