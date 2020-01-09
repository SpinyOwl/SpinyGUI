package com.spinyowl.spinygui.core.style.css.util;

import com.spinyowl.spinygui.core.node.base.Container;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;

public final class StyleUtils {
    private StyleUtils() {
    }


    public static NodeStyle getParentCalculatedStyle(Element element) {
        if (element == null) {
            return null;
        }
        Container parent = element.getParent();
        if (parent == null) {
            return null;
        }
        return parent.getCalculatedStyle();
    }
}
