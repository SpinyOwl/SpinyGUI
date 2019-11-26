package com.spinyowl.spinygui.core.style.css.property.padding;

import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class PaddingProperty extends Property {

    private ValueExtractor<Length> lengthValueExtractor = ValueExtractors.getInstance().getValueExtractor(Length.class);

    public PaddingProperty() {
        super(Properties.PADDING, null, false, true);
    }

    public PaddingProperty(String value) {
        this();
        setValue(value);
    }

    /**
     * Used to update node style with this property.
     *
     * @param nodeStyle node style to update.
     */
    @Override
    protected void updateNodeStyle(NodeStyle nodeStyle) {
        String value = this.getValue();
        String[] values = value.split("\\s+");
        switch (values.length) {
            case 1:
                nodeStyle.setPadding(
                        lengthValueExtractor.extract(values[0])
                );
                break;
            case 2:
                nodeStyle.setPadding(
                        lengthValueExtractor.extract(values[0]),
                        lengthValueExtractor.extract(values[1])
                );
                break;
            case 3:
                nodeStyle.setPadding(
                        lengthValueExtractor.extract(values[0]),
                        lengthValueExtractor.extract(values[1]),
                        lengthValueExtractor.extract(values[2])
                );
                break;
            case 4:
                nodeStyle.setPadding(
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
