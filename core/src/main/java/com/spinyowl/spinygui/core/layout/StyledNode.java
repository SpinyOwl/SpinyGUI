package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.style.stylesheet.Ruleset;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class StyledNode {
  @NonNull private Node node;
  @NonNull private List<Ruleset> rules;
  @NonNull private Map<String, List<Ruleset>> pseudoElementRules;
  @NonNull private List<StyledNode> children;

  public StyledNode(Node node) {
    this(node, List.of(), Map.of(), List.of());
  }
}
