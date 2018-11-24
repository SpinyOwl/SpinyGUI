package com.spinyowl.spinygui.core.style.selector;

import com.spinyowl.spinygui.core.component.base.Component;

import java.util.Objects;

public interface StyleSelector {

    boolean test(Component component);

    default StyleSelector and(StyleSelector selector) {
        Objects.requireNonNull(selector);
        return t -> test(t) && selector.test(t);
    }

    default StyleSelector or(StyleSelector selector) {
        Objects.requireNonNull(selector);
        return t -> test(t) || selector.test(t);
    }

    default StyleSelector child(StyleSelector selector) {
        Objects.requireNonNull(selector);
        return t -> t.getParent() != null && test(t) && selector.test(t.getParent());
    }
}
