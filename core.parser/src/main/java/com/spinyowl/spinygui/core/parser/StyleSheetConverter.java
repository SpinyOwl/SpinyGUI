package com.spinyowl.spinygui.core.parser;

import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;

public interface StyleSheetConverter {

  StyleSheet fromCss(String css);
}
