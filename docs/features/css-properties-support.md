Supported means the property is registered by a `PropertyProvider` in
`core.style.stylesheet.property` and can be parsed/applied through the stylesheet property store.

## Bounded 2D transform and transition support

`transform` and `transform-origin` support the delivered 2D subset: ordered `translate`,
`translateX`, `translateY`, `scale`, `scaleX`, `scaleY`, and `rotate` operations with pixel or
percentage translations. Layout geometry remains computed-style based; transforms affect visual
presentation and the existing affine coordinate path.

`transition`, `transition-property`, `transition-duration`, `transition-delay`, and
`transition-timing-function` support the shorthand and longhands for the paint-only target subset:
`opacity`, `color`, `background-color`, the four border-color longhands, and compatible 2D
`transform` operation lists. Supported timing functions are `linear`, `ease`, `ease-in`,
`ease-out`, `ease-in-out`, and `cubic-bezier(x1, y1, x2, y2)`. Compatible transform lists have
the same operation count and operation kinds; translation units must match. Unsupported or
incompatible pairs apply immediately.

Transitions do not animate layout-affecting or discrete properties. `box-shadow` and scrollbar
pseudo-part values remain static; scrollbar pseudo-part transitions are deferred. Keyframes are
also deferred. The focused core and NanoVG recording tests are present, but automated Gradle
verification is still pending because the current environment has no configured JDK (`java` and
`JAVA_HOME` are unavailable).

Estimate scale for unchecked entries:
- `XS`: property provider/type/accessor only; no new layout or renderer path.
- `S`: small parser/style addition plus a localized existing layout or renderer hook.
- `M`: several coordinated changes across property parsing, resolved style, layout/rendering, and tests.
- `L`: new layout/rendering behavior or cross-cutting event/hit-test integration.
- `XL`: subsystem-level work; best implemented as a feature family, not property by property.
- `N/A`: browser CSS feature outside the current GUI model unless a missing subsystem is introduced.

Approximate implementation estimates for unchecked entries:

