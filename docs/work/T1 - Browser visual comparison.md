# T1 - Browser visual comparison

## Document Context
- Status: Completed
- Dependencies: None
- Parent: None (standalone approved test tooling)
- Children: None
- Related: [Benchmark capture policy](../../spinygui.benchmark/LOCAL_IMAGE_COMPARISON.md)
- Next: [Runner documentation](../../spinygui.visual-tests/README.md)

## Purpose
Compare existing demos and focused layout, text, borders, clipping, and scrolling fixtures
in Chromium and SpinyGUI, using shared inputs and fresh same-run captures.

## Changes
- Add the approved separate `spinygui.visual-tests` Gradle subproject and explicit `compareViews` task.
- Reuse `gui-test-html-app` for manual and automated preview, with pinned Chromium automation.
- Capture border-box geometry and scroll metrics, compare pixels with documented tolerances,
  and retain per-case images, diffs, metrics, runtime metadata and an HTML report.
- Keep graphics execution outside ordinary test/check. Preserve existing benchmark policy.

## Acceptance Checks
- [x] Subproject compiles and focused comparison/fixture/failure tests pass.
- [x] Existing XML/CSS demos and focused fixtures use identical inputs in both renderers.
- [x] A native/browser run produces paired captures, geometry comparisons and a report.
- [x] Mismatches and setup/capture failures yield nonzero exit; empty selections cannot pass.
- [x] Portable setup, native graphics requirements, tolerances and limitations are documented.

## Risks
Cross-engine font rasterization and supported CSS differ. Report discrepancies without silently
relaxing thresholds or accepting missing captures. Cross-platform support requires native libraries,
Chromium dependencies and an OpenGL display/context. Local evidence does not prove every OS.
This task does not repair discovered renderer defects or add a general interaction framework.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Separate Gradle subproject, shared preview inputs, eight resource demos and seven focused/state cases, isolated native capture, Chromium capture, independent geometry/pixel comparisons, reports and ten unit tests.
- Relevant Files and Symbols: `spinygui.visual-tests/build.gradle.kts`; `ViewCase`, `PreviewServer`, `NativeCaptureMain`, `Geometry`, `ViewComparison`, `CompareViewsMain`; `gui-test-html-app/comparison.js` and `comparison.css`; module README.
- Acceptance Evidence:
  - Compilation and focused checks: Verified — Automated — `:spinygui.visual-tests:check` passed; 10 tests, zero failures; PMD and SpotBugs main/test passed. Ordinary checks launch no graphics.
  - Shared inputs: Verified — Automated — fixture/ID validation tests and retained per-case `case.json`; both capture paths consume the same XML/CSS and bundled Roboto.
  - Paired captures/report: Verified — Native Host — final run `run-18295419870537634196` captured all 15 cases on Windows 11 amd64, JDK 25.0.3, NVIDIA GTX 1060/OpenGL 3.2. Browser metadata records Chromium 145.0.7632.6 via Playwright 1.58.0. All 15 cases report mismatches; zero capture errors. This validates the comparison tooling, not rendering equivalence.
  - Failure semantics: Verified — Automated — invalid-selection test rejects empty/unknown cases and replaces stale reports; missing-browser runtime attempt emitted setup-error and exit 1; full native comparison emitted mismatch and exit 1. Capture-error handling and timeout cleanup inspected; native crash/timeout injection was not performed.
  - Setup/policy documentation: Verified — Documentation — README covers browser installation/cache, explicit Gradle task, matching native classifiers, macOS first thread, Linux graphics prerequisites, fixed tolerances, output artifacts, and limitations. Linux/macOS execution not performed.
- Decisions and Deviations: User approved this subproject and implementation. Fixed viewport and forced bundled Roboto normalize capture inputs; animations/transitions use initial static states. Existing renderer discrepancies remain findings, without threshold increases. The old benchmark reference policy is untouched.
- Review Outcome: Not Required — no independent review requested for this direct implementation; self-review included final source/diff inspection, unit/static checks, actual paired images, and configuration-cache reuse verification.
- Remaining Work: None in the approved test-tooling scope. Renderer parity fixes and Linux/macOS runtime validation remain separate follow-up work.
- Resume or Closure: Run `:spinygui.visual-tests:compareViews` and inspect `spinygui.visual-tests/build/reports/view-comparison/index.html` to investigate reported discrepancies. Changes are uncommitted; unrelated worktree changes preserved.

## Follow-up: visible demo launcher (2026-09-08)

User requested an explicit side-by-side Gradle launcher with a default and named selection.
Added `:spinygui.visual-tests:launchDemos`, defaulting to `button-demo`; `-Pdemo` accepts a
resource stem or comparison case ID. The standard native host supplies input callbacks, Chromium
uses the existing preview server, and both receive the same screen-fitted view. Closing either
ends the pair. Native JVM startup is shared with the screenshot runner via `NativeProcess`.

Verification: `:spinygui.visual-tests:check` passed (12 unit tests and PMD/SpotBugs).
Timed visible launches passed on Windows for both the default and `-Pdemo=overflow-demo`.
Native first-frame readiness and actual window coordinates were recorded under
`build/demo-sessions/session-5266312486235711768` and `session-17978734528478902582`:
native client origin `(16,110)`, browser outer origin `(1728,24)`, shared viewport `960x720`.
Automatic graceful shutdown passed; manual close gestures and Linux/macOS placement were not
exercised. No renderer behavior, benchmark policy, or unrelated worktree edits were changed.
