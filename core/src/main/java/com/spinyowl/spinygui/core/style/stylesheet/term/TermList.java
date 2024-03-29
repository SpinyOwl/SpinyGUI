package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.stylesheet.Term;
import java.util.List;
import java.util.StringJoiner;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class TermList extends Term<List<Term<?>>> {
  @NonNull private Operator operator;

  public TermList(Operator operator, List<Term<?>> value) {
    super(value);
    this.operator = operator;
  }

  public TermList(Operator operator, Term<?>... value) {
    super(List.of(value));
    this.operator = operator;
  }

  public List<Term<?>> terms() {
    return value;
  }

  public Term<?> get(int index) {
    return value.get(index);
  }

  public int size() {
    return value.size();
  }

  public boolean isEmpty() {
    return value.isEmpty();
  }

  @Override
  public String toString() {
    StringJoiner joiner = new StringJoiner(operator.value);
    terms().forEach(term -> joiner.add(term.toString()));
    return joiner.toString();
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
