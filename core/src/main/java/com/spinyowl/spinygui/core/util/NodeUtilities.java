package com.spinyowl.spinygui.core.util;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.api.Layer;
import com.spinyowl.spinygui.core.node.base.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public final class NodeUtilities {

    private NodeUtilities() {
    }

    /**
     * Used to determine if component is visible in parent components.
     *
     * @param component component to check.
     * @return true if component is visible in all chain of parent components.
     */
    public static boolean visibleInParents(Node component) {
        // TODO: Add overflow check (if overflow support will be added).
        List<Node> parentList = new ArrayList<>();
        for (Node parent = component.getParent(); parent != null; parent = parent.getParent()) {
            parentList.add(parent);
        }

        if (!parentList.isEmpty()) {
            Vector2f pos = new Vector2f(0, 0);
            Vector2f rect = new Vector2f(0, 0);
            Vector2f absolutePosition = component.getAbsolutePosition();

            Vector2fc cSize = component.getSize();
            Vector2fc cPos = component.getPosition();

            float lx = absolutePosition.x;
            float rx = absolutePosition.x + cSize.x();
            float ty = absolutePosition.y;
            float by = absolutePosition.y + cSize.y();

            // check top parent

            if (cPos.x() > component.getParent().getSize().x() ||
                cPos.x() + cSize.x() < 0 ||
                cPos.y() > component.getParent().getSize().y() ||
                cPos.y() + cSize.y() < 0
            ) {
                return false;
            }
            if (parentList.size() != 1) {
                // check from bottom parent to top parent
                for (int i = parentList.size() - 1; i >= 1; i--) {
                    Node parent = parentList.get(i);
                    pos.add(parent.getPosition());
                    rect.set(pos).add(parent.getSize());

                    if (lx > rect.x || rx < pos.x || ty > rect.y || by < pos.y) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static Frame getFrame(Node node) {
        Objects.requireNonNull(node);
        do {
            if (node instanceof Layer) {
                return ((Layer) node).getFrame();
            }
        } while ((node = node.getParent()) != null);
        return null;
    }
}
