# com.spinyowl.spinygui.core.parser.impl

Default parser implementations and parser factory code.

- Modules: core
- Source sets: main
- Direct classes: 4
- Descendant packages: 3

## Classes

### DefaultNodeParser

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/DefaultNodeParser.java`
- Declaration: `public class DefaultNodeParser implements NodeParser`
- Responsibility: Represents default node parser in this package.

### DefaultStyleSheetParser

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/DefaultStyleSheetParser.java`
- Declaration: `final class DefaultStyleSheetParser implements StyleSheetParser`
- Responsibility: Used to read stylesheets from css.

### ParseException

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/ParseException.java`
- Declaration: `public class ParseException extends RuntimeException`
- Responsibility: Thrown when there is some node conversion exception happens.

### StyleSheetParserFactory

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/StyleSheetParserFactory.java`
- Declaration: `final class StyleSheetParserFactory`
- Responsibility: Represents style sheet parser factory in this package.

## Child Packages

- [com.spinyowl.spinygui.core.parser.impl.css](css/README.md) - CSS parser namespace containing generated ANTLR artifacts and handwritten semantic visitors.

## Aggregated Contents

This package aggregates 3 descendant package(s) with 14 descendant class(es).
