package com.spinyowl.spinygui.demo.complex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.event.processor.DefaultEventProcessor;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.layout.impl.LayoutServiceProvider;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessorImpl;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProviderImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import com.spinyowl.spinygui.core.util.IOUtil;
import org.junit.jupiter.api.Test;

class MainMenuExampleTest {

  @Test
  void mainMenuResourceStylesCenterRootContent() {
    Frame frame = styledMainMenuFrame();

    assertEquals(Display.FLEX, frame.resolvedStyle().display());
    assertSame(AlignItems.CENTER, frame.resolvedStyle().alignItems());
    assertSame(JustifyContent.CENTER, frame.resolvedStyle().justifyContent());
  }

  @Test
  void mainMenuResourceLayoutCentersPanel() {
    Frame frame = styledMainMenuFrame();
    frame.frameSize(720, 640);

    var layoutService =
        LayoutServiceProvider.create(
            SystemEventProcessorImpl.builder()
                .eventListenerProvider(new SystemEventListenerProviderImpl())
                .build(),
            new DefaultEventProcessor(),
            () -> 0,
            new FontServiceImpl(new FontStorageImpl(), true));

    layoutService.layout(frame);

    var center = frame.getElementById("main-menu-center");
    assertEquals(173, center.box().content().x(), 0.5f);
    assertEquals(150, center.absolutePosition().x(), 0.5f);
    assertTrue(center.absolutePosition().y() > 0);
  }

  @Test
  void mainMenuResourceLayoutExpandsActionButtonsToPanelWidth() {
    Frame frame = styledMainMenuFrame();
    frame.frameSize(720, 640);

    var layoutService =
        LayoutServiceProvider.create(
            SystemEventProcessorImpl.builder()
                .eventListenerProvider(new SystemEventListenerProviderImpl())
                .build(),
            new DefaultEventProcessor(),
            () -> 0,
            new FontServiceImpl(new FontStorageImpl(), true));

    layoutService.layout(frame);

    var actions = frame.getElementById("main-menu-actions");
    var startGame = frame.getElementById("main-menu-action-start-game");
    assertEquals(actions.box().content().width(), startGame.box().borderBox().width(), 0.5f);
  }

  private static Frame styledMainMenuFrame() {
    Frame frame =
        new DefaultNodeParser()
            .fromHtml(readResource("com/spinyowl/spinygui/demo/main-menu.xml"))
            .frame();
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(propertyStore);
    frame.styleSheets().add(parser.parse(readResource("com/spinyowl/spinygui/demo/main-menu.css")));
    new StyleManagerImpl(propertyStore, parser).recalculate(frame);
    return frame;
  }

  private static String readResource(String path) {
    String resource = IOUtil.resourceAsString(path);
    if (resource == null) {
      throw new IllegalStateException("Demo resource not found: " + path);
    }
    return resource;
  }
}
