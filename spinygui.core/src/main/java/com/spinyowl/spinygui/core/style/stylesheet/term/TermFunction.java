package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.stylesheet.Term;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TermFunction extends TermList {
  private final String name;

  public TermFunction(String name, Operator operator, List<Term<?>> value) {
    super(operator, value);
    this.name = name;
  }

  public TermFunction(String name, Operator operator, Term<?>... value) {
    super(operator, value);
    this.name = name;
  }

  @Override
  public String toString() {
    return name + "(" + super.toString() + ")";
  }
}
