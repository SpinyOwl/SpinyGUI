package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.stylesheet.Term;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class TermIdent extends Term<String> {

  public static final TermIdent INHERIT = new TermIdent("inherit");
  public static final TermIdent INITIAL = new TermIdent("initial");

  public TermIdent(String value) {
    super(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
