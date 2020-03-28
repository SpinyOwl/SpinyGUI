package com.spinyowl.spinygui.core.node.element;

import com.spinyowl.spinygui.core.converter.dom.annotations.Tag;
import com.spinyowl.spinygui.core.node.base.Container;
import java.util.Objects;

@Tag("radio-button")
public class RadioButton extends Container {

    private RadioButtonGroup radioButtonGroup;

    public RadioButtonGroup getRadioButtonGroup() {
        return radioButtonGroup;
    }

    public void setRadioButtonGroup(RadioButtonGroup radioButtonGroup) {
        this.radioButtonGroup = radioButtonGroup;
    }

    public static class RadioButtonGroup {

        private final String name;

        public RadioButtonGroup(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RadioButtonGroup that = (RadioButtonGroup) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
