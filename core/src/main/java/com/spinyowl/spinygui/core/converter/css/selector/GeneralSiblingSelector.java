package com.spinyowl.spinygui.core.converter.css.selector;

import com.spinyowl.spinygui.core.node.Element;
import java.util.Collections;
import java.util.List;
import lombok.Data;

@Data
public class GeneralSiblingSelector implements StyleSelector {

    private final StyleSelector first;
    private final StyleSelector second;

    @Override
    public boolean test(Element element) {
        boolean secondTest = second.test(element);
        if (!secondTest) {
            return false;
        }

        Element parent = element.parent();
        if (parent == null) {
            return false;
        }

        List<Element> siblings = parent.children();
        int nodeIndex = siblings.indexOf(element);
        if (nodeIndex == 0) {
            return false;
        }

        siblings = siblings.subList(0, nodeIndex);
        Collections.reverse(siblings);

        for (Element sibling : siblings) {
            if (first.test(sibling)) {
                return true;
            }
        }

        return false;
    }
}
