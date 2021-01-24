package com.spinyowl.spinygui.demo;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_PASSWORD;
import static com.spinyowl.spinygui.core.node.NodeBuilder.button;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.input;
import static com.spinyowl.spinygui.core.node.NodeBuilder.radioButton;
import static com.spinyowl.spinygui.core.node.NodeBuilder.text;
import com.spinyowl.spinygui.core.parser.NodeConverter;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeConverter;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
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

    NodeConverter nodeConverter = new DefaultNodeConverter();
    Node element = div(
        button(
            text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n"),
            div(text("Bold")),
            button(text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n"))
        ).with("name", "myAwesomeButton").with("id", "bid1"),
        input(TYPE_PASSWORD, "myPass", "PASS_@!@#&"),
        radioButton("radio", "firstValue")
    );

    Frame frame = new Frame();
    frame.addChild(element);
    log.info(
        String.valueOf(input(TYPE_PASSWORD, "myPass", "PASS_@!@#&").frame() == frame)); // false
    log.info(String.valueOf(element.children().get(2).frame() == frame)); // true

    String xml = DefaultNodeConverter.toXml(element, false);
    log.info(xml);
    //language=XML
    String xml2 = """
        <?xml version="1.0" encoding="UTF-8"?>
        <div>
          <button>asdfasdfasd</button>
        <button>
          s
          Hello World
          <pre _pre="true">
            s
            
            Hello World
          </pre>
          <div>Bold</div>  
        </button>  
        <input name="password" value="PASS_@!@#&amp;" />  
        <radio-button />
        </div>
        """;
    Node unmarshal = nodeConverter.fromXml(xml2);

    log.info("UNMARSHALLING IS FINISHED");
    log.info("--------------------------------");
    log.info(xml2);
    log.info(nodeConverter.toXml(unmarshal));

//        window.setVisible(true);
  }

}
