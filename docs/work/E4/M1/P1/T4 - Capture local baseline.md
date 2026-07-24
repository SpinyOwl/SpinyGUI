# T4: Capture Local Baseline

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Scope

Run the accepted CPU and rendering harnesses on the current machine and record an informational baseline with evidence-backed observations. Do not optimize production code or introduce automated performance gates.

## Dependencies

- Depends on: T2, T3 complete.
- Enables: None.
- Parallelizable with: None.

## Required Changes

- [x] Run `:spinygui.benchmark:jmhCpu` and retain the generated local JSON report under the module build directory.
- [x] Run `:spinygui.benchmark:jmhRendering` and retain the generated local JSON report under the module build directory.
- [x] Add a concise benchmark baseline document containing the date, Java/OS/GPU environment, benchmark profiles, CPU latency and allocation results, rendering percentiles and budget use, and report paths.
- [x] Identify measured hotspots and recommendations without changing production behavior.
- [x] State clearly that the baseline is machine-specific, informational, and must be compared on equivalent hardware and software.

## Acceptance Checks

- [x] Both reports are non-empty and contain every expected benchmark or scene.
- [x] CPU results include normalized bytes per operation.
- [x] Rendering results include passing pixel validation and both scene sizes.
- [x] Benchmark module tests and `git diff --check` pass.

## Constraints

- Do not add benchmark tasks to `test`, `check`, or CI.
- Do not modify production text or renderer code in response to the baseline.
- Preserve T1-T3 and unrelated worktree changes.
- Do not commit.
- Report files changed, behavior changed, tests run/not run, model/fallback status, and risks.
