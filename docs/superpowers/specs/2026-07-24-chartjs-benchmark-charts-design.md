# Chart.js Benchmark Charts Design

## Summary

Replace every hand-built benchmark report chart with Chart.js 4.5.1 while keeping each generated report as one portable, offline HTML file. The report will embed a pinned Chart.js UMD distribution, render four overview charts plus one reusable history chart, preserve the existing raw tables, and provide explicit accessible fallback text when JavaScript or canvas rendering is unavailable.

This change intentionally relaxes the prior JavaScript-free constraint. It does not relax the no-network or single-file constraints.

## Goals

- Use Chart.js for CPU latency, CPU allocation, CPU rendering latency, GPU rendering latency, and history trends.
- Keep `spinygui.benchmark/reports/index.html` self-contained and usable without network access.
- Preserve current benchmark parsing, archive selection, chronological history, scene identity, and raw tables.
- Preserve the wrapping history metric toolbar above one full-width chart.
- Add useful tooltips without adding zoom, pan, or third-party Chart.js plugins.
- Preserve keyboard-operable metric selection and provide understandable non-JavaScript fallback content.
- Keep narrow-screen horizontal overflow local to each chart viewport.

## Non-Goals

- Loading Chart.js from a CDN or any other external URL.
- Adding Node, npm, pnpm, or a browser bundler to the Gradle build.
- Adding zoom, pan, annotations, export controls, or other Chart.js ecosystem plugins.
- Replacing raw benchmark tables or changing benchmark/archive formats.
- Retaining the SVG charts as a second rendering implementation.

## Dependency Packaging

Vendor the official `chart.umd.min.js` distribution for Chart.js 4.5.1 as a benchmark classpath resource. Preserve its Chart.js and bundled `@kurkle/color` license banners, and add the full corresponding MIT license texts beside the vendored asset or in a benchmark third-party notice. Remove only a trailing source-map directive if the distribution contains one, so opening the report cannot request a companion map file.

`BenchmarkHtmlReportGenerator` loads the pinned resource and supplies it to the precompiled JTE report template. The generated HTML embeds the bundle in an inline `<script>` block before the report bootstrap code. No generated `<script src>`, stylesheet `<link>`, source-map directive, or other resource-loading reference is permitted. URL literals inside preserved upstream license banners or library source are allowed because they do not initiate requests; browser verification must confirm that the finished report performs no network access.

The generated file will be larger by approximately the minified Chart.js bundle size. Portability and deterministic offline behavior take priority over minimizing report size.

## Architecture

### Report Data

Keep human-formatted strings used by summaries and tables, but provide typed numeric values for Chart.js instead of deriving numbers back from display text. Remove SVG-only coordinate and segment fields once no template consumes them.

The chart payload contains:

- CPU operation labels, latency values, uncertainty values, allocation values, and allocation rates.
- Rendering scene labels and numeric median, p95, and p99 values for CPU submission and GPU completion.
- The global ordered history run labels.
- One history series per metric with numeric values or `null` at runs where that metric is absent.
- Per-point signed change text for history tooltips.

The generator serializes the payload with Gson using HTML-safe escaping and places it in one `<script id="benchmark-chart-data" type="application/json">` element. The bootstrap parses that element rather than interpolating benchmark values into executable JavaScript. In particular, `<`, `>`, `&`, quotes, and label content capable of terminating a script element must remain escaped. The template may bypass JTE escaping only for the trusted vendored bundle and generator-owned, verified-safe JSON or bootstrap source.

### Chart Runtime

The report creates these Chart instances after the DOM is available:

1. CPU operation latency.
2. Normalized CPU allocation.
3. CPU submission latency by rendering scene.
4. GPU-complete latency by rendering scene.
5. One reusable history trend chart.

The first four charts initialize independently. The history toolbar stores each series identifier on a native button. Activating a button updates the existing history Chart instance's label, unit, data, tooltip metadata, and y-axis range, then calls `update()`.

Each chart initialization is isolated with error handling. A failure leaves that chart's fallback visible and must not prevent later charts from initializing.

## Chart Configuration

### CPU Charts

CPU latency and normalized allocation use horizontal bar charts with logarithmic x-axes. Labels remain operation names. The dark report palette supplies grid, tick, bar, hover, and tooltip colors.

Latency tooltips show the exact `us/op` value and uncertainty. Allocation tooltips show exact `B/op` and allocation rate in `MB/sec`. These values continue to appear in the precise table below the charts.

### Rendering Charts

CPU-submission and GPU-complete rendering charts use horizontal grouped bars with median, p95, and p99 datasets. The x-axis remains fixed from `0` to `16,667 us` to preserve direct comparison with the 60 Hz frame budget.

A small report-owned Chart.js plugin draws vertical markers at `8,333 us` and `16,667 us`. This plugin is part of the report bootstrap and does not introduce another dependency. Values over `16,667 us` retain their full tooltip values, are clipped by the fixed display scale, and use the existing over-budget warning color.

