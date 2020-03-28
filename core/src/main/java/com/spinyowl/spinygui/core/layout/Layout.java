package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.base.Element;

/**
 * Layout interface. Specifies rules on how to lay out child and parent components.
 */
public interface Layout {

    /**
     * Used to lay out element and it's child nodes.
     *
     * @param element element to lay out.
     */
    void layout(Element element);

}
