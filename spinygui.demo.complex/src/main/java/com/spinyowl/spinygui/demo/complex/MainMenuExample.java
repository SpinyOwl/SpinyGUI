package com.spinyowl.spinygui.demo.complex;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainMenuExample extends Demo {

  private static final Logger LOG = LoggerFactory.getLogger(MainMenuExample.class);
  private static final String XML_RESOURCE = "com/spinyowl/spinygui/demo/main-menu.xml";
  private static final String CSS_RESOURCE = "com/spinyowl/spinygui/demo/main-menu.css";

  private int activationCount;

  public MainMenuExample() {
    super(720, 640, "Main Menu Example", new NvgRenderer());
  }

  public static void main(String[] args) {
    Demo demo = new MainMenuExample();
    demo.run();
  }

  @Override
  protected Frame createGuiElements(int width, int height) {
    String xml =
        readResource(XML_RESOURCE)
            .replace("{{connectionStatus}}", "Local demo: menu ready")
            .replace("{{playerName}}", "Crawler")
            .replace("{{playerNameValidation}}", "Name is valid.");
    String styles = readResource(CSS_RESOURCE);

    Frame frame = nodeParser.fromHtml(xml).frame();
    frame.styleSheets().add(styleSheetParser.parse(styles));

    Text statusText = firstText(frame.getElementById("main-menu-status"));
    InputElement playerNameInput = input(frame, "main-menu-player-name-input");

    addMenuAction(frame, "main-menu-action-start-game", "Start Game", statusText, playerNameInput);
    addMenuAction(frame, "main-menu-action-open-find-game", "Find Game", statusText, playerNameInput);
    addMenuAction(frame, "main-menu-action-open-settings", "Settings", statusText, playerNameInput);
    addMenuAction(frame, "main-menu-action-exit", "Exit", statusText, playerNameInput);

    return frame;
  }

  private void addMenuAction(
      Frame frame, String id, String label, Text statusText, InputElement playerNameInput) {
    Element element = frame.getElementById(id);
    if (element == null) {
      throw new IllegalStateException("Main menu action is missing: " + id);
    }
    element.addListener(
        ActionEvent.class,
        event -> updateStatus(statusText, label + " selected for " + playerName(playerNameInput)));
  }


  private void updateStatus(Text statusText, String message) {
    activationCount++;
    String numberedMessage = activationCount + ": " + message;
    statusText.content(numberedMessage);
    LOG.info(numberedMessage);
  }

  private String playerName(InputElement input) {
    String value = input.value().trim();
    return value.isEmpty() ? "unnamed player" : value;
  }


  private InputElement input(Frame frame, String id) {
    Element element = frame.getElementById(id);
    if (element instanceof InputElement inputElement) {
      return inputElement;
    }
    throw new IllegalStateException("Main menu input is missing: " + id);
  }

  private Text firstText(Element element) {
    if (element == null
        || element.childNodes().isEmpty()
        || !(element.childNodes().getFirst() instanceof Text text)) {
      throw new IllegalStateException("Main menu status text is missing");
    }
    return text;
  }

  private String readResource(String path) {
    String resource = IOUtil.resourceAsString(path);
    if (resource == null) {
      throw new IllegalStateException("Demo resource not found: " + path);
    }
    return resource;
  }
}
