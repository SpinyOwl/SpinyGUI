# com.spinyowl.spinygui.core.system.font

Platform font loading, text metrics, and font storage abstractions.

- Modules: core
- Source sets: main
- Direct classes: 7
- Descendant packages: 1

## Classes

### FontDirectoriesProvider

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/font/FontDirectoriesProvider.java`
- Declaration: `public interface FontDirectoriesProvider`
- Responsibility: Provider contract for supplying subsystem collaborators.

### FontLoadingException

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/font/FontLoadingException.java`
- Declaration: `public class FontLoadingException extends RuntimeException`
- Responsibility: Represents font loading exception in this package.

### FontService

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/font/FontService.java`
- Declaration: `public interface FontService`
- Responsibility: Font service, responsible for loading and caching font data, and calculating text metrics.

### FontStorage

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/font/FontStorage.java`
- Declaration: `public interface FontStorage`
- Responsibility: Represents font storage in this package.

### SystemFontLoader

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/font/SystemFontLoader.java`
- Declaration: `public class SystemFontLoader`
- Responsibility: Represents system font loader in this package.

### TextLineMetrics

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/font/TextLineMetrics.java`
- Declaration: `final class TextLineMetrics`
- Responsibility: Represents text line metrics in this package.

### TextMetrics

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/system/font/TextMetrics.java`
- Declaration: `final class TextMetrics`
- Responsibility: Represents text metrics in this package.

## Child Packages

- [com.spinyowl.spinygui.core.system.font.impl](impl/README.md) - Default font service, storage, and platform-specific font directory discovery.

## Aggregated Contents

This package aggregates 1 descendant package(s) with 3 descendant class(es).
