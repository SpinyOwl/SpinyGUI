package com.spinyowl.spinygui.core.style.stylesheet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Term<T> {
  protected T value;
}
