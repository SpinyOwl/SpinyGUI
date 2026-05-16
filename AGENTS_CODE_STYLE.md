# Agent Code Style And Principles

This project is a modular Java GUI library with a browser-engine-like architecture. Treat the codebase as a DOM/CSS/layout/event/rendering pipeline, not as a widget toolkit with isolated controls.

## Architectural Principles

- Keep the core backend-agnostic. Core nodes, styles, layout, input, events, fonts, and parsers should not depend on NanoVG/OpenGL rendering details.
- Preserve the pipeline shape: parse/build nodes, parse CSS, resolve style, run layout, translate system events, dispatch GUI events, render an already styled and laid-out `Frame`.
- Prefer interface boundaries for services and put defaults in `impl` packages. Existing examples include parser, layout, style manager, event processor, font service, mouse service, and shortcut registry.
- Model GUI state explicitly on nodes and services. Hover, focus, pressed state, scroll offsets, layout boxes, mouse positions, and resolved styles are mutable runtime state.
- Keep CSS concepts typed where possible. Use `Property`, `Term`, `Selector`, `Length`, `Color`, and style type classes rather than passing raw strings past parser/property boundaries.
- Treat generated ANTLR files under `core/src/main/java/.../parser/impl/css/antlr` as generated. Change `CSS3.g4` and regenerate outputs instead of manually editing generated parser classes.

## Java Style

- Java modules are deliberate. Update `module-info.java` when adding exported APIs or new module dependencies.
- The build uses Java 25 source/target compatibility.
- Formatting follows the checked-in IntelliJ Google Java style. Keep two-space indentation and readable wrapped method chains.
- Lombok is part of the project style. Existing code uses `@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@NonNull`, `@ToString`, `@NoArgsConstructor`, and `@Slf4j`.
- Lombok accessors are fluent and not chained. Prefer `element.box()` over `getBox()` and avoid introducing chained setter assumptions.
- Keep constructors small and use required-constructor dependency injection where the surrounding package already does so.
- Use static utility classes for cross-cutting calculations only when they match existing patterns such as `LayoutUtils`, `StyleUtils`, `NodeUtilities`, `TextUtil`, and NanoVG utility classes.
- Do not add broad frameworks for dependency injection, rendering, parsing, or event dispatch. Existing composition is manual and explicit.

## Package Conventions

- `core.node` owns the DOM-like tree model. Add tree behavior here only when it belongs to all nodes/elements, not to rendering or parsing.
- `core.style.stylesheet` owns the CSS object model. Add new CSS properties through property providers and the property store, not by special-casing `ResolvedStyle` first.
- `core.style.types` owns strongly typed CSS values. Add new value types here when a property needs a reusable domain object.
- `core.parser.impl.css.visitor` is the semantic CSS conversion layer. The grammar recognizes more CSS than visitors support; update visitors and property providers together.
- `core.layout.impl` owns layout algorithms and box updates. Keep rendering out of layout; layout should populate geometry and layout-parent relationships.
- `core.system.event.*` handles raw platform events. Convert them into GUI events before application listeners see them.
- `core.event.*` handles GUI-level events and listener dispatch.
- `core.backend.*` should consume `Frame`, `Element`, `Text`, style, and box data; it should not mutate core model state except through clearly intentional renderer lifecycle effects.

## CSS And Style Rules

- Respect rule ordering: default rules, frame stylesheets ordered by specificity, then inline style declarations.
- Preserve specificity semantics when adding selectors. Selector classes should implement matching and specificity consistently.
- Be explicit about unsupported CSS. Current visitors often return `null` or throw `NotImplementedException` for partial features; do not silently pretend full browser CSS support exists.
- The ANTLR grammar supports more constructs than the semantic model. When enabling a grammar feature, add model classes, visitors, property conversion, defaults, tests, and serialization behavior as needed.
- `ResolvedStyle` is a typed facade over a generic map. Add typed accessors for supported properties that are used outside property conversion.

## Event And Input Rules

- Keep the two-stage event pipeline intact: system event queue first, GUI event queue second.
- Event dispatch is exact-class based in current processors. Do not assume superclass listeners receive subclass events unless you change and test that behavior.
- System listeners should update state and emit GUI events in the same place, following existing cursor, mouse, key, char, scroll, and window listener patterns.
- Mouse hit-testing should respect `NodeUtilities` and each node's `Intersection` strategy.
- Keyboard behavior should pass through `KeyboardLayout` and `ShortcutRegistry`; avoid hard-coding native key codes in GUI-level code.

## Layout Rules

- Layout writes to `Box`, offset parent, layout child nodes, scroll sizes, client sizes, and text cursor positions.
- Keep block, flex, text, and none layout behavior separate. Add shared calculations to `LayoutUtils` only when multiple algorithms need them.
- Flex layout delegates to Yoga. Extend the mapping between SpinyGUI style values and Yoga properties rather than reimplementing flexbox manually.
- Use `Length` and style accessors for dimensions, border, padding, margin, and positioning. Avoid raw numeric shortcuts unless the existing method is already in pixel space.

## Rendering Rules

- `Renderer` is intentionally minimal: `initialize`, `render`, `destroy`.
- NanoVG code must manage native resources carefully. Follow try-with-resources patterns for `NVGColor`, `NVGPaint`, and other stack/calloc resources where applicable.
- Rendering should traverse the layout tree, not the raw child tree, because positioned and normal-flow children may differ.
- Current NanoVG text rendering is placeholder/debug-like. Do not document it or build on it as complete glyph rendering without implementing and testing actual text drawing.
- Current border rendering is not full side-specific CSS border support. Be explicit when extending it.

## Testing And Verification

- For parser changes, test both successful parse/model conversion and unsupported/invalid syntax behavior.
- For style changes, test property parsing, defaults, inheritance if applicable, shorthand expansion, and resolved style output.
- For layout changes, test box geometry, scroll/client size updates, positioned nodes, and text wrapping/metrics.
- For event changes, test queue behavior and listener side effects. Existing tests under `core/src/test/java/.../system/event/listener` are the closest pattern.
- For backend changes, run a demo when possible and verify lifecycle/resource cleanup paths.

## Known Caution Areas

- Some CSS features are grammar-recognized but semantically unsupported or partially supported.
- Generated ANTLR outputs are currently modified in the working tree; do not overwrite them accidentally.
- `GeneralSiblingSelector`, clickable-only recursive hit testing, text metrics edge handling, and exact-class event dispatch have potential edge cases noted in current code analysis.
- Demo classes contain exploratory assertions and embedded HTML/CSS examples. Do not treat them as complete production tests.
