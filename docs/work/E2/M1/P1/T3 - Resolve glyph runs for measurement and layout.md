# T3: Resolve Glyph Runs for Measurement and Layout

Parent plan: `docs/features/font-family-resolution-plan.md`

## Scope

Implement only T3 after accepted T1/T2 work. Introduce a core resolved-text/run contract for ordered font chains and use it for measurement, line wrapping, caret geometry, and input/textarea selection geometry. Do not change NanoVG drawing to paint individual runs; that is T4.

## Dependencies

- Depends on: T1, T2 complete.
- Enables: T4, T5.
- Parallelizable with: None.

## Required Changes

- [x] Add immutable resolved glyph/run data retaining source UTF-16 ranges, selected font, and visible marker behavior for unsupported code points.
- [x] Resolve per code point from the ordered family chain; kerning applies only within the same face/run.
- [x] Update `FontServiceImpl` measurement and caret APIs, plus all callers, to use the family chain rather than one selected font where required for compatible metrics.
- [x] Retain source indices through wrapping and ensure surrogate pairs remain atomic.
- [x] Keep a compatibility overload only if required by current public callers; do not retain the old global hardcoded fallback path as the behavior source.

## Acceptance Checks

- [x] Mixed `R\u00f8gue \u96ea Seed` uses Roboto then Noto CJK according to the CSS chain in measurement.
- [x] Caret coordinates, selection, replacement, backspace, and delete remain correct at U+96EA and supplementary-plane boundaries.
- [x] A code point absent from every configured family is represented by a non-zero-width U+FFFD marker in core metrics.
- [x] Existing Latin, icon, and emoji metric tests pass.
- [x] Core focused tests and `git diff --check` pass.

## Constraints

- Preserve accepted T1/T2 changes and unrelated untracked `.worktrees/` and plan documents.
- Do not implement NanoVG per-run drawing; T4 owns it.
- Do not commit.
- Report files changed, behavior changed, tests run/not run, model/fallback status, and risks.
