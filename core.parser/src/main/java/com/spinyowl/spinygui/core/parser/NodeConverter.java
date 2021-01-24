package com.spinyowl.spinygui.core.parser;

import com.spinyowl.spinygui.core.node.Node;

public interface NodeConverter {

  Node fromXml(String xml);

  String toXml(Node node);
}
