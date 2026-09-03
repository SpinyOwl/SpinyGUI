package com.spinyowl.spinygui.core.binding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.event.MouseClickEvent;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import org.junit.jupiter.api.Test;

class XmlEventDeclarationTest {
  /** Real parser used to prove normalization and ordinary attribute round trips. */
  private final DefaultNodeParser parser = new DefaultNodeParser();

  @Test
  void mapsNormalizedSupportedAttributesToExactEventClasses() {
    Element button =
        assertInstanceOf(
            Element.class,
            parser.fromHtml("<button ON-ACTION=\"save\" ON-CLICK=\"inspect\">Save</button>"));

    XmlEventDeclaration action =
        XmlEventDeclaration.fromAttribute("on-action", button.getAttribute("on-action"))
            .orElseThrow();
    XmlEventDeclaration click =
        XmlEventDeclaration.fromAttribute("on-click", button.getAttribute("on-click"))
            .orElseThrow();

    assertEquals("on-action", action.attributeName());
    assertEquals("save", action.handlerName());
    assertEquals(ActionEvent.class, action.eventClass());
    assertEquals("on-click", click.attributeName());
    assertEquals("inspect", click.handlerName());
    assertEquals(MouseClickEvent.class, click.eventClass());
  }

  @Test
  void supportedDeclarationsRoundTripThroughOrdinaryAttributes() {
    Element original =
        assertInstanceOf(
            Element.class,
            parser.fromHtml("<button on-action=\"save\" on-click=\"inspect\">Save</button>"));

    String serialized = parser.toHtml(original, false);
    Element reparsed = assertInstanceOf(Element.class, parser.fromHtml(serialized));

    assertEquals("save", reparsed.getAttribute("on-action"));
    assertEquals("inspect", reparsed.getAttribute("on-click"));
    assertEquals(
        ActionEvent.class,
        XmlEventDeclaration.fromAttribute("on-action", reparsed.getAttribute("on-action"))
            .orElseThrow()
            .eventClass());
    assertEquals(
        MouseClickEvent.class,
        XmlEventDeclaration.fromAttribute("on-click", reparsed.getAttribute("on-click"))
            .orElseThrow()
            .eventClass());
  }

  @Test
  void rejectsBlankValuesForSupportedDeclarations() {
    Element button =
        assertInstanceOf(Element.class, parser.fromHtml("<button on-action=\"  \"/>"));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            XmlEventDeclaration.fromAttribute("on-action", button.getAttribute("on-action")));
    assertThrows(
        NullPointerException.class,
        () -> XmlEventDeclaration.fromAttribute("on-click", null));
  }

  @Test
  void leavesUnrelatedAndUnsupportedOnAttributesUntouched() {
    Element element =
        assertInstanceOf(
            Element.class,
            parser.fromHtml("<section id=\"panel\" data-handler=\"meta\" on-hover=\"preview\"/>"));

    assertEquals("panel", element.getAttribute("id"));
    assertEquals("meta", element.getAttribute("data-handler"));
    assertEquals("preview", element.getAttribute("on-hover"));
    assertTrue(XmlEventDeclaration.fromAttribute("data-handler", null).isEmpty());
    assertTrue(XmlEventDeclaration.fromAttribute("on-hover", "preview").isEmpty());
    assertTrue(XmlEventDeclaration.fromAttribute("ON-ACTION", "save").isEmpty());
  }

  @Test
  void leavesTemplatesWithoutHandlerAttributesUnchanged() {
    Element original =
        assertInstanceOf(
            Element.class,
            parser.fromHtml("<section id=\"panel\" data-state=\"ready\"><span>Ready</span></section>"));

    String serialized = parser.toHtml(original, false);
    Element reparsed = assertInstanceOf(Element.class, parser.fromHtml(serialized));

    assertEquals(original.nodeName(), reparsed.nodeName());
    assertEquals(original.attributes(), reparsed.attributes());
    assertEquals(original.childNodes().size(), reparsed.childNodes().size());
    assertTrue(XmlEventDeclaration.fromAttribute("id", reparsed.getAttribute("id")).isEmpty());
    assertTrue(
        XmlEventDeclaration.fromAttribute("data-state", reparsed.getAttribute("data-state"))
            .isEmpty());
  }
}
