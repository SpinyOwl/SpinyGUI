package com.spinyowl.spinygui.core.system.font;

import lombok.Value;

/**
 * Caret hit-test result for the text argument supplied to the measurer.
 *
 * <p>The index is a UTF-16 code-unit offset into that argument. The x coordinate is text-local and
 * measured from the start of the supplied line; layout position, viewport scroll, and presentation
 * transforms are outside this value.
 */
@Value
public class TextCaretMetrics {
  /** UTF-16 code-unit offset into the measured text argument. */
  int charIndex;

  /** Text-local horizontal advance from the start of the measured line, in pixels. */
  float x;
}
