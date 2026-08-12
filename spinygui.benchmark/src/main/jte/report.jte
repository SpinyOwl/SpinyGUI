@import com.spinyowl.spinygui.benchmark.report.BenchmarkReportPage
@param BenchmarkReportPage page
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>SpinyGUI Local Benchmark Report</title>
    <style>
        html { scroll-behavior:smooth; scroll-padding-top:72px }
        body { background:#10151f; color:#e8edf5; font:15px system-ui,sans-serif; margin:0 }
        main { max-width:1200px; margin:auto; padding:32px }
        section { background:#192230; border-radius:8px; padding:20px; margin:18px 0; scroll-margin-top:72px }
        h1,h2 { margin-top:0 }
        table { border-collapse:collapse; width:100% }
        th,td { padding:8px; text-align:left; border-bottom:1px solid #304052; vertical-align:top }
        .skip-link { position:absolute; inset-inline-start:16px; inset-block-start:-48px; background:#55b6e8; color:#10151f; padding:8px; z-index:4 }
        .skip-link:focus { inset-block-start:16px }
        .report-nav { position:sticky; top:0; z-index:2; display:flex; flex-wrap:wrap; gap:8px; padding:12px 0; background:#10151f }
        .report-nav a { border:1px solid #55b6e8; border-radius:4px; color:#e8edf5; padding:7px 10px; text-decoration:none }
        .report-nav a:hover,.report-nav a:focus-visible { background:#55b6e8; color:#10151f; outline:3px solid #f2d05c; outline-offset:2px }
        .chart-shell { margin:16px 0 }
        .chart-shell figcaption { font-weight:700; margin-bottom:8px }
        .chart-guidance { color:#aebed0; margin:0 0 8px }
        .chart-scroll { overflow-x:auto; overscroll-behavior-inline:contain }
        .chart-frame { position:relative; min-width:820px; height:480px }
        .chart-fallback { color:#aebed0 }
        .chart-shell[data-chart-ready="true"] .chart-fallback { display:none }
        .note { color:#aebed0 }
        .pill { display:inline-block; background:#29384b; padding:4px 8px; border-radius:4px; margin:2px }
        .table-wrap { overflow-x:auto; -webkit-overflow-scrolling:touch }
        .trend-explorer { min-width:0 }
        .trend-controls { display:flex; flex-wrap:wrap; gap:4px; margin-bottom:16px }
        .trend-option { cursor:pointer; border:1px solid #304052; border-radius:4px; padding:8px 10px; background:#29384b; color:#e8edf5 }
        .trend-option[aria-pressed=true] { background:#55b6e8; color:#10151f; font-weight:700 }
        .trend-option:focus-visible { outline:3px solid #f2d05c; outline-offset:2px }
        .history-run h3 { margin-block:22px 8px }
        .info { position:relative; vertical-align:middle; border:1px solid #55b6e8; border-radius:50%; background:transparent; color:#e8edf5; font-weight:700; inline-size:1.4em; block-size:1.4em; padding:0 }
        .tooltip { display:none; position:absolute; z-index:3; inset-inline-start:0; inset-block-start:calc(100% + 6px); width:min(320px,75vw); padding:8px; border-radius:4px; background:#080c12; color:#e8edf5; text-align:left; font:13px system-ui,sans-serif }
        .info:hover .tooltip,.info:focus .tooltip { display:block }
        @media(max-width:700px) {
            main { padding:16px }
            .report-nav { margin-inline:-16px; padding:12px 16px 16px; flex-wrap:nowrap; overflow-x:auto }
            .report-nav a { flex:none; white-space:nowrap }
            table { min-width:640px }
            .tooltip { position:fixed; z-index:4; inset:16px; box-sizing:border-box; width:auto; max-height:calc(100vh - 32px); overflow:auto }
        }
    </style>
</head>
<body><a class="skip-link" href="#overview">Skip to report content</a><main id="content">
    <h1>SpinyGUI Local Benchmark Report</h1>
    <p class="note">Informational local results. Compare only equivalent hardware, drivers, operating systems, and Java runtimes.</p>
    <nav class="report-nav" aria-label="Benchmark report sections"><a href="#overview">Overview</a><a href="#cpu">CPU</a><a href="#rendering">Rendering</a><a href="#history">History</a><a href="#methodology">Methodology</a></nav>
    <section id="overview" aria-labelledby="overview-heading">
        <h2 id="overview-heading">Overview</h2>
        <p>Current run: <b>${page.report().currentRunIdentifier()}</b></p>
        <p>Comparability metadata: @for(var entry : page.report().comparability())<span class="pill"><b>${entry.key()}:</b> ${entry.value()}</span>@endfor</p>
        <p>Implementation metadata: @for(var entry : page.report().implementation())<span class="pill"><b>${entry.key()}:</b> ${entry.value()}</span>@endfor</p>
        <section><h2>Hotspots</h2><ul><li>Slowest CPU operation: <b>${page.report().slowestCpuName()}</b> at ${page.report().slowestCpuLatency()} us/op.</li><li>Largest allocation: <b>${page.report().largestAllocationName()}</b> at ${page.report().largestAllocation()} B/op.</li><li>Largest GPU p99: <b>${page.report().largestGpuFragments()} fragments</b> at ${page.report().largestGpuP99()} us; ${page.report().largestGpuBudget120()} of the 120 Hz budget.</li></ul></section>
        <p class="note">Structural validation: <b>${page.report().structuralValidationStatus()}</b> @template.components.help("help-structural-validation", "Structural validation", "Records production text commands and verifies source-bound text, state, and ordering before timing results are reported.")</p>
    </section>
    <section id="cpu" aria-labelledby="cpu-heading">
        <h2 id="cpu-heading">CPU operations @template.components.help("help-latency", "Latency", "Latency is average microseconds per operation; lower values are faster.")</h2>
        <p class="note">Average latency and normalized allocation. Both charts use a logarithmic base-10 scale so smaller workloads remain distinguishable.</p>
        <figure class="chart-shell"><figcaption>CPU operation latency (us/op)</figcaption><p class="chart-guidance">How to read: Lower values are better.</p><div class="chart-scroll"><div class="chart-frame"><canvas id="cpu-latency-chart" role="img" aria-label="Horizontal logarithmic bar chart of CPU operation latency in microseconds per operation."></canvas></div></div><p class="chart-fallback">Interactive chart unavailable. <a href="#cpu-data">Use the precise CPU data table.</a></p></figure>
        <figure class="chart-shell"><figcaption>Normalized CPU allocation (B/op)</figcaption><p class="chart-guidance">How to read: Lower values are better.</p><div class="chart-scroll"><div class="chart-frame"><canvas id="cpu-allocation-chart" role="img" aria-label="Horizontal logarithmic bar chart of normalized CPU allocation in bytes per operation."></canvas></div></div><p class="chart-fallback">Interactive chart unavailable. <a href="#cpu-data">Use the precise CPU data table.</a></p></figure>
        <div id="cpu-data" class="table-wrap"><table><tr><th>Operation / parameters</th><th>Latency / uncertainty @template.components.help("help-uncertainty", "Uncertainty", "JMH score error is the reported confidence interval half-width for the average score.")</th><th>Allocation @template.components.help("help-allocation", "Allocation", "Normalized allocation is bytes allocated per operation; allocation rate is megabytes allocated per second.")</th></tr>@for(var row : page.report().cpuRows())<tr><td>${row.name()} @for(var parameter : row.parameters())<span class="pill"><b>${parameter.key()}:</b> ${parameter.value()}</span>@endfor<br>@for(var entry : row.comparability())<span class="pill"><b>${entry.key()}:</b> ${entry.value()}</span>@endfor</td><td>${row.latency()} +/- ${row.uncertainty()} us/op</td><td>${row.allocation()} B/op; ${row.allocationRate()} MB/sec</td></tr>@endfor</table></div>
    </section>
    <section id="rendering" aria-labelledby="rendering-heading">
        <h2 id="rendering-heading">NanoVG rendering @template.components.help("help-percentiles", "Percentiles", "Median is the middle sample; p95 and p99 show the latency not exceeded by 95% and 99% of samples.")</h2>
        <p class="note">Median, p95, and p99 latency. Each chart has a fixed 0-16,667 us (60 Hz) scale; values exceeding it are clipped and explicitly labeled. @template.components.help("help-frame-budgets", "Frame budgets", "60 Hz permits 16,667 us per frame and 120 Hz permits 8,333 us per frame.")</p>
        <figure class="chart-shell"><figcaption>CPU submission latency by rendering scene</figcaption><p class="chart-guidance">How to read: Lower values are better.</p><div class="chart-scroll"><div class="chart-frame"><canvas id="cpu-rendering-chart" role="img" aria-label="Horizontal grouped bar chart of CPU submission median, p95, and p99 latency by rendering scene on a 60 hertz frame-budget scale."></canvas></div></div><p class="chart-fallback">Interactive chart unavailable. <a href="#rendering-data">Use the precise rendering data table.</a></p></figure>
        <figure class="chart-shell"><figcaption>GPU-complete latency by rendering scene</figcaption><p class="chart-guidance">How to read: Lower values are better.</p><div class="chart-scroll"><div class="chart-frame"><canvas id="gpu-rendering-chart" role="img" aria-label="Horizontal grouped bar chart of GPU-complete median, p95, and p99 latency by rendering scene on a 60 hertz frame-budget scale."></canvas></div></div><p class="chart-fallback">Interactive chart unavailable. <a href="#rendering-data">Use the precise rendering data table.</a></p></figure>
        <div id="rendering-data" class="table-wrap"><table><tr><th>Declared scene / observed evidence @template.components.help("help-scene-complexity", "Scene evidence", "The scene name comes from declared-input metadata. Fragments, nodes, code points, glyphs, and runs are per-run observed evidence only; they never identify or fingerprint a series.")</th><th>CPU median/p95/p99 (us)</th><th>GPU median/p95/p99 (us)</th><th>CPU 60/120% @template.components.help("help-cpu-budget", "CPU budget", "CPU submission percentages compare CPU time with the 60 Hz and 120 Hz frame budgets.")</th><th>GPU 60/120% @template.components.help("help-gpu-budget", "GPU budget", "GPU completion percentages compare synchronized GPU time with the 60 Hz and 120 Hz frame budgets.")</th><th>Samples @template.components.help("help-samples", "Samples", "Warmup frames prepare the renderer; measured frames provide the reported latency statistics.")</th></tr>@for(var row : page.report().sceneRows())<tr><td><b>${row.name()}</b><br>@for(var entry : row.evidence())<span class="pill"><b>${entry.key()}:</b> ${entry.value()}</span>@endfor<br>@for(var entry : row.comparability())<span class="pill"><b>${entry.key()}:</b> ${entry.value()}</span>@endfor</td><td>${row.cpuLatency()}</td><td>${row.gpuLatency()}</td><td>${row.cpuBudget()}</td><td>${row.gpuBudget()}</td><td>${row.samples()}</td></tr>@endfor</table></div>
    </section>
    <section id="history" aria-labelledby="history-heading">
        <h2 id="history-heading">Performance history</h2><p class="note">Values are chronological. Signed changes are shown only when every required identity, workload-input, environment, and benchmark-settings fingerprint matches the immediately previous complete run; otherwise the raw values remain visible with a non-comparable reason.</p>
        <h3>Trend viewport</h3><p class="note">Choose a CPU operation or complete rendering-scene identity. Missing runs leave visible gaps; raw tables below remain the precise data view.</p>
        <figure class="chart-shell"><figcaption>Performance history trend</figcaption><p class="chart-guidance">How to read: Lower values are better.</p><div class="chart-scroll"><div class="chart-frame"><canvas id="history-chart" role="img" aria-label="Performance history trend chart. Use the precise history tables below for values."></canvas></div></div><p class="chart-fallback">Interactive chart unavailable. <a href="#history-data">Use the precise history data tables below.</a></p></figure>
        <div class="trend-explorer">
            <div class="trend-controls" role="toolbar" aria-label="Trend metric selector">
                @for(var series : page.report().charts().trends())
                    <button class="trend-option" type="button" data-trend-id="${series.id()}" aria-pressed="${series.id().equals(page.report().charts().trends().getFirst().id())}">${series.label()}</button>
                @endfor
            </div>
        </div>
        <div id="history-data">@for(var run : page.report().history())<section class="history-run" id="run-${run.identifier()}" aria-labelledby="run-heading-${run.identifier()}"><h3 id="run-heading-${run.identifier()}">${run.identifier()}</h3><p>Comparability metadata: @for(var entry : run.comparability())<span class="pill"><b>${entry.key()}:</b> ${entry.value()}</span>@endfor</p><p>Implementation metadata: @for(var entry : run.implementation())<span class="pill"><b>${entry.key()}:</b> ${entry.value()}</span>@endfor</p><div class="table-wrap"><table><tr><th>CPU operation / parameters</th><th>Latency (us/op)</th><th>Change</th><th>Allocation (B/op)</th><th>Change</th></tr>@for(var row : run.cpuRows())<tr><td>${row.name()} @for(var parameter : row.parameters())<span class="pill"><b>${parameter.key()}:</b> ${parameter.value()}</span>@endfor</td><td>${row.latency()}</td><td>${row.latencyChange()}</td><td>${row.allocation()}</td><td>${row.allocationChange()}</td></tr>@endfor</table></div><div class="table-wrap"><table><tr><th>Declared scene / observed evidence</th><th>CPU median/p95/p99</th><th>CPU change</th><th>GPU median/p95/p99</th><th>GPU change</th><th>CPU/GPU 120 Hz</th><th>Budget change</th></tr>@for(var row : run.sceneRows())<tr><td><b>${row.name()}</b><br>@for(var entry : row.evidence())<span class="pill"><b>${entry.key()}:</b> ${entry.value()}</span>@endfor</td><td>${row.cpuLatency()}</td><td>${row.cpuChange()}</td><td>${row.gpuLatency()}</td><td>${row.gpuChange()}</td><td>${row.cpuBudget120()} / ${row.gpuBudget120()}</td><td>${row.cpuBudgetChange()} / ${row.gpuBudgetChange()}</td></tr>@endfor</table></div></section>@endfor</div>
    </section>
    <section id="methodology" aria-labelledby="methodology-heading">
        <h2 id="methodology-heading">Methodology</h2><section><h2>Environment</h2>@for(var entry : page.report().environment())<span class="pill"><b>${entry.key()}:</b> ${entry.value()}</span>@endfor</section>
        <p>CPU results are JMH average-time measurements; rendering results use a hidden context and synchronized GPU completion. Accepted timing/allocation runs use diagnostics-disabled mode and one report-owned CPU/rendering pair. Diagnostics-enabled counter runs are separate evidence and their timing or allocation values are never compared. Valid complete E5 artifacts are retained from their self-describing declared profile, including intentionally versioned workload, schema, identity, and rendering behavior changes. Semantic identity and the identity, workload-input, environment/JVM/driver, settings, and required fingerprints qualify every signed delta; changed profiles remain separate and never receive a cross-profile signed delta. Implementation, build, and commit revisions are traceability metadata and never participate in equality. The current profile reports the corrected 31/30 total exposures; every profile reports each scene's declared alternating warmups plus synchronized validation exposure. Glyph, run, fragment, line, command, and culling counts are observed structural evidence only and never identify or fingerprint a series. All measurements are local and hardware-, operating-system-, JVM-, GPU-, and driver-sensitive; raw values remain visible when comparison is unsafe.</p>
        <h3>Report inputs</h3><span class="pill">reports/text-calculation-&lt;datetime&gt;.json</span><span class="pill">reports/nanovg-text-&lt;datetime&gt;.json</span><p class="note">All CSS and chart graphics are embedded in this file; no external resources are loaded.</p>
    </section>
</main><script>$unsafe{page.chartJs()}</script><script id="benchmark-chart-data" type="application/json">$unsafe{page.chartDataJson()}</script><script>$unsafe{page.chartBootstrap()}</script></body></html>
