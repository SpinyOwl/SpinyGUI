package org.liquidengine.legui;

import org.liquidengine.legui.core.Legui;
import org.liquidengine.legui.core.api.Monitor;
import org.liquidengine.legui.core.api.Window;
import org.liquidengine.legui.core.component.Button;
import org.liquidengine.legui.core.component.Input;
import org.liquidengine.legui.core.system.component.Element;
import org.liquidengine.legui.core.system.component.TextNode;


/**
 * Created by ShchAlexander on 09.08.2018.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Monitor monitor = Legui.getPrimaryMonitor();
        Window window = Legui.createWindow(800, 600, "Example", null);
//        window.setSize(800, 600);
//        window.setPosition(50,50);
//        window.setVisible(true);
//        window.setTitle("Example 2");


        Element element = new Element();
        Button node = new Button();
        TextNode textNode = new TextNode();
        node.addChild(textNode);
        element.addChild(node);
        element.addChild(new Input());

//        window.addCloseEventListener(event -> Legui.destroyWindow(window));
//        window.
    }

}
