# T1: Add Benchmark Infrastructure

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Scope

Create the standalone benchmark module and deterministic shared text workloads. Do not add calculation or rendering benchmark implementations yet.

## Dependencies

- Depends on: None.
- Enables: T2, T3.
- Parallelizable with: None.

## Required Changes

- [x] Include a `spinygui.benchmark` subproject using the existing Java 25 conventions.
- [x] Add JMH dependencies and generation support without wiring benchmarks into `test` or `check`.
- [x] Add deterministic Latin, wrapped paragraph, mixed CJK, supplementary Unicode, and missing-glyph workloads.
- [x] Provide report directories and stable Gradle task boundaries for later CPU and rendering harnesses.

## Acceptance Checks

- [x] The benchmark module compiles independently.
- [x] Existing project tests are not configured to execute benchmarks.
- [x] `git diff --check` passes for this node.

## Constraints

- Follow `AGENTS_CODE_STYLE.md` and existing Gradle conventions.
- Preserve unrelated demo CSS and `.worktrees/` changes.
- Do not commit.
- Report files changed, behavior changed, tests run/not run, model/fallback status, and risks.
