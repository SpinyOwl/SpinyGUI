package com.spinyowl.spinygui.core.node;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.node.base.Text;
import com.spinyowl.spinygui.core.node.element.Button;
import com.spinyowl.spinygui.core.node.element.Div;
import com.spinyowl.spinygui.core.node.element.Label;
import java.util.Map;
import org.joml.Vector2fc;

public class ElementBuilder<T extends Element> {

    private T node;

    public ElementBuilder(T node) {
        this.node = node;
    }

    public static ElementBuilder<Button> button(Node... nodes) {
        Button node = new Button();
        if (nodes != null && nodes.length > 0) {
            for (Node child : nodes) {
                node.addChild(child);
            }
        }
        return new ElementBuilder<>(node);
    }

    public static ElementBuilder<Div> div(Node... nodes) {
        Div node = new Div();
        if (nodes != null && nodes.length > 0) {
            for (Node child : nodes) {
                node.addChild(child);
            }
        }
        return new ElementBuilder<>(node);
    }

    public static ElementBuilder<Label> label(Node... nodes) {
        Label node = new Label();
        if (nodes != null && nodes.length > 0) {
            for (Node child : nodes) {
                node.addChild(child);
            }
        }
        return new ElementBuilder<>(node);
    }

    public static Text text(String text) {
        return new Text(text);
    }

    public ElementBuilder<T> position(Vector2fc position) {
        node.setPosition(position);
        return this;
    }

    public ElementBuilder<T> size(Vector2fc size) {
        node.setSize(size);
        return this;
    }

    public ElementBuilder<T> dimensions(Vector2fc position, Vector2fc size) {
        node.setPosition(position);
        node.setSize(size);
        return this;
    }

    public ElementBuilder<T> position(float x, float y) {
        node.setPosition(x, y);
        return this;
    }

    public ElementBuilder<T> dimensions(float x, float y, float width, float height) {
        node.setPosition(x, y);
        node.setSize(width, height);
        return this;
    }

    public ElementBuilder<T> size(float width, float height) {
        node.setSize(width, height);
        return this;
    }

    public ElementBuilder<T> attributes(Map<String, String> attributes) {
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            node.setAttribute(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public ElementBuilder<T> attribute(String key, String value) {
        node.setAttribute(key, value);
        return this;
    }

    /**
     * Used to add listener for specified event class.
     *
     * @param eventClass event class.
     * @param listener   listener
     */
    public <E extends Event, L extends EventListener<E>> ElementBuilder<T> listener(
        Class<E> eventClass, L listener) {
        node.addListener(eventClass, listener);
        return this;
    }

    public T build() {
        return node;
    }

}
