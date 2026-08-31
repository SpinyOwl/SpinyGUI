package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.system.MemoryUtil.memLengthUTF8;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import java.nio.ByteBuffer;
import java.util.Objects;

/** Call-local rendered text that can stage resolved glyph code points without constructing a String. */
sealed interface NvgRenderedText permits NvgRenderedText.Literal, NvgRenderedText.Run {
  int utf8Length();

  int encode(ByteBuffer target);

  String displayValue();

  static NvgRenderedText literal(String value) {
    return new Literal(value);
  }

  static NvgRenderedText run(ResolvedTextRun value) {
    return new Run(value);
  }

  record Literal(String value) implements NvgRenderedText {
    public Literal {
      Objects.requireNonNull(value, "value");
    }

    public int utf8Length() {
      return memLengthUTF8(value, false);
    }

    public int encode(ByteBuffer target) {
      return memUTF8(value, false, target, 0);
    }

    public String displayValue() {
      return value;
    }
  }

  record Run(ResolvedTextRun value) implements NvgRenderedText {
    public Run {
      Objects.requireNonNull(value, "value");
    }

    public int utf8Length() {
      int bytes = 0;
      for (ResolvedGlyph glyph : value.glyphs()) {
        bytes += NvgRenderedText.utf8Length(glyph.renderedCodePoint());
      }
      return bytes;
    }

    public int encode(ByteBuffer target) {
      int start = target.position();
      for (ResolvedGlyph glyph : value.glyphs()) putUtf8(target, glyph.renderedCodePoint());
      return target.position() - start;
    }

    public String displayValue() {
      return value.renderedText();
    }
  }

  private static int utf8Length(int codePoint) {
    codePoint = validCodePoint(codePoint);
    if (codePoint <= 0x7F) return 1;
    if (codePoint <= 0x7FF) return 2;
    if (codePoint <= 0xFFFF) return 3;
    return 4;
  }

  private static void putUtf8(ByteBuffer target, int codePoint) {
    codePoint = validCodePoint(codePoint);
    if (codePoint <= 0x7F) {
      target.put((byte) codePoint);
    } else if (codePoint <= 0x7FF) {
      target.put((byte) (0xC0 | codePoint >>> 6));
      target.put((byte) (0x80 | codePoint & 0x3F));
    } else if (codePoint <= 0xFFFF) {
      target.put((byte) (0xE0 | codePoint >>> 12));
      target.put((byte) (0x80 | codePoint >>> 6 & 0x3F));
      target.put((byte) (0x80 | codePoint & 0x3F));
    } else {
      target.put((byte) (0xF0 | codePoint >>> 18));
      target.put((byte) (0x80 | codePoint >>> 12 & 0x3F));
      target.put((byte) (0x80 | codePoint >>> 6 & 0x3F));
      target.put((byte) (0x80 | codePoint & 0x3F));
    }
  }

  private static int validCodePoint(int codePoint) {
    return !Character.isValidCodePoint(codePoint)
            || codePoint >= Character.MIN_SURROGATE && codePoint <= Character.MAX_SURROGATE
        ? 0xFFFD
        : codePoint;
  }
}
