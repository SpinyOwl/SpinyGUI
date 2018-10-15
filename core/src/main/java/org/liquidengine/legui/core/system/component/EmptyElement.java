package org.liquidengine.legui.core.system.component;

public class EmptyElement extends Element {

    /**
     * Child operations are not supported for TextNode.
     *
     * @param node node.
     * @throws UnsupportedOperationException because child operations are not supported for TextNode.
     */
    @Override
    public void removeChild(Node node) {
        throw new UnsupportedOperationException("Child operations are not supported for TextNode");
    }

    /**
     * Child operations are not supported for TextNode.
     *
     * @param node node.
     * @throws UnsupportedOperationException because child operations are not supported for TextNode.
     */
    @Override
    public void addChild(Node node) {
        throw new UnsupportedOperationException("Child operations are not supported for TextNode");
    }

}
