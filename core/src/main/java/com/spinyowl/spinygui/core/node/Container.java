package com.spinyowl.spinygui.core.node;

import com.spinyowl.spinygui.core.util.Reference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class Container extends Element {

    /**
     * Child nodes.
     */
    private List<Node> childNodes = new CopyOnWriteArrayList<>();

    /**
     * Used to remove child node.
     *
     * @param node node to remove.
     */
    @Override
    public void removeChild(Node node) {
        childNodes.remove(node);
    }

    /**
     * Used to add child node.
     *
     * @param node node to add.
     */
    @Override
    public void addChild(Node node) {
        if (node == null || node == this || Reference.contains(childNodes, node)) {
            return;
        }

        Container parent = node.parent();
        if (parent != null) {
            parent.removeChild(node);
        }

        childNodes.add(node);

        node.parent(this);
    }

    /**
     * Used to get child nodes.
     *
     * @return list of child nodes.
     */
    @Override
    public List<Node> childNodes() {
        return childNodes.stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean hasChildNodes() {
        return !childNodes.isEmpty();
    }

}
