package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Test composition helper for production compatibility-query fixtures. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FontTestOwner {
  /** Explicitly installs an owner-backed built-in font service on the current test thread. */
  public static FontServiceImpl install() {
    FontServiceImpl service = new FontServiceImpl(new FontStorageImpl(), false);
    service.installSemanticOwner();
    return service;
  }
}
