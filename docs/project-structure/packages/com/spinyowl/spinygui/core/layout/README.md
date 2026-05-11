# com.spinyowl.spinygui.core.layout

Layout contracts, layout context, and text/element layout interfaces.

- Modules: core
- Source sets: main
- Direct classes: 5
- Descendant packages: 1

## Classes

### ElementLayout

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/layout/ElementLayout.java`
- Declaration: `public interface ElementLayout extends Layout<Element>`
- Responsibility: Defines branch for element node layout implementations.

### Layout

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/layout/Layout.java`
- Declaration: `public interface Layout<T extends Node>`
- Responsibility: Layout manager.

### LayoutContext

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/layout/LayoutContext.java`
- Declaration: `public class LayoutContext`
- Responsibility: Represents layout context in this package.

### LayoutService

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/layout/LayoutService.java`
- Declaration: `public interface LayoutService`
- Responsibility: Layout service is an entry point to layout system.

### TextLayout

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/layout/TextLayout.java`
- Declaration: `public interface TextLayout extends Layout<Text>`
- Responsibility: Defines branch for text node layout implementations.

## Child Packages

- [com.spinyowl.spinygui.core.layout.impl](impl/README.md) - This reference describes Concrete layout algorithms and utilities for block, flex, none, text, and layout tree updates, lists 7 direct classes, and aggregates 0 descendant packages.

## Aggregated Contents

This package aggregates 1 descendant package(s) with 7 descendant class(es).
