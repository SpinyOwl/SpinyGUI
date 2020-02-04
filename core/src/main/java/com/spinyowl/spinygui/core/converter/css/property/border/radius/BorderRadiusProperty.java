package com.spinyowl.spinygui.core.converter.css.property.border.radius;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.converter.css.util.StyleUtils;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderRadiusProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);

    public BorderRadiusProperty() {
        super(Properties.BORDER_RADIUS, "0", false, true);
    }

    public BorderRadiusProperty(String value) {
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
            nodeStyle.getBorderRadius().set(Length.pixel(0));
        } else if (INHERIT.equals(value)) {
            NodeStyle pStyle = StyleUtils.getParentCalculatedStyle(element);
            if (pStyle != null) {
                nodeStyle.getBorderRadius().set(pStyle.getBorderRadius());
            } else {
                nodeStyle.getBorderRadius().set(Length.pixel(0));
            }
        }
        String value = this.getValue();
        String[] values = value.split("\\s+");
        switch (values.length) {
            case 1:
                nodeStyle.getBorderRadius().set(
                        lengthValueExtractor.extract(values[0])
                );
                break;
            case 2:
                nodeStyle.getBorderRadius().set(
                        lengthValueExtractor.extract(values[0]),
                        lengthValueExtractor.extract(values[1])
                );
                break;
            case 3:
                nodeStyle.getBorderRadius().set(
                        lengthValueExtractor.extract(values[0]),
                        lengthValueExtractor.extract(values[1]),
                        lengthValueExtractor.extract(values[2])
                );
                break;
            case 4:
                nodeStyle.getBorderRadius().set(
                        lengthValueExtractor.extract(values[0]),
                        lengthValueExtractor.extract(values[1]),
                        lengthValueExtractor.extract(values[2]),
                        lengthValueExtractor.extract(values[3])
                );
                break;
            default:
                break;
        }
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return super.isValid() && StyleUtils.validOneFourValue(value, lengthValueExtractor);
    }
}
