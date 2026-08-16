package com.spinyowl.spinygui.core.system.font;

import java.nio.ByteBuffer;
import lombok.NonNull;

public interface FontStorage {
  /**
   * Returns a read-only duplicate view of data previously published by an installed font-service
   * transaction.
   *
   * <p>The returned view shares the JVM-managed direct-buffer allocation with storage. Callers may
   * retain and read that view after the corresponding storage entry is replaced or retired; storage
   * therefore cannot deterministically invalidate or free previously returned aliases.
   *
   * @param path path to font file.
   * @return read-only duplicate of the published font data, or {@code null} when unavailable
   * @throws IllegalStateException before semantic service installation, off the owner thread, or
   *     after the service aggregate is closed
   */
  ByteBuffer getFontData(@NonNull String path);
  /**
   * Legacy direct mutation alias. Production storage implementations reject this operation because
   * publishing bytes without descriptor validation would bypass semantic identity and generation.
   *
   * @param fontPath path to font file.
   * @return never returns for production storage
   * @deprecated use {@link FontService#loadFont(String)} after owner installation
   */
  @Deprecated(forRemoval = true)
  ByteBuffer loadFont(String fontPath);
}
