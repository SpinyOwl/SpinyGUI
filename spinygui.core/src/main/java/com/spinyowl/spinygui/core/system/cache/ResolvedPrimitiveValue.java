package com.spinyowl.spinygui.core.system.cache;

import java.util.List;
import java.util.Objects;

/** Immutable source-local primitives; it deliberately contains no width or final line state. */
public record ResolvedPrimitiveValue(List<Primitive> primitives) {
  public ResolvedPrimitiveValue {
    primitives = List.copyOf(Objects.requireNonNull(primitives, "primitives"));
  }

  public record Primitive(
      int sourceStart,
      int sourceEnd,
      int codePoint,
      String fontIdentity,
      int glyphIndex,
      float baseAdvance,
      List<KerningInput> kerningInputs) {
    public Primitive {
      if (sourceStart < 0 || sourceEnd < sourceStart) throw new IllegalArgumentException("Invalid source range");
      fontIdentity = Objects.requireNonNull(fontIdentity, "fontIdentity");
      kerningInputs = List.copyOf(Objects.requireNonNull(kerningInputs, "kerningInputs"));
    }
  }

  public record KerningInput(String leftFont, int leftGlyph, String rightFont, int rightGlyph) {
    public KerningInput {
      leftFont = Objects.requireNonNull(leftFont, "leftFont");
      rightFont = Objects.requireNonNull(rightFont, "rightFont");
    }
  }
}
