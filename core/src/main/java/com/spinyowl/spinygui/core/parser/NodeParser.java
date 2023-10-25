package com.spinyowl.spinygui.core.parser;

import com.spinyowl.spinygui.core.node.Node;

/** Node marshaller. Used to convert node to xml and vise versa. */
public interface NodeParser {

  Node fromHtml(String xml);

  String toHtml(Node node);

  String toHtml(Node node, boolean pretty);
}
