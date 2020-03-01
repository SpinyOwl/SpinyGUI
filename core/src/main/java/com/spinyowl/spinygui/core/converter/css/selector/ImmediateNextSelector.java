package com.spinyowl.spinygui.core.converter.css.selector;

import com.spinyowl.spinygui.core.node.base.Element;
import java.util.List;
import lombok.Data;

@Data
public class ImmediateNextSelector implements StyleSelector {

    private final StyleSelector first;
    private final StyleSelector second;

    @Override
    public boolean test(Element t) {
        boolean secondTest = second.test(t);
        if (!secondTest) {
            return false;
        }

        Element parent = t.getParent();
        if (parent == null) {
            return false;
        }

        List<Element> siblings = parent.getChildElements();
        int indexOfComponent = siblings.indexOf(t);
        if (indexOfComponent != 0) {
            return first.test(siblings.get(indexOfComponent - 1));
        }
        return false;
    }
}