### History Chart

The history chart uses a line chart with the global chronological run identifiers as x-axis labels. Every metric dataset has one entry per global run. Missing observations are represented by `null`, and `spanGaps` is false, so missing workloads leave visible line gaps. One-point series still display their point.

The y-axis uses the existing per-series padded minimum and maximum. Tooltips show run identifier, formatted value and unit, and signed change from the previous complete run. Animations are disabled so toolbar changes are immediate and deterministic.

## Layout And Interaction

Every canvas has a dedicated relatively positioned parent, as required by Chart.js responsive sizing. The inner chart viewport retains a readable minimum width of `820px`; an outer wrapper owns `overflow-x:auto` and inline overscroll containment. Selector controls wrap instead of scrolling horizontally.

The history controls are native `<button type="button">` elements. The selected button has `aria-pressed="true"`, the existing selected colors, and the existing visible keyboard focus outline. Selecting another metric updates `aria-pressed` on both the old and new buttons.

The history section contains one chart container rather than one hidden panel per series. This avoids Chart.js measuring canvases while their ancestors are `display:none` and avoids creating unnecessary Chart instances.

## Accessibility And Fallback

Chart.js canvas pixels are not exposed to screen readers. Each canvas therefore has `role="img"` and a descriptive `aria-label` naming the metric, scale, and relationship represented by the chart.

Each chart also has adjacent fallback text that identifies the matching precise table. The fallback is visible in the initial HTML and is hidden only after that specific Chart instance initializes successfully. If JavaScript is disabled, the vendored bundle fails, canvas is unsupported, or one chart configuration throws, users still receive an explanation and retain the complete data tables.

The report must not rely on canvas fallback children alone because browsers that support canvas but block JavaScript do not display those children. The adjacent fallback element is the authoritative failure state.

Chart.js tooltips are pointer interactions and are not treated as the accessible representation of the data. Keyboard users can operate the native history metric buttons, while the persistent summaries and tables remain the authoritative keyboard and screen-reader data views. This design does not add custom keyboard navigation inside canvas pixels.

## Cleanup

Remove code that exists only for the old renderers after Chart.js output is covered:

- `components/trendChart.jte`.
- `components/chartRow.jte`.
- SVG trend styles and CSS-only panel mappings.
- Hidden trend panels and radio inputs.
- SVG x/y coordinate and segment model fields.
- Percentage width and CSS class fields used only by the CSS bar charts.

Do not retain compatibility paths for old generated reports. Existing report archives contain benchmark JSON, not generated chart state, so regenerated reports can use the new format directly.

## Error Handling

- Missing or unreadable vendored Chart.js resources fail report generation with a clear exception rather than producing a knowingly broken report.
- Invalid or non-finite chart values are rejected or represented as missing data before JSON serialization; emitted JSON must never contain JavaScript `NaN` or infinities.
- One runtime chart failure is caught independently, logged to the browser console, and leaves only that chart's fallback visible.
- A missing history series identifier does not destroy the current chart; the selection handler reports the error and retains the previous selection.

## Testing

Follow red-green TDD for each behavior change.

Automated tests cover:

- The pinned Chart.js 4.5.1 resource and retained MIT license notice.
- Embedding the bundle and bootstrap while rejecting resource-loading tags, attributes, and source-map directives. Incidental URL literals inside preserved third-party source are not failures.
- Preserving the Chart.js and bundled `@kurkle/color` notices and full MIT license texts.
- HTML-safe serialization of benchmark labels and script-termination attempts.
- Numeric CPU and rendering datasets and units.
- Fixed rendering scale and both frame-budget marker values.
- Global history labels, aligned values, `null` gaps, per-point changes, and one-point series.
- Five accessible canvases, adjacent visible fallback elements, and native history buttons.
- Initial selected history metric and generated data identifiers.
- Absence of obsolete SVG charts, trend panels, CSS-only selection mappings, and old CSS bar markup.
- Precompiled JTE classes and the vendored Chart.js resource in the benchmark JAR.

Final verification includes:

- Focused report generator tests.
- All benchmark module tests.
- Full repository tests.
- JTE precompilation and benchmark JAR assembly/inspection.
- Report regeneration from the existing archive without rerunning benchmarks.
- `git diff --check`.
- Manual desktop and narrow-screen browser checks for tooltips, toolbar updates, budget markers, chart-local scrolling, and layout.
- Browser developer-tools confirmation that opening the generated file performs no network requests.
- A disabled-JavaScript check confirming that fallback explanations and raw tables remain usable.

## Documentation

Update `spinygui.benchmark/README.md` to state that generated reports embed Chart.js 4.5.1 and inline JavaScript, remain single-file and offline, provide interactive tooltips and history selection, and retain accessible tables/fallback explanations.

Remove statements that describe the report as JavaScript-free or as containing only inline styles and graphics.
