package com.spinyowl.spinygui.core.component.base;

public abstract class EmptyComponent extends Container {

    /**
     * Child operations are not supported for {@link Text}.
     *
     * @param component component.
     * @throws UnsupportedOperationException because child operations are not supported for {@link Text}.
     */
    @Override
    public final void removeChild(Component component) {
        throw new UnsupportedOperationException("Child operations are not supported for Text.");
    }

    /**
     * Child operations are not supported for {@link Text}.
     *
     * @param component component.
     * @throws UnsupportedOperationException because child operations are not supported for {@link Text}.
     */
    @Override
    public final void addChild(Component component) {
        throw new UnsupportedOperationException("Child operations are not supported for Text.");
    }

}
