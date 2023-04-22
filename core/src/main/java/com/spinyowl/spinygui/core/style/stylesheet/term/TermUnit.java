package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import lombok.EqualsAndHashCode;

/** Represents unit term. */
@EqualsAndHashCode
public class TermUnit<U extends Unit> extends Term<U> {
  public TermUnit(U value) {
    super(value);
  }
}
