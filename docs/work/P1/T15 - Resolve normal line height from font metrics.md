# T15 - Resolve normal line height from font metrics

## Document Context
- Status: Planned
- Dependencies: T6
- Parent: [P1](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [T13](<T13 - Verify the full suite and account for residual differences.md>); [E5](<../E5 - Text performance improvements.md>)
- Next: T13 rerun

## Purpose
Preserve the semantic distinction between CSS normal line-height and a numeric multiplier. Current normal maps to Configuration.LINE_HEIGHT (1.2), whereas bundled Roboto in Chromium has font-dependent normal line boxes.

## Prerequisites
T6 is complete; retain its explicit pixel/unitless height and inheritance behavior.

## Changes
- [ ] Retain a typed normal value through style resolution; resolve its actual line advance and baseline from the selected font at measurement time.
- [ ] Preserve the existing public numeric setter and explicitly handle its compatibility getter; do not encode normal as an arbitrary multiplier or non-finite number.
- [ ] Apply the resolved metrics consistently to block/inline/flex/grid/control layout and rendering, including font-size/weight changes and cache invalidation.
- [ ] Add direct parsed-style and real-font regressions; recapture all original cases.

## Acceptance Checks
- [ ] Roboto normal at 14/16/18/30 px reproduces browser line-box heights 19/21/24/39 px under the pinned capture environment, or documents a measured platform distinction without claiming equivalence.
- [ ] Main-menu intrinsic height approaches the measured browser 492 px instead of native 456.4 px; declarations and transition panel vertical offsets no longer accumulate normal-line-height error.
- [ ] Explicit 24 px and unitless 1.25 still obey T6, including inheritance and glyph ink exceeding short lines.
- [ ] Existing text/control/cache tests and strict captures pass for corrected vertical geometry; remaining width/rasterization differences remain attributed to T16/T13.

## Verification
Core font/style/inline/control tests, full affected module checks, then compareViews -PvisualCases=all. Browser evidence: t13-captures.log ResidualProbe; final pre-fix run-5596582886669772906.

## Risks
This is a shared semantic font contract change, not a harness reset. Avoid changing comparison CSS to force normal to 1.2 and avoid adjusting screenshot tolerances.

## Execution Record
- Status: Planned
- Last Updated: 2026-09-08
- Implemented Scope: None
- Relevant Files and Symbols: FontPropertyProvider line-height updater; ResolvedStyle.lineHeight; FontServiceImpl.measureFontMetrics; inline/flex/control measurement callers.
- Acceptance Evidence:
  - Font-dependent normal line height: Not Run — Automated — pending.
  - Menu and panel geometry: Failed — Native Host — current run retains accumulated vertical differences.
  - Explicit line height: Not Run — Automated — retain T6 regression coverage during this change.
  - Full regression and capture gates: Not Run — Automated / Native Host — pending.
- Decisions and Deviations: Source-confirmed residual discovered by T13; not merely font rasterization noise.
- Review Outcome: Not Reviewed
- Remaining Work: Implement normal semantics and verify every measurement consumer.
- Resume or Closure: Begin with a real-font regression distinguishing normal from explicit 1.2.
