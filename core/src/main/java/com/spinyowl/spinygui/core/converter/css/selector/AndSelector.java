package com.spinyowl.spinygui.core.converter.css.selector;

import com.spinyowl.spinygui.core.node.base.Element;
import lombok.Data;

@Data
public class AndSelector implements StyleSelector {

    private final StyleSelector first;
    private final StyleSelector second;

    @Override
    public boolean test(Element element) {
        return first.test(element) && second.test(element);
    }

}
