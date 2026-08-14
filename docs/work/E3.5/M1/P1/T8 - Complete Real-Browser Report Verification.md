# T8 - Complete Real-Browser Report Verification

## Document Context

- Parent: [P1 - Chart.js Report Implementation](../P1%20-%20Chart.js%20Report%20Implementation.md).
- Children: None.
- Related: [T7 - Regenerate And Inspect The Current Report](T7%20-%20Regenerate%20And%20Inspect%20The%20Current%20Report.md), [Chart.js benchmark charts source plan](../../../../superpowers/plans/2026-07-24-chartjs-benchmark-charts.md).
- Next: None.

## Purpose

Supply observable desktop, narrow-screen, offline, keyboard, and fallback evidence for the only
remaining E3.5 acceptance gate.

## Prerequisites

- T7 produced the exact report under review.
- A browser can open it directly from disk with developer tools and JavaScript controls available.

## Changes

- [x] At a desktop viewport, verify all five charts initialize independently; inspect CPU logarithmic
  axes, rendering median/p95/p99 groups, 120/60 Hz budget markers, over-budget colors, precise
  tooltips, visible guidance, and fallback hiding after successful initialization.
- [x] Use the history `<select>` by keyboard to switch from a CPU series to a rendering series and
  back. Verify the existing chart updates in place, the y-axis title/unit, tooltip values/changes,
  and accessible canvas label follow the selected series, and missing observations remain gaps.
- [x] At a narrow viewport, verify navigation and selector usability, readable chart titles, wrapping
  page content, and horizontal scrolling confined to each chart viewport rather than the page.
- [x] Open the generated report directly from disk with developer tools and verify it issues no network
  requests. Disable JavaScript and reload; verify all fallback explanations, guidance, summaries, and
  raw CPU/rendering/history tables remain readable.
- [x] Record the browser/version, viewport sizes, report run ID, observations, console state, and any
  limitation in the E3.5 completion evidence.

## Acceptance Checks

- [x] Every observable check above passes in a real browser; local HTTP behavior or static HTML
  inspection does not substitute for the direct-file/offline check.
- [x] Console output contains no chart-initialization errors during desktop, narrow, or history-switch
  scenarios.
- [x] E3.5, M1, and P1 status move to complete only after the evidence is recorded.

## Verification Evidence

- Initial automated browser surface: connected Chrome extension; the exact browser version was
  unavailable because navigation was rejected before the report loaded.
- Completion surface: Codex in-app browser opened the report directly from disk. The browser version
  was not exposed.
- Report under review: `spinygui.benchmark/reports/index.html`, current run
  `20260812-180405-466967300` from the accepted T7 artifact.
- Automated direct navigation to
  `file:///G:/Programming/workspace_idea/SpinyOwl/SpinyGUI/spinygui.benchmark/reports/index.html`
  was rejected by the browser URL security policy before page load.
- User-supplied visual evidence captured the direct-file report at `1369x1270`; the exact narrow
  viewport size was not reported.
- On 2026-08-14, the user confirmed that the full checklist works correctly against this exact
  artifact: all five desktop charts, tooltips and guidance; keyboard CPU/rendering history switching;
  narrow-layout navigation and local chart scrolling; zero network requests; readable
  JavaScript-disabled fallbacks and raw tables; and no chart-initialization console errors.

## Risks

Browser automation policies may block `file://` navigation even when a user-observable browser
supports it. Preserve the manual evidence and unavailable version/viewport details rather than
inventing values that were not exposed.
