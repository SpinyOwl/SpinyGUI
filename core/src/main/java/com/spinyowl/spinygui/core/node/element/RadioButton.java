package com.spinyowl.spinygui.core.node.element;
import com.spinyowl.spinygui.core.converter.dom.annotations.Tag;
import com.spinyowl.spinygui.core.node.base.Container;

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
    }
}
