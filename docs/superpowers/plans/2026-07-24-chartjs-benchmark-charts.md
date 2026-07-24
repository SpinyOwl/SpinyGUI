# Chart.js Benchmark Charts Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace every benchmark report chart with embedded Chart.js 4.5.1 charts while retaining one portable offline HTML file, precise tables, accessible fallbacks, and the wrapping history selector.

**Architecture:** The report generator builds a typed numeric `ChartPayload`, serializes it with Gson's HTML-safe defaults, and renders it through a `BenchmarkReportPage` alongside pinned classpath JavaScript resources. Four overview canvases initialize independently, while native history buttons update one reusable line-chart instance; each canvas keeps a visible table fallback until its Chart instance succeeds.

**Tech Stack:** Java 25, Gradle 9.5.1, JTE 3.2.4 precompiled templates, Gson 2.14.0, Chart.js 4.5.1 UMD, JUnit 6.

## Global Constraints

- Keep `spinygui.benchmark/reports/index.html` a single self-contained offline file.
- Pin Chart.js to exactly `4.5.1`; do not add Node, npm, pnpm, a CDN, or another Chart.js plugin.
- Preserve Chart.js and bundled `@kurkle/color` license banners and package their full MIT license texts.
- Strip the upstream `sourceMappingURL` directive and emit no resource-loading `<script src>` or stylesheet `<link>`.
- URL literals inside the embedded third-party source are allowed; opening the report must perform no network requests.
- Preserve benchmark parsing, archive selection, chronological history, scene identity, summaries, and raw tables.
- Use Gson HTML-safe JSON in `<script id="benchmark-chart-data" type="application/json">`; never interpolate benchmark labels into executable JavaScript.
- Keep history controls keyboard-operable native buttons with `aria-pressed`.
- Keep each chart's initial fallback visible until that chart initializes successfully.
- Keep a dedicated relatively positioned parent per canvas and local horizontal scrolling around an `820px` minimum chart viewport.
- Chart.js tooltips are pointer enhancements; summaries and tables remain the keyboard and screen-reader data representation.
- Follow red-green TDD and verify every expected failure before production edits.
- Create focused implementation commits after each reviewed task; the user explicitly authorized task commits for subagent-driven execution.
- Do not modify or revert unrelated changes in `spinygui.demo.complex/.../overflow-demo.css` or `.worktrees/`.

## File Structure

- `BenchmarkReportView.java`: final typed report and numeric chart payload records.
- `BenchmarkHtmlReportGenerator.java`: archive parsing, numeric chart payload construction, HTML-safe JSON serialization, trusted resource loading, and JTE rendering.
- `BenchmarkReportPage.java`: template boundary containing the report view, embedded assets, and chart JSON.
- `.gitattributes`: LF normalization for hash-pinned JavaScript and license resources.
- `report.jte`: accessible chart markup, report styling, native history buttons, and trusted inline assets.
- `benchmark-charts.js`: report-owned Chart.js configuration, custom budget-marker plugin, isolated initialization, and history selection behavior.
- `chart.umd.min.js`: pinned Chart.js 4.5.1 distribution with only the source-map directive removed.
- `THIRD-PARTY-LICENSES.txt`: full Chart.js and `@kurkle/color` MIT licenses.
- `BenchmarkChartAssetsTest.java`: version, hash, source-map, and license integrity checks.
- `BenchmarkHtmlReportGeneratorTest.java`: payload, escaping, markup, fallback, history, and legacy-removal tests.

---

### Task 1: Add Typed Numeric Chart Payloads

**Files:**
- Modify: `spinygui.benchmark/src/main/java/com/spinyowl/spinygui/benchmark/report/BenchmarkReportView.java`
- Modify: `spinygui.benchmark/src/main/java/com/spinyowl/spinygui/benchmark/report/BenchmarkHtmlReportGenerator.java`
- Test: `spinygui.benchmark/src/test/java/com/spinyowl/spinygui/benchmark/report/BenchmarkHtmlReportGeneratorTest.java`

