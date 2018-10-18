package org.liquidengine.legui;

import org.liquidengine.legui.core.Legui;
import org.liquidengine.legui.core.api.Monitor;
import org.liquidengine.legui.core.api.Window;
import org.liquidengine.legui.core.component.Button;
import org.liquidengine.legui.core.component.Panel;
import org.liquidengine.legui.core.component.Input;
import org.liquidengine.legui.core.component.RadioButton;
import org.liquidengine.legui.core.component.base.ComponentBase;
import org.liquidengine.legui.core.component.base.TextComponent;
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
        ComponentBase element = new Panel()
                .add(new Button()
                        .add(new TextComponent("Hello World "))
                        .add(new Panel()
                                .add(new TextComponent("Bold"))
                        )
                )
                .add(input)
                .add(new RadioButton());


        String xml = ComponentMarshaller.marshal(element);
        System.out.println(xml);

        ComponentBase unmarshal = ComponentMarshaller.unmarshal(xml);
        System.out.println(ComponentMarshaller.marshal(unmarshal));
//        window.addCloseEventListener(event -> Legui.destroyWindow(window));
//        window.
    }

}
