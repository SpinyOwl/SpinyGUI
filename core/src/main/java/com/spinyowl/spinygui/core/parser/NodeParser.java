package com.spinyowl.spinygui.core.parser;

import com.spinyowl.spinygui.core.node.Node;

/** Node marshaller. Used to convert node to xml and vise versa. */
public interface NodeParser {

  Node fromXml(String xml);

  String toXml(Node node);

  String toXml(Node node, boolean pretty);
}
