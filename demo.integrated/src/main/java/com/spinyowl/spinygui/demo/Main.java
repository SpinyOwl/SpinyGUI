package com.spinyowl.spinygui.demo;

import com.spinyowl.spinygui.core.api.Window;
import com.spinyowl.spinygui.core.node.*;
import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.node.base.Text;
import com.spinyowl.spinygui.core.event.listener.impl.DefaultWindowCloseEventListener;

public class Main {

    public static void main(String[] args) {

        Window window = Window.createWindow(800, 600, "Example");
        window.addWindowCloseEventListener(new DefaultWindowCloseEventListener());

        Input input = new Input();
        input.setName("password");
        input.setValue("PASS_@!@#&");

        Node element = new Panel()
                .add(new Button()
                        .add(new Text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n"))
                        .add(new Panel()
                                .add(new Text("Bold"))
                        )
                        .add(new Pre().add(new Text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n")))
                )
                .add(input)
                .add(new RadioButton());

        window.getContainer().add(element);
        window.setVisible(true);
    }
}
