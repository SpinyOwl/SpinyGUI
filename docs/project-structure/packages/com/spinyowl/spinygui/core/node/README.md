# com.spinyowl.spinygui.core.node

DOM-like node hierarchy: frames, elements, empty elements, text nodes, and builders.

- Modules: core
- Source sets: main
- Direct classes: 6
- Descendant packages: 2

## Classes

### Element

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/node/Element.java`
- Declaration: `public class Element extends Node implements EventTarget`
- Responsibility: Represents element in this package.

### EmptyElement

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/node/EmptyElement.java`
- Declaration: `public class EmptyElement extends Element`
- Responsibility: Defines node that can not contain child elements.

### Frame

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/node/Frame.java`
- Declaration: `public class Frame extends Element`
- Responsibility: The root element that holds all stylesheets and other nodes.

### Node

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/node/Node.java`
- Declaration: `abstract class Node`
- Responsibility: Base structure of any node.

### NodeBuilder

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/node/NodeBuilder.java`
- Declaration: `final class NodeBuilder`
- Responsibility: Represents node builder in this package.

### Text

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/node/Text.java`
- Declaration: `final class Text extends Node`
- Responsibility: Represents text in this package.

## Child Packages

- [com.spinyowl.spinygui.core.node.intersection](intersection/README.md) - This reference describes Hit-testing strategy objects for node intersection checks, lists 3 direct classes, and aggregates 0 descendant packages.
- [com.spinyowl.spinygui.core.node.layout](layout/README.md) - This reference describes Box-model geometry value objects used by layout and rendering, lists 3 direct classes, and aggregates 0 descendant packages.

## Aggregated Contents

This package aggregates 2 descendant package(s) with 6 descendant class(es).
