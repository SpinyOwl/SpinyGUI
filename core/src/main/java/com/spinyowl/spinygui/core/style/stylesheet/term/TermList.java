package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.stylesheet.Term;
import java.util.List;

public class TermList extends Term<List<Term>> {
  public TermList(List<Term> value) {
    this.value = value;
  }
}
