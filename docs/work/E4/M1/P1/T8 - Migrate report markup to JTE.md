# T8: Migrate Report Markup To JTE

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Scope

Replace hand-built report HTML with precompiled JTE templates while preserving all accepted report data, behavior, accessibility, responsive layout, and offline guarantees.

## Dependencies

- Depends on: T7 complete.
- Enables: None.
- Parallelizable with: None.

## Required Changes

- [x] Add a pinned JTE plugin/runtime dependency only to `spinygui.benchmark` and precompile HTML templates during the existing Java build.
- [x] Introduce a typed public report view model that keeps JSON parsing, metric calculations, formatting, chart widths, hotspot selection, and frame-budget math in Java.
- [x] Move document markup and inline CSS into a readable main JTE template.
- [x] Extract repeated chart rows, metric help, and other useful repeated markup into small JTE component templates without speculative abstraction.
- [x] Render through JTE HTML content mode with automatic escaping; do not retain a parallel manual HTML builder or homemade escaping path.
- [x] Keep `benchmarkReport`, report paths, tabs, tooltips, expanded metrics, responsive behavior, and final self-contained HTML unchanged from the user's perspective.
- [x] Adapt focused tests to exercise precompiled template rendering and prove escaping, offline output, accessibility associations, chart/budget correctness, and complete current-run data.
- [x] Update benchmark documentation only where JTE build behavior matters to contributors.

## Acceptance Checks

- [x] `:spinygui.benchmark:benchmarkReport` renders through precompiled JTE templates and produces the accepted offline report.
- [x] No report HTML document, CSS stylesheet, tab structure, chart row, or tooltip markup is assembled in Java string concatenation.
- [x] Benchmark module tests, full project tests, dry-run isolation, explicit template compilation, and whitespace checks pass.
- [x] Final review finds no output regression, unsafe unescaped value, template/runtime packaging issue, or unnecessary dependency leakage.

## Constraints

- Do not add JavaScript, browser automation, Node.js, external assets, or network dependencies.
- Keep JTE dependencies isolated from published production modules.
- Do not modify benchmark calculations or production code.
- Preserve T1-T7 and unrelated worktree changes.
- Do not commit.
- Report files changed, tests run/not run, model/fallback status, and risks.
