package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.StyleSheet;

public interface StyleManager {

    void recalculateStyles(Frame frame);

    void recalculateStyles(Element elementTree, StyleSheet styleSheets);

    void recalculateStyles(Element element, Property property);

}
