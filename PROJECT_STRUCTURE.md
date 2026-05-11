# Project Structure

SpinyGUI is a modular Java GUI library with a browser-engine-like split between node tree, CSS parsing/style resolution, layout, event processing, and rendering backends.

Generated package documents are stored under `docs/project-structure/packages/`. Each package document lists direct classes first, then links to child packages so the documentation can be read from deepest packages upward.

## Gradle Modules

- `core` - Core DOM-like node model, CSS parser/style system, layout, events, input, fonts, animation, and platform abstraction.
- `core.backend` - Renderer backend API shared by concrete rendering implementations.
- `core.backend.lwjgl.nanovg` - LWJGL/NanoVG renderer implementation for drawing the core scene graph.
- `demo.simple` - Small launcher-style examples for exercising the aggregate SpinyGUI module.
- `demo.complex` - GLFW/LWJGL demo harness and NanoVG example runner.
- `spinygui` - Aggregate module that re-exports the core and default backend modules.

## Main Subsystems

- Node tree: core node classes model frames, elements, text, attributes, parent/child links, pseudo-state, and box geometry.
- Style and CSS: stylesheet model, selectors, property providers, ANTLR visitors, typed CSS values, and `ResolvedStyle` convert parsed CSS into values usable by layout/rendering.
- Layout: layout contracts and implementations calculate box-model rectangles, text metrics, normal-flow/positioned layout trees, scroll sizes, and client sizes.
- Events and input: system events are translated by system listeners/processors into application events and node state changes.
- Fonts and metrics: font service/storage abstractions load platform fonts and expose text metrics for layout/rendering.
- Rendering: backend SPI defines `Renderer`; the LWJGL/NanoVG backend traverses layout nodes and draws elements, borders, and text.
- Demos: simple and complex demo modules exercise the aggregate API and NanoVG backend.

## Package Index

