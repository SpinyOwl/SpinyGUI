package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Frame;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public class LayoutTree {
  @NonNull private Frame frame;
  @NonNull private List<LayoutNode> children;
}