**Interfaces:**
- Consumes: parsed `CpuResult`, `RenderingResult`, ordered `ArchivedRun`, and existing formatted table rows.
- Produces: `BenchmarkReportView.ChartPayload`, `CpuChartDatum`, `RenderingChartDatum`, and `ChartTrend` with aligned values for later JSON serialization.

- [ ] **Step 1: Add failing assertions for raw overview values**

In `loadsCompleteTimestampedPairsChronologicallyAndComputesChanges()`, assert literal numeric values independent of display strings:

```java
BenchmarkReportView.ChartPayload charts = view.charts();
assertTrue(charts.cpu().getFirst().label().equals("measure<Latin>"));
assertTrue(charts.cpu().getFirst().latency() == 20.0);
assertTrue(charts.cpu().getFirst().uncertainty() == 0.5);
assertTrue(charts.cpu().getFirst().allocation() == 42.0);
assertTrue(charts.cpu().getFirst().allocationRate() == 7.0);
assertTrue(charts.rendering().getFirst().cpuMedian() == 1.0);
assertTrue(charts.rendering().getFirst().cpuP95() == 2.0);
assertTrue(charts.rendering().getFirst().cpuP99() == 3.0);
assertTrue(charts.rendering().getFirst().gpuMedian() == 2.0);
assertTrue(charts.rendering().getFirst().gpuP95() == 3.0);
assertTrue(charts.rendering().getFirst().gpuP99() == 12.0);
```

- [ ] **Step 2: Replace SVG-coordinate expectations with failing aligned-history expectations**

Use the final payload API in the three history tests:

```java
BenchmarkReportView.ChartTrend cpuTrend = chartTrend(view, "CPU latency: measure<Latin>");
assertTrue(charts.historyRuns().equals(List.of(first, second, collision)));
assertTrue(cpuTrend.values().equals(List.of(12.5, 25.0, 20.0)));
assertTrue(cpuTrend.changes().equals(List.of("not available", "+100.000%", "-20.000%")));
assertTrue(cpuTrend.minimum() == 11.25);
assertTrue(cpuTrend.maximum() == 26.25);
```

For the missing middle workload:

```java
BenchmarkReportView.ChartTrend missingCpu = chartTrend(view, "CPU latency: measureLatin");
assertTrue(missingCpu.values().equals(java.util.Arrays.asList(12.5, null, 12.5)));
assertTrue(missingCpu.changes().equals(java.util.Arrays.asList("not available", null, "not available")));
```

For boundary and one-run cases:

```java
assertTrue(boundaryOnly.values().equals(java.util.Arrays.asList(null, 12.5, null)));
assertTrue(single.values().equals(List.of(12.5)));
```

Add this helper, leaving the existing SVG helper temporarily available until Task 4 removes it:

```java
private static BenchmarkReportView.ChartTrend chartTrend(BenchmarkReportView view, String label) {
  return view.charts().trends().stream().filter(series -> series.label().equals(label)).findFirst().orElseThrow();
}
```

Add a focused non-finite input test using `assertThrows`:

```java
IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
    () -> BenchmarkHtmlReportGenerator.generate(cpuJson().replace("12.5", "1e309"), renderingJson()));
assertTrue(failure.getMessage().contains("Non-finite benchmark chart value"));
```

- [ ] **Step 3: Run the focused test and verify RED**

Run:

```powershell
.\gradlew.bat :spinygui.benchmark:test --tests com.spinyowl.spinygui.benchmark.report.BenchmarkHtmlReportGeneratorTest
```

Expected: compilation fails because `charts()`, `ChartPayload`, and `ChartTrend` do not exist.

- [ ] **Step 4: Add final chart payload records**

