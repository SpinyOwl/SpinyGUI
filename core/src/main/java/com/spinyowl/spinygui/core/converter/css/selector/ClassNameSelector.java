package com.spinyowl.spinygui.core.converter.css.selector;

import com.spinyowl.spinygui.core.node.Element;
import java.util.stream.Stream;
import lombok.Data;

@Data
public class ClassNameSelector implements StyleSelector {

    private final String className;

    @Override
    public boolean test(Element element) {
        var classes = element.getAttribute("class");
        if (classes != null) {
            var classList = classes.split(" ");
            return Stream.of(classList).anyMatch(clazz -> clazz.equals(className));
        }
        return false;
    }

}
