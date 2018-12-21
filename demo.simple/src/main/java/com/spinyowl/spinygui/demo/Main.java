package com.spinyowl.spinygui.demo;

import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;
import com.spinyowl.spinygui.core.component.*;
import com.spinyowl.spinygui.core.component.base.Component;
import com.spinyowl.spinygui.core.component.base.Text;
import com.spinyowl.spinygui.core.converter.ComponentMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by ShchAlexander on 09.08.2018.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        LOGGER.info("a test message");
        Monitor monitor = Monitor.getPrimaryMonitor();
        System.out.println(monitor);

        Window window = Window.createWindow(800, 600, "Example");

        Input input = new Input();
        input.setName("password");
        input.setValue("PASS_@!@#&");

        Button button = new Button();
        Text buttonText = new Text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n");
        Panel buttonPanel = new Panel();
        Text buttonPanelText = new Text("Bold");
        Pre buttonPre = new Pre();
        Text buttonPreText = new Text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n");
        RadioButton radioButton = new RadioButton();
        Component element = new Panel()
                .add(button
                        .add(buttonText)
                        .add(buttonPanel.add(buttonPanelText))
                        .add(buttonPre.add(buttonPreText))
                )
                .add(input)
                .add(radioButton);
        element.setPosition(100, 100);
        window.getContainer().add(element);

        String xml = ComponentMarshaller.marshal(element, false);
        System.out.println(xml);

        String xml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<div>\n" +
                "  <button>asdfasdfasd</button>" +
                "  <button>\n" +
                "    \n\n\n\n\n\n" +
                "    s\n" +
                "    \n" +
                "    Hello World\n\n\n\n\n" +
                "    <pre>\n" +
                "    \n\n\n\n\n\n" +
                "    s\n" +
                "    \n" +
                "    Hello World\n" +
                "    </pre>\n" +
                "    <div>Bold</div>\n" +
                "  </button>\n" +
                "  <input name=\"password\" value=\"PASS_@!@#&amp;\" />\n" +
                "  <RadioButton />\n" +
                "</div>";
        Component unmarshal = ComponentMarshaller.unmarshal(xml2);
        System.out.println(ComponentMarshaller.marshal(unmarshal));
        window.setVisible(true);
    }

}
