# T5: Generate Local HTML Report

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Scope

Generate one self-contained local HTML visualization from the accepted CPU JMH and NanoVG rendering JSON reports. The output must work directly from disk without network access or uploaded data.

## Dependencies

- Depends on: T4 complete.
- Enables: None.
- Parallelizable with: None.

## Required Changes

- [x] Add a Java report generator that parses both existing JSON formats and writes `build/reports/jmh/index.html`.
- [x] Render CPU latency and normalized allocation charts, rendering median/p95/p99 charts with 60 Hz and 120 Hz budget references, environment metadata, scene complexity, and concise hotspot summaries.
- [x] Keep all CSS and graphics inline; emit no external scripts, stylesheets, fonts, images, or network references.
- [x] Add a `benchmarkReport` Gradle task that generates fresh missing inputs through the existing benchmark tasks and then writes the HTML report.
- [x] Add focused tests for JSON parsing, expected report content, HTML escaping, and absence of external resources.
- [x] Document the command and output path in `spinygui.benchmark/README.md`.

## Acceptance Checks

- [x] `:spinygui.benchmark:benchmarkReport` produces a non-empty HTML file from the current reports.
- [x] The generated report contains all nine CPU operations and both rendering scenes.
- [x] The report can be opened from disk and contains no `http://`, `https://`, protocol-relative, or external resource references.
- [x] Benchmark module tests, full project tests, and `git diff --check` pass.

## Constraints

- Do not add a browser, Node.js, Python, plotting service, or frontend dependency.
- Do not change production text or renderer behavior.
- Preserve T1-T4 and unrelated worktree changes.
- Do not commit.
- Report files changed, behavior changed, tests run/not run, model/fallback status, and risks.
