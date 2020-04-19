package com.spinyowl.spinygui.core.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public final class Text extends Node {

  public static final String ATTRIBUTE_OPERATIONS_ARE_NOT_SUPPORTED
      = "Attribute operations are not supported for Text";
  public static final String CHILD_OPERATIONS_ARE_NOT_SUPPORTED
      = "Child operations are not supported for Text";
  private String content;

}
