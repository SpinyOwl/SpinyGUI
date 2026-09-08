# T18 - Align thin native borders to physical pixels

## Purpose
Remove split pixel coverage from fractional axis-aligned thin uniform borders in main-menu.

## Changes
Snap uniform one/two-device-pixel border bounds after translation and DPI conversion. Preserve rotation, scaling and fractional-width rendering. Correct NanoVG's frame pixel ratio to framebuffer/window width.

## Acceptance Checks
- Device-space snapping respects translation and DPI; rotated/scaled contours remain unchanged.
- Main-menu's straight Start Game top border occupies one physical row.

## Risks
This intentionally covers thin uniform borders; arbitrary transforms and unequal side contours keep continuous geometry. Layout and input coordinates are unchanged.

## Execution Record
- Status: In Progress
- Last Updated: 2026-09-08
- Implemented Scope: NvgBorderRenderer device-space alignment and NvgRenderer frame ratio correction.
- Relevant Files and Symbols: NvgBorderRenderer.alignBorder/NativeShapeSink; NvgRenderer.renderFrame; NvgBorderRendererTest.
- Acceptance Evidence: Passed — Automated — NvgBorderRendererTest including DPI/translation/rotation/scale cases. Fresh main-menu capture recorded separately; full visual mismatch remains expected before text fixes.
- Decisions and Deviations: DPI ratio inversion was found while making physical-pixel alignment correct and is fixed together. No demo CSS changes.
- Review Outcome: Not Reviewed
- Remaining Work: Aggregate backend checks with the following text fixes; acceptance review.
- Resume or Closure: Continue with T15 normal line height and text rasterization checks.
