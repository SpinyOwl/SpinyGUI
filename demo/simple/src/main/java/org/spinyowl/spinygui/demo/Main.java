package org.spinyowl.spinygui.demo;

import org.spinyowl.spinygui.core.SpinyGui;
import org.spinyowl.spinygui.core.api.Monitor;
import org.spinyowl.spinygui.core.api.Window;
import org.spinyowl.spinygui.core.component.*;
import org.spinyowl.spinygui.core.component.base.Component;
import org.spinyowl.spinygui.core.component.base.Text;
import org.spinyowl.spinygui.core.converter.ComponentMarshaller;


/**
 * Created by ShchAlexander on 09.08.2018.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Monitor monitor = SpinyGui.getPrimaryMonitor();
        Window window = SpinyGui.createWindow(800, 600, "Example", null);

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
