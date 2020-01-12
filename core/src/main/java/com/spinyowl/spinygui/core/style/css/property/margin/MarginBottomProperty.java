package com.spinyowl.spinygui.core.style.css.property.margin;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.length.Auto;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class MarginBottomProperty extends Property {

    private ValueExtractor<Unit> unitValueExtractor = ValueExtractors.getInstance().getValueExtractor(Unit.class);

    public MarginBottomProperty() {
        super(Properties.MARGIN_BOTTOM, "0", false, true);
    }

    public MarginBottomProperty(String value) {
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
            nodeStyle.getMargin().setBottom(Auto.AUTO);
        } else if (INHERIT.equals(value)) {
            NodeStyle pStyle = StyleUtils.getParentCalculatedStyle(element);
            if (pStyle != null) {
                nodeStyle.getMargin().setBottom(pStyle.getMargin().getBottom());
            } else {
                nodeStyle.getMargin().setBottom(Auto.AUTO);
            }
        }
        nodeStyle.getMargin().setBottom(unitValueExtractor.extract(value));
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return super.isValid() || unitValueExtractor.isValid(value);
    }
}
