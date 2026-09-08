# P1 - Repair browser-native view parity

## Document Context
- Status: In Progress
- Dependencies: None
- Parent: None — standalone follow-up to completed comparison tooling
- Children:
  - [T1 - Normalize paired capture defaults](<P1/T1 - Normalize paired capture defaults.md>)
  - [T2 - Correct client and scroll geometry semantics](<P1/T2 - Correct client and scroll geometry semantics.md>)
  - [T3 - Clamp programmatic scrolling before presentation](<P1/T3 - Clamp programmatic scrolling before presentation.md>)
  - [T4 - Apply normal-flow block margins](<P1/T4 - Apply normal-flow block margins.md>)
  - [T5 - Accept numeric font weights](<P1/T5 - Accept numeric font weights.md>)
  - [T6 - Resolve CSS line height and preserve fractional metrics](<P1/T6 - Resolve CSS line height and preserve fractional metrics.md>)
  - [T7 - Measure auto-sized flex items intrinsically](<P1/T7 - Measure auto-sized flex items intrinsically.md>)
  - [T8 - Correct intrinsic form-control dimensions](<P1/T8 - Correct intrinsic form-control dimensions.md>)
  - [T9 - Correct flexible grid track allocation](<P1/T9 - Correct flexible grid track allocation.md>)
  - [T10 - Reflow grid text using final track widths](<P1/T10 - Reflow grid text using final track widths.md>)
  - [T11 - Support transform-origin keywords](<P1/T11 - Support transform-origin keywords.md>)
  - [T12 - Paint per-side borders and rounded outlines](<P1/T12 - Paint per-side borders and rounded outlines.md>)
  - [T13 - Verify the full suite and account for residual differences](<P1/T13 - Verify the full suite and account for residual differences.md>)
  - [T14 - Exclude trailing padding from visible overflow extents](<P1/T14 - Exclude trailing padding from visible overflow extents.md>)
- Related: [Original analysis](<T1 - Browser visual comparison analysis.md>); [Tooling execution record](<T1 - Browser visual comparison.md>); [E1 transforms](<E1 - CSS animation support.md>); [E3 Grid](<E3 - CSS Grid support.md>); [E5 text](<E5 - Text performance improvements.md>)
- Next: [T1 - Normalize paired capture defaults](<P1/T1 - Normalize paired capture defaults.md>)

**Depends on:** None. The comparison subproject already exists in commit `f809341f`.

## Goal
Repair concrete browser/native differences across eight demos and seven focused/state cases. Establish equivalent capture conditions, correct shared layout and text behavior, then validate the complete suite without increasing tolerances or hiding failures.

## Context
The analyzed run captured all 15 cases with zero capture errors and 15 mismatches. The analysis separates source-confirmed defects from harness-policy mismatches and residual text rendering differences. The original report is baseline evidence, not verification for these tasks.
Existing E1/E3/E5 plans overlap source areas; link resulting fixes to those plans where relevant without declaring unrelated milestones complete. Preserve unrelated working-tree edits.

## Assumptions and Decisions
- Task execution records below contain the implementation and verification evidence.
- Recommended boundary: explicit shared root, inline-tag and form-control presentation defaults in the comparison harness; preserve generic native Element defaults. Numeric CSS values, declared margins, intrinsic control sizes and supported layout rules remain parity obligations.
- T1 records the exact shared-default policy before changing captures. Public API compatibility breaks or broader HTML/CSS support require a bounded decision before dependent implementation; complete Chromium emulation is not assumed.
- Native client sizes should follow the padding-inclusive contract already documented on Element. Scrolling must be valid before presentation, without a full layout on ordinary scroll updates.
- No golden-image replacement, screenshot masking, case removal, or automatic threshold relaxation.
- Windows paired captures establish local evidence. Linux/macOS execution, dynamic hover/transition behavior, Java demo actions and general browser compatibility certification remain separate work.

## Phase Tasks
Order is recommended; hard prerequisites live in individual task documents.

- [x] [T1 - Normalize paired capture defaults](<P1/T1 - Normalize paired capture defaults.md>)
- [x] [T2 - Correct client and scroll geometry semantics](<P1/T2 - Correct client and scroll geometry semantics.md>)
- [x] [T3 - Clamp programmatic scrolling before presentation](<P1/T3 - Clamp programmatic scrolling before presentation.md>)
- [x] [T4 - Apply normal-flow block margins](<P1/T4 - Apply normal-flow block margins.md>)
- [x] [T5 - Accept numeric font weights](<P1/T5 - Accept numeric font weights.md>)
- [x] [T6 - Resolve CSS line height and preserve fractional metrics](<P1/T6 - Resolve CSS line height and preserve fractional metrics.md>)
- [x] [T7 - Measure auto-sized flex items intrinsically](<P1/T7 - Measure auto-sized flex items intrinsically.md>)
- [x] [T8 - Correct intrinsic form-control dimensions](<P1/T8 - Correct intrinsic form-control dimensions.md>)
- [x] [T9 - Correct flexible grid track allocation](<P1/T9 - Correct flexible grid track allocation.md>)
- [x] [T10 - Reflow grid text using final track widths](<P1/T10 - Reflow grid text using final track widths.md>)
- [x] [T11 - Support transform-origin keywords](<P1/T11 - Support transform-origin keywords.md>)
- [x] [T12 - Paint per-side borders and rounded outlines](<P1/T12 - Paint per-side borders and rounded outlines.md>)
- [ ] [T13 - Verify the full suite and account for residual differences](<P1/T13 - Verify the full suite and account for residual differences.md>)
- [x] [T14 - Exclude trailing padding from visible overflow extents](<P1/T14 - Exclude trailing padding from visible overflow extents.md>)

## Verification Strategy
Keep core geometry/style regressions in core, paint regressions in NanoVG, and orchestration/report regressions in visual-tests. Each repair pairs a focused behavioral regression with targeted fresh captures.
Inspect metric categories and affected regions while other failures remain. Final closure requires all original cases green at the existing 1 px geometry and 16-channel/0.5% pixel thresholds; partial improvements and green unit tests do not substitute for the final native/browser run.
Residual rasterization uncertainty belongs to T13. If it reveals another functional defect, add a concrete follow-up and leave the phase open. Policy changes require a separate explicit decision.

## Phase Implementation State
- Progress: 13/14 task documents completed.
- Active task or next action: T13 — full-suite verification and residual attribution.
- Phase-level blockers or decisions: Remaining typography and scroll extents require attribution in T13; all-green results remain an acceptance target, not a prediction.
