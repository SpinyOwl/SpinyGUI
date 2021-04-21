package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import com.spinyowl.spinygui.core.style.stylesheet.Properties;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

/** Currently supports only url/none argument. */
public class BackgroundImageProperty extends Property<String> {

  public static final String NONE = "none";

  public BackgroundImageProperty() {
    super(
        Properties.BACKGROUND_IMAGE,
        NONE,
        !INHERITED,
        !ANIMATABLE,
        (s, i) -> s.background().image(i),
        s -> s.background().image(),
        BackgroundImageProperty::extractUrl,
        BackgroundImageProperty::test);
  }

  private static boolean test(String value) {
    if (value == null || value.isEmpty()) {
      return false;
    }
    if (NONE.equals(value)) {
      return true;
    }
    String trim = value.trim();
    if (!trim.startsWith("url(") || !trim.endsWith(")")) {
      return false;
    }
    String cut = trim.substring(4, trim.length() - 1);

    return escaped(cut, "\"") || escaped(cut, "'") || (!cut.contains("\"") && !cut.contains("'"));
  }

  private static boolean escaped(String cut, String escapeString) {
    return cut.startsWith(escapeString)
        && cut.endsWith(escapeString)
        && cut.replace(escapeString, "").length() == cut.length() - 2;
  }

  private static String extractUrl(String value) {
    if (NONE.equals(value)) {
      return null;
    }
    String trimmed = value.trim();
    trimmed = trimmed.substring(4, trimmed.length() - 1); // removed 'url(' and ')'
    return trimmed.replace("\"", "").replace("'", "");
  }
}
