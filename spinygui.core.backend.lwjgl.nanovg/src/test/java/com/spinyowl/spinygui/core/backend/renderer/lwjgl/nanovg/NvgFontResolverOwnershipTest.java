package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import java.io.InputStream;
import java.lang.classfile.ClassFile;
import java.lang.classfile.Opcode;
import java.lang.classfile.instruction.FieldInstruction;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NvgFontResolverOwnershipTest {
  private static final String FAMILY = "T3 NanoVG Family";
  private FontServiceImpl fontService;

  @BeforeEach
  void installProductionOwner() {
    fontService = NvgFontTestOwner.install();
  }

  @Test
  void inputAndDebugRenderHelpersObserveTheSameOwnerMutation() {
    ResolvedStyle style = new ResolvedStyle();
    style.fontFamilies(List.of(FAMILY));
    style.fontStyle(FontStyle.NORMAL);
    style.fontWeight(FontWeight.REGULAR);
    NvgInputRenderer input = new NvgInputRenderer();
    input.textMeasurer(fontService);
    NvgDebugRenderer debug = new NvgDebugRenderer();

    assertEquals(List.of(), input.findFonts(style));
    assertEquals(Font.DEFAULT, debug.findFont(style));

    long generation = Font.semanticOwner().generation();
    Font registered = register();

    assertEquals(List.of(registered), input.findFonts(style));
    assertEquals(registered, debug.findFont(style));
    assertEquals(generation + 1, Font.semanticOwner().generation());
  }

  @Test
  void rendererBytecodeDoesNotReadTheCompatibilityDefaultField() throws Exception {
    assertNoDefaultFieldReference(NvgInputRenderer.class);
    assertNoDefaultFieldReference(NvgDebugRenderer.class);
  }

  private Font register() {
    Font font =
        new Font(
            FAMILY,
            FontStyle.NORMAL,
            FontStretch.NORMAL,
            FontWeight.REGULAR,
            "fonts/t3-nanovg.ttf");
    Font.semanticOwner()
        .add(
            SemanticFontOwner.FontRequest.from(
                font,
                () -> ByteBuffer.wrap(font.path().getBytes(StandardCharsets.UTF_8)),
                bytes -> {},
                (request, bytes) -> {}));
    return font;
  }

  private static void assertNoDefaultFieldReference(Class<?> type) throws Exception {
    String resource = "/" + type.getName().replace('.', '/') + ".class";
    try (InputStream stream = type.getResourceAsStream(resource)) {
      byte[] bytecode = java.util.Objects.requireNonNull(stream).readAllBytes();
      boolean readsDefault =
          ClassFile.of().parse(bytecode).methods().stream()
              .flatMap(method -> method.code().stream())
              .flatMap(code -> code.elementList().stream())
              .filter(FieldInstruction.class::isInstance)
              .map(FieldInstruction.class::cast)
              .anyMatch(
                  field ->
                      field.opcode() == Opcode.GETSTATIC
                          && field
                              .owner()
                              .asInternalName()
                              .equals("com/spinyowl/spinygui/core/system/font/FontChainResolver")
                          && field.name().equalsString("DEFAULT"));
      assertFalse(readsDefault, type.getName() + " must not read FontChainResolver.DEFAULT");
    }
  }
}
