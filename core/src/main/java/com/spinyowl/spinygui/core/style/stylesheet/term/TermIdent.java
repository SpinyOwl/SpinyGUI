package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.stylesheet.Term;
import lombok.EqualsAndHashCode;

/** Represents ident term (string keyword). */
@EqualsAndHashCode
public class TermIdent extends Term<String> {
  public TermIdent(String value) {
    super(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
