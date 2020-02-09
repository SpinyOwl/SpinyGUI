package com.spinyowl.spinygui.core.converter.css.util;

import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
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

    public static boolean validOneFourValue(String value, ValueExtractor<?> valueExtractor) {
        String[] values = value.split("\\s+");
        if (values.length == 0 || values.length > 4) {
            return false;
        }
        for (String length : values) {
            if (!valueExtractor.isValid(length)) {
                return false;
            }
        }

        return true;
    }
}
