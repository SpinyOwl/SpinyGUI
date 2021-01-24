package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WHITE_SPACE;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.node.style.types.WhiteSpace;

public class WhiteSpaceProperty extends Property<WhiteSpace> {

  public WhiteSpaceProperty() {
    super(WHITE_SPACE, WhiteSpace.NORMAL.name(), INHERITED, !ANIMATABLE,
        NodeStyle::whiteSpace, NodeStyle::whiteSpace,
        WhiteSpace::find, WhiteSpace::contains);
  }

}
