package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LayoutContext {
  private Float lastTextEndX;
  private Float lastTextEndY;
  private Float lastBlockBottomY;
  private Node previousNode;

  public void cleanup() {
    lastTextEndX = null;
    lastTextEndY = null;
    previousNode = null;
    lastBlockBottomY = null;
  }
}