| Property | Estimate | Main work |
| --- | --- | --- |
| `all` | M | Cascade-wide reset/inherit/revert behavior across registered properties. |
| `animation`, `animation-delay`, `animation-direction`, `animation-duration`, `animation-fill-mode`, `animation-iteration-count`, `animation-name`, `animation-play-state`, `animation-timing-function`, `@keyframes` | XL | Animation timeline, keyframe model, interpolation, invalidation, and render/layout integration. |
| `backface-visibility` | L | Depends on transform/3D scene semantics that are not present. |
| `background` | M | Shorthand parser that expands current background color/image/position/size/origin plus future repeat/clip/attachment. |
| `background-attachment` | M | Requires scroll/viewport semantics for fixed/local behavior. |
| `background-blend-mode` | L | Renderer blend/compositing support. |
| `background-clip` | S | Clip target selection for existing background renderer. |
| `background-repeat` | S | Type already exists; needs provider plus image tiling in renderer. |
| `border-collapse`, `border-spacing`, `caption-side`, `empty-cells`, `table-layout` | XL | Requires table layout model, table boxes, border conflict handling, and renderer support. |
| `border-image`, `border-image-outset`, `border-image-repeat`, `border-image-slice`, `border-image-source`, `border-image-width` | L | Image border slicing, scaling/repeating, and border renderer changes. |
| `box-decoration-break` | M | Inline fragment background/border painting policy. |
| `box-sizing` | M | Sizing calculations must support content-box and border-box consistently in block/flex/inline layout. |
| `break-after`, `break-before`, `break-inside`, `page-break-after`, `page-break-before`, `page-break-inside` | N/A | Fragmentation/paged layout is not currently modeled. |
| `caret-color` | M | Needs caret styling in text input/caret rendering path. |
| `@charset` | XS | Parser-level metadata handling; little runtime effect if source is already decoded. |
| `clear`, `float` | L | Requires float layout and block formatting interaction. |
| `clip` | M | Absolute-position clipping plus renderer and hit-test clipping. |
| `clip-path` | L | Shape/path clipping in renderer and hit-testing. |
| `column-count`, `column-fill`, `column-gap`, `column-rule`, `column-rule-color`, `column-rule-style`, `column-rule-width`, `column-span`, `column-width`, `columns` | XL | Multi-column layout, fragmentation, balancing, and column rule painting. |
| `content`, `counter-increment`, `counter-reset`, `quotes` | L | Generated content, pseudo-elements, and counter state. |
| `cursor` | M | Property parsing plus wiring resolved style to cursor service during hover/focus transitions. |
| `direction`, `unicode-bidi`, `writing-mode` | XL | Bidirectional and vertical text shaping/layout support. |
| `filter` | L | Renderer effects pipeline for blur/color/filter operations. |
| `flex` | S | Shorthand expansion into `flex-grow`, `flex-shrink`, and `flex-basis`. |
| `flex-flow` | XS | Shorthand expansion into `flex-direction` and `flex-wrap`. |
| `font` | M | CSS font shorthand parser with optional style/variant/weight/stretch/size/line-height/family handling. |
| `font-feature-settings`, `font-kerning`, `font-variant`, `font-variant-caps` | L | Font shaping/OpenType feature support through font service and NanoVG/text backend. |
| `font-size-adjust` | M | Font metrics integration and adjusted used font-size calculations. |
| `grid`, `grid-area`, `grid-auto-columns`, `grid-auto-flow`, `grid-auto-rows`, `grid-column`, `grid-column-end`, `grid-column-gap`, `grid-column-start`, `grid-gap`, `grid-row`, `grid-row-end`, `grid-row-gap`, `grid-row-start`, `grid-template`, `grid-template-areas`, `grid-template-columns`, `grid-template-rows` | Supported subset | Grid Level 1 formatting context with typed values, fixed/percentage/auto/fr/minmax/fit-content/repeat tracks, gaps, template areas, explicit placement, row/column auto-flow, dense packing, stretch/start/center/end item alignment, scroll metrics, and demo coverage. Deferred: subgrid, masonry, baseline alignment, negative line indexes, and advanced browser shorthand forms beyond the supported `rows / columns` grid-template/grid form. |
| `hanging-punctuation` | L | Text layout punctuation positioning support. |
| `hyphens` | L | Language-aware hyphenation and line breaking. |
| `@import` | M | Stylesheet loading, URL/resource resolution, cycle/error handling, and cascade ordering. |
| `isolation`, `mix-blend-mode` | L | Stacking context and compositing support. |
| `letter-spacing`, `word-spacing` | M | Text measurement and renderer glyph positioning changes. |
| `list-style`, `list-style-image`, `list-style-position`, `list-style-type` | L | List item display model, marker layout, and marker rendering. |
| `@media` | L | Media query evaluation, environment model, and dynamic stylesheet invalidation. |
| `object-fit`, `object-position` | M | Replaced element/image sizing and drawing behavior. |
| `order` | M | Flex item ordering plus layout invalidation and traversal implications. |
| `outline`, `outline-color`, `outline-offset`, `outline-style`, `outline-width` | M | Outline property parsing and paint path outside border box. |
| `overflow`, `overflow-x`, `overflow-y` | Supported | Supports `visible`, `hidden`, `auto`, and `scroll` for block/flex scroll containers, including scroll input, clipping, layout metrics, and hit-testing. |
| `perspective`, `perspective-origin`, `transform-style` | XL | 3D transform matrices, stacking contexts, renderer transforms, and hit-testing. |
| `transform`, `transform-origin` | Supported subset | Static 2D translate/scale/rotate operations and visual composition; 3D and `transform-style` are unsupported. |
| `position: fixed` | L | Viewport-relative containing block, scroll behavior, stacking, and event coordinate handling. |
| `resize` | L | User interaction, constraints, layout invalidation, and cursor behavior. |
| `scroll-behavior` | M | Scroll containers first, then animated scroll behavior. |
| `text-align-last` | M | Inline layout line-final alignment logic. |
| `text-decoration`, `text-decoration-color`, `text-decoration-line`, `text-decoration-style` | M | Text decoration model plus underline/overline/strike rendering. |
| `text-indent` | M | First-line inline layout offset. |
| `text-justify` | L | Text justification algorithms and spacing distribution. |
| `text-overflow` | M | Overflow clipping plus ellipsis measurement/rendering. |
| `text-shadow` | S | Similar to `box-shadow`, localized to text renderer. |
| `text-transform` | S | Text preprocessing before measurement/rendering; locale-sensitive cases may raise this to M. |
| `transition`, `transition-delay`, `transition-duration`, `transition-property`, `transition-timing-function` | Supported subset | Paint-only targets, bounded timing functions, deterministic transition tracks, and presentation overlays; layout/discrete/incompatible values remain immediate. |
| `user-select` | M | Selection model and input behavior integration. |
| `vertical-align` | M | Inline formatting baseline/alignment behavior; type classes partly exist. |
| `visibility` | M | Paint suppression while preserving layout, plus event/hit-test decisions. |

