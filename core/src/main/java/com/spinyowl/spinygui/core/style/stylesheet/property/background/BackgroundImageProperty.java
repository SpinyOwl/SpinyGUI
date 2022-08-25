package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_IMAGE;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;

/** Currently supports only url/none argument. */
public class BackgroundImageProperty extends Property {

  public static final String NONE = "none";
  public static final String EMPTY_STRING = "";

  public BackgroundImageProperty() {
    super(
        BACKGROUND_IMAGE,
        new TermIdent(NONE),
        !INHERITABLE,
        !ANIMATABLE,
        (term, styles) -> styles.put(BACKGROUND_IMAGE, extractUrl(((TermIdent) term).value())),
        term -> term instanceof TermIdent ti && test(ti.value()));
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
    var cut = trim.substring(4, trim.length() - 1);

    return escaped(cut, "\"") || escaped(cut, "'") || (!cut.contains("\"") && !cut.contains("'"));
  }

  private static boolean escaped(String cut, String escapeString) {
    return cut.startsWith(escapeString)
        && cut.endsWith(escapeString)
        && cut.replace(escapeString, EMPTY_STRING).length() == cut.length() - 2;
  }

  private static String extractUrl(String value) {
    if (NONE.equals(value)) {
      return EMPTY_STRING;
    }
    String trimmed = value.trim();
    trimmed = trimmed.substring(4, trimmed.length() - 1); // removed 'url(' and ')'
    return trimmed.replace("\"", EMPTY_STRING).replace("'", EMPTY_STRING);
  }
}
