package com.spinyowl.spinygui.core.system.font;

import java.nio.ByteBuffer;
import lombok.NonNull;

public interface FontStorage {
  /**
   * Returns data if it was loaded before. Otherwise, loads font data from specified path.
   *
   * @param path path to font file.
   * @return {@link ByteBuffer} with font data.
   */
  ByteBuffer getFontData(@NonNull String path);
  /**
   * Loads font data from specified path.
   *
   * @param fontPath path to font file.
   * @return {@link ByteBuffer} with font data.
   */
  ByteBuffer loadFont(String fontPath);
}