Append `ChartPayload charts` to `BenchmarkReportView` while retaining the old `cpuChartRows`, `gpuChartRows`, and SVG `trends` fields only until their consuming templates are replaced in Tasks 3 and 4.

Add these final records:

```java
public record ChartPayload(List<CpuChartDatum> cpu, List<RenderingChartDatum> rendering,
    List<String> historyRuns, List<ChartTrend> trends) { }

public record CpuChartDatum(String label, double latency, double uncertainty,
    double allocation, double allocationRate) { }

public record RenderingChartDatum(String label, double cpuMedian, double cpuP95, double cpuP99,
    double gpuMedian, double gpuP95, double gpuP99) { }

public record ChartTrend(String id, String label, String unit, double minimum, double maximum,
    List<Double> values, List<String> changes) { }
```

- [ ] **Step 5: Build numeric payloads without parsing display text**

In `toView`, build `CpuChartDatum` and `RenderingChartDatum` directly from `CpuResult` and `SceneResult`. Add `double numericMinimum` and `double numericMaximum` to the temporary SVG `TrendSeries`, plus `double numericValue` to its temporary `TrendPoint`, so aligned trends use raw values without calling `Double.parseDouble` on formatted strings.

Pass every raw number added to `ChartPayload` through a small `finite(String metric, double value)` guard. It returns finite values unchanged and throws `IllegalArgumentException("Non-finite benchmark chart value: " + metric)` otherwise. This prevents Gson from ever receiving `NaN` or either infinity.

Create `chartPayload(...)` that:

1. Uses `history` identifiers as `historyRuns`.
2. Creates `ArrayList` instances prefilled with `null` for every run.
3. Places each trend point's numeric value and change at its matching global run index.
4. Copies the temporary SVG series' numeric padded minimum and maximum into `ChartTrend`; Task 4 moves those source values directly into `ChartTrend` and removes the bridge.
5. Returns overview rows and aligned trends in archive order.

For `generate(cpuJson, renderingJson)`, return populated overview arrays with empty `historyRuns` and `trends`.

- [ ] **Step 6: Run focused and module tests for GREEN**

Run:

```powershell
.\gradlew.bat :spinygui.benchmark:test --rerun-tasks
```

Expected: all benchmark tests pass while the old chart markup remains unchanged.

---

### Task 2: Pin And Embed Chart.js Assets

**Files:**
- Create: `spinygui.benchmark/src/main/resources/com/spinyowl/spinygui/benchmark/report/chart.umd.min.js`
- Create: `spinygui.benchmark/src/main/resources/com/spinyowl/spinygui/benchmark/report/benchmark-charts.js`
- Create: `spinygui.benchmark/src/main/resources/com/spinyowl/spinygui/benchmark/report/THIRD-PARTY-LICENSES.txt`
- Modify: `.gitattributes`
- Create: `spinygui.benchmark/src/main/java/com/spinyowl/spinygui/benchmark/report/BenchmarkReportPage.java`
- Modify: `spinygui.benchmark/src/main/java/com/spinyowl/spinygui/benchmark/report/BenchmarkHtmlReportGenerator.java`
- Modify: `spinygui.benchmark/src/main/jte/report.jte`
- Create: `spinygui.benchmark/src/test/java/com/spinyowl/spinygui/benchmark/report/BenchmarkChartAssetsTest.java`
- Test: `spinygui.benchmark/src/test/java/com/spinyowl/spinygui/benchmark/report/BenchmarkHtmlReportGeneratorTest.java`

**Interfaces:**
- Consumes: `BenchmarkReportView.charts()` from Task 1 and classpath resources under `/com/spinyowl/spinygui/benchmark/report/`.
- Produces: `BenchmarkReportPage(BenchmarkReportView report, String chartJs, String chartBootstrap, String chartDataJson)` and three trusted inline script blocks.

- [ ] **Step 1: Write failing asset integrity tests**

Create `BenchmarkChartAssetsTest` with a `resource(String)` helper using `getResourceAsStream`. Assert:

