# SpinyGUI Benchmarks

This module contains CPU text benchmarks, a NanoVG rendering harness, untimed structural
diagnostics, and a self-contained HTML report.

Run all commands from the repository root. The examples use the Unix/macOS wrapper; in Windows
PowerShell, replace `./gradlew` with `.\gradlew.bat`. The build uses the configured Java 25
toolchain. Rendering tasks also require a desktop environment with a working GLFW/OpenGL driver,
even though their window is hidden.

## Quick start: capture a complete run and generate the report

```shell
./gradlew :spinygui.benchmark:benchmarkReport
```

This is the recommended target for a comparable local benchmark run. It always performs these
stages in order:

1. Runs the CPU JMH benchmarks.
2. Runs the NanoVG rendering benchmark.
3. Generates `spinygui.benchmark/reports/index.html` from the newly captured pair and the eligible
   local history.

The two JSON files in the pair share a reserved run ID:

- `spinygui.benchmark/reports/text-calculation-<run-id>.json`
- `spinygui.benchmark/reports/nanovg-text-<run-id>.json`

Every invocation executes the producers and report generator afresh. The report is a single offline
HTML file: its styles, Chart.js 4.5.1, and report JavaScript are embedded, so it can be opened
directly from disk without a server or network connection.

The `reports/` archive is machine-local and ignored by Git. `clean` does not delete it; remove old
files manually when local retention is no longer useful. The report keeps raw files on disk but uses
only complete, valid, comparable paired runs for its accepted history and signed deltas.

## Gradle targets

| Target | Use it for | Output |
| --- | --- | --- |
| `benchmarkReport` | Recommended complete CPU/rendering run and HTML report | Paired CPU/rendering JSON plus `reports/index.html` |
| `jmhCpu` | Standalone CPU timing/allocation investigation | `reports/text-calculation-<run-id>.json` |
| `jmhRendering` | Standalone NanoVG rendering investigation | `reports/nanovg-text-<run-id>.json` |
| `counterDiagnostics` | Untimed CPU and renderer structural counters | `reports/text-diagnostics-<run-id>.json` |
| `localImageComparison` | Optional, environment-specific renderer image validation | `build/local-image-comparison/` |
| `reserveBenchmarkRunId` | Print and briefly reserve a fresh archive ID without running benchmarks | Console output only |
| `precompileJte` | Compile report templates as a build/debugging step | Generated classes under `build/` |

List the registered tasks and their Gradle descriptions with:

```shell
./gradlew :spinygui.benchmark:tasks --all
```

`benchmarkReportCpu` and `benchmarkReportRendering` also appear in that list. They are ordered,
report-owned producer stages used by `benchmarkReport`; normally, do not invoke them directly.
There is currently no report-only target that regenerates `index.html` without capturing a fresh
paired run.

Timed benchmarks and optional image comparison are deliberately not part of `test` or `check`.
Run the benchmark module's automated tests separately with:

```shell
./gradlew :spinygui.benchmark:test
```

## Run standalone CPU benchmarks

```shell
./gradlew :spinygui.benchmark:jmhCpu
```

The task selects only `com.spinyowl.spinygui.benchmark.cpu` benchmarks. It uses three 500 ms warmup
iterations, five 500 ms measurement iterations, and two forks. Each JSON result reports average
operation latency in microseconds and JMH's `gc.alloc.rate.norm` secondary metric in bytes allocated
per operation.

A direct `jmhCpu` invocation is marked as an unpaired investigation. Its raw data remains available,
but it cannot become an accepted report baseline by being combined with a separate rendering run.
Use `benchmarkReport` when you need a comparable pair.

## Run the standalone rendering benchmark

```shell
./gradlew :spinygui.benchmark:jmhRendering
```

The harness creates a hidden 1280 x 720 GLFW/OpenGL context on the application main thread. The
scene pair receives 60 alternating warmups (30 small and 30 large), followed by 200 measured frames
per scene. CPU submission timing ends when `NvgRenderer` returns; GPU-complete timing ends after
`glFinish`. Color and stencil clearing completes before each sample timer starts.

Rendering results are hardware- and driver-sensitive. Portable correctness is gated by structural
command fixtures. A direct `jmhRendering` invocation is an unpaired investigation and is not eligible
as an accepted report baseline; use `benchmarkReport` for paired evidence.

## Run untimed counter diagnostics

```shell
./gradlew :spinygui.benchmark:counterDiagnostics
```

This task runs the identified scaled CPU and normal-text/input/textarea scenario matrix once per
semantic variant with diagnostics enabled. It records declared inputs, comparability fingerprints,
versioned counters, and observed structural outputs. The runner contains no elapsed-time
measurement, so its output is investigation evidence rather than a timing or allocation baseline.

## Run optional local image comparison

Image comparison is secondary, environment-specific evidence and does not replace the portable
structural assertions. It is disabled unless explicitly opted in:

```shell
./gradlew -Dspinygui.rendering.localImageComparison=true :spinygui.benchmark:localImageComparison
```

The task captures the approved renderer boundary scenes, runs structural recording first, and
compares only compatible references under `spinygui.benchmark/local-image-references/`. Missing or
environment-incompatible references produce an `unvalidated` outcome, not a pass. See
[`LOCAL_IMAGE_COMPARISON.md`](LOCAL_IMAGE_COMPARISON.md) for the reference policy and review workflow.

## Advanced build targets and contracts

Print a fresh sortable run ID without running benchmarks:

```shell
./gradlew :spinygui.benchmark:reserveBenchmarkRunId --configuration-cache
```

Compile the JTE report templates directly when working on report generation:

```shell
./gradlew :spinygui.benchmark:precompileJte
```

Template precompilation is already part of the benchmark module's normal Java build, so it is not
required before `benchmarkReport`.

The versioned declared-input identity and workload inventory are documented in
[`IDENTITY.md`](IDENTITY.md). Equality fingerprints, paired-run eligibility, and delta qualification
are documented in [`COMPARABILITY.md`](COMPARABILITY.md). The checked-in historical snapshot in
[`BASELINE.md`](BASELINE.md) is informational and machine-specific; the local HTML report is the live
view of newly captured results.
