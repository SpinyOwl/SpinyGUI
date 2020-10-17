package com.spinyowl.spinygui.demo;

import static com.spinyowl.spinygui.core.node.NodeBuilder.INPUT_PASSWORD;
import static com.spinyowl.spinygui.core.node.NodeBuilder.button;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.input;
import static com.spinyowl.spinygui.core.node.NodeBuilder.radioButton;
import static com.spinyowl.spinygui.core.node.NodeBuilder.text;
import com.spinyowl.spinygui.core.api.DefaultFrame;
import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.converter.NodeConverter;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by ShchAlexander on 09.08.2018.
 */
@Slf4j
public class Main {

  public static void main(String[] args) throws Exception {

//        Monitor monitor = Monitor.getPrimaryMonitor();
//        Window window = Window.createWindow("Example window", 800, 600, monitor);

//        window.addCloseEventListener(Window.EXIT_ON_CLOSE);

    Node element = div(
        button(
            text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n"),
            div(text("Bold")),
            button(text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n"))
        ).with("name", "myAwesomeButton").with("id", "bid1"),
        input(INPUT_PASSWORD, "myPass", "PASS_@!@#&"),
        radioButton("radio", "firstValue")
    );

    Frame frame = new DefaultFrame();// we can obtain frame also using window.getFrame()
    frame.defaultLayer().addChild(element);
    log.info(
        String.valueOf(NodeUtilities.getFrame(input("password", "myPass", "PASS_@!@#&")) == frame));

    String xml = NodeConverter.toXml(element, false);
    log.info(xml);
    //language=XML
    String xml2 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<div>" +
            "  <button>asdfasdfasd</button>"
            + "  " +
            "<button>\n" +
            "    s\n" +
            "    \n" +
            "    Hello World\n\n" +

            "    <pre _pre=\"true\">\n" +
            "    s\n" +
            "    \n" +
            "    Hello World\n" +
            "    </pre>" +
            "    <div>Bold</div>" +
            "  </button>" +
            "  <input name=\"password\" value=\"PASS_@!@#&amp;\" />" +
            "  <radio-button />" +
            "</div>";
    Node unmarshal = NodeConverter.fromXml(xml2);

    log.info("UNMARSHALLING IS FINISHED");
    log.info("--------------------------------");
    log.info(xml2);
    log.info(NodeConverter.toXml(unmarshal));

//        window.setVisible(true);
  }

}
