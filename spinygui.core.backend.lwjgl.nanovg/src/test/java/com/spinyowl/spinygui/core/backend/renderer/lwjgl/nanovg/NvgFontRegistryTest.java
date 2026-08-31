package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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

  @Test
  void repeatedFaceSelectionReusesOneGenerationScopedResourceKey() throws Exception {
    AtomicInteger faceCreations = new AtomicInteger();
    NvgFontRegistry registry =
        new NvgFontRegistry(null, (context, name, bytes) -> faceCreations.incrementAndGet());
    Font equivalent =
        new Font(
            Font.DEFAULT.fontFamily(),
            Font.DEFAULT.style(),
            Font.DEFAULT.stretch(),
            Font.DEFAULT.weight(),
            Font.DEFAULT.path());

    String firstFace = registry.fontFace(Font.DEFAULT, CONTEXT);
    Map<?, ?> resourceKeys = backendMap(registry, "resourceKeysByFont");
    Map<?, ?> fontFaces = backendMap(registry, "fontFacesByFont");
    Object firstKey = resourceKeys.get(Font.DEFAULT);
    Object firstFaceEntry = fontFaces.get(Font.DEFAULT);
    String repeatedFace = registry.fontFace(Font.DEFAULT, CONTEXT);
    String equivalentFace = registry.fontFace(equivalent, CONTEXT);

    assertAll(
        () -> assertEquals(firstFace, repeatedFace),
        () -> assertEquals(firstFace, equivalentFace),
        () -> assertEquals(1, faceCreations.get()),
        () -> assertEquals(1, resourceKeys.size()),
        () -> assertSame(firstKey, resourceKeys.get(equivalent)),
        () -> assertEquals(1, fontFaces.size()),
        () -> assertSame(firstFaceEntry, fontFaces.get(equivalent)));

    registry.releaseAfterContextDelete();

    assertAll(
        () -> assertEquals(0, resourceKeys.size()),
        () -> assertEquals(0, fontFaces.size()));
  }

  @Test
  void semanticGenerationChangeInvalidatesMemoizedResourceKeys() throws Exception {
    NvgFontRegistry registry = new NvgFontRegistry();
    assertEquals("A", registry.displayText(CONTEXT, Font.DEFAULT, "A"));
    Map<?, ?> resourceKeys = backendMap(registry, "resourceKeysByFont");
    Object before = resourceKeys.get(Font.DEFAULT);
    long beforeGeneration = registry.observation().semanticGeneration();

    fontService.clear();
    assertEquals("A", registry.displayText(CONTEXT, Font.DEFAULT, "A"));
    Object after = resourceKeys.get(Font.DEFAULT);

    assertAll(
        () -> assertEquals(beforeGeneration + 1, registry.observation().semanticGeneration()),
        () -> assertEquals(1, resourceKeys.size()),
        () -> assertNotSame(before, after));
  }

  @Test
  void resourceKeyMemoizationIsHardBoundedAndReleasedWithTheContext() throws Exception {
    NvgFontRegistry registry = new NvgFontRegistry();
    registry.bindContext(CONTEXT, Font.semanticOwner().observation());
    Method resourceKey = NvgFontRegistry.class.getDeclaredMethod("resourceKey", Font.class);
    resourceKey.setAccessible(true);

    for (int index = 0; index < 65; index++) {
      resourceKey.invoke(registry, new Font("Memoized family " + index, Font.DEFAULT.path()));
    }
    Map<?, ?> resourceKeys = backendMap(registry, "resourceKeysByFont");
    int retainedBeforeRelease = resourceKeys.size();
    registry.releaseAfterContextDelete();

    assertAll(
        () -> assertTrue(retainedBeforeRelease <= 64),
        () -> assertEquals(0, resourceKeys.size()));
  }

  @SuppressWarnings("unchecked")
  private static Map<?, ?> backendMap(NvgFontRegistry registry, String fieldName)
      throws ReflectiveOperationException {
    Field field = NvgFontRegistry.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return (Map<?, ?>) field.get(registry);
  }
}