Scrollbar pseudo-elements:

- Supported selector names: `::-webkit-scrollbar`, `::-webkit-scrollbar-thumb`, `::-webkit-scrollbar-track`, `::-webkit-scrollbar-track-piece`, `::-webkit-scrollbar-button`, `::-webkit-scrollbar-corner`, and `::-webkit-scrollbar-resizer`.
- Legacy alias: `::scrollbar` is accepted as an alias for `::-webkit-scrollbar`.
- Painted parts: `::-webkit-scrollbar-track`, `::-webkit-scrollbar-thumb`, and `::-webkit-scrollbar-corner`.
- Sizing part: `::-webkit-scrollbar` supports `width` and `height` for non-overlay gutter thickness.
- Parsed and stored, but not currently painted: `::-webkit-scrollbar-track-piece`, `::-webkit-scrollbar-button`, and `::-webkit-scrollbar-resizer`.
- Supported painted-part properties: `background-color`, `border-color`, `border-width`, `border-radius`, and `opacity`.
- Unsupported scrollbar states/selectors: pseudo-classes such as `:hover`, `:active`, `:horizontal`, and `:vertical`.

At-rules with dedicated parser support:
-  [x] `@font-face`
   - [x] `font-family`
   - [x] `src`
   - [x] `font-stretch`
   - [x] `font-style`
   - [x] `font-weight`

Checklist of CSS properties:
-  [x] `align-content`
-  [x] `align-items`
-  [x] `align-self`
-  [ ] `all`
-  [ ] `animation`
-  [ ] `animation-delay`
-  [ ] `animation-direction`
-  [ ] `animation-duration`
-  [ ] `animation-fill-mode`
-  [ ] `animation-iteration-count`
-  [ ] `animation-name`
-  [ ] `animation-play-state`
-  [ ] `animation-timing-function`
-  [ ] `backface-visibility`
-  [ ] `background`
-  [ ] `background-attachment`
-  [ ] `background-blend-mode`
-  [ ] `background-clip`
-  [x] `background-color`
-  [x] `background-image`
-  [x] `background-origin`
-  [x] `background-position`
-  [x] `background-position-x`
-  [x] `background-position-y`
-  [ ] `background-repeat`
-  [x] `background-size`
-  [x] `border`
-  [x] `border-bottom`
-  [x] `border-bottom-color`
-  [x] `border-bottom-left-radius`
-  [x] `border-bottom-right-radius`
-  [x] `border-bottom-style`
-  [x] `border-bottom-width`
-  [ ] `border-collapse`
-  [x] `border-color`
-  [ ] `border-image`
-  [ ] `border-image-outset`
-  [ ] `border-image-repeat`
-  [ ] `border-image-slice`
-  [ ] `border-image-source`
-  [ ] `border-image-width`
-  [x] `border-left`
-  [x] `border-left-color`
-  [x] `border-left-style`
-  [x] `border-left-width`
-  [x] `border-radius`
-  [x] `border-right`
-  [x] `border-right-color`
-  [x] `border-right-style`
-  [x] `border-right-width`
-  [ ] `border-spacing`
-  [x] `border-style`
-  [x] `border-top`
-  [x] `border-top-color`
-  [x] `border-top-left-radius`
-  [x] `border-top-right-radius`
-  [x] `border-top-style`
-  [x] `border-top-width`
-  [x] `border-width`
-  [x] `bottom`
-  [ ] `box-decoration-break`
-  [x] `box-shadow`
-  [ ] `box-sizing`
-  [ ] `break-after`
-  [ ] `break-before`
-  [ ] `break-inside`
-  [ ] `caption-side`
-  [ ] `caret-color`
-  [ ] `@charset`
-  [ ] `clear`
-  [ ] `clip`
-  [ ] `clip-path`
-  [x] `color`
-  [ ] `column-count`
-  [ ] `column-fill`
-  [x] `column-gap`
-  [ ] `column-rule`
-  [ ] `column-rule-color`
-  [ ] `column-rule-style`
-  [ ] `column-rule-width`
-  [ ] `column-span`
-  [ ] `column-width`
-  [ ] `columns`
-  [ ] `content`
-  [ ] `counter-increment`
-  [ ] `counter-reset`
-  [ ] `cursor`
-  [ ] `direction`
-  [x] `display`
   - [x] `flex`
   - [x] `none`
   - [x] `block`
   - [x] `inline`
   - [x] `inline-block`
