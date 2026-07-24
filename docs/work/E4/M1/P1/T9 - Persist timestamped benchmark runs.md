# T9: Persist Timestamped Benchmark Runs

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Scope

Archive every benchmark invocation outside Gradle's build directory with one shared sortable timestamp identifier for its CPU and rendering JSON files. Prepare typed multi-run history data for the HTML report without changing benchmark calculations.

## Dependencies

- Depends on: T8 complete.
- Enables: T10.
- Parallelizable with: None.

## Required Changes

- [x] Create machine-local reports under `spinygui.benchmark/reports/` and ignore generated contents in Git while allowing Gradle to create the directory.
- [x] Generate one execution-time local datetime identifier per combined benchmark invocation; do not freeze it through Gradle configuration-cache reuse.
- [x] Write CPU and rendering files as `text-calculation-<datetime>.json` and `nanovg-text-<datetime>.json` using the same identifier when `benchmarkReport` runs both.
- [x] Keep direct `jmhCpu` and `jmhRendering` tasks functional with timestamped outputs.
- [x] Change report generation to scan all archived JSON, pair complete runs by identifier, sort them chronologically, reject or clearly skip incomplete/malformed pairs, and select the newest complete run as current.
- [x] Add typed history data for per-operation CPU latency/allocation changes and per-scene rendering latency/budget changes without moving metric math into JTE.
- [x] Generate the self-contained HTML as `spinygui.benchmark/reports/index.html` and embed parsed history so the page requires no runtime fetch or server.
- [x] Add tests for identifier extraction, pairing, chronological ordering, latest selection, previous-run percentage change, incomplete pairs, and HTML escaping.
- [x] Document archive retention, naming, clean behavior, and regeneration semantics.

## Acceptance Checks

- [x] Two consecutive report runs preserve both JSON pairs with different sortable identifiers and regenerate one history-aware index.
- [x] `:spinygui.benchmark:clean` does not remove archived JSON or the HTML report.
- [x] Configuration-cache reuse still creates a fresh run identifier.
- [x] Benchmark module tests, full project tests, dry-run isolation, JTE compilation, packaging, and whitespace checks pass.

## Constraints

- Do not make the browser fetch local JSON; embed history at generation time to preserve direct `file://` use.
- Do not commit machine-specific reports.
- Do not modify production code or benchmark calculations.
- Preserve T1-T8 and unrelated worktree changes.
- Do not commit.
- Report files changed, tests run/not run, model/fallback status, and risks.
