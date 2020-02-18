package com.spinyowl.spinygui.demo;

import static com.spinyowl.spinygui.core.node.ElementBuilder.button;
import static com.spinyowl.spinygui.core.node.ElementBuilder.div;
import static com.spinyowl.spinygui.core.node.ElementBuilder.text;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.converter.NodeConverter;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.node.element.Div;
import com.spinyowl.spinygui.core.node.element.Input;
import com.spinyowl.spinygui.core.node.element.RadioButton;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by ShchAlexander on 09.08.2018.
 */
public class Main {

    private static final Log LOGGER = LogFactory.getLog(Main.class);

    public static void main(String[] args) throws Exception {

        LOGGER.info("a core message");

//        Monitor monitor = Monitor.getPrimaryMonitor();
//        Window window = Window.createWindow("Example window", 800, 600, monitor);

//        window.addCloseEventListener(Window.EXIT_ON_CLOSE);

        Input input = new Input();
        input.setName("password");
        input.setValue("PASS_@!@#&");

        Div buttonPanel = div(text("Bold")).build();
        Node buttonPre = button(text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n")).build();
        Node button = button(text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n"),
            buttonPanel, buttonPre).build();
        RadioButton radioButton = new RadioButton();
        Node element = div(button, input, radioButton).position(100, 100).build();

        Frame frame = new Frame();// window.getFrame();
        frame.getDefaultLayer().addChild(element);
        LOGGER.info(NodeUtilities.getFrame(input) == frame);
//        window.getContainer().addChild(element);

        String xml = NodeConverter.toXml(element, false);
        LOGGER.info(xml);
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
        Node unmarshal = NodeConverter.fromXml(xml2);

        LOGGER.info("UNMARSHALLING IS FINISHED");
        LOGGER.info(unmarshal instanceof Element);

//        window.setVisible(true);
    }

}
