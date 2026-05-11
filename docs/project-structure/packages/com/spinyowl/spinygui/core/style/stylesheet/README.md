# com.spinyowl.spinygui.core.style.stylesheet

CSS stylesheet domain model: properties, rulesets, declarations, terms, specificity, and provider registry.

- Modules: core
- Source sets: main
- Direct classes: 12
- Descendant packages: 11

## Classes

### AtRule

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/AtRule.java`
- Declaration: `public interface AtRule`
- Responsibility: Represents at rule in this package.

### Declaration

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/Declaration.java`
- Declaration: `public class Declaration`
- Responsibility: Declaration is combination of property and it's value.

### Properties

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/Properties.java`
- Declaration: `final class Properties`
- Responsibility: Represents properties in this package.

### PropertiesScanner

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/PropertiesScanner.java`
- Declaration: `final class PropertiesScanner`
- Responsibility: Represents properties scanner in this package.

### Property

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/Property.java`
- Declaration: `public class Property`
- Responsibility: Root class that describes property.

### PropertyProvider

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/PropertyProvider.java`
- Declaration: `public interface PropertyProvider`
- Responsibility: Provider contract for supplying subsystem collaborators.

### PropertyStore

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/PropertyStore.java`
- Declaration: `public interface PropertyStore`
- Responsibility: Represents property store in this package.

### PropertyStoreProvider

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/PropertyStoreProvider.java`
- Declaration: `public interface PropertyStoreProvider`
- Responsibility: Provider contract for supplying subsystem collaborators.

### Ruleset

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/Ruleset.java`
- Declaration: `public class Ruleset`
- Responsibility: Combines set of selectors and set of declarations which should be applied for elements accessed by those selectors.

### Specificity

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/Specificity.java`
- Declaration: `public class Specificity implements Comparable<Specificity>`
- Responsibility: Represents specificity in this package.

### StyleSheet

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/StyleSheet.java`
- Declaration: `public class StyleSheet`
- Responsibility: Represents style sheet in this package.

### Term

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/Term.java`
- Declaration: `abstract class Term<T>`
- Responsibility: Term is a part of a property value.

## Child Packages

- [com.spinyowl.spinygui.core.style.stylesheet.annotation](annotation/README.md) - This reference describes Annotations used by stylesheet property providers, lists 1 direct class, and aggregates 0 descendant packages.
- [com.spinyowl.spinygui.core.style.stylesheet.atrule](atrule/README.md) - This reference describes CSS at-rule model objects, lists 1 direct class, and aggregates 0 descendant packages.
- [com.spinyowl.spinygui.core.style.stylesheet.impl](impl/README.md) - This reference describes Default property-store implementation and provider scanner integration, lists 2 direct classes, and aggregates 0 descendant packages.
- [com.spinyowl.spinygui.core.style.stylesheet.property](property/README.md) - This reference describes CSS property providers that parse declarations into typed style values, lists 21 direct classes, and aggregates 0 descendant packages.
- [com.spinyowl.spinygui.core.style.stylesheet.selector](selector/README.md) - This reference describes Selector contracts and base selector types, lists 4 direct classes, and aggregates 4 descendant packages.
- [com.spinyowl.spinygui.core.style.stylesheet.term](term/README.md) - This reference describes Typed CSS term values produced by parser visitors, lists 9 direct classes, and aggregates 0 descendant packages.
- [com.spinyowl.spinygui.core.style.stylesheet.util](util/README.md) - This reference describes Utility functions for converting and validating stylesheet values, lists 1 direct class, and aggregates 0 descendant packages.

## Aggregated Contents

This package aggregates 11 descendant package(s) with 52 descendant class(es).
