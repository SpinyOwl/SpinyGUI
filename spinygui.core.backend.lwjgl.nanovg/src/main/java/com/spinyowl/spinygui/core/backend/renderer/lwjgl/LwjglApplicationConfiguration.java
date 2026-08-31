package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import java.util.Objects;

/** Immutable window and presentation settings for the default LWJGL application wrapper. */
public record LwjglApplicationConfiguration(
    int width, int height, String title, boolean resizable, boolean vSync) {

  public LwjglApplicationConfiguration {
    if (width <= 0) throw new IllegalArgumentException("width must be positive");
    if (height <= 0) throw new IllegalArgumentException("height must be positive");
    title = Objects.requireNonNull(title, "title");
    if (title.isBlank()) throw new IllegalArgumentException("title must not be blank");
  }

  public static LwjglApplicationConfiguration windowed(int width, int height, String title) {
    return new LwjglApplicationConfiguration(width, height, title, true, true);
  }
}
