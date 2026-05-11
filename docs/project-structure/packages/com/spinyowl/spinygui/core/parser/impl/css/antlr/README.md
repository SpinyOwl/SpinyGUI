# com.spinyowl.spinygui.core.parser.impl.css.antlr

Generated ANTLR CSS3 lexer/parser/listener/visitor artifacts. Regenerate from the grammar instead of hand-editing.

- Modules: core
- Source sets: main
- Direct classes: 6
- Descendant packages: 0

## Classes

### CSS3BaseListener

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/antlr/CSS3BaseListener.java`
- Declaration: `public class CSS3BaseListener implements CSS3Listener`
- Responsibility: This class provides an empty implementation of CSS3Listener, which can be extended to create a listener which only needs to handle a subset of the available methods.

### CSS3BaseVisitor

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/antlr/CSS3BaseVisitor.java`
- Declaration: `public class CSS3BaseVisitor<T> extends AbstractParseTreeVisitor<T> implements CSS3Visitor<T>`
- Responsibility: This class provides an empty implementation of CSS3Visitor, which can be extended to create a visitor which only needs to handle a subset of the available methods.

### CSS3Lexer

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/antlr/CSS3Lexer.java`
- Declaration: `public class CSS3Lexer extends Lexer`
- Responsibility: Generated ANTLR CSS3 parser artifact; regenerate it from CSS3.g4 rather than editing by hand.

### CSS3Listener

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/antlr/CSS3Listener.java`
- Declaration: `public interface CSS3Listener extends ParseTreeListener`
- Responsibility: This interface defines a complete listener for a parse tree produced by CSS3Parser.

### CSS3Parser

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/antlr/CSS3Parser.java`
- Declaration: `public class CSS3Parser extends Parser`
- Responsibility: Generated ANTLR CSS3 parser artifact; regenerate it from CSS3.g4 rather than editing by hand.

### CSS3Visitor

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/antlr/CSS3Visitor.java`
- Declaration: `public interface CSS3Visitor<T> extends ParseTreeVisitor<T>`
- Responsibility: This interface defines a complete generic visitor for a parse tree produced by CSS3Parser.
