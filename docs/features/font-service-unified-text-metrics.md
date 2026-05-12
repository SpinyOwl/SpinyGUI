# FontService Unified Text Metrics Plan

## Summary
The ticket is only partially done. `FontService` now exposes vertical font metrics, but callers still need separate calls for text width and font metrics. Implement a unified text measurement API that returns horizontal and vertical metrics in one result, then update layout callers to use that single result.

## Key Changes
- Add a font-system metric model under `core.system.font`:
  - `FontMetrics`: `ascent`, `descent`, `lineGap`, `lineHeight`, `baseline`.
  - `TextLineMetrics`: ordered line text, `startIndex`, `endIndex`, `charCount`, `width`, `height`, `baseline`, and `FontMetrics`.
  - `TextMetrics`: ordered `List<TextLineMetrics>`, total `width`, total `height`, `lineHeight`, and top-level `FontMetrics`.
- Add a single primary API to `FontService`:
  - `TextMetrics measureText(String text, Font font, float fontSize, float lineHeight)`.
  - Keep wrapped measurement as `measureText(String text, float offsetX, Font font, float fontSize, float lineHeight, float maxWidth, boolean wordWrap)`.
  - Keep `getTextLineMetrics()` and `getFontMetrics()` as active compatibility wrappers over the new API while the project is in active development.
- Update `FontServiceImpl`:
  - Compute horizontal advance and vertical metrics in one STB-backed call path.
  - Use one scale strategy consistently.
  - Fix the current `maxWidth < 0.1` branch so it returns empty metrics immediately.
  - Preserve line order with `List`, not `ImmutableSet`.
- Update layout integration:
  - Change `TextMeasurer` to return unified line metrics from one method instead of separate `measure()` and `metrics()` calls.
  - Update `InlineFormattingContext` to use the returned `width`, `lineHeight`, `baseline`, `ascent`, and `descent`.
  - Remove or replace `core.layout.FontMetrics`; layout should not own font-system metric types.
- Rendering compatibility:
  - Keep NanoVG rendering based on `InlineFragment`.
  - Keep fragment `baseline`, `font`, `fontSize`, and `color`; add ascent/descent/lineHeight only if needed by renderer or hit testing.

## Test Plan
- Add `FontServiceImpl` tests for:
  - single-line measurement returns width and vertical metrics from one call;
  - compatibility wrappers match the unified API;
  - zero/near-zero max width returns empty metrics;
  - wrapped metrics preserve line order;
  - whitespace text still reports correct advance.
- Update inline layout tests:
  - fake `TextMeasurer` returns unified metrics;
  - assert layout uses one measurement result per text unit;
  - keep existing wrapping, alignment, line-height, and fragment-union tests.
- Run:
  - `./gradlew compileJava`
  - `./gradlew compileTestJava`
  - targeted font/layout tests once JaCoCo dependency resolution is fixed.

## Assumptions
- This does not implement shaping, bidi, kerning, glyph atlas data, font fallback, or renderer-specific NanoVG APIs.
- "Useful for rendering" means baseline and vertical metrics are available with text width from one service call.
- Existing public methods remain during migration, but new layout code must use the unified API.
