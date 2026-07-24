# T10: Render Anchored Performance History

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Scope

Replace report tabs with one accessible page whose navigation buttons link to anchored sections, and visualize current results together with archived performance changes.

## Dependencies

- Depends on: T9 complete.
- Enables: None.
- Parallelizable with: None.

## Required Changes

- [x] Remove radio controls, tab panels, `:has()` tab CSS, and tab-specific documentation/tests.
- [x] Add responsive sticky navigation links for Overview, CPU, Rendering, History, and Methodology with visible keyboard focus and anchor offsets.
- [x] Render every section in normal document flow with stable unique heading/section IDs and a useful skip-to-content path.
- [x] Show the current run identifier and timestamp prominently.
- [x] Add CPU history by operation with current value and percentage change from the immediately previous complete run.
- [x] Add rendering history by scene with current median/p95/p99 and percentage changes from the immediately previous complete run.
- [x] Add chronological history tables or compact charts that expose all archived complete runs, not only the newest pair.
- [x] Retain accessible hover/focus metric tooltips, logarithmic current CPU charts, fixed frame-budget markers, environment data, narrow layout behavior, offline output, and JTE auto-escaping.
- [x] Add focused tests for navigation anchors, absence of tab controls/CSS, complete history rendering, change direction/sign formatting, tooltips, offline guarantees, and responsive CSS.
- [x] Update README instructions and output paths.

## Acceptance Checks

- [x] Generated HTML is one continuous page with working anchor navigation and no tab implementation remnants.
- [x] At least two archived complete runs produce visible CPU and rendering comparisons and chronological history.
- [x] Report tests, full project tests, fresh report generation, dry-run isolation, clean-retention check, and `git diff --check` pass.
- [x] Final review finds no history pairing, comparison math, accessibility, responsive, offline, or packaging defects.

## Constraints

- Keep the report self-contained and JavaScript-free.
- Do not add external resources or runtime local-file fetching.
- Preserve T1-T9 and unrelated worktree changes.
- Do not commit.
- Report files changed, tests run/not run, model/fallback status, and risks.
