# com.spinyowl.spinygui.core.style.stylesheet.selector.simple

Simple selectors for all, element, class, and id matching.

- Modules: core
- Source sets: main
- Direct classes: 4
- Descendant packages: 0

## Classes

### AllSelector

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/simple/AllSelector.java`
- Declaration: `public class AllSelector implements Selector`
- Responsibility: CSS selector implementation used to match elements and calculate specificity.

### ClassAttributeSelector

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/simple/ClassAttributeSelector.java`
- Declaration: `public class ClassAttributeSelector implements Selector`
- Responsibility: The class selector selects elements with a specific class attribute.

### ElementSelector

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/simple/ElementSelector.java`
- Declaration: `public class ElementSelector implements Selector`
- Responsibility: The element selector selects elements based on the Element's nodeName.

### IdAttributeSelector

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/simple/IdAttributeSelector.java`
- Declaration: `public class IdAttributeSelector implements Selector`
- Responsibility: The CSS ID selector matches an element based on the value of the element’s id attribute.