-  [ ] `empty-cells`
-  [ ] `filter`
-  [ ] `flex`
-  [x] `flex-basis`
-  [x] `flex-direction`
-  [ ] `flex-flow`
-  [x] `flex-grow`
-  [x] `flex-shrink`
-  [x] `flex-wrap`
-  [ ] `float`
-  [ ] `font`
-  [x] `font-family`
-  [ ] `font-feature-settings`
-  [ ] `font-kerning`
-  [x] `font-size`
-  [ ] `font-size-adjust`
-  [x] `font-stretch`
-  [x] `font-style`
-  [ ] `font-variant`
-  [ ] `font-variant-caps`
-  [x] `font-weight`
-  [x] `grid`
-  [x] `grid-area`
-  [x] `grid-auto-columns`
-  [x] `grid-auto-flow`
-  [x] `grid-auto-rows`
-  [x] `grid-column`
-  [x] `grid-column-end`
-  [x] `grid-column-gap`
-  [x] `grid-column-start`
-  [x] `grid-gap`
-  [x] `grid-row`
-  [x] `grid-row-end`
-  [x] `grid-row-gap`
-  [x] `grid-row-start`
-  [x] `grid-template`
-  [x] `grid-template-areas`
-  [x] `grid-template-columns`
-  [x] `grid-template-rows`
-  [ ] `hanging-punctuation`
-  [x] `height`
-  [ ] `hyphens`
-  [ ] `@import`
-  [ ] `isolation`
-  [x] `justify-content`
-  [x] `justify-items`
-  [x] `justify-self`
-  [ ] `@keyframes`
-  [x] `left`
-  [ ] `letter-spacing`
-  [x] `line-height`
-  [ ] `list-style`
-  [ ] `list-style-image`
-  [ ] `list-style-position`
-  [ ] `list-style-type`
-  [x] `margin`
-  [x] `margin-bottom`
-  [x] `margin-left`
-  [x] `margin-right`
-  [x] `margin-top`
-  [x] `max-height`
-  [x] `max-width`
-  [ ] `@media`
-  [x] `min-height`
-  [x] `min-width`
-  [ ] `mix-blend-mode`
-  [ ] `object-fit`
-  [ ] `object-position`
-  [x] `opacity`
-  [ ] `order`
-  [ ] `outline`
-  [ ] `outline-color`
-  [ ] `outline-offset`
-  [ ] `outline-style`
-  [ ] `outline-width`
-  [x] `overflow`
-  [x] `overflow-x`
-  [x] `overflow-y`
-  [x] `place-content`
-  [x] `place-items`
-  [x] `place-self`
-  [x] `overflow-wrap`
-  [x] `padding`
-  [x] `padding-bottom`
-  [x] `padding-left`
-  [x] `padding-right`
-  [x] `padding-top`
-  [ ] `page-break-after`
-  [ ] `page-break-before`
-  [ ] `page-break-inside`
-  [ ] `perspective`
-  [ ] `perspective-origin`
-  [x] `pointer-events`
-  [x] `position`
   - [x] `absolute`
   - [x] `relative`
   - [x] `static`
   - [ ] `fixed`
-  [ ] `quotes`
-  [ ] `resize`
-  [x] `right`
-  [x] `row-gap`
-  [ ] `scroll-behavior`
-  [x] `tab-size`
-  [ ] `table-layout`
-  [x] `text-align`
-  [ ] `text-align-last`
-  [ ] `text-decoration`
-  [ ] `text-decoration-color`
-  [ ] `text-decoration-line`
-  [ ] `text-decoration-style`
-  [ ] `text-indent`
-  [ ] `text-justify`
-  [ ] `text-overflow`
-  [ ] `text-shadow`
-  [ ] `text-transform`
-  [x] `top`
-  [x] `transform`
-  [x] `transform-origin`
-  [ ] `transform-style`
-  [x] `transition`
-  [x] `transition-delay`
-  [x] `transition-duration`
-  [x] `transition-property`
-  [x] `transition-timing-function`
-  [ ] `unicode-bidi`
-  [ ] `user-select`
-  [ ] `vertical-align`
-  [ ] `visibility`
-  [x] `white-space`
-  [x] `width`
-  [x] `word-break`
-  [ ] `word-spacing`
-  [x] `word-wrap`
-  [ ] `writing-mode`
-  [x] `z-index`
