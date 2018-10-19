package org.liquidengine.legui;

import org.liquidengine.legui.core.Legui;
import org.liquidengine.legui.core.api.Monitor;
import org.liquidengine.legui.core.api.Window;
import org.liquidengine.legui.core.component.*;
import org.liquidengine.legui.core.component.base.Component;
import org.liquidengine.legui.core.component.base.Text;
import org.liquidengine.legui.core.converter.ComponentMarshaller;


/**
 * Created by ShchAlexander on 09.08.2018.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Monitor monitor = Legui.getPrimaryMonitor();
        Window window = Legui.createWindow(800, 600, "Example", null);

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
                "  <org.liquidengine.legui.core.component.RadioButton />\n" +
                "</div>";

        Component unmarshal = ComponentMarshaller.unmarshal(xml2);
        System.out.println(ComponentMarshaller.marshal(unmarshal));
    }

}
