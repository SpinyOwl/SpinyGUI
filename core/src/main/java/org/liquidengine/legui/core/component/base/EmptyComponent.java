package org.liquidengine.legui.core.component.base;

public abstract class EmptyComponent extends Component {

    /**
     * Child operations are not supported for TextComponent.
     *
     * @param component component.
     * @throws UnsupportedOperationException because child operations are not supported for TextComponent.
     */
    @Override
    public void removeChild(ComponentBase component) {
        throw new UnsupportedOperationException("Child operations are not supported for TextComponent");
    }

    /**
     * Child operations are not supported for TextComponent.
     *
     * @param component component.
     * @throws UnsupportedOperationException because child operations are not supported for TextComponent.
     */
    @Override
    public void addChild(ComponentBase component) {
        throw new UnsupportedOperationException("Child operations are not supported for TextComponent");
    }

}
