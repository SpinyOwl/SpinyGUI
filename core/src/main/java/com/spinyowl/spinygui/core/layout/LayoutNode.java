package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Node;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public class LayoutNode {

  @NonNull private final Node node;
  @NonNull private final List<LayoutNode> children;
}
