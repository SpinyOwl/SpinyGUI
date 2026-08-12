package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OPACITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.style.types.Color;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class NvgRenderUtilsTest {

  @Test
  void withPresentedOpacity_usesComputedOpacityWhenNoPresentationValueExists() {
    Element element = new Element("div");
    element.resolvedStyle().opacity(0.5f);

    assertEquals(Color.RED.withA(0.5f), NvgRenderUtils.withPresentedOpacity(Color.RED, element));
  }

  @Test
  void withPresentedOpacity_composesPresentedOpacityThroughPaintAncestors() {
    Element parent = new Element("div");
    parent.presentationState().setValue(OPACITY, 0.5f);
    Element child = new Element("div");
    child.presentationState().setValue(OPACITY, 0.4f);
    parent.addChild(child);

    assertEquals(Color.BLUE.withA(0.2f), NvgRenderUtils.withPresentedOpacity(Color.BLUE, child));
  }

  @Test
  void legacyClippingFacadeRemainsPublicAndStatic() throws NoSuchMethodException {
    assertPublicStatic("inScissor", long.class, Node.class, Runnable.class);
    assertPublicStatic("createScissor", long.class, Node.class);
    assertPublicStatic("createScissorByParent", long.class, Node.class);
    assertPublicStatic("resetScissor", long.class);
  }

  private void assertPublicStatic(String name, Class<?>... parameterTypes)
      throws NoSuchMethodException {
    int modifiers = NvgRenderUtils.class.getMethod(name, parameterTypes).getModifiers();
    assertTrue(Modifier.isPublic(modifiers));
    assertTrue(Modifier.isStatic(modifiers));
  }
}
