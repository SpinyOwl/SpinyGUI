package com.spinyowl.spinygui.core.converter;

import com.spinyowl.spinygui.core.converter.css.model.StyleSheet;

public interface StyleSheetConverter {

  StyleSheet convertFromCss(String css);

}
