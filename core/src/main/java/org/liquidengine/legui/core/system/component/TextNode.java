package org.liquidengine.legui.core.system.component;

public class TextNode extends Node {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected void removeChild(Node node) {
        throw new UnsupportedOperationException("Child operations are not supported for TextNode");
    }

    @Override
    protected void addChild(Node node) {
        throw new UnsupportedOperationException("Child operations are not supported for TextNode");
    }
}
