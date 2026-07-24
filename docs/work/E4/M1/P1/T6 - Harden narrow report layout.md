# T6: Harden Narrow Report Layout

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Scope

Prevent long benchmark labels and values from overflowing chart rows on very narrow local browser viewports.

## Dependencies

- Depends on: T5 complete.
- Enables: None.
- Parallelizable with: None.

## Required Changes

- [x] Allow the mobile chart label column to shrink below its min-content width.
- [x] Permit long operation labels to wrap without affecting desktop layout.
- [x] Add a focused generated-CSS assertion for the narrow-layout contract.

## Acceptance Checks

- [x] Report tests, `benchmarkReport`, and `git diff --check` pass.
- [x] The generated report retains all T5 offline and visualization guarantees.

## Constraints

- Do not change benchmark calculations, production code, or report data semantics.
- Preserve T1-T5 and unrelated worktree changes.
- Do not commit.
- Report files changed, tests run/not run, model/fallback status, and risks.
