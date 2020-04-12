package com.spinyowl.spinygui.core.converter.css.selector;

import com.spinyowl.spinygui.core.node.Element;
import lombok.Data;

@Data
public class TypeSelector implements StyleSelector {

    private final Class<?> type;

    @Override
    public boolean test(Element node) {
        return node.getClass().equals(type);
    }

}
