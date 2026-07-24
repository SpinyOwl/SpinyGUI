# T13: Contain Trend Explorer Width

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Scope

Prevent the large selected trend chart from causing page-level horizontal overflow at tablet and narrow-desktop widths.

## Dependencies

- Depends on: T12 complete.
- Enables: None.
- Parallelizable with: None.

## Required Changes

- [x] Contain the trend panel at every width and provide local horizontal scrolling whenever the readable chart minimum width cannot fit.
- [x] Preserve the two-column selector/chart layout where it fits and the stacked mobile layout below 700px.
- [x] Add focused CSS/output assertions for the 701-1,100px containment contract.

## Acceptance Checks

- [x] Generated report has no page-level overflow caused by the trend chart across desktop, tablet, or mobile layouts.
- [x] Report tests, JTE compilation, generated output, and whitespace checks pass.

## Constraints

- Do not change trend data, chart math, archive behavior, or benchmark calculations.
- Preserve T1-T12 and unrelated worktree changes.
- Do not commit.
- Report files changed, tests run/not run, model/fallback status, and risks.
