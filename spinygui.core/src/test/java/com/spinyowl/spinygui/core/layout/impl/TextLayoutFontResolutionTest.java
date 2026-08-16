package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TextLayoutFontResolutionTest {
  private static final String FAMILY = "T3 Layout Family";

  @BeforeEach
  void installProductionOwner() {
    if (Font.hasSemanticOwner()) {
      Font.semanticService().close();
    }
    new FontServiceImpl(new FontStorageImpl(), false).installSemanticOwner();
  }

  @Test
  void layoutResolutionObservesOwnerMutationThroughItsFontService() {
    FontService fontService = mock(FontService.class);
    TextMeasurer textMeasurer = mock(TextMeasurer.class);
    when(fontService.fontChainResolver()).thenAnswer(ignored -> Font.semanticOwner().resolver());
    when(fontService.isFontAvailable(any(Font.class))).thenReturn(true);
    when(textMeasurer.diagnostics()).thenReturn(DiagnosticSession.disabled());
    TextLayoutImpl layout = new TextLayoutImpl(fontService, textMeasurer);

    assertEquals(
        List.of(Font.DEFAULT),
        layout.findFonts(List.of(FAMILY), FontStyle.NORMAL, FontWeight.REGULAR));

    long generation = Font.semanticOwner().generation();
    Font registered = register();

    assertEquals(
        List.of(registered),
        layout.findFonts(List.of(FAMILY), FontStyle.NORMAL, FontWeight.REGULAR));
    assertEquals(generation + 1, Font.semanticOwner().generation());
  }

  private Font register() {
    Font font =
        new Font(
            FAMILY,
            FontStyle.NORMAL,
            FontStretch.NORMAL,
            FontWeight.REGULAR,
            "fonts/t3-layout.ttf");
    Font.semanticOwner()
        .add(
            SemanticFontOwner.FontRequest.from(
                font,
                () -> ByteBuffer.wrap(font.path().getBytes(StandardCharsets.UTF_8)),
                bytes -> {},
                (request, bytes) -> {}));
    return font;
  }
}
