package com.spinyowl.spinygui.core.system.font.impl;

import static org.slf4j.LoggerFactory.getLogger;
import com.spinyowl.spinygui.core.system.font.FontStorage;
import com.spinyowl.spinygui.core.util.IOUtil;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.slf4j.Logger;

public class FontStorageImpl implements FontStorage {
  private static final Logger LOG = getLogger(FontStorageImpl.class);
  private final Map<String, ByteBuffer> dataMap = new ConcurrentHashMap<>();

  @Override
  public ByteBuffer getFontData(@NonNull String path) {
    if (dataMap.containsKey(path)) return dataMap.get(path);
    return loadFont(path);
  }

  /**
   * Error safe method to load font and add font data to file storage. In case of error it will
   * return null.
   *
   * @param fontPath path to font which should be loaded.
   * @return {@link ByteBuffer} with font data or null in case of failure.
   */
  @Override
  public ByteBuffer loadFont(String fontPath) {
    ByteBuffer fontData = null;
    try {
      fontData = IOUtil.resourceAsByteBuffer(fontPath);
    } catch (Exception e) {
      LOG.warn("Failed to load font from {}", fontPath);
    }
    if (fontData != null) {
      dataMap.put(fontPath, fontData);
    }
    return fontData;
  }
}
