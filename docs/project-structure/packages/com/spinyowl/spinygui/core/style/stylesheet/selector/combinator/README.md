# com.spinyowl.spinygui.core.style.stylesheet.selector.combinator

Combinator selectors for descendant, child, sibling, adjacent sibling, and compound matching.

- Modules: core
- Source sets: main
- Direct classes: 5
- Descendant packages: 0

## Classes

### AdjacentSiblingSelector

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/combinator/AdjacentSiblingSelector.java`
- Declaration: `public class AdjacentSiblingSelector extends CombinatorSelector`
- Responsibility: Adjacent Sibling Selector (+) The adjacent sibling selector is used to select an element that is directly after another specific element.

### AndSelector

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/combinator/AndSelector.java`
- Declaration: `public class AndSelector extends CombinatorSelector`
- Responsibility: CSS selector implementation used to match elements and calculate specificity.

### ChildSelector

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/combinator/ChildSelector.java`
- Declaration: `public class ChildSelector extends CombinatorSelector`
- Responsibility: Child Selector (>) The child selector selects all elements that are the children of a specified element.

### DescendantSelector

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/combinator/DescendantSelector.java`
- Declaration: `public class DescendantSelector extends CombinatorSelector`
- Responsibility: Descendant Selector The descendant selector matches all elements that are descendants of a specified element.

### GeneralSiblingSelector

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/combinator/GeneralSiblingSelector.java`
- Declaration: `public class GeneralSiblingSelector extends CombinatorSelector`
- Responsibility: General Sibling Selector (~) The general sibling selector selects all elements that are siblings of a specified element.
