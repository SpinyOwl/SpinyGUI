@import com.spinyowl.spinygui.benchmark.report.BenchmarkReportPage
@param BenchmarkReportPage page
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>SpinyGUI Local Benchmark Report</title>
    <style>
        html { scroll-behavior:smooth; scroll-padding-top:76px }
        body { background:#10151f; color:#e8edf5; font:15px system-ui,sans-serif; margin:0 }
        main { box-sizing:border-box; max-width:1200px; min-width:0; margin:auto; padding:32px }
        section { box-sizing:border-box; min-width:0; background:#192230; border-radius:8px; padding:20px; margin:18px 0 }
        h1,h2 { margin-top:0 }
        h3 { margin-block:18px 8px }
        p { line-height:1.5 }
        table { border-collapse:collapse; width:100% }
        caption { color:#aebed0; font-weight:700; padding:0 0 10px; text-align:left }
        th,td { padding:8px; text-align:left; border-bottom:1px solid #304052; vertical-align:top }
        .skip-link { position:absolute; inset-inline-start:16px; inset-block-start:-48px; background:#55b6e8; color:#10151f; padding:8px; z-index:4 }
        .skip-link:focus { inset-block-start:16px }
        .report-nav { position:sticky; top:0; z-index:2; display:flex; flex-wrap:wrap; gap:8px; padding:12px 0; background:#10151f }
        .report-nav a { border:1px solid #55b6e8; border-radius:4px; color:#e8edf5; padding:7px 10px; text-decoration:none }
        .report-nav a:hover,.report-nav a:focus-visible,.report-nav a[aria-current="location"] { background:#55b6e8; color:#10151f; outline:3px solid #f2d05c; outline-offset:2px }
        .summary-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(210px,1fr)); gap:12px; margin:16px 0 }
        .summary-card { min-width:0; border:1px solid #304052; border-radius:6px; background:#111a26; padding:14px }
        .summary-label { display:block; color:#aebed0; font-size:13px; margin-bottom:6px }
        .summary-value { display:block; overflow-wrap:anywhere; font-size:17px; font-weight:700 }
        .summary-detail { display:block; color:#aebed0; margin-top:4px }
        .status-line { border-inline-start:4px solid #55b6e8; background:#111a26; padding:10px 12px }
        details { min-width:0; border:1px solid #304052; border-radius:6px; margin:14px 0; padding:10px 12px }
        summary { cursor:pointer; font-weight:700 }
        summary:focus-visible { outline:3px solid #f2d05c; outline-offset:3px }
        .metadata-grid { display:grid; grid-template-columns:minmax(140px,max-content) minmax(0,1fr); gap:8px 12px; margin:14px 0 0 }
        .metadata-grid dt { font-weight:700 }
        .metadata-grid dd { min-width:0; margin:0; overflow-wrap:anywhere }
        .chart-shell { margin:20px 0 }
        .chart-shell figcaption { font-weight:700; margin-bottom:8px }
        .chart-guidance { color:#aebed0; margin:0 0 8px }
        .chart-scroll { max-width:100%; overflow-x:auto; overscroll-behavior-inline:contain }
        .chart-frame { position:relative; min-width:820px; height:480px }
        .chart-fallback { color:#aebed0 }
        .chart-shell[data-chart-ready="true"] .chart-fallback { display:none }
        .note { color:#aebed0 }
        .pill { display:inline-block; box-sizing:border-box; max-width:100%; overflow-wrap:anywhere; vertical-align:top; background:#29384b; padding:4px 8px; border-radius:4px; margin:2px }
        .table-wrap { max-width:100%; overflow-x:auto; -webkit-overflow-scrolling:touch }
        .trend-explorer { min-width:0; margin:16px 0 }
        .trend-label { display:block; font-weight:700; margin-bottom:6px }
        .trend-select { box-sizing:border-box; width:min(100%,560px); border:1px solid #55b6e8; border-radius:4px; padding:9px 10px; background:#29384b; color:#e8edf5 }
        .trend-select:focus-visible { outline:3px solid #f2d05c; outline-offset:2px }
        .empty-state { border:1px dashed #55b6e8; border-radius:6px; background:#111a26; padding:18px; margin:16px 0 }
        .empty-state h3 { margin-top:0 }
        .history-run h3 { margin-block:22px 8px }
        .archive-status-included { color:#8fd6ad }
        .archive-status-excluded { color:#ff9a93 }
        .archive-status-separate-evidence { color:#f2d05c }
        .info { position:relative; vertical-align:middle; border:1px solid #55b6e8; border-radius:50%; background:transparent; color:#e8edf5; font-weight:700; inline-size:1.4em; block-size:1.4em; padding:0 }
        .tooltip { display:none; position:absolute; z-index:3; inset-inline-start:0; inset-block-start:calc(100% + 6px); width:min(320px,75vw); padding:8px; border-radius:4px; background:#080c12; color:#e8edf5; text-align:left; font:13px system-ui,sans-serif }
        .info:hover .tooltip,.info:focus .tooltip { display:block }
        @media(max-width:700px) {
            main { padding:16px }
            section { padding:16px }
            .report-nav { margin-inline:-16px; padding:12px 16px 10px; flex-wrap:nowrap; overflow-x:auto; scrollbar-width:thin }
            .report-nav::-webkit-scrollbar { height:6px }
            .report-nav::-webkit-scrollbar-thumb { background:#55b6e8; border-radius:3px }
            .report-nav a { flex:none; white-space:nowrap }
            .summary-grid { grid-template-columns:1fr }
            .metadata-grid { grid-template-columns:minmax(0,1fr) }
            .metadata-grid dd { margin-bottom:8px }
            .chart-frame { min-width:720px; height:440px }
            table { min-width:640px }
            .tooltip { position:fixed; z-index:4; inset:16px; box-sizing:border-box; width:auto; max-height:calc(100vh - 32px); overflow:auto }
        }
    </style>
</head>
<body><a class="skip-link" href="#overview">Skip to report content</a><main id="content">
    <h1>SpinyGUI Local Benchmark Report</h1>
    <p class="note">Informational local results. Compare only equivalent hardware, drivers, operating systems, and Java runtimes.</p>
    <nav class="report-nav" aria-label="Benchmark report sections"><a href="#overview" aria-current="location">Overview</a><a href="#cpu">CPU</a><a href="#rendering">Rendering</a><a href="#history">History</a><a href="#methodology">Methodology</a></nav>
    <section id="overview" aria-labelledby="overview-heading">
        <h2 id="overview-heading">Overview</h2>
        <p class="status-line"><b>${page.report().evidenceStatus()}</b> Structural validation: ${page.report().structuralValidationStatus()} @template.components.help("help-structural-validation", "Structural validation", "Records production text commands and verifies source-bound text, state, and ordering before timing results are reported.")</p>
        <div class="summary-grid">
            <div class="summary-card"><span class="summary-label">Current run</span><span class="summary-value">${page.report().currentRunIdentifier()}</span><span class="summary-detail">Build: ${page.report().buildStatus()}</span></div>
            <div class="summary-card"><span class="summary-label">Eligible archive runs</span><span class="summary-value">${page.report().archiveHealth().eligibleRunCount()}</span><span class="summary-detail">${page.report().archiveHealth().excludedTimingArtifactCount()} excluded timing artifacts</span></div>
            <div class="summary-card"><span class="summary-label">Slowest CPU operation</span><span class="summary-value">${page.report().slowestCpuLatency()} us/op</span><span class="summary-detail">${page.report().slowestCpuName()}</span></div>
            <div class="summary-card"><span class="summary-label">Largest allocation</span><span class="summary-value">${page.report().largestAllocation()} B/op</span><span class="summary-detail">${page.report().largestAllocationName()}</span></div>
            <div class="summary-card"><span class="summary-label">Largest GPU p99</span><span class="summary-value">${page.report().largestGpuP99()} us</span><span class="summary-detail">${page.report().largestGpuBudget120()} of the 120 Hz budget; ${page.report().largestGpuFragments()} fragments</span></div>
        </div>
        <details><summary>Technical evidence and fingerprints</summary>
            <h3>Comparability metadata</h3><dl class="metadata-grid">@for(var entry : page.report().comparability())<dt>${entry.key()}</dt><dd>${entry.value()}</dd>@endfor</dl>
            <h3>Implementation metadata</h3><dl class="metadata-grid">@for(var entry : page.report().implementation())<dt>${entry.key()}</dt><dd>${entry.value()}</dd>@endfor</dl>
        </details>
        <details><summary>Archive status: ${page.report().archiveHealth().eligibleRunCount()} eligible runs, ${page.report().archiveHealth().excludedTimingArtifactCount()} excluded timing artifacts, ${page.report().archiveHealth().diagnosticArtifactCount()} diagnostic artifacts</summary>
            @if(page.report().archiveHealth().artifacts().isEmpty())
                <p class="note">This report was generated from direct inputs, so no archive scan was available.</p>
            @else
                <div class="table-wrap"><table><caption>Archive artifacts and report eligibility</caption><thead><tr><th scope="col">File</th><th scope="col">Kind</th><th scope="col">Status</th><th scope="col">Reason</th></tr></thead><tbody>@for(var artifact : page.report().archiveHealth().artifacts())<tr><td>${artifact.fileName()}</td><td>${artifact.kind()}</td><td class="archive-status-${artifact.status()}">${artifact.status()}</td><td>${artifact.reason()}</td></tr>@endfor</tbody></table></div>
            @endif
        </details>
    </section>
    <section id="cpu" aria-labelledby="cpu-heading">
        <h2 id="cpu-heading">CPU operations @template.components.help("help-latency", "Latency", "Latency is average microseconds per operation; lower values are faster.")</h2>
        <p class="note">Average latency and normalized allocation. Both charts use a logarithmic base-10 scale so smaller workloads remain distinguishable.</p>
        <figure class="chart-shell"><figcaption>CPU operation latency (us/op)</figcaption><p class="chart-guidance">How to read: Lower values are better.</p><div class="chart-scroll"><div class="chart-frame"><canvas id="cpu-latency-chart" role="img" aria-label="Horizontal logarithmic bar chart of CPU operation latency in microseconds per operation."></canvas></div></div><p class="chart-fallback">Interactive chart unavailable. <a href="#cpu-data">Use the precise CPU data table.</a></p></figure>
        <figure class="chart-shell"><figcaption>Normalized CPU allocation (B/op)</figcaption><p class="chart-guidance">How to read: Lower values are better.</p><div class="chart-scroll"><div class="chart-frame"><canvas id="cpu-allocation-chart" role="img" aria-label="Horizontal logarithmic bar chart of normalized CPU allocation in bytes per operation."></canvas></div></div><p class="chart-fallback">Interactive chart unavailable. <a href="#cpu-data">Use the precise CPU data table.</a></p></figure>
        <div id="cpu-data" class="table-wrap"><table><caption>Precise CPU benchmark results</caption><thead><tr><th scope="col">Operation / parameters</th><th scope="col">Latency / uncertainty @template.components.help("help-uncertainty", "Uncertainty", "JMH score error is the reported confidence interval half-width for the average score.")</th><th scope="col">Allocation @template.components.help("help-allocation", "Allocation", "Normalized allocation is bytes allocated per operation; allocation rate is megabytes allocated per second.")</th></tr></thead><tbody>@for(var row : page.report().cpuRows())<tr><th scope="row">${row.name()} @for(var parameter : row.parameters())<span class="pill"><b>${parameter.key()}:</b> ${parameter.value()}</span>@endfor<br><details><summary>Identity details</summary><dl class="metadata-grid">@for(var entry : row.comparability())<dt>${entry.key()}</dt><dd>${entry.value()}</dd>@endfor</dl></details></th><td>${row.latency()} +/- ${row.uncertainty()} us/op</td><td>${row.allocation()} B/op; ${row.allocationRate()} MB/sec</td></tr>@endfor</tbody></table></div>
    </section>
    <section id="rendering" aria-labelledby="rendering-heading">
        <h2 id="rendering-heading">NanoVG rendering @template.components.help("help-percentiles", "Percentiles", "Median is the middle sample; p95 and p99 show the latency not exceeded by 95% and 99% of samples.")</h2>
        <p class="note">Median, p95, and p99 latency. The dashed markers are labeled directly at the 120 Hz and 60 Hz frame budgets; values above 60 Hz are clipped and highlighted. @template.components.help("help-frame-budgets", "Frame budgets", "60 Hz permits 16,667 us per frame and 120 Hz permits 8,333 us per frame.")</p>
        <figure class="chart-shell"><figcaption>CPU submission latency by rendering scene</figcaption><p class="chart-guidance">How to read: Lower values are better.</p><div class="chart-scroll"><div class="chart-frame"><canvas id="cpu-rendering-chart" role="img" aria-label="Horizontal grouped bar chart of CPU submission median, p95, and p99 latency by rendering scene on a 60 hertz frame-budget scale."></canvas></div></div><p class="chart-fallback">Interactive chart unavailable. <a href="#rendering-data">Use the precise rendering data table.</a></p></figure>
        <figure class="chart-shell"><figcaption>GPU-complete latency by rendering scene</figcaption><p class="chart-guidance">How to read: Lower values are better.</p><div class="chart-scroll"><div class="chart-frame"><canvas id="gpu-rendering-chart" role="img" aria-label="Horizontal grouped bar chart of GPU-complete median, p95, and p99 latency by rendering scene on a 60 hertz frame-budget scale."></canvas></div></div><p class="chart-fallback">Interactive chart unavailable. <a href="#rendering-data">Use the precise rendering data table.</a></p></figure>
        <div id="rendering-data" class="table-wrap"><table><caption>Precise NanoVG rendering results</caption><thead><tr><th scope="col">Declared scene / observed evidence @template.components.help("help-scene-complexity", "Scene evidence", "The scene name comes from declared-input metadata. Fragments, nodes, code points, glyphs, and runs are per-run observed evidence only; they never identify or fingerprint a series.")</th><th scope="col">CPU median/p95/p99 (us)</th><th scope="col">GPU median/p95/p99 (us)</th><th scope="col">CPU 60/120% @template.components.help("help-cpu-budget", "CPU budget", "CPU submission percentages compare CPU time with the 60 Hz and 120 Hz frame budgets.")</th><th scope="col">GPU 60/120% @template.components.help("help-gpu-budget", "GPU budget", "GPU completion percentages compare synchronized GPU time with the 60 Hz and 120 Hz frame budgets.")</th><th scope="col">Samples @template.components.help("help-samples", "Samples", "Warmup frames prepare the renderer; measured frames provide the reported latency statistics.")</th></tr></thead><tbody>@for(var row : page.report().sceneRows())<tr><th scope="row"><b>${row.name()}</b><br>@for(var entry : row.evidence())<span class="pill"><b>${entry.key()}:</b> ${entry.value()}</span>@endfor<details><summary>Identity details</summary><dl class="metadata-grid">@for(var entry : row.comparability())<dt>${entry.key()}</dt><dd>${entry.value()}</dd>@endfor</dl></details></th><td>${row.cpuLatency()}</td><td>${row.gpuLatency()}</td><td>${row.cpuBudget()}</td><td>${row.gpuBudget()}</td><td>${row.samples()}</td></tr>@endfor</tbody></table></div>
    </section>
    <section id="history" aria-labelledby="history-heading">
        <h2 id="history-heading">Performance history</h2><p class="note">Values are chronological. Signed changes appear only when identity, workload, environment, and settings fingerprints match the immediately previous complete run.</p>
        @if(page.report().history().size() < 2)
            <div class="empty-state"><h3>No trend yet</h3><p>One eligible run is available. Generate one more comparable CPU/rendering pair to see a trend; the current values remain available in the table below.</p></div>
        @else
            <h3>Trend viewport</h3><p class="note">Choose a CPU operation or rendering scene. Missing runs leave gaps; the tables below remain the precise data view.</p>
            <div class="trend-explorer"><label class="trend-label" for="trend-select">Metric</label><select class="trend-select" id="trend-select">@for(var series : page.report().charts().trends())@if(series.id().equals(page.report().charts().trends().getFirst().id()))<option value="${series.id()}" selected>${series.label()}</option>@else<option value="${series.id()}">${series.label()}</option>@endif@endfor</select></div>
            <figure class="chart-shell"><figcaption>Performance history trend</figcaption><p class="chart-guidance">How to read: Lower values are better.</p><div class="chart-scroll"><div class="chart-frame"><canvas id="history-chart" role="img" aria-label="Performance history trend chart. Use the precise history tables below for values."></canvas></div></div><p class="chart-fallback">Interactive chart unavailable. <a href="#history-data">Use the precise history data tables below.</a></p></figure>
        @endif
        <div id="history-data">@for(var run : page.report().history())<section class="history-run" id="run-${run.identifier()}" aria-labelledby="run-heading-${run.identifier()}"><h3 id="run-heading-${run.identifier()}">${run.identifier()}</h3><details><summary>Run evidence and revisions</summary><h4>Comparability metadata</h4><dl class="metadata-grid">@for(var entry : run.comparability())<dt>${entry.key()}</dt><dd>${entry.value()}</dd>@endfor</dl><h4>Implementation metadata</h4><dl class="metadata-grid">@for(var entry : run.implementation())<dt>${entry.key()}</dt><dd>${entry.value()}</dd>@endfor</dl></details><div class="table-wrap"><table><caption>CPU history for ${run.identifier()}</caption><thead><tr><th scope="col">CPU operation / parameters</th><th scope="col">Latency (us/op)</th><th scope="col">Change</th><th scope="col">Allocation (B/op)</th><th scope="col">Change</th></tr></thead><tbody>@for(var row : run.cpuRows())<tr><th scope="row">${row.name()} @for(var parameter : row.parameters())<span class="pill"><b>${parameter.key()}:</b> ${parameter.value()}</span>@endfor</th><td>${row.latency()}</td><td>${row.latencyChange()}</td><td>${row.allocation()}</td><td>${row.allocationChange()}</td></tr>@endfor</tbody></table></div><div class="table-wrap"><table><caption>Rendering history for ${run.identifier()}</caption><thead><tr><th scope="col">Declared scene / observed evidence</th><th scope="col">CPU median/p95/p99</th><th scope="col">CPU change</th><th scope="col">GPU median/p95/p99</th><th scope="col">GPU change</th><th scope="col">CPU/GPU 120 Hz</th><th scope="col">Budget change</th></tr></thead><tbody>@for(var row : run.sceneRows())<tr><th scope="row"><b>${row.name()}</b><br>@for(var entry : row.evidence())<span class="pill"><b>${entry.key()}:</b> ${entry.value()}</span>@endfor</th><td>${row.cpuLatency()}</td><td>${row.cpuChange()}</td><td>${row.gpuLatency()}</td><td>${row.gpuChange()}</td><td>${row.cpuBudget120()} / ${row.gpuBudget120()}</td><td>${row.cpuBudgetChange()} / ${row.gpuBudgetChange()}</td></tr>@endfor</tbody></table></div></section>@endfor</div>
    </section>
    <section id="methodology" aria-labelledby="methodology-heading">
        <h2 id="methodology-heading">Methodology</h2><section><h3>Environment</h3>@for(var entry : page.report().environment())<span class="pill"><b>${entry.key()}:</b> ${entry.value()}</span>@endfor</section>
        <h3>Measurement</h3><p>CPU results are JMH average-time measurements. Rendering uses a hidden context and synchronized GPU completion. Accepted timing/allocation runs use diagnostics-disabled mode and one report-owned CPU/rendering pair.</p>
        <h3>Comparison rules</h3><p>Identity, workload-input, environment/JVM/driver, settings, and required fingerprints qualify every signed delta. Changed profiles remain separate and never receive a cross-profile delta. Implementation revisions are traceability metadata only.</p>
        <h3>Evidence limits</h3><p>Diagnostics-enabled counter runs are separate evidence. Glyph, run, fragment, line, command, and culling counts describe observed structure and never identify a series. All results remain hardware-, operating-system-, JVM-, GPU-, and driver-sensitive.</p>
        <h3>Report inputs</h3><span class="pill">reports/text-calculation-&lt;datetime&gt;.json</span><span class="pill">reports/nanovg-text-&lt;datetime&gt;.json</span><span class="pill">reports/report-manifest.json</span><p class="note">All CSS and chart graphics are embedded in this file; no external resources are loaded.</p>
    </section>
</main><script>$unsafe{page.chartJs()}</script><script id="benchmark-chart-data" type="application/json">$unsafe{page.chartDataJson()}</script><script>$unsafe{page.chartBootstrap()}</script></body></html>
