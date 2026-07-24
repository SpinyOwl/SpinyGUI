# T12: Add Historical Trend Charts

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Scope

Visualize performance trends across complete archived runs with self-contained, accessible line charts in the History section.

## Dependencies

- Depends on: T10 complete.
- Enables: None.
- Parallelizable with: None.

## Required Changes

- [x] Build typed CPU-latency trend series per operation and GPU-p99 trend series per complete rendering-scene identity from chronologically sorted runs.
- [x] Calculate padded chart domains, large-chart coordinates, readable timeline labels, and previous-run changes in Java rather than JTE.
- [x] Split series into line segments when matching data is absent so charts show gaps rather than connecting across missing runs.
- [x] Add a reusable JTE inline-SVG line-chart component with axes, grid, line segments, points, aligned first/latest timeline labels, units, and an empty/single-run state.
- [x] Make each SVG chart accessible with a title/description and keyboard-focusable points whose labels include run identifier, value, unit, and signed change.
- [x] Render one large trend viewport controlled by a vertical, keyboard-accessible CSS-only metric selector for CPU and rendering series.
- [x] Keep metric controls and the selected chart responsive by stacking them at narrow widths, without JavaScript or external resources.
- [x] Preserve raw chronological tables as the precise data view.
- [x] Add focused tests for chronological point order, coordinate bounds, constant-value domains, signed changes, duplicate-fragment scene identity, missing boundary/intermediate data, line gaps, selector behavior, escaped labels, SVG accessibility, offline output, and responsive chart CSS.
- [x] Update benchmark documentation with trend-chart semantics.

## Acceptance Checks

- [x] A report with multiple archived pairs exposes every CPU operation and distinct rendering scene through the vertical selector and displays one large selected chart.
- [x] Every matching run contributes one correctly positioned point, while missing runs create visible line gaps and retain global timeline alignment.
- [x] Report generation, module/full tests, JTE/package checks, archive retention, and whitespace checks pass.
- [x] Final review finds no trend identity, math, scaling, chronological, selector, accessibility, responsive, or offline defect.

## Constraints

- Keep the report self-contained and JavaScript-free.
- Do not add charting libraries or external resources.
- Do not modify benchmark calculations or production code.
- Preserve T1-T11 and unrelated worktree changes.
- Do not commit.
- Report files changed, tests run/not run, model/fallback status, and risks.
