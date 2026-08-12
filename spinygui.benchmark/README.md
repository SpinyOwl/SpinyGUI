# SpinyGUI Benchmarks

The versioned declared-input identity contract and current workload inventory are documented in
[`IDENTITY.md`](IDENTITY.md). Equality fingerprints and report delta qualification are documented in
[`COMPARABILITY.md`](COMPARABILITY.md).

Run the CPU text benchmarks with:

```shell
./gradlew :spinygui.benchmark:jmhCpu
```

The task runs only `com.spinyowl.spinygui.benchmark.cpu` benchmarks with three 500 ms warmup
iterations, five 500 ms measurement iterations, and two forks. It writes
`reports/text-calculation-<datetime>.json`. Each JSON entry reports average operation latency
in microseconds and includes JMH's `gc.alloc.rate.norm` secondary metric, the normalized bytes
allocated per operation. A direct invocation is an unpaired investigation artifact and cannot be
selected as an accepted timing baseline. Results are not part of `test` or `check`.
---
Run the NanoVG rendering harness with:

```shell
./gradlew :spinygui.benchmark:jmhRendering
```

It creates a hidden 1280x720 GLFW/OpenGL context on the application main thread and writes
`reports/nanovg-text-<datetime>.json`. CPU submission time ends when `NvgRenderer`
returns; GPU-complete time ends after `glFinish`, so it intentionally measures synchronized GPU
completion rather than presentation. Color and stencil clearing completes before each sample timer
starts. The pair receives 60 alternating warmups: 30 small and 30 large, then 200 measured frames per
scene. Portable rendering correctness is gated by backend structural command fixtures. Optional local
boundary image comparison follows [`LOCAL_IMAGE_COMPARISON.md`](LOCAL_IMAGE_COMPARISON.md) and is not
run implicitly by this task. A direct invocation is an unpaired investigation artifact and
cannot be selected as an accepted timing baseline. Rendering results are hardware- and
driver-sensitive.
---
Run identified structural diagnostics without timers with:

```shell
./gradlew :spinygui.benchmark:counterDiagnostics
```

The task executes the scaled CPU and normal-text/input/textarea scenario matrix once per semantic
variant with diagnostics enabled. CPU scenarios prewarm the exact operation once; each renderer
scenario uses fresh hidden-context, renderer, font, and prepared-scene state, with one predecessor
frame only for unchanged-submission cases. It resets immediately before each recorded operation or
frame, snapshots immediately afterward, and writes `reports/text-diagnostics-<datetime>.json` with
complete semantic IDs, declared inputs, comparability fingerprints, counter vocabulary versions,
counters, and observed structural outputs.
The runner contains no elapsed-time measurement. Its `counter-only-diagnostics-enabled` artifact is
an unpaired investigation and cannot be selected as a timing/allocation baseline. The `jmhCpu`,
`jmhRendering`, and report-owned producer paths explicitly construct disabled diagnostic sessions.
---
Generate a self-contained local report with:

```shell
./gradlew :spinygui.benchmark:benchmarkReport
```

The task owns one complete CPU/rendering pair and runs its CPU producer before its rendering producer,
even with Gradle parallel execution enabled. It refreshes both benchmark JSON reports with one shared
sortable local datetime identifier,
adding a zero-padded numeric suffix only when that datetime is already archived or reserved,
then writes `reports/index.html`. Both artifacts explicitly record paired ownership and the
`timed-allocation-diagnostics-disabled` evidence mode. The archive is machine-local and ignored by
 Git; `clean` does not remove it. The report selects exactly its freshly reserved ID rather than
substituting the newest prior archive pair. Each report-owned producer and the report task are deliberately
non-trackable because their runtime-reserved output paths cannot be safely tracked; they execute freshly.
The report still scans all eligible complete JSON pairs, including intentionally evolved,
self-describing E5 workload/schema/identity/rendering profiles. Such profiles remain distinct raw
historical series and never produce a cross-profile signed delta. It retains raw incomplete,
standalone, counter-mode, mismatched-ID, missing/invalid-comparability, warmup-mismatch, and
pre-correction files without using them as baselines, and includes eligible pairs as history. Delete
unwanted archive files manually
to apply local retention. Gradle atomically reserves identifiers with transient ignored lock files;
normal task completion removes the lock, and a stale lock is ignored by report generation.

Run
`./gradlew :spinygui.benchmark:reserveBenchmarkRunId --configuration-cache` to print a fresh ID
without running benchmarks. The one-file report embeds Chart.js 4.5.1 and report-owned inline
JavaScript, with no CDN or other network resource used, so it can be opened directly from disk
offline. It initializes four overview charts and one reusable history chart while retaining visible
fallback explanations and precise raw tables. Pointer tooltips enhance the charts, and
keyboard-operable metric buttons in a wrapping toolbar select the history series. It is one
continuous page with sticky anchored Overview, CPU, Rendering, History, and Methodology navigation
plus a skip link. History shows every complete archived run and signed changes from the immediately
previous complete run. On narrow screens, only the chart viewport scrolls when required. Rendering
series and chart selectors use declared-input display labels and exact semantic identity. Fragments,
nodes, code points, glyphs, runs, and any reported line/command/cull counts are observed per-run
evidence only: they remain visible in current and historical raw tables but never rename, group, or
fingerprint a series. Missing workloads leave visible line gaps while retaining their global timeline
position; raw chronological tables remain the precise view. Information icons reveal metric help on
hover or keyboard focus. Its
self-contained markup is precompiled from JTE templates during the benchmark module's normal Java
build;

run `./gradlew :spinygui.benchmark:precompileJte` to compile templates directly.
