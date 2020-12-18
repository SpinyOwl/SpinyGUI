package com.spinyowl.spinygui.demo;

import static com.spinyowl.spinygui.core.node.NodeBuilder.button;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.label;
import com.spinyowl.spinygui.core.api.DefaultFrame;
import com.spinyowl.spinygui.core.converter.NodeConverter;
import com.spinyowl.spinygui.core.converter.StyleSheetConverter;
import com.spinyowl.spinygui.core.converter.css.model.RuleSet;
import com.spinyowl.spinygui.core.converter.css.model.StyleSheet;
import com.spinyowl.spinygui.core.converter.css.model.selector.Selector;
import com.spinyowl.spinygui.core.converter.css.parser.StyleSheetException;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.manager.DefaultStyleManger;
import com.spinyowl.spinygui.core.style.manager.StyleManager;
import com.spinyowl.spinygui.core.style.types.Color;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test {

  public static void main(String[] args) throws Exception {
    createFromCSS();
    searchComponents();
    parseText();
  }

  public static void createFromCSS() throws StyleSheetException {
    String css = """
        @font-face {
            font-family: My Font Family;
            src: local("some-font.ttf");
        }

        div > button .test {
           background-color: #ffff80;
           color: red;
           width: 50%;
           left: 50%;
           top: 50px;
           right: 50.2%;
        }

        .test:hover {
          box-shadow: 1px 2px 1px red 1px 2px;
          background-color: white;
        }""";

    var stylesheet = StyleSheetConverter.createFromCSS(css);

    Element testLabel = label(Map.of("class", "test"));

    Element div = div(button(testLabel));

    List<RuleSet> ruleSets = stylesheet.ruleSets();

    assert (!ruleSets.get(0).test(div));
    assert (ruleSets.get(0).test(testLabel));

    assert (!ruleSets.get(1).test(testLabel));
    testLabel.hovered(true);
    assert (ruleSets.get(1).test(testLabel));

    DefaultFrame frame = new DefaultFrame();
    frame.styleSheets().add(stylesheet);
    frame.defaultLayer().addChild(div);

    StyleManager styleManager = new DefaultStyleManger();
    styleManager.recalculateStyles(frame);

    assert (Objects.equals(Color.RED, testLabel.calculatedStyle().color()));
  }

  @SneakyThrows
  public static void searchComponents() {
    var css =
        """
            div .test label {
              background-color: red; 
            }
            div .test {
              background-color: green; border: 1px, 1px, 2px, 1px
            }
            button {
              color: black;
            }
                        
            div + p {
              background-color: yellow;
            }
                        
            div ~ p {
              background-color: yellow;
            }
            """;

    var stylesheet = StyleSheetConverter.createFromCSS(css);

    var xml = """
        <div id="1">
            <div id="2" class="test">
                <label id="3">Label 1</label>
            </div>
            <button id="4" class="test"/>
            <div id="5" class="test">
                <div id="6">
                    <div id="7" class="test">
                        <label id="8">Label 1</label>
                    </div>
                </div>
            </div>
            <p> sibling </p>
            <p> sibling 2 </p>
        </div>""";
    var componentTree = (Element) NodeConverter.fromXml(xml);

    List<RuleSet> ruleSets = stylesheet.ruleSets();

    var labels = StyleSheet.searchElements(ruleSets.get(0), componentTree);
    for (Node node : labels) {
      assert (Objects.equals("label", node.nodeName()));
    }
    var test = StyleSheet.searchElements(ruleSets.get(1), componentTree);
    for (Element node : test) {
      assert (Objects.equals("test", node.getAttribute("class")));
    }
    var buttons = StyleSheet.searchElements(ruleSets.get(2), componentTree);
    for (Node node : buttons) {
      assert (Objects.equals("button", node.nodeName()));
    }

    var siblingParagraphs = StyleSheet.searchElements(ruleSets.get(3), componentTree);
    assert siblingParagraphs.size() == 1;
    Element first = siblingParagraphs.get(0);
    assert (Objects.equals("p", first.nodeName()));
    assert (Objects.equals(" sibling ", ((Text) first.childNodes().get(0)).content()));

    var immediateParagraphs = StyleSheet.searchElements(ruleSets.get(4), componentTree);
    assert immediateParagraphs.size() == 1;
    first = immediateParagraphs.get(0);
    assert (Objects.equals("p", first.nodeName()));
    assert (Objects.equals(" sibling ", ((Text) first.childNodes().get(0)).content()));
    Element second = immediateParagraphs.get(1);
    assert (Objects.equals("p", second.nodeName()));
    assert (Objects.equals(" sibling 2 ", ((Text) second.childNodes().get(0)).content()));
  }

  public static void parseText() {
    var xml = "<div>just a text</div>";
    var componentTree = NodeConverter.fromXml(xml);
    log.info("Component tree: {}", componentTree);
  }
}
