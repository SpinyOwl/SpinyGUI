# com.spinyowl.spinygui.core.parser.impl.css.visitor

ANTLR visitors that convert CSS parse trees into stylesheet, selector, declaration, and term model objects.

- Modules: core
- Source sets: main
- Direct classes: 8
- Descendant packages: 0

## Classes

### AtRuleVisitor

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/visitor/AtRuleVisitor.java`
- Declaration: `public class AtRuleVisitor extends CSS3BaseVisitor<AtRule>`
- Responsibility: ANTLR visitor that maps parse-tree nodes into the stylesheet domain model.

### DeclarationListVisitor

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/visitor/DeclarationListVisitor.java`
- Declaration: `public class DeclarationListVisitor extends CSS3BaseVisitor<List<Declaration>>`
- Responsibility: ANTLR visitor that maps parse-tree nodes into the stylesheet domain model.

### DeclarationVisitor

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/visitor/DeclarationVisitor.java`
- Declaration: `public class DeclarationVisitor extends CSS3BaseVisitor<Declaration>`
- Responsibility: ANTLR visitor that maps parse-tree nodes into the stylesheet domain model.

### PropertyValueVisitor

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/visitor/PropertyValueVisitor.java`
- Declaration: `public class PropertyValueVisitor extends CSS3BaseVisitor<Term<?>>`
- Responsibility: ANTLR visitor that maps parse-tree nodes into the stylesheet domain model.

### RulesetVisitor

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/visitor/RulesetVisitor.java`
- Declaration: `public class RulesetVisitor extends CSS3BaseVisitor<Ruleset>`
- Responsibility: ANTLR visitor that maps parse-tree nodes into the stylesheet domain model.

### SelectorGroupVisitor

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/visitor/SelectorGroupVisitor.java`
- Declaration: `public class SelectorGroupVisitor extends CSS3BaseVisitor<List<Selector>>`
- Responsibility: ANTLR visitor that maps parse-tree nodes into the stylesheet domain model.

### SelectorVisitor

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/visitor/SelectorVisitor.java`
- Declaration: `public class SelectorVisitor extends CSS3BaseVisitor<Selector>`
- Responsibility: ANTLR visitor that maps parse-tree nodes into the stylesheet domain model.

### StyleSheetVisitor

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/parser/impl/css/visitor/StyleSheetVisitor.java`
- Declaration: `public class StyleSheetVisitor extends CSS3BaseVisitor<StyleSheet>`
- Responsibility: ANTLR visitor that maps parse-tree nodes into the stylesheet domain model.