- [com](docs/project-structure/packages/com/README.md) - 0 direct class(es); Top-level Java namespace folder for project packages.
- [com.spinyowl](docs/project-structure/packages/com/spinyowl/README.md) - 0 direct class(es); SpinyOwl namespace folder.
- [com.spinyowl.spinygui](docs/project-structure/packages/com/spinyowl/spinygui/README.md) - 0 direct class(es); SpinyGUI namespace folder aggregating core, backend, and demo packages.
- [com.spinyowl.spinygui.core](docs/project-structure/packages/com/spinyowl/spinygui/core/README.md) - 1 direct class(es); Top-level core configuration and shared entry points for the GUI engine.
- [com.spinyowl.spinygui.core.animation](docs/project-structure/packages/com/spinyowl/spinygui/core/animation/README.md) - 3 direct class(es); Frame-time animation contracts and a simple animator loop.
- [com.spinyowl.spinygui.core.backend](docs/project-structure/packages/com/spinyowl/spinygui/core/backend/README.md) - 0 direct class(es); Backend namespace folder for renderer APIs and implementations.
- [com.spinyowl.spinygui.core.backend.renderer](docs/project-structure/packages/com/spinyowl/spinygui/core/backend/renderer/README.md) - 1 direct class(es); Renderer SPI consumed by backend implementations.
- [com.spinyowl.spinygui.core.backend.renderer.lwjgl](docs/project-structure/packages/com/spinyowl/spinygui/core/backend/renderer/lwjgl/README.md) - 0 direct class(es); Package for lwjgl related classes.
- [com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg](docs/project-structure/packages/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/README.md) - 4 direct class(es); NanoVG renderer orchestration and specialized element/text/border renderers.
- [com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util](docs/project-structure/packages/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/util/README.md) - 3 direct class(es); NanoVG drawing and color helpers.
- [com.spinyowl.spinygui.core.clipboard](docs/project-structure/packages/com/spinyowl/spinygui/core/clipboard/README.md) - 1 direct class(es); Clipboard abstraction used by platform integrations.
- [com.spinyowl.spinygui.core.cursor](docs/project-structure/packages/com/spinyowl/spinygui/core/cursor/README.md) - 3 direct class(es); Cursor model and cursor service abstraction.
- [com.spinyowl.spinygui.core.event](docs/project-structure/packages/com/spinyowl/spinygui/core/event/README.md) - 21 direct class(es); Application-level events emitted to nodes and event targets.
- [com.spinyowl.spinygui.core.event.listener](docs/project-structure/packages/com/spinyowl/spinygui/core/event/listener/README.md) - 1 direct class(es); Generic event listener contract for application events.
- [com.spinyowl.spinygui.core.event.processor](docs/project-structure/packages/com/spinyowl/spinygui/core/event/processor/README.md) - 2 direct class(es); Dispatch logic for routing application events to node listeners.
- [com.spinyowl.spinygui.core.font](docs/project-structure/packages/com/spinyowl/spinygui/core/font/README.md) - 5 direct class(es); CSS-like font value objects: family, size, stretch, style, and weight.
- [com.spinyowl.spinygui.core.image](docs/project-structure/packages/com/spinyowl/spinygui/core/image/README.md) - 1 direct class(es); Image abstraction used by style and rendering layers.
- [com.spinyowl.spinygui.core.input](docs/project-structure/packages/com/spinyowl/spinygui/core/input/README.md) - 10 direct class(es); Input domain model for keyboard, mouse, shortcuts, and user-facing key mappings.
- [com.spinyowl.spinygui.core.input.impl](docs/project-structure/packages/com/spinyowl/spinygui/core/input/impl/README.md) - 3 direct class(es); Default mutable implementations of input services.
- [com.spinyowl.spinygui.core.layout](docs/project-structure/packages/com/spinyowl/spinygui/core/layout/README.md) - 5 direct class(es); Layout contracts, layout context, and text/element layout interfaces.
- [com.spinyowl.spinygui.core.layout.impl](docs/project-structure/packages/com/spinyowl/spinygui/core/layout/impl/README.md) - 7 direct class(es); Concrete layout algorithms and utilities for block, flex, none, text, and layout tree updates.
- [com.spinyowl.spinygui.core.node](docs/project-structure/packages/com/spinyowl/spinygui/core/node/README.md) - 6 direct class(es); DOM-like node hierarchy: frames, elements, empty elements, text nodes, and builders.
- [com.spinyowl.spinygui.core.node.intersection](docs/project-structure/packages/com/spinyowl/spinygui/core/node/intersection/README.md) - 3 direct class(es); Hit-testing strategy objects for node intersection checks.
- [com.spinyowl.spinygui.core.node.layout](docs/project-structure/packages/com/spinyowl/spinygui/core/node/layout/README.md) - 3 direct class(es); Box-model geometry value objects used by layout and rendering.
- [com.spinyowl.spinygui.core.parser](docs/project-structure/packages/com/spinyowl/spinygui/core/parser/README.md) - 2 direct class(es); Parser interfaces for HTML-like node trees and stylesheets.
- [com.spinyowl.spinygui.core.parser.impl](docs/project-structure/packages/com/spinyowl/spinygui/core/parser/impl/README.md) - 4 direct class(es); Default parser implementations and parser factory code.
- [com.spinyowl.spinygui.core.parser.impl.css](docs/project-structure/packages/com/spinyowl/spinygui/core/parser/impl/css/README.md) - 0 direct class(es); CSS parser namespace containing generated ANTLR artifacts and handwritten semantic visitors.
- [com.spinyowl.spinygui.core.parser.impl.css.antlr](docs/project-structure/packages/com/spinyowl/spinygui/core/parser/impl/css/antlr/README.md) - 6 direct class(es); Generated ANTLR CSS3 lexer/parser/listener/visitor artifacts. Regenerate from the grammar instead of hand-editing.
- [com.spinyowl.spinygui.core.parser.impl.css.visitor](docs/project-structure/packages/com/spinyowl/spinygui/core/parser/impl/css/visitor/README.md) - 8 direct class(es); ANTLR visitors that convert CSS parse trees into stylesheet, selector, declaration, and term model objects.
- [com.spinyowl.spinygui.core.style](docs/project-structure/packages/com/spinyowl/spinygui/core/style/README.md) - 1 direct class(es); Resolved style state applied to nodes after rule matching and property conversion.
- [com.spinyowl.spinygui.core.style.manager](docs/project-structure/packages/com/spinyowl/spinygui/core/style/manager/README.md) - 2 direct class(es); Style manager contract and implementation for applying stylesheets to node trees.
- [com.spinyowl.spinygui.core.style.stylesheet](docs/project-structure/packages/com/spinyowl/spinygui/core/style/stylesheet/README.md) - 12 direct class(es); CSS stylesheet domain model: properties, rulesets, declarations, terms, specificity, and provider registry.
- [com.spinyowl.spinygui.core.style.stylesheet.annotation](docs/project-structure/packages/com/spinyowl/spinygui/core/style/stylesheet/annotation/README.md) - 1 direct class(es); Annotations used by stylesheet property providers.
- [com.spinyowl.spinygui.core.style.stylesheet.atrule](docs/project-structure/packages/com/spinyowl/spinygui/core/style/stylesheet/atrule/README.md) - 1 direct class(es); CSS at-rule model objects.
- [com.spinyowl.spinygui.core.style.stylesheet.impl](docs/project-structure/packages/com/spinyowl/spinygui/core/style/stylesheet/impl/README.md) - 2 direct class(es); Default property-store implementation and provider scanner integration.
- [com.spinyowl.spinygui.core.style.stylesheet.property](docs/project-structure/packages/com/spinyowl/spinygui/core/style/stylesheet/property/README.md) - 21 direct class(es); CSS property providers that parse declarations into typed style values.
- [com.spinyowl.spinygui.core.style.stylesheet.selector](docs/project-structure/packages/com/spinyowl/spinygui/core/style/stylesheet/selector/README.md) - 4 direct class(es); Selector contracts and base selector types.
- [com.spinyowl.spinygui.core.style.stylesheet.selector.combinator](docs/project-structure/packages/com/spinyowl/spinygui/core/style/stylesheet/selector/combinator/README.md) - 5 direct class(es); Combinator selectors for descendant, child, sibling, adjacent sibling, and compound matching.
- [com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoclass](docs/project-structure/packages/com/spinyowl/spinygui/core/style/stylesheet/selector/pseudoclass/README.md) - 1 direct class(es); Pseudo-class selector implementations.
- [com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoelement](docs/project-structure/packages/com/spinyowl/spinygui/core/style/stylesheet/selector/pseudoelement/README.md) - 3 direct class(es); Pseudo-element selector implementations.
- [com.spinyowl.spinygui.core.style.stylesheet.selector.simple](docs/project-structure/packages/com/spinyowl/spinygui/core/style/stylesheet/selector/simple/README.md) - 4 direct class(es); Simple selectors for all, element, class, and id matching.
- [com.spinyowl.spinygui.core.style.stylesheet.term](docs/project-structure/packages/com/spinyowl/spinygui/core/style/stylesheet/term/README.md) - 9 direct class(es); Typed CSS term values produced by parser visitors.
- [com.spinyowl.spinygui.core.style.stylesheet.util](docs/project-structure/packages/com/spinyowl/spinygui/core/style/stylesheet/util/README.md) - 1 direct class(es); Utility functions for converting and validating stylesheet values.
- [com.spinyowl.spinygui.core.style.types](docs/project-structure/packages/com/spinyowl/spinygui/core/style/types/README.md) - 10 direct class(es); Typed CSS value objects and constants for non-nested style domains.
- [com.spinyowl.spinygui.core.style.types.background](docs/project-structure/packages/com/spinyowl/spinygui/core/style/types/background/README.md) - 3 direct class(es); Background-origin, repeat, and sizing value objects.
- [com.spinyowl.spinygui.core.style.types.border](docs/project-structure/packages/com/spinyowl/spinygui/core/style/types/border/README.md) - 2 direct class(es); Border item and border-style value objects.
- [com.spinyowl.spinygui.core.style.types.flex](docs/project-structure/packages/com/spinyowl/spinygui/core/style/types/flex/README.md) - 6 direct class(es); Flexbox alignment, direction, wrapping, and justification value constants.
- [com.spinyowl.spinygui.core.style.types.length](docs/project-structure/packages/com/spinyowl/spinygui/core/style/types/length/README.md) - 3 direct class(es); CSS length units, length wrappers, and conversion contract.
- [com.spinyowl.spinygui.core.system](docs/project-structure/packages/com/spinyowl/spinygui/core/system/README.md) - 0 direct class(es); Package for system related classes.
- [com.spinyowl.spinygui.core.system.event](docs/project-structure/packages/com/spinyowl/spinygui/core/system/event/README.md) - 16 direct class(es); Raw platform/window/input events before conversion into application-level events.
- [com.spinyowl.spinygui.core.system.event.listener](docs/project-structure/packages/com/spinyowl/spinygui/core/system/event/listener/README.md) - 27 direct class(es); Adapters that translate raw system events into core event processing and state changes.
- [com.spinyowl.spinygui.core.system.event.processor](docs/project-structure/packages/com/spinyowl/spinygui/core/system/event/processor/README.md) - 2 direct class(es); System-event processor contract and implementation for dispatching platform events.
- [com.spinyowl.spinygui.core.system.event.provider](docs/project-structure/packages/com/spinyowl/spinygui/core/system/event/provider/README.md) - 2 direct class(es); Provider for mapping raw system event classes to listener instances.
- [com.spinyowl.spinygui.core.system.font](docs/project-structure/packages/com/spinyowl/spinygui/core/system/font/README.md) - 7 direct class(es); Platform font loading, text metrics, and font storage abstractions.
- [com.spinyowl.spinygui.core.system.font.impl](docs/project-structure/packages/com/spinyowl/spinygui/core/system/font/impl/README.md) - 3 direct class(es); Default font service, storage, and platform-specific font directory discovery.
- [com.spinyowl.spinygui.core.system.input](docs/project-structure/packages/com/spinyowl/spinygui/core/system/input/README.md) - 3 direct class(es); Platform-facing key, modifier, action, and mouse-button enums.
- [com.spinyowl.spinygui.core.time](docs/project-structure/packages/com/spinyowl/spinygui/core/time/README.md) - 1 direct class(es); Time service abstraction for animation and frame timing.
- [com.spinyowl.spinygui.core.util](docs/project-structure/packages/com/spinyowl/spinygui/core/util/README.md) - 7 direct class(es); Small utilities for class-key maps, IO, node visibility, references, and text handling.
- [com.spinyowl.spinygui.demo](docs/project-structure/packages/com/spinyowl/spinygui/demo/README.md) - 0 direct class(es); Demo namespace folder for runnable examples.
- [com.spinyowl.spinygui.demo.complex](docs/project-structure/packages/com/spinyowl/spinygui/demo/complex/README.md) - 2 direct class(es); Windowed GLFW/LWJGL demo framework and concrete NanoVG demo.
- [com.spinyowl.spinygui.demo.simple](docs/project-structure/packages/com/spinyowl/spinygui/demo/simple/README.md) - 2 direct class(es); Simple demo entry points.

## Reading Order

For bottom-up navigation, start with the deepest packages such as `style.stylesheet.selector.*`, `style.stylesheet.term`, `style.types.*`, `system.event.listener`, `layout.impl`, and `backend.renderer.lwjgl.nanovg.util`; then move upward through their parent package documents and finish with this root overview.