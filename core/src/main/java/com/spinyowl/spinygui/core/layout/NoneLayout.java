package com.spinyowl.spinygui.core.layout;

public class NoneLayout implements ElementLayout {

  /** {@inheritDoc} */
  @Override
  public void layout(LayoutElement element, LayoutContext context) {
    // do nothing, as there is no need to do anything with this or child nodes, it is excluded.
  }
}
