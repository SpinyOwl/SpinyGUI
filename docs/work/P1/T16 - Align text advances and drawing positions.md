# T16 - Align text advances and drawing positions

## Document Context
- Status: Planned
- Dependencies: T5, T6
- Parent: [P1](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [T13](<T13 - Verify the full suite and account for residual differences.md>); [T15](<T15 - Resolve normal line height from font metrics.md>); [E5](<../E5 - Text performance improvements.md>)
- Next: T13 closure attempt after T15

## Purpose
Remove measured horizontal text geometry differences while keeping rendered glyph positions, wrapping, carets and hit testing consistent.

## Prerequisites
T5/T6 are complete. Final full-capture acceptance also requires T15.

## Changes
- [ ] Characterize raw advance, kerning, shaping and submitted NanoVG glyph positions for representative bundled Roboto regular/bold strings.
- [ ] Correct the production advance/drawing contract together; current measureBaseAdvance unconditionally truncates to tenths then rounds every glyph to whole pixels, even in the otherwise fractional measurement mode.
- [ ] Preserve cache identity, fallback source provenance, caret/hit-test consistency and diagnostic accounting.
- [ ] Cover simple Latin, kerning pairs, fi text, mixed weights, wrapped lines and editable controls. Re-evaluate antialiasing only after text positions match.

## Acceptance Checks
- [ ] Main-menu Start Game bold 16 px width agrees with browser 81.3125 px within the existing geometry tolerance instead of native 83 px; nested button width no longer differs by about 5 px.
- [ ] Text runs, wrap boundaries, caret placement and renderer submission use the same measured advances after cache reuse and font changes.
- [ ] Shared-font text/layout/input/textarea pixel residuals are measured after geometry correction; no blanket attribution of translated or differently sized text to antialiasing.
- [ ] Full affected checks pass and original paired cases are re-run without weaker tolerances or excluded fields.

## Verification
FontServiceImplMeasurementContractTest, FontServiceImplLinearScalingTest, font fallback/cache tests, inline/control/caret tests, NvgTextRendererTest and full module checks. Evidence: run-5596582886669772906 and t13-captures.log browser range/canvas measurements.

## Risks
Changing only core measurement would desynchronize drawing and input. NanoVG's existing fontstash rounding contract has explicit tests; change the contract and backend together. Broader shaping support must be identified explicitly rather than silently substituting fonts.

## Execution Record
- Status: Planned
- Last Updated: 2026-09-08
- Implemented Scope: None
- Relevant Files and Symbols: FontServiceImpl.measureBaseAdvance/measurePairKerning; resolved runs and caret stops; NvgTextRenderer and NanoVG text submission.
- Acceptance Evidence:
  - Post-T15/T18/T19 raster check: Passed for characterization — Native Host — run-16902693949945211376 places Start Game visible pixels at rows 345–356 in both renderers. At RGB-channel threshold 150, native bright ink occupies 346–356 versus browser 345–356; title bright ink spans 29 rows in both at that threshold. Line heights/positions are corrected; edge coverage remains different. No font-size inflation, gamma tweak or threshold relaxation was applied. A separate rasterizer/hinting investigation is still required before claiming matching edge coverage.
  - Horizontal text geometry: Failed — Native Host — source-confirmed rounded advance differences remain.
  - Layout/drawing/input consistency: Not Run — Automated — implementation pending.
  - Residual classification: Passed — Native Host — text screenshot pair has visible width/baseline shifts; current 6.447% text difference is not established as pure antialiasing.
  - Full checks and captures after correction: Not Run — Automated / Native Host — pending.
- Decisions and Deviations: T13 follow-up; retain historical renderer alignment tests as a contract to update deliberately.
- Review Outcome: Not Reviewed
- Remaining Work: Establish and implement a coherent fractional text pipeline, then isolate residual rasterization.
- Resume or Closure: Start with simultaneous raw-font, core-run and renderer-position measurements for the failing short label.
