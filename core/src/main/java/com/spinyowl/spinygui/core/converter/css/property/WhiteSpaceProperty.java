package com.spinyowl.spinygui.core.converter.css.property;

import static com.spinyowl.spinygui.core.converter.css.Properties.WHITE_SPACE;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;

public class WhiteSpaceProperty extends Property<WhiteSpace> {

  public WhiteSpaceProperty() {
    super(WHITE_SPACE, WhiteSpace.NORMAL.name(), INHERITED, !ANIMATABLE,
        NodeStyle::whiteSpace, NodeStyle::whiteSpace,
        WhiteSpace::find, WhiteSpace::contains);
  }

}
