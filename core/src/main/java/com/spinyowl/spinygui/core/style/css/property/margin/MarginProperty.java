package com.spinyowl.spinygui.core.style.css.property.margin;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class MarginProperty extends Property {

    private ValueExtractor<Unit> unitValueExtractor = ValueExtractors.getInstance().getValueExtractor(Unit.class);

    public MarginProperty() {
        super(Properties.MARGIN, null, false, true);
    }

    public MarginProperty(String value) {
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
        if (INITIAL.equalsIgnoreCase(value) || INHERIT.equalsIgnoreCase(value)) {
            // todo: add implementation for initial and inherit values
            return;
        }
        String value = this.getValue();
        String[] values = value.split("\\s+");
        switch (values.length) {
            case 1:
                nodeStyle.getMargin().set(
                        unitValueExtractor.extract(values[0])
                );
                break;
            case 2:
                nodeStyle.getMargin().set(
                        unitValueExtractor.extract(values[0]),
                        unitValueExtractor.extract(values[1])
                );
                break;
            case 3:
                nodeStyle.getMargin().set(
                        unitValueExtractor.extract(values[0]),
                        unitValueExtractor.extract(values[1]),
                        unitValueExtractor.extract(values[2])
                );
                break;
            case 4:
                nodeStyle.getMargin().set(
                        unitValueExtractor.extract(values[0]),
                        unitValueExtractor.extract(values[1]),
                        unitValueExtractor.extract(values[2]),
                        unitValueExtractor.extract(values[3])
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
            if (!unitValueExtractor.isValid(length)) {
                return false;
            }
        }

        return true;
    }
}