```java
assertTrue(chartJs.contains("Chart.js v4.5.1"));
assertTrue(chartJs.contains("@kurkle/color v0.3.2"));
assertFalse(chartJs.contains("sourceMappingURL"));
assertTrue(licenses.contains("Copyright (c) 2014-2024 Chart.js Contributors"));
assertTrue(licenses.contains("Copyright (c) 2018-2021 Jukka Kurkela"));
assertTrue(licenses.split("Permission is hereby granted", -1).length - 1 == 2);
assertTrue(sha256(chartJs).equals("84d0e233daba702b8f77d669d8c137cad36d441a10f200b6f2d3ab553bdfcf6b"));
```

Implement `sha256` with `MessageDigest.getInstance("SHA-256")` and `HexFormat.of().formatHex(...)`, hashing UTF-8 bytes.

- [ ] **Step 2: Run the asset test and verify RED**

Run:

```powershell
.\gradlew.bat :spinygui.benchmark:test --tests com.spinyowl.spinygui.benchmark.report.BenchmarkChartAssetsTest
```

Expected: FAIL because the resources do not exist.

- [ ] **Step 3: Vendor the exact third-party assets**

Import `https://cdn.jsdelivr.net/npm/chart.js@4.5.1/dist/chart.umd.min.js`, removing only the line containing `sourceMappingURL`. The unmodified upstream SHA-256 is `48444a82d4edcb5bec0f1965faacdde18d9c17db3063d042abada2f705c9f54a`; the required stripped UTF-8 resource SHA-256 is `84d0e233daba702b8f77d669d8c137cad36d441a10f200b6f2d3ab553bdfcf6b`.

Before importing, add these rules to `.gitattributes` so Git cannot change the hash-pinned bytes on Windows:

```gitattributes
/spinygui.benchmark/src/main/resources/com/spinyowl/spinygui/benchmark/report/*.js text eol=lf
/spinygui.benchmark/src/main/resources/com/spinyowl/spinygui/benchmark/report/*.txt text eol=lf
```

Create `THIRD-PARTY-LICENSES.txt` with these two complete texts and headings:

```text
Chart.js 4.5.1
The MIT License (MIT)
Copyright (c) 2014-2024 Chart.js Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

@kurkle/color 0.3.2
The MIT License (MIT)
Copyright (c) 2018-2021 Jukka Kurkela

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```

Use the exact package license sources:

- `https://cdn.jsdelivr.net/npm/chart.js@4.5.1/LICENSE.md`
- `https://cdn.jsdelivr.net/npm/@kurkle/color@0.3.2/LICENSE.md`

- [ ] **Step 4: Add the page boundary and failing embedding assertions**

Create:

```java
package com.spinyowl.spinygui.benchmark.report;

public record BenchmarkReportPage(BenchmarkReportView report, String chartJs,
    String chartBootstrap, String chartDataJson) { }
```

In `generatesSelfContainedEscapedReportFromBothJsonFormats`, replace old JavaScript-free URL assertions with:

```java
assertTrue(html.contains("Chart.js v4.5.1"));
assertTrue(html.contains("@kurkle/color v0.3.2"));
assertTrue(html.contains("<script id=\"benchmark-chart-data\" type=\"application/json\">"));
assertTrue(html.contains("data.chartPayloadVersion"));
assertFalse(html.contains("<script src="));
assertFalse(html.contains("<link rel=\"stylesheet\""));
assertFalse(html.contains("sourceMappingURL"));
```

Add a malicious-label case using `measure</script><script>alert(1)</script>` and assert the generated data contains `measure\\u003c/script\\u003e\\u003cscript\\u003ealert(1)\\u003c/script\\u003e` but never contains the literal injected script.

- [ ] **Step 5: Run the focused generator test and verify RED**

Run the focused test. Expected: FAIL because the page does not embed Chart.js, chart JSON, or the bootstrap.

