package com.spinyowl.spinygui.demo.simple;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_PASSWORD;
import static com.spinyowl.spinygui.core.node.NodeBuilder.button;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.input;
import static com.spinyowl.spinygui.core.node.NodeBuilder.radioButton;
import static com.spinyowl.spinygui.core.node.NodeBuilder.text;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.parser.NodeParser;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  public static void main(String[] args) throws Exception {

//        Monitor monitor = Monitor.getPrimaryMonitor();
//        Window window = Window.createWindow("Example window", 800, 600, monitor);

//        window.addCloseEventListener(Window.EXIT_ON_CLOSE);

    NodeParser nodeParser = new DefaultNodeParser();
    Node element = div(
        button(
            Map.of(
                "name", "myAwesomeButton",
                "id", "bid1"
            ),
            text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n"),
            div(text("Bold")),
            button(text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n"))
        ),
        input(TYPE_PASSWORD, "myPass", "PASS_@!@#&"),
        radioButton("radio", "firstValue")
    );

    var frame = new Frame();
    frame.addChild(element);
    log.info(
        String.valueOf(input(TYPE_PASSWORD, "myPass", "PASS_@!@#&").frame() == frame)); // false
    log.info(String.valueOf(element.children().get(2).frame() == frame)); // true

    String xml = nodeParser.toXml(element, false);
    log.info(xml);
    //language=XML
    var xml2 = """
        <?xml version="1.0" encoding="UTF-8"?>
        <div>
          <button>asdfasdfasd</button>
          <button>
            s
            Hello World
            <pre _pre="true">
            s
           \s
            Hello World
          </pre>
            <div>Bold</div>
          </button>
          <input name="password" value="PASS_@!@#&amp;"/>
          <radio-button/>
        </div>
        """;
    var unmarshal = nodeParser.fromXml(xml2);

    log.info("UNMARSHALLING IS FINISHED");
    log.info("--------------------------------");
    log.info(xml2);
    log.info(nodeParser.toXml(unmarshal));

//        window.setVisible(true);
  }

}
