package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Represents render tree. Render tree is a tree of {@link LayoutNode} instances. Each node contains
 * reference to {@link Node} instance that forms this node.
 */
@Data
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Viewport extends LayoutElement {

  static Viewport of(@NonNull Frame frame) {
    Viewport viewport = new Viewport();
    viewport.node(frame);
    return viewport;
  }

  public Frame frame() {
    return node().frame();
  }
}