- [ ] **Step 6: Load trusted resources and serialize safe JSON**

In `BenchmarkHtmlReportGenerator`, add a UTF-8 `resource(String name)` method that throws `IllegalStateException("Missing benchmark report resource: " + name)` for a missing stream and wraps read failures with the same resource name.

Change `render` to construct:

```java
BenchmarkReportPage page = new BenchmarkReportPage(
    view,
    resource("chart.umd.min.js"),
    resource("benchmark-charts.js"),
    new Gson().toJson(Map.of("chartPayloadVersion", 1, "charts", view.charts())));
```

Render `page` instead of `view`. In `report.jte`, change the parameter to `BenchmarkReportPage page`, declare `model = page.report()`, and embed in this order immediately before `</body>`:

```jte
<script>$unsafe{page.chartJs()}</script>
<script id="benchmark-chart-data" type="application/json">$unsafe{page.chartDataJson()}</script>
<script>$unsafe{page.chartBootstrap()}</script>
```

Create an initial `benchmark-charts.js` that parses the payload inside an IIFE and exposes no globals other than the vendored `Chart`:

```javascript
(() => {
    'use strict';
    const data = JSON.parse(document.getElementById('benchmark-chart-data').textContent);
    data.chartPayloadVersion;
})();
```

- [ ] **Step 7: Run asset, focused, JTE, and JAR checks for GREEN**

Run:

```powershell
.\gradlew.bat :spinygui.benchmark:test --rerun-tasks
.\gradlew.bat :spinygui.benchmark:precompileJte :spinygui.benchmark:jar
```

Inspect the JAR and confirm all three resources and precompiled `JtereportGenerated.class` are present.

---

### Task 3: Replace Overview Charts

**Files:**
- Modify: `spinygui.benchmark/src/main/jte/report.jte`
- Modify: `spinygui.benchmark/src/main/resources/com/spinyowl/spinygui/benchmark/report/benchmark-charts.js`
- Modify: `spinygui.benchmark/src/main/java/com/spinyowl/spinygui/benchmark/report/BenchmarkReportView.java`
- Modify: `spinygui.benchmark/src/main/java/com/spinyowl/spinygui/benchmark/report/BenchmarkHtmlReportGenerator.java`
- Delete: `spinygui.benchmark/src/main/jte/components/chartRow.jte`
- Test: `spinygui.benchmark/src/test/java/com/spinyowl/spinygui/benchmark/report/BenchmarkHtmlReportGeneratorTest.java`

**Interfaces:**
- Consumes: `data.charts.cpu`, `data.charts.rendering`, global `Chart`, and canvas IDs from the template.
- Produces: four independently initialized horizontal bar charts and `mountChart(canvasId, config)` for Task 4.

- [ ] **Step 1: Write failing overview canvas and fallback assertions**

Assert these four IDs each occur once and every canvas has `role="img"` plus a descriptive `aria-label`:

```text
cpu-latency-chart
cpu-allocation-chart
cpu-rendering-chart
gpu-rendering-chart
```

Also assert:

```java
assertTrue(count(html, "class=\"chart-fallback\"") == 5);
assertTrue(html.contains("class=\"chart-scroll\""));
assertTrue(html.contains("class=\"chart-frame\""));
assertTrue(compactHtml.contains(".chart-frame{position:relative;min-width:820px;height:480px}"));
assertTrue(compactHtml.contains(".chart-scroll{overflow-x:auto;overscroll-behavior-inline:contain}"));
assertTrue(html.contains("id=\"cpu-data\""));
assertTrue(html.contains("id=\"rendering-data\""));
assertFalse(html.contains("class=\"track\""));
assertFalse(html.contains("class=\"budget-marker"));
```

The fifth fallback belongs to the still-existing history area and is added with the final chart shell in this task; Task 4 activates it.

- [ ] **Step 2: Run the focused test and verify RED**

Expected: FAIL because the four canvas elements and Chart.js layout styles do not exist.

