package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.types.grid.GridFraction;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class TermGridFraction extends TermUnit<GridFraction> {
  public TermGridFraction(GridFraction value) {
    super(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
