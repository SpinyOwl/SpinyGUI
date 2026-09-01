package com.spinyowl.spinygui.demo.simple;

import static com.spinyowl.spinygui.core.input.KeyAction.CLICK;
import static com.spinyowl.spinygui.core.node.NodeBuilder.button;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.text;

import com.spinyowl.spinygui.LwjglApplicationHost;
import com.spinyowl.spinygui.core.FrameNavigator;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.LwjglApplicationConfiguration;
import com.spinyowl.spinygui.core.event.MouseClickEvent;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import java.util.Map;

/** Runnable production-host example for frame navigation and nested modal top-layer behavior. */
public final class NavigationModalHostExample {

  /** CSS shared by both navigation frames. */
  private static final String STYLES =
      """
      winframe {
        display: block;
        background-color: #eef2ff;
        padding: 36px;
      }
      .panel, .modal {
        display: block;
        width: 520px;
        padding: 24px;
        background-color: white;
        border: 1px solid #a5b4fc;
        border-radius: 12px;
      }
      .modal {
        position: absolute;
        left: 100px;
        top: 90px;
        width: 420px;
        background-color: #fff7ed;
        border: 2px solid #f97316;
      }
      .nested {
        left: 170px;
        top: 150px;
        width: 330px;
        background-color: #fefce8;
      }
      .title {
        display: block;
        color: #172554;
        font-size: 28px;
        font-weight: bold;
        margin-bottom: 16px;
      }
      button {
        display: block;
        width: 250px;
        height: 40px;
        margin-top: 10px;
        background-color: #4f46e5;
        color: white;
      }
      """;

  private NavigationModalHostExample() {}

  /** Opens the standalone owned GLFW/NanoVG host. */
  public static void main(String[] args) {
    Frame initialFrame = new Frame();
    try (LwjglApplicationHost host =
        LwjglApplicationHost.owned(
            LwjglApplicationConfiguration.windowed(760, 520, "SpinyGUI navigation and modals"),
            initialFrame,
            8,
            new DemoLifecycle(initialFrame))) {
      host.run();
    }
  }

  /** Builds the example after the production host has initialized its standard services. */
  static final class DemoLifecycle implements LwjglApplicationHost.Lifecycle {
    /** Initial frame supplied to the owned host before its services exist. */
    private final Frame initialFrame;

    DemoLifecycle(Frame initialFrame) {
      this.initialFrame = initialFrame;
    }

    @Override
    public void initialize(LwjglApplicationHost host) {
      new DemoUi(initialFrame, host.navigator(), host.services().styleSheetParser()).initialize();
    }
  }

  /** Pure UI controller retained separately so navigation and modal flows are headlessly testable. */
  static final class DemoUi {
    /** Home frame and owner of the example modal stack. */
    private final Frame home;
    /** Second navigation frame retained in browser-like history. */
    private final Frame details = new Frame();
    /** Navigator shared with callback capture and the production render loop. */
    private final FrameNavigator navigator;
    /** Host parser used to give both frames the same visual rules. */
    private final StyleSheetParser styleSheetParser;
    /** Background focus target restored after the last modal closes. */
    private Element openModal;
    /** Bottom modal root, promoted only while open. */
    private Element primaryModal;
    /** Focus target restored when the nested modal closes. */
    private Element openNestedModal;
    /** Nested topmost modal root. */
    private Element nestedModal;
    /** Focus target inside the nested topmost modal. */
    private Element closeNestedModal;

    DemoUi(Frame home, FrameNavigator navigator, StyleSheetParser styleSheetParser) {
      this.home = home;
      this.navigator = navigator;
      this.styleSheetParser = styleSheetParser;
    }

    /** Builds both frames and establishes the initial background focus target. */
    void initialize() {
      home.styleSheets().add(styleSheetParser.parse(STYLES));
      details.styleSheets().add(styleSheetParser.parse(STYLES));

      openModal = action("Open modal", this::openPrimaryModal);
      Element navigate = action("Navigate to details", () -> navigator.navigate(details));
      Element forward = action("Forward", navigator::forward);
      home.addChild(
          div(
              Map.of("class", "panel"),
              div(Map.of("class", "title"), text("Home frame")),
              text("Only this current frame is rendered. Modal content belongs to this frame."),
              navigate,
              forward,
              openModal));

      details.addChild(
          div(
              Map.of("class", "panel"),
              div(Map.of("class", "title"), text("Details frame")),
              text("Back returns to Home; Forward remains available after going back."),
              action("Back", navigator::back),
              action("Forward", navigator::forward)));

      openNestedModal = action("Open nested modal", this::openNestedModal);
      primaryModal =
          div(
              Map.of("class", "modal"),
              div(Map.of("class", "title"), text("First modal")),
              text("The backdrop keeps Home visible but inert."),
              openNestedModal,
              action("Close first modal", this::closePrimaryModal));
      primaryModal.style("display: none");

      closeNestedModal = action("Close nested modal", this::closeNestedModal);
      nestedModal =
          div(
              Map.of("class", "modal nested"),
              div(Map.of("class", "title"), text("Nested modal")),
              text("Only this topmost modal accepts input."),
              closeNestedModal);
      nestedModal.style("display: none");
      home.addChildren(primaryModal, nestedModal);
      openModal.focused(true);
    }

    /** Navigates to the second frame through the same navigator used by callbacks. */
    void navigateToDetails() {
      navigator.navigate(details);
    }

    /** Opens the first modal and transfers focus into its interaction surface. */
    void openPrimaryModal() {
      primaryModal.style("display: block");
      home.topLayer().showModal(primaryModal);
      openNestedModal.focused(true);
    }

    /** Opens a nested modal above the first and transfers focus to it. */
    void openNestedModal() {
      nestedModal.style("display: block");
      home.topLayer().showModal(nestedModal);
      closeNestedModal.focused(true);
    }

    /** Closes the nested modal; the top layer restores focus to the first modal. */
    void closeNestedModal() {
      home.topLayer().closeTopModal();
      nestedModal.style("display: none");
    }

    /** Closes the first modal; the top layer restores focus to the background opener. */
    void closePrimaryModal() {
      home.topLayer().closeTopModal();
      primaryModal.style("display: none");
    }

    /** Returns the navigation destination used by headless example verification. */
    Frame details() {
      return details;
    }

    /** Returns the background focus target used by headless example verification. */
    Element openModal() {
      return openModal;
    }

    /** Returns the first modal root used by headless example verification. */
    Element primaryModal() {
      return primaryModal;
    }

    /** Returns the first-modal focus target used by headless example verification. */
    Element openNestedModalControl() {
      return openNestedModal;
    }

    /** Returns the nested modal root used by headless example verification. */
    Element nestedModal() {
      return nestedModal;
    }

    /** Returns the nested-modal focus target used by headless example verification. */
    Element closeNestedModalControl() {
      return closeNestedModal;
    }

    private static Element action(String label, Runnable action) {
      Element control = button(text(label));
      control.addListener(
          MouseClickEvent.class,
          event -> {
            if (event.action() == CLICK) action.run();
          });
      return control;
    }
  }
}
