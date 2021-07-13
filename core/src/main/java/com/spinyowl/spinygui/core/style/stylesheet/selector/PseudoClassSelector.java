package com.spinyowl.spinygui.core.style.stylesheet.selector;

import com.spinyowl.spinygui.core.style.stylesheet.Specificity;

/**
 * Interface for pseudo-class selectors, which used to define a special state of an element.
 *
 * <p>For example, it can be used to:
 *
 * <ul>
 *   <li>Style an element when a user mouses over it
 *   <li>Style visited and unvisited links differently
 *   <li>Style an element when it gets focus
 * </ul>
 */
public interface PseudoClassSelector extends Selector {

  @Override
  default Specificity specificity() {
    return Specificity.CLASS;
  }
}
