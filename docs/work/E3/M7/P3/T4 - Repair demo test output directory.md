# T4 - Repair demo test output directory

## Document Context

- Status: Completed
- Dependencies: P3/T1
- Parent: [P3 - Prove complex Grid panels](../P3%20-%20Prove%20complex%20Grid%20panels.md)
- Children: None
- Related: spinygui.demo.complex test sources and Gradle configuration
- Next: None.

## Purpose

Remove the environmental or project configuration cause that prevents demo tests from compiling.

## Changes

- [x] Diagnose why compileTestJava cannot create parent directories for ButtonExampleTest.
- [x] Apply the smallest project-local repair and run GridStyleExampleTest.

## Acceptance Checks

- [x] :spinygui.demo.complex:test --tests "*GridStyleExampleTest" compiles and passes.
- [x] The repair does not weaken test isolation or change unrelated test behavior.

## Risks

Do not delete build output or use machine-global permission changes as a workaround. Preserve all
unrelated worktree changes.

## Execution Record

- Status: Completed
- Last Updated: 2026-09-21
- Implemented Scope: Routed complex-demo test classes and reports to a UUID-scoped module-local ignored `.gradle/test-output` directory and disabled incremental compilation for this task because the existing `build/classes/java/test` and `build/tmp/compileTestJava` paths deny directory creation and result persistence. The test task uses the relocated classes directly; no source-set output is altered.
- Relevant Files and Symbols: spinygui.demo.complex/build.gradle.kts; compileTestJava; test; ButtonExampleTest; GridStyleExampleTest.
- Acceptance Evidence:
  - Existing demo regression: Passed — Automated — `:spinygui.demo.complex:test --tests "*ButtonExampleTest" --no-configuration-cache --no-daemon` compiles and executes successfully through the relocated outputs.
  - Focused Grid demo regression: Passed — Automated — `:spinygui.demo.complex:test --tests "*GridStyleExampleTest" --no-configuration-cache --no-daemon` compiles and executes successfully through the relocated outputs.
  - Test isolation: Passed — Automated — each Gradle configuration receives a UUID-scoped module-local output directory for test classes, binary results, JUnit XML, and HTML reports. No build output was deleted and no machine-wide permission was changed.
- Decisions and Deviations: Used the ignored module-local Gradle state directory instead of inaccessible stale `build` output paths. UUID-scoped task output prevents concurrent demo-test runs from sharing mutable report files; the redundant source-set output hook was removed.
- Review Outcome: Accepted — independent re-review found the UUID-scoped module-local outputs execution-safe; it independently reran both focused tests successfully.
- Remaining Work: None.
- Resume or Closure: Completed 2026-09-21. The remaining P3 blocker is the separate native manual-interaction evidence in T2.
