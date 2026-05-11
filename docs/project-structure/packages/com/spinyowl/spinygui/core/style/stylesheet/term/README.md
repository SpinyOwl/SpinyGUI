# com.spinyowl.spinygui.core.style.stylesheet.term

Typed CSS term values produced by parser visitors.

- Modules: core
- Source sets: main
- Direct classes: 9
- Descendant packages: 0

## Classes

### TermColor

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/term/TermColor.java`
- Declaration: `public class TermColor extends Term<Color>`
- Responsibility: Represents term color in this package.

### TermFloat

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/term/TermFloat.java`
- Declaration: `public class TermFloat extends Term<Float>`
- Responsibility: Represents term float in this package.

### TermFunction

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/term/TermFunction.java`
- Declaration: `public class TermFunction extends TermList`
- Responsibility: Represents term function in this package.

### TermIdent

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/term/TermIdent.java`
- Declaration: `public class TermIdent extends Term<String>`
- Responsibility: Represents term ident in this package.

### TermInteger

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/term/TermInteger.java`
- Declaration: `public class TermInteger extends Term<Integer>`
- Responsibility: Represents term integer in this package.

### TermLength

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/term/TermLength.java`
- Declaration: `public class TermLength extends TermUnit<Length<?>>`
- Responsibility: Represents term length in this package.

### TermList

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/term/TermList.java`
- Declaration: `public class TermList extends Term<List<Term<?>>>`
- Responsibility: Represents term list in this package.

### TermUnit

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/term/TermUnit.java`
- Declaration: `public class TermUnit<U extends Unit> extends Term<U>`
- Responsibility: Represents term unit in this package.

### TermURI

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/term/TermURI.java`
- Declaration: `public class TermURI extends Term<String>`
- Responsibility: Represents term uri in this package.
