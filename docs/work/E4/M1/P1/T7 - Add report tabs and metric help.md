# T7: Add Report Tabs And Metric Help

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Scope

Organize the self-contained report into accessible in-page tabs and explain every measurement through hover and keyboard-focus help. Expose useful details already present in the JSON without adding network or frontend dependencies.

## Dependencies

- Depends on: T6 complete.
- Enables: None.
- Parallelizable with: None.

## Required Changes

- [x] Add script-free Overview, CPU, Rendering, and Methodology tabs with clear selected state and responsive layout.
- [x] Add accessible inline information icons whose tooltips open on hover and keyboard focus, remain readable at narrow viewports, and explain latency, uncertainty, allocation, percentiles, frame budgets, scene complexity, and validation fields.
- [x] Expand CPU details with JMH score error and normalized allocation rate when present.
- [x] Expand rendering details with CPU and GPU budget percentages, sample counts, scene complexity, and pixel-validation status.
- [x] Keep summary hotspot calculations correct and surface them in the Overview tab.
- [x] Preserve inline-only CSS/graphics and emit no scripts or external resources.
- [x] Add focused tests for tab structure and labels, tooltip accessibility/content, expanded metrics, HTML escaping, offline guarantees, and narrow-layout CSS.
- [x] Update benchmark documentation with the report sections and interaction behavior.

## Acceptance Checks

- [x] `:spinygui.benchmark:benchmarkReport` produces a report containing all four usable tabs and expanded current-run data.
- [x] Every measurement family has discoverable hover/focus help and keyboard-readable markup.
- [x] Report tests, full project tests, dry-run isolation, and `git diff --check` pass.
- [x] Final review finds no correctness, accessibility, offline, or responsive-layout regressions.

## Constraints

- Do not add JavaScript, browser automation, Node.js, external assets, or network dependencies.
- Do not modify benchmark calculations or production code.
- Preserve T1-T6 and unrelated worktree changes.
- Do not commit.
- Report files changed, tests run/not run, model/fallback status, and risks.
