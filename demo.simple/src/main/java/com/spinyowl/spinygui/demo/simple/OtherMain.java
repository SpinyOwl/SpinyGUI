package com.spinyowl.spinygui.demo.simple;

import static com.spinyowl.spinygui.core.node.NodeBuilder.button;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.label;
import static com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory.createParser;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStoreProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Ruleset;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.system.tree.StyleTreeBuilder;
import com.spinyowl.spinygui.core.system.tree.StyleTreeBuilderImpl;
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
    // language=CSS
    var css =
        """
        * {
          color: #222;
        }

         winframe,
         winframe * {
           box-sizing: border-box; /* default behaviour */
           overflow: hidden; /* default behaviour */
           background-color: green;
           padding: 20.1px;
           flex-direction: column;
           border: 1px solid #ccc;
           left: 20px;
         }
         winframe:hover,
         winframe *:hover {
           border: 8px solid #fc3131;
         }

         .text {
           display: block;
           overflow: auto;
           height: 90px;
           font-size: 16px;
           border-color: #45AAFF;
         }

         #t2 {
           position: absolute;
           bottom: 10px;
           top: 10px;
           height: auto;
           background-color: rgba(190,200,255,.8);
           border-color: rgba(190,200,255,.8) rgba(90,200,255,.8);
         }

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
        }

        #some button {
          color: rgba(150,250,250,1);
        }
        button #some {
          color: red;
          border-color: rgb(1,1,1) rgb(2, 2, 2) rgb(100, 200, 100) rgb(222, 19, 221)
        }
        """;

    PropertyStoreProvider provider = new DefaultPropertyStoreProvider();
    PropertyStore propertyStore = provider.createPropertyStore();
    StyleSheetParser parser = createParser(propertyStore);
    var stylesheet = parser.parse(css);

    Element testLabel = label(Map.of("class", "test"));

    Element div = div(button(testLabel));

    List<Ruleset> rulesets = stylesheet.rulesets();

    assert (!rulesets.get(0).test(div));
    assert (rulesets.get(0).test(testLabel));

    assert (!rulesets.get(1).test(testLabel));
    testLabel.hovered(true);
    assert (rulesets.get(1).test(testLabel));

    var frame = new Frame();
    frame.styleSheets().add(stylesheet);
    frame.addChild(div);

    List<Ruleset> rules = stylesheet.searchSpecificRules(testLabel);
    for (Ruleset rule : rules) {
      log.info(Arrays.toString(rule.selectors().toArray()) + " --- " + rule.specificity(testLabel));
    }

    StyleTreeBuilder treeBuilder = new StyleTreeBuilderImpl(propertyStore, parser);
    treeBuilder.build(frame, frame.styleSheets());

    log.info("EQUALS: " + Objects.equals(Color.RED, testLabel.resolvedStyle().color()));
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

    PropertyStoreProvider provider = new DefaultPropertyStoreProvider();
    PropertyStore propertyStore = provider.createPropertyStore();
    StyleSheetParser parser = createParser(propertyStore);
    var stylesheet = parser.parse(css);

    var xml =
        """
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
    var componentTree = (Element) new DefaultNodeParser().fromXml(xml);

    List<Ruleset> rulesets = stylesheet.rulesets();

    var labels = rulesets.get(0).searchElements(componentTree);
    for (Node node : labels) {
      assert (Objects.equals("label", node.nodeName()));
    }
    var test = rulesets.get(0).searchElements(componentTree);
    for (Element node : test) {
      assert (Objects.equals("test", node.getAttribute("class")));
    }
    var buttons = rulesets.get(0).searchElements(componentTree);
    for (Node node : buttons) {
      assert (Objects.equals("button", node.nodeName()));
    }

    var siblingParagraphs = rulesets.get(0).searchElements(componentTree);
    assert siblingParagraphs.size() == 1;
    Element first = siblingParagraphs.get(0);
    assert (Objects.equals("p", first.nodeName()));
    assert (Objects.equals(" sibling ", ((Text) first.childNodes().get(0)).content()));

    var immediateParagraphs = rulesets.get(0).searchElements(componentTree);
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
    var nodeParser = new DefaultNodeParser();
    var componentTree = nodeParser.fromXml(xml);
    log.info("Component tree: {}", nodeParser.toXml(componentTree));
  }
}
