package com.spinyowl.spinygui.core.parser;

import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;

/** Used to read stylesheets from css. */
public interface StyleSheetConverter {

  StyleSheet fromCss(String css);
}
