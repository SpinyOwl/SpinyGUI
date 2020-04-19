package com.spinyowl.spinygui.core.node.element;

import com.spinyowl.spinygui.core.converter.dom.annotations.Tag;
import com.spinyowl.spinygui.core.node.EmptyElement;

@Tag("input")
public class Input extends EmptyElement {

  public String getValue() {
    return getAttribute("value");
  }

  public void setValue(String value) {
    setAttribute("value", value);
  }

  public String getName() {
    return getAttribute("name");
  }

  public void setName(String name) {
    setAttribute("name", name);
  }
}
