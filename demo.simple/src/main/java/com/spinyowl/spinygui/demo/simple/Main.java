package com.spinyowl.spinygui.demo.simple;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_PASSWORD;
import static com.spinyowl.spinygui.core.node.NodeBuilder.button;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static com.spinyowl.spinygui.core.node.NodeBuilder.input;
import static com.spinyowl.spinygui.core.node.NodeBuilder.label;
import static com.spinyowl.spinygui.core.node.NodeBuilder.radioButton;
import static com.spinyowl.spinygui.core.node.NodeBuilder.text;

import com.spinyowl.spinygui.core.event.MouseClickEvent;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.NodeParser;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.PropertiesScanner;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStore;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  public static void main(String[] args) throws Exception {

    //        Monitor monitor = Monitor.getPrimaryMonitor();
    //        Window window = Window.createWindow("Example window", 800, 600, monitor);

    //        window.addCloseEventListener(Window.EXIT_ON_CLOSE);

    NodeParser nodeParser = new DefaultNodeParser();
    Element element =
        div(
            button(
                Map.of("name", "myAwesomeButton", "id", "bid1"),
                text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n"),
                div(text("Bold")),
                button(text("\n\n\tFOrmantted text\n\t\n\n asdfasdfa\n"))),
            input(TYPE_PASSWORD, "myPass", "PASS_@!@#&"),
            radioButton("radio", "firstValue"));

    element.style("background-color: red; border: 1px solid black;");

    Frame frame = frame(div("Hello world!"), div(Map.of("id", "user-info"), label("User name")));

    Element button = new Element("input");
    button.setAttribute("type", "button");
    frame.addChild(button);

    button.addListener(MouseClickEvent.class, e -> log.info("Button clicked"));

    //    var frame = new Frame();
    frame.addChild(element);
    log.info(
        String.valueOf(input(TYPE_PASSWORD, "myPass", "PASS_@!@#&").frame() == frame)); // false
    log.info(String.valueOf(element.children().get(2).frame() == frame)); // true

    String xml = nodeParser.toXml(element, false);
    log.info(xml);

    // language=xml
    frame.addChild(nodeParser.fromXml("""
      <div>Additional content</div>
    """));
    // language=HTML
    String xml2 =
        """
        <div>
          <div>Hello world!</div>
          <div id="user-info">
            <label>User name</label>
          </div>
        </div>
        """;

    var unmarshal = nodeParser.fromXml(xml2);

    PropertyStore propertyStore = new DefaultPropertyStore();
    PropertiesScanner.fillPropertyStore(propertyStore);
    StyleSheetParser styleSheetParser = StyleSheetParserFactory.createParser(propertyStore);

    // language=CSS
    String styles =
        """
        winframe {
          background-color: azure;
          padding: 20px;
        }
        #user-info {
          color: darkred;
          font-weight: bold;
        }

        winframe::-webkit-scrollbar {
          width: 10px;
        }
        """;
    StyleSheet styleSheet = styleSheetParser.parse(styles);
    frame.styleSheets().add(styleSheet);

    log.info("UNMARSHALLING IS FINISHED");
    log.info("--------------------------------");
    log.info(xml2);
    log.info(nodeParser.toXml(unmarshal));

    //        window.setVisible(true);
  }
}
