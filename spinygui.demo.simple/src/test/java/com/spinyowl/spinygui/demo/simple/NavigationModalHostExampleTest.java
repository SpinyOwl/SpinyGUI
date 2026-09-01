package com.spinyowl.spinygui.demo.simple;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.FrameNavigator;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import java.util.List;
import org.junit.jupiter.api.Test;

class NavigationModalHostExampleTest {

  @Test
  void demonstratesNavigationNestedModalsBackdropAndFocusRestoration() {
    Frame home = new Frame();
    FrameNavigator navigator = new FrameNavigator(home, 4);
    StyleSheetParser parser =
        StyleSheetParserFactory.createParser(
            new DefaultPropertyStoreProvider().createPropertyStore());
    NavigationModalHostExample.DemoUi demo =
        new NavigationModalHostExample.DemoUi(home, navigator, parser);
    demo.initialize();

    demo.navigateToDetails();
    assertSame(demo.details(), navigator.currentFrame());
    assertTrue(navigator.back());
    assertSame(home, navigator.currentFrame());
    assertTrue(navigator.forward());
    assertSame(demo.details(), navigator.currentFrame());
    assertTrue(navigator.back());

    demo.openPrimaryModal();
    assertSame(home, home.topLayer().backdrop().frame());
    assertSame(demo.openNestedModalControl(), home.getFocusedElement());
    assertFalse(demo.openModal().focused());

    demo.openNestedModal();
    assertEquals(List.of(demo.primaryModal(), demo.nestedModal()), home.topLayer().modalRoots());
    assertSame(demo.closeNestedModalControl(), home.getFocusedElement());

    demo.closeNestedModal();
    assertSame(demo.primaryModal(), home.topLayer().topModal());
    assertSame(demo.openNestedModalControl(), home.getFocusedElement());

    demo.closePrimaryModal();
    assertFalse(home.topLayer().hasModal());
    assertSame(demo.openModal(), home.getFocusedElement());
    assertNull(home.topLayer().backdrop().parent());
  }
}
