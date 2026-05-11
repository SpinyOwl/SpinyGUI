# com.spinyowl.spinygui.core.layout.impl

Concrete layout algorithms and utilities for block, flex, none, text, and layout tree updates.

- Modules: core
- Source sets: main
- Direct classes: 7
- Descendant packages: 0

## Classes

### BlockLayout

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/layout/impl/BlockLayout.java`
- Declaration: `public class BlockLayout implements ElementLayout`
- Responsibility: Layout algorithm or layout contract for the named display/text mode.

### FlexLayout

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/layout/impl/FlexLayout.java`
- Declaration: `public class FlexLayout implements ElementLayout`
- Responsibility: Layout algorithm or layout contract for the named display/text mode.

### LayoutServiceImpl

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/layout/impl/LayoutServiceImpl.java`
- Declaration: `public class LayoutServiceImpl implements LayoutService`
- Responsibility: Layout service is an entry point to layout system.

### LayoutServiceProvider

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/layout/impl/LayoutServiceProvider.java`
- Declaration: `final class LayoutServiceProvider`
- Responsibility: Provider implementation or registry for constructing subsystem collaborators.

### LayoutUtils

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/layout/impl/LayoutUtils.java`
- Declaration: `final class LayoutUtils`
- Responsibility: Static helper methods for the named subsystem.

### NoneLayout

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/layout/impl/NoneLayout.java`
- Declaration: `public class NoneLayout implements ElementLayout`
- Responsibility: Layout algorithm or layout contract for the named display/text mode.

### TextLayoutImpl

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/layout/impl/TextLayoutImpl.java`
- Declaration: `public class TextLayoutImpl implements TextLayout`
- Responsibility: Default implementation of the matching interface.
