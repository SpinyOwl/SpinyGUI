package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NvgFontRegistryTest {
  private static final long CONTEXT = 51L;

  private FontServiceImpl fontService;

  @BeforeEach
  void installFontOwner() {
    fontService = NvgFontTestOwner.install();
  }

  @AfterEach
  void closeFontOwner() {
    fontService.close();
  }

  @Test
  void displayText_keepsPrimaryGlyphsAndMarksUnsupportedCodePoints() {
    NvgFontRegistry registry = new NvgFontRegistry();

    assertEquals("\ufffd", registry.displayText(CONTEXT, Font.DEFAULT, "\u96ea"));
    assertEquals(
        "\ufffd",
        registry.displayText(
            CONTEXT, Font.DEFAULT, new String(Character.toChars(0x10FFFF))));
  }

  @Test
  void displayBeforeFaceSelectionBindsOneContextWithoutDuplication() {
    NvgFontRegistry registry = new NvgFontRegistry(null, (context, name, bytes) -> 7);
    NvgFontResourceObservation initially = registry.observation();
    List<String> resourceCalls = new ArrayList<>();
    NvgTextCommandSink commands =
        (NvgTextCommandSink)
            Proxy.newProxyInstance(
                NvgTextCommandSink.class.getClassLoader(),
                new Class<?>[] {NvgTextCommandSink.class},
                (proxy, method, arguments) -> {
                  if (method.getName().equals("displayText")) {
                    resourceCalls.add(method.getName());
                    return registry.displayText(
                        (Long) arguments[0], (Font) arguments[1], (String) arguments[2]);
                  }
                  if (method.getName().equals("selectFace")) {
                    resourceCalls.add(method.getName());
                    return registry.fontFace((Font) arguments[2], (Long) arguments[0]) != null;
                  }
                  return null;
                });
    NvgTextRenderer renderer = new NvgTextRenderer(commands);
    Element parent = new Element("div");
    Text text = new Text("A");
    parent.addChild(text);
    text.inlineFragments(
        List.of(
            InlineFragment.builder()
                .text("A")
                .font(Font.DEFAULT)
                .fontSize(16)
                .color(Color.BLACK)
                .baseline(12)
                .build()));

    assertThrows(
        IllegalArgumentException.class,
        () -> registry.displayText(0, Font.DEFAULT, "A"));
    NvgFontResourceObservation afterRejectedUnboundUse = registry.observation();
    renderer.render(text, CONTEXT);
    NvgFontResourceObservation afterRenderOrder = registry.observation();

    assertAll(
        () -> assertEquals(0, initially.contextCount()),
        () -> assertEquals(0, initially.bufferEntries()),
        () -> assertEquals(0, initially.fontInfoEntries()),
        () -> assertEquals(initially, afterRejectedUnboundUse),
        () -> assertEquals(List.of("displayText", "selectFace"), resourceCalls),
        () -> assertEquals(CONTEXT, afterRenderOrder.contextIdentity()),
        () -> assertEquals(1, afterRenderOrder.contextCount()),
        () -> assertEquals(1, afterRenderOrder.bufferEntries()),
        () -> assertEquals(1, afterRenderOrder.fontInfoEntries()),
        () -> assertEquals(1, afterRenderOrder.faceEntries()));
  }
}
