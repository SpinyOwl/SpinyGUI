package com.spinyowl.spinygui.demo;

import com.spinyowl.spinygui.core.api.Window;
import com.spinyowl.spinygui.core.component.*;
import com.spinyowl.spinygui.core.component.base.Component;
import com.spinyowl.spinygui.core.component.base.Text;

public class Main {

    public static void main(String[] args) {

        Window window = Window.createWindow(800, 600, "Example");

        Input input = new Input();
        input.setName("password");
        input.setValue("PASS_@!@#&");

        Component element = new Panel()
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
