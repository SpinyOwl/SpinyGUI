# SpinyGUI Benchmarks

Run the CPU text benchmarks with:

```shell
./gradlew :spinygui.benchmark:jmhCpu
```

The task runs only `com.spinyowl.spinygui.benchmark.cpu` benchmarks with three 500 ms warmup
iterations, five 500 ms measurement iterations, and two forks. It writes
`reports/text-calculation-<datetime>.json`. Each JSON entry reports average operation latency
in microseconds and includes JMH's `gc.alloc.rate.norm` secondary metric, the normalized bytes
allocated per operation. Results are informational local baselines and are not part of `test` or
`check`.

Run the NanoVG rendering harness with:

```shell
./gradlew :spinygui.benchmark:jmhRendering
```

It creates a hidden 1280x720 GLFW/OpenGL context on the application main thread and writes
`reports/nanovg-text-<datetime>.json`. CPU submission time ends when `NvgRenderer`
returns; GPU-complete time ends after `glFinish`, so it intentionally measures synchronized GPU
completion rather than presentation. Color and stencil clearing completes before each sample timer
starts. Each scene receives 60 warmup frames followed by 200 measured frames. Rendering results
are hardware- and driver-sensitive.

Generate a self-contained local report with:

```shell
./gradlew :spinygui.benchmark:benchmarkReport
```

The task refreshes both benchmark JSON reports with one shared sortable local datetime identifier,
adding a zero-padded numeric suffix only when that datetime is already archived or reserved,
then writes `reports/index.html`. The archive is machine-local and ignored by Git; `clean` does not
remove it. Each report generation scans all complete JSON pairs, retains them for the next report
regeneration, and selects the newest complete pair as current. Delete unwanted archive files manually
to apply local retention. Gradle atomically reserves identifiers with transient ignored lock files;
normal task completion removes the lock, and a stale lock is ignored by report generation. Run
`./gradlew :spinygui.benchmark:reserveBenchmarkRunId --configuration-cache` to print a fresh ID
without running benchmarks. The one-file report embeds Chart.js 4.5.1 and report-owned inline
JavaScript, with no CDN or other network resource used, so it can be opened directly from disk
offline. It initializes four overview charts and one reusable history chart while retaining visible
fallback explanations and precise raw tables. Pointer tooltips enhance the charts, and
keyboard-operable metric buttons in a wrapping toolbar select the history series. It is one
continuous page with sticky anchored Overview, CPU, Rendering, History, and Methodology navigation
plus a skip link. History shows every complete archived run and signed changes from the immediately
previous complete run. On narrow screens, only the chart viewport scrolls when required. Rendering
metrics identify scenes by fragments, nodes, code points, glyphs, and runs. Missing workloads leave
visible line gaps while retaining their global timeline position; raw chronological tables remain
the precise view. Information icons reveal metric help on hover or keyboard focus. Its
self-contained markup is precompiled from JTE templates during the benchmark module's normal Java
build; run `./gradlew :spinygui.benchmark:precompileJte` to compile templates directly.
