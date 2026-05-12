package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.style.types.Color;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InlineFragment {
  Node node;
  String text;
  float x;
  float y;
  float width;
  float height;
  float baseline;
  Font font;
  float fontSize;
  Color color;

  public boolean textFragment() {
    return text != null && !text.isEmpty();
  }

  public InlineFragment translate(float dx, float dy) {
    return InlineFragment.builder()
        .node(node)
        .text(text)
        .x(x + dx)
        .y(y + dy)
        .width(width)
        .height(height)
        .baseline(baseline + dy)
        .font(font)
        .fontSize(fontSize)
        .color(color)
        .build();
  }
}
