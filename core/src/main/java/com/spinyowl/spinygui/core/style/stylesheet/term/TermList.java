package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.stylesheet.Term;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
public class TermList extends Term<List<Term<?>>> {
  @NonNull private Operator operator;

  public TermList(List<Term<?>> value, Operator operator) {
    super(value);
    this.operator = operator;
  }

  @Getter
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public enum Operator {
    SPACE(" "),
    SLASH("/"),
    COMMA(", ");

    private final String value;
  }
}
