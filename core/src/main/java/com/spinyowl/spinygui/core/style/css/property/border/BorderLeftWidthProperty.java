package com.spinyowl.spinygui.core.style.css.property.border;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.length.Length;

import static com.spinyowl.spinygui.core.style.css.property.border.BorderWidthProperty.*;

public class BorderLeftWidthProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);

    public BorderLeftWidthProperty() {
        super(Properties.BORDER_LEFT_WIDTH, MEDIUM, false, true);
    }

    public BorderLeftWidthProperty(String value) {
        super(Properties.BORDER_LEFT_WIDTH, MEDIUM, false, true, value);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        NodeStyle nodeStyle = element.getCalculatedStyle();
        if (INITIAL.equals(value)) {
            nodeStyle.getBorder().getLeft().setWidth(MEDIUM_VALUE);
        } else if (INHERIT.equals(value)) {
            NodeStyle parentStyle = StyleUtils.getParentCalculatedStyle(element);
            if (parentStyle != null) {
                Length parentLeftWidth = parentStyle.getBorder().getLeft().getWidth();
                nodeStyle.getBorder().getLeft().setWidth(parentLeftWidth == null ? MEDIUM_VALUE : parentLeftWidth);
            } else {
                nodeStyle.getBorder().getLeft().setWidth(MEDIUM_VALUE);
            }
        }
        nodeStyle.getBorder().getLeft().setWidth(getLength(value, lengthValueExtractor));
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return super.isValid() ||
                isValidBorderWidthValue(value, lengthValueExtractor);
    }
}
