package com.spinyowl.spinygui.core.system.input;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_BUTTON;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_CHECKBOX;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_EMAIL;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_PASSWORD;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RADIO;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RANGE;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RESET;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_SEARCH;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_SUBMIT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_TEL;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_TEXT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_URL;

import com.spinyowl.spinygui.core.node.InputElement;
import java.util.Locale;
import java.util.Objects;

/** Central classification point for native {@code input} behavior dispatch. */
public final class InputBehaviorRegistry {

  private InputBehaviorRegistry() {}

  public static Kind kind(InputElement input) {
    Objects.requireNonNull(input);
    String type = input.type() == null ? TYPE_TEXT : input.type().toLowerCase(Locale.ROOT);
    return switch (type) {
      case TYPE_TEXT, TYPE_EMAIL, TYPE_SEARCH, TYPE_TEL, TYPE_URL, TYPE_PASSWORD -> Kind.TEXT;
      case TYPE_BUTTON, TYPE_SUBMIT, TYPE_RESET -> Kind.BUTTON;
      case TYPE_CHECKBOX -> Kind.CHECKBOX;
      case TYPE_RADIO -> Kind.RADIO;
      case TYPE_RANGE -> Kind.RANGE;
      default -> Kind.UNSUPPORTED;
    };
  }

  public static boolean textEditable(InputElement input) {
    return kind(input) == Kind.TEXT;
  }

  public static boolean buttonLike(InputElement input) {
    return kind(input) == Kind.BUTTON;
  }

  public static boolean activatable(InputElement input) {
    if (input.disabled()) {
      return false;
    }
    return switch (kind(input)) {
      case BUTTON, CHECKBOX, RADIO, RANGE -> true;
      default -> false;
    };
  }

  public enum Kind {
    TEXT,
    BUTTON,
    CHECKBOX,
    RADIO,
    RANGE,
    UNSUPPORTED
  }
}
