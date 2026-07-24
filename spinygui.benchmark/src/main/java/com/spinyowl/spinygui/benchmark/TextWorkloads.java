package com.spinyowl.spinygui.benchmark;

/** Deterministic text inputs shared by the CPU and rendering benchmark harnesses. */
public final class TextWorkloads {

  public static final String LATIN = "The quick brown fox jumps over the lazy dog.";
  public static final String WRAPPED_PARAGRAPH = String.join(" ",
      "Text layout must measure words, preserve whitespace, and wrap lines at a fixed width.",
      "This deterministic paragraph provides enough content to exercise repeated line breaking.",
      "Its wording is intentionally stable so local benchmark runs remain comparable.");
  public static final String MIXED_CJK = "Latin text with 中文、ひらがな、カタカナ, and 한국어 content.";
  public static final String SUPPLEMENTARY_UNICODE = "Astral characters: \uD83D\uDE80 \uD842\uDFB7 \uD83E\uDD8A.";
  public static final String MISSING_GLYPHS = "Unassigned noncharacters: \uFDD0 \uFDEF \uDBFF\uDFFF.";
  public static final String LONG_SINGLE_FONT = (LATIN + " ").repeat(128);

  private TextWorkloads() {
  }
}
