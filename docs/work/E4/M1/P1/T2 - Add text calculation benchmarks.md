# T2: Add Text Calculation Benchmarks

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Scope

Add steady-state JMH benchmarks for text measurement, caret lookup, and a text-dense layout path. Wire the CPU benchmark task to produce a local JSON report with allocation data. Do not add NanoVG or GLFW setup.

## Dependencies

- Depends on: T1 complete.
- Enables: T4.
- Parallelizable with: T3 after shared fixtures are stable.

## Required Changes

- [x] Benchmark `FontServiceImpl.measureText` for short Latin, wrapped paragraph, mixed CJK, supplementary Unicode, missing glyphs, and a long single-font string.
- [x] Benchmark caret lookup near the beginning and end of a long string.
- [x] Benchmark a repeatable text-dense frame layout or the narrowest representative inline-layout boundary available without timing fixture construction.
- [x] Warm font data in trial setup and consume or validate results so measured work cannot be eliminated.
- [x] Configure `jmhCpu` to run only CPU benchmarks with warmup, measurement, forks, the GC profiler, and JSON output under `build/reports/jmh/cpu`.
- [x] Document the CPU command and report semantics in the benchmark module.

## Acceptance Checks

- [x] `:spinygui.benchmark:jmhCpu` executes non-empty benchmarks and emits JSON.
- [x] The report includes latency and normalized allocation metrics.
- [x] Long single-font input is large enough to expose nonlinear run construction behavior.
- [x] Benchmark module tests and `git diff --check` pass.

## Constraints

- Do not optimize production text code in this node.
- Exclude corpus construction, parser setup, and service construction from measured methods.
- Preserve T1 and unrelated worktree changes.
- Do not commit.
- Report files changed, behavior changed, tests run/not run, model/fallback status, and risks.
