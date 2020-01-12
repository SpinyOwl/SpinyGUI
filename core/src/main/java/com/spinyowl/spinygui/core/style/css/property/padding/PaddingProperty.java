package com.spinyowl.spinygui.core.style.css.property.padding;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class PaddingProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);

    public PaddingProperty() {
        super(Properties.PADDING, "0", false, true);
    }

    public PaddingProperty(String value) {
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
            nodeStyle.getPadding().set(Length.pixel(0));
        } else if (INHERIT.equals(value)) {
            NodeStyle pStyle = StyleUtils.getParentCalculatedStyle(element);
            if (pStyle != null) {
                nodeStyle.getPadding().set(pStyle.getPadding());
            } else {
                nodeStyle.getPadding().set(Length.pixel(0));
            }
        }
        String value = this.getValue();
        String[] values = value.split("\\s+");
        switch (values.length) {
            case 1:
                nodeStyle.getPadding().set(
                        lengthValueExtractor.extract(values[0])
                );
                break;
            case 2:
                nodeStyle.getPadding().set(
                        lengthValueExtractor.extract(values[0]),
                        lengthValueExtractor.extract(values[1])
                );
                break;
            case 3:
                nodeStyle.getPadding().set(
                        lengthValueExtractor.extract(values[0]),
                        lengthValueExtractor.extract(values[1]),
                        lengthValueExtractor.extract(values[2])
                );
                break;
            case 4:
                nodeStyle.getPadding().set(
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
        if (super.isValid()) {
            return true;
        }

        String value = getValue();
        String[] values = value.split("\\s+");
        if (values.length <= 0 || values.length > 4) {
            return false;
        }
        for (String length : values) {
            if (!lengthValueExtractor.isValid(length)) {
                return false;
            }
        }

        return true;
    }
}
