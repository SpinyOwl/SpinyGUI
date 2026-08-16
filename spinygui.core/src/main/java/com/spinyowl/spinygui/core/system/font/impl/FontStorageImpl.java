package com.spinyowl.spinygui.core.system.font.impl;

import static org.slf4j.LoggerFactory.getLogger;
import com.spinyowl.spinygui.core.system.font.FontStorage;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner;
import com.spinyowl.spinygui.core.util.IOUtil;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import org.slf4j.Logger;

public class FontStorageImpl implements FontStorage {
  private static final Logger LOG = getLogger(FontStorageImpl.class);
  private final Map<String, ByteBuffer> dataMap = new HashMap<>();
  private long issuedExternalAliasViews;
  private SemanticFontOwner semanticOwner;
  private FontServiceImpl semanticAggregate;

  @Override
  public ByteBuffer getFontData(@NonNull String path) {
    requirePublicReadAllowed();
    ByteBuffer stored = dataMap.get(SemanticFontOwner.normalizeLocator(path));
    if (stored == null) {
      return null;
    }
    issuedExternalAliasViews++;
    return stored.asReadOnlyBuffer();
  }

  void bindPublicReads(
      @NonNull SemanticFontOwner owner, @NonNull FontServiceImpl aggregate) {
    semanticOwner = owner;
    semanticAggregate = aggregate;
  }

  private void requirePublicReadAllowed() {
    if (semanticOwner == null || semanticAggregate == null) {
      throw new IllegalStateException(
          "Font storage reads require the installed semantic service aggregate");
    }
    semanticAggregate.verifyPublicStorageRead(this, semanticOwner);
  }

  /** {@inheritDoc} */
  @Override
  @Deprecated(forRemoval = true)
  public ByteBuffer loadFont(String fontPath) {
    throw new UnsupportedOperationException(
        "FontStorage.loadFont cannot publish semantic bytes; use FontService.loadFont");
  }

  ByteBuffer stageFontData(@NonNull String fontPath) {
    ByteBuffer fontData = null;
    try {
      fontData = IOUtil.resourceAsByteBuffer(fontPath);
    } catch (Exception e) {
      LOG.warn("Failed to load font from {}", fontPath);
    }
    return fontData;
  }

  void commitFontData(@NonNull String fontPath, @NonNull ByteBuffer fontData) {
    dataMap.put(SemanticFontOwner.normalizeLocator(fontPath), fontData);
  }

  ByteBuffer ownedFontData(@NonNull String fontPath) {
    return dataMap.get(SemanticFontOwner.normalizeLocator(fontPath));
  }

  void restoreFontData(@NonNull String fontPath, ByteBuffer previous) {
    String locator = SemanticFontOwner.normalizeLocator(fontPath);
    if (previous == null) {
      dataMap.remove(locator);
    } else {
      dataMap.put(locator, previous);
    }
  }

  void retireFontData(@NonNull String fontPath) {
    dataMap.remove(SemanticFontOwner.normalizeLocator(fontPath));
  }

  void clearFontData() {
    dataMap.clear();
  }

  boolean hasFontData() {
    return !dataMap.isEmpty();
  }

  boolean hasFontData(@NonNull String fontPath) {
    return dataMap.containsKey(SemanticFontOwner.normalizeLocator(fontPath));
  }

  ResourceSnapshot resourceSnapshot() {
    long capacity = dataMap.values().stream().mapToLong(ByteBuffer::capacity).sum();
    return new ResourceSnapshot(dataMap.size(), capacity, issuedExternalAliasViews);
  }

  record ResourceSnapshot(int byteEntries, long byteCapacity, long issuedExternalAliasViews) {}
}
