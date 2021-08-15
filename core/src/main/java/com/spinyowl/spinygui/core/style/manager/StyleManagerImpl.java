package com.spinyowl.spinygui.core.style.manager;

import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.NonNull;

/** Default style manager implementation based on {@link LinkedBlockingQueue}. */
public class StyleManagerImpl extends AbstractStyleManager {

  public StyleManagerImpl(
      @NonNull PropertyStore propertyStore, @NonNull StyleSheetParser styleSheetParser) {
    super(propertyStore, styleSheetParser);
  }
}
