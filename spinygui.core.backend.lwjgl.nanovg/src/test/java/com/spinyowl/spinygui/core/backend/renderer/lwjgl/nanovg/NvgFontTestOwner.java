package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Test composition helper for renderer fixtures that use production font compatibility queries. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class NvgFontTestOwner {
  static FontServiceImpl install() {
    FontServiceImpl service = new FontServiceImpl(new FontStorageImpl(), false);
    service.installSemanticOwner();
    return service;
  }
}
