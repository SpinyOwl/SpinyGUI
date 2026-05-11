# com.spinyowl.spinygui.core.style.stylesheet.selector

Selector contracts and base selector types.

- Modules: core
- Source sets: main
- Direct classes: 4
- Descendant packages: 4

## Classes

### CombinatorSelector

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/CombinatorSelector.java`
- Declaration: `abstract class CombinatorSelector implements Selector`
- Responsibility: CSS selector implementation used to match elements and calculate specificity.

### PseudoClassSelector

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/PseudoClassSelector.java`
- Declaration: `public interface PseudoClassSelector extends Selector`
- Responsibility: Interface for pseudo-class selectors, which used to define a special state of an element.

### PseudoElementSelector

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/PseudoElementSelector.java`
- Declaration: `public interface PseudoElementSelector extends Selector`
- Responsibility: Interface for pseudo-element selectors, which used to define a special inner part of element.

### Selector

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/Selector.java`
- Declaration: `public interface Selector extends Comparable<Selector>`
- Responsibility: Style selector interface.

## Child Packages

- [com.spinyowl.spinygui.core.style.stylesheet.selector.combinator](combinator/README.md) - Combinator selectors for descendant, child, sibling, adjacent sibling, and compound matching.
- [com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoclass](pseudoclass/README.md) - Pseudo-class selector implementations.
- [com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoelement](pseudoelement/README.md) - Pseudo-element selector implementations.
- [com.spinyowl.spinygui.core.style.stylesheet.selector.simple](simple/README.md) - Simple selectors for all, element, class, and id matching.

## Aggregated Contents

This package aggregates 4 descendant package(s) with 13 descendant class(es).
