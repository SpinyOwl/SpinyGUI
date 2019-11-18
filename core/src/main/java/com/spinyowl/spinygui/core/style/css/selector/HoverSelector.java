package com.spinyowl.spinygui.core.style.css.selector;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.converter.css.parser.annotations.PseudoSelector;

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
