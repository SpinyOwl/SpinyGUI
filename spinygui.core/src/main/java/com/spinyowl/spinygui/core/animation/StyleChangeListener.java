package com.spinyowl.spinygui.core.animation;

import com.spinyowl.spinygui.core.node.Element;
import java.util.Map;

/** Receives one old/new snapshot after a successful complete style cascade. */
public interface StyleChangeListener {
  void stylesResolved(Element element, Map<String, Object> previous, Map<String, Object> current);
}
