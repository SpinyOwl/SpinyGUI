package com.spinyowl.spinygui.core;

import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.node.base.Text;
import com.spinyowl.spinygui.core.node.element.*;

public final class NodeBuilder {
    private NodeBuilder() {
    }

    public static Button button() {
        return new Button();
    }

    public static Button button(Node... nodes) {
        Button button = new Button();
        for (Node node : nodes) {
            button.addChild(node);
        }
        return button;
    }

    public static Text text(String text) {
        return new Text(text);
    }

    public static Input input() {
        return new Input();
    }

    public static Label label() {
        return new Label();
    }

    public static Label label(Node... nodes) {
        Label label = new Label();
        for (Node node : nodes) {
            label.addChild(node);
        }
        return label;
    }

    public static Div panel() {
        return new Div();
    }

    public static Div panel(Node... nodes) {
        Div div = new Div();
        for (Node node : nodes) {
            div.addChild(node);
        }
        return div;
    }

    public static RadioButton radioButton() {
        return new RadioButton();
    }

    public static RadioButton radioButton(Node... nodes) {
        RadioButton radioButton = new RadioButton();
        for (Node node : nodes) {
            radioButton.addChild(node);
        }
        return radioButton;
    }
}
