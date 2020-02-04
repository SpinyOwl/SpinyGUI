package com.spinyowl.spinygui.core.converter.css.selector;

import com.spinyowl.spinygui.core.converter.css.parser.annotations.PseudoSelector;
import com.spinyowl.spinygui.core.node.base.Element;

import java.util.StringJoiner;

@PseudoSelector(":hover")
public class HoverSelector implements StyleSelector {
    @Override
    public boolean test(Element node) {
        return node.isHovered();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HoverSelector.class.getSimpleName() + "[", "]")
                .toString();
    }

}