- [ ] **Step 3: Render accessible chart shells and retain precise tables**

Replace each CSS chart with this structure, using unique caption, ID, label, and fallback link text:

```html
<figure class="chart-shell">
    <figcaption>CPU operation latency (us/op)</figcaption>
    <div class="chart-scroll"><div class="chart-frame">
        <canvas id="cpu-latency-chart" role="img" aria-label="Horizontal logarithmic bar chart of CPU operation latency in microseconds per operation."></canvas>
    </div></div>
    <p class="chart-fallback">Interactive chart unavailable. <a href="#cpu-data">Use the precise CPU data table.</a></p>
</figure>
```

Add equivalent shells for allocation, CPU rendering, GPU rendering, and the history chart. Add `id="cpu-data"`, `id="rendering-data"`, and a later `id="history-data"` target around the existing precise tables. Default CSS leaves `.chart-fallback` visible; only `.chart-shell[data-chart-ready="true"] .chart-fallback` hides it.

- [ ] **Step 4: Implement isolated overview Chart.js initialization**

Expand `benchmark-charts.js` with:

```javascript
const report = data.charts;
const colors = { text:'#e8edf5', muted:'#aebed0', grid:'#304052', blue:'#55b6e8', orange:'#e8a855', purple:'#b27ce8', warning:'#e85c55' };

function mountChart(canvasId, config) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return null;
    try {
        const chart = new Chart(canvas, config);
        canvas.closest('.chart-shell').dataset.chartReady = 'true';
        return chart;
    } catch (error) {
        console.error(`Unable to initialize ${canvasId}`, error);
        return null;
    }
}
```

Create horizontal CPU bar configs with `indexAxis:'y'`, logarithmic x-axes, `responsive:true`, `maintainAspectRatio:false`, and `animation:false`. Tooltip callbacks read uncertainty and allocation rate from `report.cpu[context.dataIndex]`.

Create grouped rendering configs with scene labels and median/p95/p99 datasets. Use a linear x-axis with `min:0` and `max:16667`. Dataset background color callbacks return `colors.warning` when `Number(context.raw) > 16667`.

Register a local `budgetMarkers` plugin in each rendering chart's `plugins` array. Its `afterDraw` uses `chart.scales.x.getPixelForValue` to draw lines at `8333` and `16667`; it must save and restore the canvas context.

- [ ] **Step 5: Remove obsolete overview renderer state**

Delete `chartRow.jte`, `ChartRow`, `cpuChartRows`, and `gpuChartRows`. Remove `latencyWidth`, `allocationWidth`, `cssClass`, `suffix`, `logarithmicWidth`, `addChartRows`, and `chartRow`. Keep formatted table rows and numeric `ChartPayload` rows.

Remove `.chart`, `.row`, `.track`, `.bar`, `.alloc`, `.gpu`, `.budget-marker`, and their mobile rules from `report.jte`.

- [ ] **Step 6: Run focused and module tests for GREEN**

Run:

```powershell
.\gradlew.bat :spinygui.benchmark:test --rerun-tasks
```

Expected: all tests pass; four overview charts have executable configs while the old SVG history chart still renders.

---

### Task 4: Replace SVG History With One Reusable Chart

**Files:**
- Modify: `spinygui.benchmark/src/main/jte/report.jte`
- Modify: `spinygui.benchmark/src/main/resources/com/spinyowl/spinygui/benchmark/report/benchmark-charts.js`
- Modify: `spinygui.benchmark/src/main/java/com/spinyowl/spinygui/benchmark/report/BenchmarkReportView.java`
- Modify: `spinygui.benchmark/src/main/java/com/spinyowl/spinygui/benchmark/report/BenchmarkHtmlReportGenerator.java`
- Delete: `spinygui.benchmark/src/main/jte/components/trendChart.jte`
- Test: `spinygui.benchmark/src/test/java/com/spinyowl/spinygui/benchmark/report/BenchmarkHtmlReportGeneratorTest.java`

