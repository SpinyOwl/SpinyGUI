package org.liquidengine.legui.core.system.component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ElementNode extends Node {
    private Attributes attributes = new Attributes();
    private List<Node> childComponents = new CopyOnWriteArrayList<>();


    @Override
    protected void removeChild(Node node) {
        childComponents.remove(node);
    }

    @Override
    protected void addChild(Node node) {
        if (node == null || node == this || isContainsByRef(node)) return;

        Node parent = node.getParent();
        if (parent != null) parent.removeChild(node);

        node.setParent(this);
    }

    private boolean isContainsByRef(Node node) {
        return childComponents.stream().anyMatch(n -> n == node);
    }
}
