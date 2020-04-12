package com.spinyowl.spinygui.core.node.element;

import com.spinyowl.spinygui.core.converter.dom.annotations.Tag;
import com.spinyowl.spinygui.core.node.Container;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Tag("radio-button")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RadioButton extends Container {

    private RadioButtonGroup radioButtonGroup;

    @Data
    public static class RadioButtonGroup {

        private final String name;
    }
}