**Interfaces:**
- Consumes: `ChartPayload.historyRuns`, `ChartPayload.trends`, `mountChart`, and `history-chart`.
- Produces: wrapping native history buttons and one Chart instance updated in place.

- [ ] **Step 1: Write failing final history markup assertions**

Replace radio/panel/SVG assertions with:

```java
assertTrue(html.contains("<div class=\"trend-controls\" role=\"toolbar\" aria-label=\"Trend metric selector\">"));
assertTrue(html.contains("<button class=\"trend-option\" type=\"button\" data-trend-id=\"cpu-trend-1\" aria-pressed=\"true\">"));
assertTrue(html.contains("<canvas id=\"history-chart\" role=\"img\""));
assertTrue(html.contains("id=\"history-data\""));
assertTrue(count(html, "aria-pressed=\"true\"") == 1);
assertFalse(html.contains("type=\"radio\""));
assertFalse(html.contains("class=\"trend-panel\""));
assertFalse(html.contains("<svg"));
assertFalse(html.contains(":has("));
assertFalse(html.contains("cx=\""));
assertFalse(html.contains("<polyline"));
```

Assert the embedded bootstrap contains stable behavior markers for `spanGaps:false`, `aria-pressed`, assignment to the history dataset, and `historyChart.update()`.

Also call `BenchmarkHtmlReportGenerator.generate(cpuJson(), renderingJson())` and assert it still renders `history-chart` and its visible fallback without rendering any `data-trend-id` button. This protects the no-archive path from an empty-list failure.

- [ ] **Step 2: Run the focused test and verify RED**

Expected: FAIL because radios, hidden panels, and SVG output still exist.

- [ ] **Step 3: Render buttons and one history canvas**

Keep `.trend-controls { display:flex; flex-wrap:wrap; gap:4px; margin-bottom:16px }`, but style `.trend-option` as a native button. Use `[aria-pressed="true"]` for selected colors and `:focus-visible` for the existing yellow outline.

Render one button per `model.charts().trends()`. The first button has `aria-pressed="true"`; all others have `false`. Do not call `getFirst()` outside a non-empty loop. Remove `.trend-panels`, `.trend-panel`, generated `:has()` rules, radios, labels, and SVG component calls.

Use the history chart shell created in Task 3 and wrap all existing raw history tables in a target with `id="history-data"`.

- [ ] **Step 4: Initialize and update one line chart**

Add these behaviors to `benchmark-charts.js`:

```javascript
const trends = new Map(report.trends.map(trend => [trend.id, trend]));
const trendButtons = Array.from(document.querySelectorAll('[data-trend-id]'));
let historyChart = null;
let activeTrend = null;

function historyConfig(trend) {
    activeTrend = trend;
    return {
        type:'line',
        data:{labels:report.historyRuns, datasets:[{label:`${trend.label} (${trend.unit})`, data:trend.values,
            borderColor:colors.blue, backgroundColor:colors.blue, pointBackgroundColor:'#f2d05c', spanGaps:false}]},
        options:{responsive:true, maintainAspectRatio:false, animation:false,
            scales:{x:{ticks:{color:colors.muted},grid:{color:colors.grid}},
                y:{min:trend.minimum,max:trend.maximum,ticks:{color:colors.muted},grid:{color:colors.grid}}}}
    };
}
```

Add tooltip callbacks that use `activeTrend` plus `context.dataIndex` to return the run identifier, value with unit, and aligned signed change. Initialize from the button whose `aria-pressed` is `true`. If there are no trends, leave the fallback visible without throwing.

On button activation, look up the trend before changing state. If absent, log an error and keep the current selection. Otherwise update the old/new `aria-pressed` values, canvas `aria-label`, dataset label/data, assign `activeTrend = trend`, update y-axis min/max, and call `historyChart.update()`.

