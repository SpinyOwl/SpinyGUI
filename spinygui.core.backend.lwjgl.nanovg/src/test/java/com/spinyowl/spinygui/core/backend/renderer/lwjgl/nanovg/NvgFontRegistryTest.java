package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.font.Font;
import org.junit.jupiter.api.Test;

class NvgFontRegistryTest {

  @Test
  void displayText_keepsPrimaryGlyphsAndMarksUnsupportedCodePoints() {
    NvgFontRegistry registry = new NvgFontRegistry();

    assertEquals("\ufffd", registry.displayText(Font.DEFAULT, "\u96ea"));
    assertEquals("\ufffd", registry.displayText(Font.DEFAULT, new String(Character.toChars(0x10FFFF))));
  }
}
