package com.spinyowl.spinygui.core.style.selector;

import com.spinyowl.spinygui.core.component.base.Component;
import com.spinyowl.spinygui.core.converter.css3.annotations.PseudoSelector;

import java.util.StringJoiner;

@PseudoSelector(":hover")
public class HoverSelector implements StyleSelector {
    @Override
    public boolean test(Component component) {

        //TODO implement component testing for hover

        return true;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HoverSelector.class.getSimpleName() + "[", "]")
                .toString();
    }
}
