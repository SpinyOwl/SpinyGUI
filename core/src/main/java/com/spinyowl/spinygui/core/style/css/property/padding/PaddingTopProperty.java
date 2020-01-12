package com.spinyowl.spinygui.core.style.css.property.padding;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class PaddingTopProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);

    public PaddingTopProperty() {
        super(Properties.PADDING_TOP, "0", false, true);
    }

    public PaddingTopProperty(String value) {
        this();
        setValue(value);
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
            nodeStyle.getPadding().setTop(Length.pixel(0));
        } else if (INHERIT.equals(value)) {
            NodeStyle pStyle = StyleUtils.getParentCalculatedStyle(element);
            if (pStyle != null) {
                nodeStyle.getPadding().setTop(pStyle.getPadding().getTop());
            } else {
                nodeStyle.getPadding().setTop(Length.pixel(0));
            }
        }
        nodeStyle.getPadding().setTop(lengthValueExtractor.extract(value));
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return super.isValid() || lengthValueExtractor.isValid(value);
    }
}
