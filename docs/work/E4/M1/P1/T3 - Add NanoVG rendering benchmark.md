# T3: Add NanoVG Rendering Benchmark

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Scope

Add a main-thread local rendering harness that exercises actual resolved text runs through `NvgRenderer` in a hidden GLFW/OpenGL context. Report CPU submission and synchronized GPU-complete latency. Do not change production rendering behavior.

## Dependencies

- Depends on: T1 complete.
- Enables: T4.
- Parallelizable with: T2 after shared fixtures are stable.

## Required Changes

- [x] Build deterministic pre-laid-out scenes near 100 and 1,000 text fragments with Latin and mixed Roboto/CJK runs.
- [x] Create a hidden 1280x720 GLFW/OpenGL context on the application main thread, disable vsync, initialize `NvgRenderer`, and warm fonts, glyph atlases, JVM code, and GPU work before timing.
- [x] For each measured frame, record renderer CPU submission time and GPU-complete time using `glFinish` without swapping buffers.
- [x] Report median, p95, and p99 latency, 60 Hz and 120 Hz budget percentages, scene counts, Java/OS details, and OpenGL vendor/renderer/version metadata.
- [x] Validate one rendered frame outside timing by reading pixels and rejecting an all-background result.
- [x] Guarantee cleanup of NanoVG, OpenGL capabilities, the hidden window, and GLFW on success and failure.
- [x] Wire `jmhRendering` to execute the harness and write a machine-readable report under `build/reports/jmh/rendering` while remaining outside `test` and `check`.
- [x] Document the rendering command, synchronization semantics, and hardware sensitivity.

## Acceptance Checks

- [x] `:spinygui.benchmark:jmhRendering` renders both scene sizes and emits a non-empty report.
- [x] The report contains CPU submission and GPU-complete median, p95, and p99 values plus environment metadata.
- [x] Pixel validation proves text draw work occurred before measurements.
- [x] Benchmark module tests and `git diff --check` pass.

## Constraints

- Run GLFW initialization and teardown on the Java application main thread rather than a JMH worker.
- Exclude scene construction, text measurement, and renderer initialization from timed samples.
- Preserve T1-T2 and unrelated worktree changes.
- Do not commit.
- Report files changed, behavior changed, tests run/not run, model/fallback status, and risks.
