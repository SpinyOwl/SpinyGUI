package com.spinyowl.spinygui.core.node;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.node.base.Text;
import com.spinyowl.spinygui.core.node.element.Button;
import com.spinyowl.spinygui.core.node.element.Div;
import com.spinyowl.spinygui.core.node.element.Input;
import com.spinyowl.spinygui.core.node.element.Label;
import com.spinyowl.spinygui.core.node.element.RadioButton;
import java.util.Collections;
import java.util.Map;

public final class NodeBuilder {

    private NodeBuilder() {
    }

    public static Button button(Node... nodes) {
        Button button = new Button();
        if (nodes != null) {
            for (Node node : nodes) {
                button.addChild(node);
            }
        }
        return button;
    }

    public static Button button(Map<String, String> attributes, Node... nodes) {
        return addAttributes(button(Collections.emptyMap(), nodes), attributes);
    }

    public static Text text(String text) {
        return new Text(text);
    }

    public static Input input() {
        return new Input();
    }

    public static Label label(Map<String, String> attributes, Node... nodes) {
        return addAttributes(label(nodes), attributes);
    }

    public static Label label(Node... nodes) {
        Label label = new Label();
        for (Node node : nodes) {
            label.addChild(node);
        }
        return label;
    }

    public static Div div(Map<String, String> attributes, Node... nodes) {
        return addAttributes(div(nodes), attributes);
    }

    public static Div div(Node... nodes) {
        Div div = new Div();
        for (Node node : nodes) {
            div.addChild(node);
        }
        return div;
    }

    public static RadioButton radioButton(Map<String, String> attributes, Node... nodes) {
        return addAttributes(radioButton(nodes), attributes);
    }

    public static RadioButton radioButton(Node... nodes) {
        RadioButton radioButton = new RadioButton();
        for (Node node : nodes) {
            radioButton.addChild(node);
        }
        return radioButton;
    }

    private static <T extends Element> T addAttributes(T element, Map<String, String> attributes) {
        element.setAttributes(attributes);
        return element;
    }
}