- [ ] **Step 5: Remove the SVG bridge and coordinate model**

Delete `trendChart.jte`, `TrendSegment`, `TrendPoint`, old SVG `TrendSeries`, `coordinate`, and all x/y/segment generation. Change `loadArchive` to build `ChartTrend` directly from the `TrendValue` maps and global run indices, retaining the existing 10% y-range padding with a minimum padding of `1`.

Remove the temporary top-level `trends` field from `BenchmarkReportView`; the only trend source becomes `view.charts().trends()`.

Remove `.trend-chart`, `.trend-grid`, `.trend-line`, `.trend-point`, and `.trend-label` CSS.

- [ ] **Step 6: Verify the history regression tests and module suite**

Run:

```powershell
.\gradlew.bat :spinygui.benchmark:test --tests com.spinyowl.spinygui.benchmark.report.BenchmarkHtmlReportGeneratorTest --rerun-tasks
.\gradlew.bat :spinygui.benchmark:test
```

Expected: aligned null gaps, duplicate scene identity, boundaries, one-run trends, generated buttons, and all benchmark tests pass.

---

### Task 5: Document, Regenerate, And Verify The Offline Report

**Files:**
- Modify: `spinygui.benchmark/README.md`
- Verify generated ignored file: `spinygui.benchmark/reports/index.html`

**Interfaces:**
- Consumes: final report generator, precompiled templates, vendored resources, and the existing benchmark archive.
- Produces: contributor documentation and a verified generated Chart.js report.

- [ ] **Step 1: Update contributor documentation**

State that reports embed Chart.js 4.5.1 and inline JavaScript, remain one offline file, initialize four overview charts plus one reusable history chart, offer pointer tooltips and keyboard-operable metric buttons, and retain visible fallback explanations and raw tables.

Remove the claim that the file has only inline styles and graphics. Explicitly state that no CDN or network resource is used.

- [ ] **Step 2: Regenerate the report without rerunning benchmarks**

Run:

```powershell
.\gradlew.bat :spinygui.benchmark:benchmarkReport -x :spinygui.benchmark:jmhCpu -x :spinygui.benchmark:jmhRendering
```

Inspect `reports/index.html` and confirm:

- Exactly five chart canvases.
- Four overview Chart constructors and one initial history constructor can run from embedded data.
- Exactly one history button begins with `aria-pressed="true"`.
- No SVG, CSS chart row, trend panel, radio selector, `script src`, stylesheet link, or `sourceMappingURL` remains.
- Chart.js and bootstrap code precede the closing body tag.
- Raw CPU, rendering, and history tables remain present.

- [ ] **Step 3: Perform browser behavior checks**

Open the generated file directly from disk on desktop and a narrow viewport. Verify tooltips, logarithmic CPU axes, grouped percentile bars, `8,333`/`16,667` markers, over-budget warning colors, wrapped history buttons, in-place history updates, visible missing-data gaps, and chart-local horizontal scrolling.

In browser developer tools, verify no network request occurs. Disable JavaScript and reload; verify all five fallback explanations and all raw tables remain readable.

If browser automation or an observable browser is unavailable, record these checks as manual follow-up rather than claiming them complete.

- [ ] **Step 4: Run full automated verification**

Run:

```powershell
.\gradlew.bat test
.\gradlew.bat :spinygui.benchmark:precompileJte :spinygui.benchmark:jar
git diff --check
```

Inspect the benchmark JAR and confirm:

```text
gg/jte/generated/precompiled/JtereportGenerated.class
com/spinyowl/spinygui/benchmark/report/chart.umd.min.js
com/spinyowl/spinygui/benchmark/report/benchmark-charts.js
com/spinyowl/spinygui/benchmark/report/THIRD-PARTY-LICENSES.txt
```

Expected: all commands pass, the generated report remains offline and self-contained, and only intended benchmark files plus the already-approved plan/spec files are changed.
