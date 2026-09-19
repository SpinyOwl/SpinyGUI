package com.spinyowl.spinygui.core.cursor;

import static com.spinyowl.spinygui.core.node.NodeBuilder.ATTR_DISABLED;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_BUTTON;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.CURSOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.ButtonElement;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.types.CursorType;
import org.junit.jupiter.api.Test;

class CursorResolverTest {
  @Test
  void resolve_usesControlDefaults() {
    InputElement text = new InputElement();
    InputElement button = new InputElement();
    button.type(TYPE_BUTTON);

    assertEquals(CursorType.POINTER, CursorResolver.resolve(new ButtonElement()));
    assertEquals(CursorType.POINTER, CursorResolver.resolve(button));
    assertEquals(CursorType.TEXT, CursorResolver.resolve(text));
    assertEquals(CursorType.TEXT, CursorResolver.resolve(new TextareaElement()));
  }

  @Test
  void resolve_explicitStyleWinsAndDisabledControlFallsBackToDefault() {
    InputElement input = new InputElement();
    input.resolvedStyle().set(CURSOR, CursorType.MOVE);
    assertEquals(CursorType.MOVE, CursorResolver.resolve(input));

    input.resolvedStyle().set(CURSOR, CursorType.AUTO);
    input.setAttribute(ATTR_DISABLED, "");
    assertEquals(CursorType.DEFAULT, CursorResolver.resolve(input));
  }
}
