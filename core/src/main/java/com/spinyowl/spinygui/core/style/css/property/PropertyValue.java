package com.spinyowl.spinygui.core.style.css.property;

public interface PropertyValue {

    boolean isInitial();
//
    boolean isInherit();

    void updateNodeStyle(NodeStyle nodeStyle);
}
