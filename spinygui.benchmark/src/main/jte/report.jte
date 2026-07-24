@import com.spinyowl.spinygui.benchmark.report.BenchmarkReportView
@param BenchmarkReportView model
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
        .chart { display:grid; gap:8px }
        .row { display:grid; grid-template-columns:220px 1fr 190px; gap:10px; align-items:center }
        .track { position:relative; background:#29384b; border-radius:4px; overflow:hidden }
        .bar { height:18px; background:#55b6e8 }
        .alloc { background:#e8a855 }
        .gpu { background:#b27ce8 }
        .budget-marker { position:absolute; z-index:1; top:0; height:100%; border-left:2px dashed #f2d05c }
        .marker-120 { left:50% }
        .marker-60 { right:0 }
        .budget-legend { display:flex; gap:16px; margin:8px 0 }
        .budget-legend span { border-left:2px dashed #f2d05c; padding-left:5px }
        .over-budget { background:#e85c55 }
        .note { color:#aebed0 }
        .pill { display:inline-block; background:#29384b; padding:4px 8px; border-radius:4px; margin:2px }
        .table-wrap { overflow-x:auto; -webkit-overflow-scrolling:touch }
        .trend-explorer { display:grid; grid-template-columns:260px minmax(0,1fr); gap:16px; align-items:start }
        .trend-select { position:absolute; inline-size:1px; block-size:1px; opacity:0 }
        .trend-option { grid-column:1; order:1; cursor:pointer; border:1px solid #304052; border-radius:4px; padding:10px; color:#e8edf5 }
        .trend-select:focus-visible + .trend-option { outline:3px solid #f2d05c; outline-offset:2px }
        .trend-select:checked + .trend-option { background:#55b6e8; color:#10151f; font-weight:700 }
        .trend-panel { display:none; grid-column:2; grid-row:1; order:2; min-width:0; max-width:100%; overflow-x:auto; overscroll-behavior-inline:contain }
        .trend-select:checked + .trend-option + .trend-panel { display:block }
        .trend-chart { margin:0; padding:16px; border:1px solid #304052; border-radius:4px; min-width:820px; font-size:18px }
        .trend-chart svg { display:block; width:100%; height:auto; margin-top:8px }
        .trend-grid { fill:none; stroke:#304052; stroke-width:2 }
        .trend-line { fill:none; stroke:#55b6e8; stroke-width:6; stroke-linejoin:round; stroke-linecap:round }
        .trend-point { fill:#f2d05c; stroke:#10151f; stroke-width:3 }
        .trend-point:focus { outline:none; stroke:#e8edf5; stroke-width:6 }
        .trend-label { fill:#aebed0; font-size:28px }
        .history-run h3 { margin-block:22px 8px }
        .info { position:relative; vertical-align:middle; border:1px solid #55b6e8; border-radius:50%; background:transparent; color:#e8edf5; font-weight:700; inline-size:1.4em; block-size:1.4em; padding:0 }
        .tooltip { display:none; position:absolute; z-index:3; inset-inline-start:0; inset-block-start:calc(100% + 6px); width:min(320px,75vw); padding:8px; border-radius:4px; background:#080c12; color:#e8edf5; text-align:left; font:13px system-ui,sans-serif }
        .info:hover .tooltip,.info:focus .tooltip { display:block }
        @media(min-width:701px) and (max-width:1100px) {
            .trend-explorer { grid-template-columns:minmax(180px,260px) minmax(0,1fr) }
        }
        @media(max-width:700px) {
            main { padding:16px }
            .report-nav { margin-inline:-16px; padding:12px 16px 16px; flex-wrap:nowrap; overflow-x:auto }
            .report-nav a { flex:none; white-space:nowrap }
            .row { grid-template-columns:minmax(0,1fr) auto; gap:6px }
            .row>span:first-child { min-width:0; overflow-wrap:anywhere }
            .row>span:last-child { min-width:0; max-width:45vw; overflow-wrap:anywhere; text-align:right }
            .row .track { grid-column:1/-1; grid-row:2 }
            .budget-legend { flex-direction:column; gap:6px }
            table { min-width:640px }
            .tooltip { position:fixed; z-index:4; inset:16px; box-sizing:border-box; width:auto; max-height:calc(100vh - 32px); overflow:auto }
            .trend-explorer { grid-template-columns:1fr }
            .trend-option { grid-column:1 }
            .trend-panel { grid-column:1; grid-row:auto }
        }
    </style>
</head>
<body><a class="skip-link" href="#overview">Skip to report content</a><main id="content">
    <h1>SpinyGUI Local Benchmark Report</h1>
    <p class="note">Informational local results. Compare only equivalent hardware, drivers, operating systems, and Java runtimes.</p>
    <nav class="report-nav" aria-label="Benchmark report sections"><a href="#overview">Overview</a><a href="#cpu">CPU</a><a href="#rendering">Rendering</a><a href="#history">History</a><a href="#methodology">Methodology</a></nav>
    <section id="overview" aria-labelledby="overview-heading">
        <h2 id="overview-heading">Overview</h2>
        <p>Current run: <b>${model.currentRunIdentifier()}</b></p>
        <section><h2>Hotspots</h2><ul><li>Slowest CPU operation: <b>${model.slowestCpuName()}</b> at ${model.slowestCpuLatency()} us/op.</li><li>Largest allocation: <b>${model.largestAllocationName()}</b> at ${model.largestAllocation()} B/op.</li><li>Largest GPU p99: <b>${model.largestGpuFragments()} fragments</b> at ${model.largestGpuP99()} us; ${model.largestGpuBudget120()} of the 120 Hz budget.</li></ul></section>
        <p class="note">Pixel validation: <b>@if(model.pixelValidationPassed())passed@else failed@endif</b> @template.components.help("help-pixel-validation", "Pixel validation", "Checks that the rendered pixels match the expected output before timing results are reported.")</p>
    </section>
    <section id="cpu" aria-labelledby="cpu-heading">
        <h2 id="cpu-heading">CPU operations @template.components.help("help-latency", "Latency", "Latency is average microseconds per operation; lower values are faster.")</h2>
        <p class="note">Average latency and normalized allocation. Both charts use a logarithmic base-10 scale so smaller workloads remain distinguishable.</p>
        <h3>Latency (us/op)</h3><p class="note">Logarithmic scale (base 10; 5% to 100% of the positive data range).</p><div class="chart">@for(var row : model.cpuRows())<div class="row"><span>${row.name()}</span><div class="track"><div class="bar" style="width:${row.latencyWidth()}%"></div></div><span>${row.latency()}</span></div>@endfor</div>
        <h3>Normalized allocation (B/op)</h3><div class="chart">@for(var row : model.cpuRows())<div class="row"><span>${row.name()}</span><div class="track"><div class="bar alloc" style="width:${row.allocationWidth()}%"></div></div><span>${row.allocation()}</span></div>@endfor</div>
        <div class="table-wrap"><table><tr><th>Operation</th><th>Latency / uncertainty @template.components.help("help-uncertainty", "Uncertainty", "JMH score error is the reported confidence interval half-width for the average score.")</th><th>Allocation @template.components.help("help-allocation", "Allocation", "Normalized allocation is bytes allocated per operation; allocation rate is megabytes allocated per second.")</th></tr>@for(var row : model.cpuRows())<tr><td>${row.name()}</td><td>${row.latency()} +/- ${row.uncertainty()} us/op</td><td>${row.allocation()} B/op; ${row.allocationRate()} MB/sec</td></tr>@endfor</table></div>
    </section>
    <section id="rendering" aria-labelledby="rendering-heading">
        <h2 id="rendering-heading">NanoVG rendering @template.components.help("help-percentiles", "Percentiles", "Median is the middle sample; p95 and p99 show the latency not exceeded by 95% and 99% of samples.")</h2>
        <p class="note">Median, p95, and p99 latency. Each chart has a fixed 0-16,667 us (60 Hz) scale; values exceeding it are clipped and explicitly labeled. @template.components.help("help-frame-budgets", "Frame budgets", "60 Hz permits 16,667 us per frame and 120 Hz permits 8,333 us per frame.")</p>
        <h3>CPU submission latency</h3><div class="budget-legend"><span>120 Hz marker: 8,333 us</span><span>60 Hz marker: 16,667 us</span></div><div class="chart">@for(var row : model.cpuChartRows())@template.components.chartRow(row)@endfor</div>
        <h3>GPU-complete latency</h3><div class="budget-legend"><span>120 Hz marker: 8,333 us</span><span>60 Hz marker: 16,667 us</span></div><div class="chart">@for(var row : model.gpuChartRows())@template.components.chartRow(row)@endfor</div>
        <div class="table-wrap"><table><tr><th>Scene complexity @template.components.help("help-scene-complexity", "Scene complexity", "Fragments, code points, glyphs, and runs describe the amount of text resolved and drawn.")</th><th>CPU median/p95/p99 (us)</th><th>GPU median/p95/p99 (us)</th><th>CPU 60/120% @template.components.help("help-cpu-budget", "CPU budget", "CPU submission percentages compare CPU time with the 60 Hz and 120 Hz frame budgets.")</th><th>GPU 60/120% @template.components.help("help-gpu-budget", "GPU budget", "GPU completion percentages compare synchronized GPU time with the 60 Hz and 120 Hz frame budgets.")</th><th>Samples @template.components.help("help-samples", "Samples", "Warmup frames prepare the renderer; measured frames provide the reported latency statistics.")</th></tr>@for(var row : model.sceneRows())<tr><td>${row.fragments()}; ${row.complexity()}</td><td>${row.cpuLatency()}</td><td>${row.gpuLatency()}</td><td>${row.cpuBudget()}</td><td>${row.gpuBudget()}</td><td>${row.samples()}</td></tr>@endfor</table></div>
    </section>
    <section id="history" aria-labelledby="history-heading">
        <h2 id="history-heading">Performance history</h2><p class="note">Values are chronological; signed changes compare each complete run with its immediately previous complete run.</p>
        <h3>Trend viewport</h3><p class="note">Choose a CPU operation or complete rendering-scene identity. Missing runs leave visible gaps; raw tables below remain the precise data view.</p><div class="trend-explorer" role="radiogroup" aria-label="Trend metric selector">@for(var series : model.trends())@if(series.id().equals(model.trends().getFirst().id()))<input class="trend-select" type="radio" id="${series.id()}-select" name="trend-metric" checked>@else<input class="trend-select" type="radio" id="${series.id()}-select" name="trend-metric">@endif<label class="trend-option" for="${series.id()}-select">${series.label()}</label><div class="trend-panel">@template.components.trendChart(series)</div>@endfor</div>
        @for(var run : model.history())<section class="history-run" id="run-${run.identifier()}" aria-labelledby="run-heading-${run.identifier()}"><h3 id="run-heading-${run.identifier()}">${run.identifier()}</h3><div class="table-wrap"><table><tr><th>CPU operation</th><th>Latency (us/op)</th><th>Change</th><th>Allocation (B/op)</th><th>Change</th></tr>@for(var row : run.cpuRows())<tr><td>${row.name()}</td><td>${row.latency()}</td><td>${row.latencyChange()}</td><td>${row.allocation()}</td><td>${row.allocationChange()}</td></tr>@endfor</table></div><div class="table-wrap"><table><tr><th>Scene</th><th>CPU median/p95/p99</th><th>CPU change</th><th>GPU median/p95/p99</th><th>GPU change</th><th>CPU/GPU 120 Hz</th><th>Budget change</th></tr>@for(var row : run.sceneRows())<tr><td>${row.fragments()}</td><td>${row.cpuLatency()}</td><td>${row.cpuChange()}</td><td>${row.gpuLatency()}</td><td>${row.gpuChange()}</td><td>${row.cpuBudget120()} / ${row.gpuBudget120()}</td><td>${row.cpuBudgetChange()} / ${row.gpuBudgetChange()}</td></tr>@endfor</table></div></section>@endfor
    </section>
    <section id="methodology" aria-labelledby="methodology-heading">
        <h2 id="methodology-heading">Methodology</h2><section><h2>Environment</h2>@for(var entry : model.environment())<span class="pill"><b>${entry.key()}:</b> ${entry.value()}</span>@endfor</section>
        <p>CPU results are JMH average-time measurements; rendering results use a hidden context and synchronized GPU completion.</p>
        <h3>Report inputs</h3><span class="pill">reports/text-calculation-&lt;datetime&gt;.json</span><span class="pill">reports/nanovg-text-&lt;datetime&gt;.json</span><p class="note">All CSS and chart graphics are embedded in this file; no external resources are loaded.</p>
    </section>
</main></body></html>
