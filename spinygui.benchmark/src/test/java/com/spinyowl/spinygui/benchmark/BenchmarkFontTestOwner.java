package com.spinyowl.spinygui.benchmark;

import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Test composition helper for benchmark identity fixtures that resolve production font metadata. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BenchmarkFontTestOwner {
  /** Explicitly installs the production owner and built-ins on the current test thread. */
  public static void install() {
    new FontServiceImpl(new FontStorageImpl(), false).installSemanticOwner();
  }
}
