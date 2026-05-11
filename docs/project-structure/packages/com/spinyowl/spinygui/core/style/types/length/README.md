# com.spinyowl.spinygui.core.style.types.length

CSS length units, length wrappers, and conversion contract.

- Modules: core
- Source sets: main
- Direct classes: 3
- Descendant packages: 0

## Classes

### Length

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/types/length/Length.java`
- Declaration: `public class Length<T extends Number> implements Unit`
- Responsibility: CSS unit/value object used by style conversion.

### LengthConverter

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/types/length/LengthConverter.java`
- Declaration: `public interface LengthConverter<T extends Number>`
- Responsibility: Converts length to pixels.

### Unit

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/style/types/length/Unit.java`
- Declaration: `public interface Unit`
- Responsibility: Represents unit in this package.
