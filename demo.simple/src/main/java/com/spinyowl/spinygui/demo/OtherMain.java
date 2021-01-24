package com.spinyowl.spinygui.demo;

import static com.spinyowl.spinygui.core.node.NodeBuilder.button;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.label;
import com.spinyowl.spinygui.core.parser.StyleSheetConverter;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeConverter;
import com.spinyowl.spinygui.core.parser.impl.DefaultStyleSheetConverter;
import com.spinyowl.spinygui.core.style.stylesheet.RuleSet;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.manager.DefaultStyleManger;
import com.spinyowl.spinygui.core.style.manager.StyleManager;
import com.spinyowl.spinygui.core.node.style.types.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OtherMain {

  public static void main(String[] args) {
    createFromCSS();
    searchComponents();
    parseText();
  }

  public static void createFromCSS() {
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

        button .test, .test:hover {
          box-shadow: 1px 2px 1px red 1px 2px;
          background-color: white;
        }""";

    StyleSheetConverter converter = new DefaultStyleSheetConverter();
    var stylesheet = converter.fromCss(css);

    Element testLabel = label(Map.of("class", "test"));

    Element div = div(button(testLabel));

    List<RuleSet> ruleSets = stylesheet.ruleSets();

    assert (!ruleSets.get(0).test(div));
    assert (ruleSets.get(0).test(testLabel));

    assert (!ruleSets.get(1).test(testLabel));
    testLabel.hovered(true);
    assert (ruleSets.get(1).test(testLabel));

    Frame frame = new Frame();
    frame.styleSheets().add(stylesheet);
    frame.addChild(div);

    List<RuleSet> rules = stylesheet.searchSpecificRules(testLabel);
    for (RuleSet rule : rules) {
      log.info(Arrays.toString(rule.selectors().toArray()) + " --- " + rule.specificity(testLabel));
    }

    StyleManager styleManager = new DefaultStyleManger();
    styleManager.recalculateStyles(frame);

    assert (Objects.equals(Color.RED, testLabel.calculatedStyle().color()));
  }

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

    StyleSheetConverter converter = new DefaultStyleSheetConverter();
    var stylesheet = converter.fromCss(css);

    var xml = """
              <frame>
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
                </div>
              </frame>
        """;
    var componentTree = (Element) new DefaultNodeConverter().fromXml(xml);

    List<RuleSet> ruleSets = stylesheet.ruleSets();

    var labels = ruleSets.get(0).searchElements(componentTree);
    for (Node node : labels) {
      assert (Objects.equals("label", node.nodeName()));
    }
    var test = ruleSets.get(0).searchElements(componentTree);
    for (Element node : test) {
      assert (Objects.equals("test", node.getAttribute("class")));
    }
    var buttons = ruleSets.get(0).searchElements(componentTree);
    for (Node node : buttons) {
      assert (Objects.equals("button", node.nodeName()));
    }

    var siblingParagraphs = ruleSets.get(0).searchElements(componentTree);
    assert siblingParagraphs.size() == 1;
    Element first = siblingParagraphs.get(0);
    assert (Objects.equals("p", first.nodeName()));
    assert (Objects.equals(" sibling ", ((Text) first.childNodes().get(0)).content()));

    var immediateParagraphs = ruleSets.get(0).searchElements(componentTree);
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
    DefaultNodeConverter nodeConverter = new DefaultNodeConverter();
    var componentTree = nodeConverter.fromXml(xml);
    log.info("Component tree: {}", nodeConverter.toXml(componentTree));
  }
}
