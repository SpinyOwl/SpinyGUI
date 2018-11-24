package com.spinyowl.spinygui.demo;

import com.spinyowl.spinygui.core.api.Monitor;
import com.spinyowl.spinygui.core.api.Window;
import com.spinyowl.spinygui.core.component.*;
import com.spinyowl.spinygui.core.component.base.Component;
import com.spinyowl.spinygui.core.component.base.Text;
import com.spinyowl.spinygui.core.converter.ComponentMarshaller;
import com.spinyowl.spinygui.core.event.listener.impl.DefaultWindowCloseEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by ShchAlexander on 09.08.2018.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        LOGGER.info("a core message");
        Monitor monitor = Monitor.getPrimaryMonitor();
        System.out.println(monitor);

        Window window = Window.createWindow(800, 600, "Example");
        Window window2 = Window.createWindow(400, 300, "Example2", null);

        window.addWindowCloseEventListener(new DefaultWindowCloseEventListener());
//
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

//        window.getContainer().add(element);

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
    }

}
