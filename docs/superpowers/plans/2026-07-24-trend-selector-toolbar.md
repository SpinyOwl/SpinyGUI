# Trend Selector Toolbar Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Move trend metric controls into a wrapping toolbar above one full-width selected chart.

**Architecture:** Keep the existing native radio inputs and JTE-generated chart panels, but place them in separate `trend-controls` and `trend-panels` wrappers. Generated CSS maps each checked radio ID to its matching panel ID, while the controls use a wrapping flex row and the selected panel keeps local chart overflow containment.

**Tech Stack:** Java 25, precompiled JTE 3.2.4 templates, CSS-only radio selection, JUnit 6, Gradle 9.5.1.

## Global Constraints

- Keep the report self-contained and JavaScript-free.
- Do not add external resources or charting dependencies.
- Preserve trend data, SVG math, archive behavior, raw history tables, and production code.
- Selector buttons wrap without horizontal selector scrolling.
- Do not commit implementation changes unless the user explicitly requests it.

---

### Task 1: Separate Wrapped Controls From Chart Panels

**Files:**
- Modify: `spinygui.benchmark/src/main/jte/report.jte`
- Test: `spinygui.benchmark/src/test/java/com/spinyowl/spinygui/benchmark/report/BenchmarkHtmlReportGeneratorTest.java`
- Modify: `spinygui.benchmark/README.md`

**Interfaces:**
- Consumes: `BenchmarkReportView.trends()`, `TrendSeries.id()`, `TrendSeries.label()`, and `components/trendChart.jte`.
- Produces: `.trend-controls`, `.trend-panels`, and one `${series.id()}-panel` element per trend series.

- [ ] **Step 1: Write failing structural and CSS assertions**

Add assertions to `generatesSelfContainedEscapedReportFromBothJsonFormats()` after the existing trend CSS checks:

```java
assertTrue(html.contains("<div class=\"trend-controls\" role=\"radiogroup\""));
assertTrue(html.contains("<div class=\"trend-panels\">"));
assertTrue(html.indexOf("class=\"trend-controls\"") < html.indexOf("class=\"trend-panels\""));
assertTrue(html.contains("id=\"cpu-trend-1-panel\" class=\"trend-panel\""));
assertTrue(compactHtml.contains(".trend-controls{display:flex;flex-wrap:wrap;gap:4px"));
assertTrue(compactHtml.contains(".trend-panels{min-width:0}"));
assertFalse(compactHtml.contains(".trend-explorer{display:grid;grid-template-columns:"));
assertFalse(compactHtml.contains(".trend-option{grid-column:"));
```

Replace the broad assertion that forbids all `:has()` usage with assertions that specifically reject old page-tab selectors:

```java
assertFalse(html.contains("tab-control"));
assertFalse(html.contains("tab-panel"));
assertFalse(html.contains("#tab-overview"));
```

- [ ] **Step 2: Run the focused test and verify it fails**

Run:

```powershell
.\gradlew.bat :spinygui.benchmark:test --tests com.spinyowl.spinygui.benchmark.report.BenchmarkHtmlReportGeneratorTest
```

Expected: FAIL because `trend-controls`, `trend-panels`, and per-series panel IDs do not exist yet.

- [ ] **Step 3: Implement the wrapped toolbar and separate panels**

In `report.jte`, replace the two-column trend selector CSS with:

```css
.trend-explorer { min-width:0 }
.trend-controls {
    display:flex;
    flex-wrap:wrap;
    gap:4px;
    margin-bottom:16px;
}
.trend-select {
    position:absolute;
    inline-size:1px;
    block-size:1px;
    opacity:0;
}
.trend-option {
    cursor:pointer;
    border:1px solid #304052;
    border-radius:4px;
    padding:8px 10px;
    color:#e8edf5;
}
.trend-select:focus-visible + .trend-option {
    outline:3px solid #f2d05c;
    outline-offset:2px;
}
.trend-select:checked + .trend-option {
    background:#55b6e8;
    color:#10151f;
    font-weight:700;
}
.trend-panels { min-width:0 }
.trend-panel {
    display:none;
    min-width:0;
    max-width:100%;
    overflow-x:auto;
    overscroll-behavior-inline:contain;
}
```

Remove trend-specific two-column, grid-row, and ordering rules from the desktop/tablet/mobile selectors. Keep `.trend-chart { min-width:820px; ... }` so narrow panels scroll locally.

Render the selector and panels as separate siblings:

```jte
<div class="trend-explorer">
    <div class="trend-controls" role="radiogroup" aria-label="Trend metric selector">
        @for(var series : model.trends())
            <input class="trend-select" type="radio" id="${series.id()}-select" name="trend-metric" @if(series.id().equals(model.trends().getFirst().id()))checked@endif>
            <label class="trend-option" for="${series.id()}-select">${series.label()}</label>
        @endfor
    </div>
    <div class="trend-panels">
        @for(var series : model.trends())
            <div id="${series.id()}-panel" class="trend-panel">
                @template.components.trendChart(series)
            </div>
        @endfor
    </div>
</div>
```

Inside the template style block, generate one checked-state mapping per series:

```jte
@for(var series : model.trends())
.trend-explorer:has(#${series.id()}-select:checked) #${series.id()}-panel { display:block }
@endfor
```

- [ ] **Step 4: Update contributor documentation**

Change the trend description in `spinygui.benchmark/README.md` to state that metric buttons form a wrapping toolbar above the selected full-width chart and that only the chart viewport scrolls when required on narrow screens.

- [ ] **Step 5: Run focused tests and regenerate the existing archive report**

Run:

```powershell
.\gradlew.bat :spinygui.benchmark:test --rerun-tasks
.\gradlew.bat :spinygui.benchmark:benchmarkReport -x :spinygui.benchmark:jmhCpu -x :spinygui.benchmark:jmhRendering
```

Expected: tests pass and `spinygui.benchmark/reports/index.html` contains all selector labels before `trend-panels`, with one checked metric and one visible matching panel.

- [ ] **Step 6: Run full verification**

Run:

```powershell
.\gradlew.bat test
.\gradlew.bat :spinygui.benchmark:precompileJte :spinygui.benchmark:jar
git diff --check
```

Expected: all commands pass; the benchmark JAR retains precompiled JTE classes and generated HTML remains offline and script-free.
