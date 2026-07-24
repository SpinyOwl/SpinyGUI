# Local Text Benchmark Baseline

**Captured:** 2026-07-24. This is a fixed informational, machine-specific snapshot, not a live
view of the local report archive. Compare it only
with runs on equivalent hardware, drivers, operating system, and Java runtime; it is not a
performance gate.

## Environment and Profiles

- Java: Oracle JDK 25.0.3; OS: Windows 11 10.0 (amd64).
- GPU: NVIDIA GeForce GTX 1060 6GB/PCIe/SSE2; OpenGL 4.6.0 NVIDIA 582.66.
- CPU JMH: 2 forks, 3 x 500 ms warmups, 5 x 500 ms measurements, average-time mode, GC profiler,
  and `--enable-native-access=ALL-UNNAMED` in forked JVMs.
- Rendering: hidden 1280x720 GLFW/OpenGL context; color/stencil clear completes before timing;
  60 warmups and 200 measured frames per scene; GPU-complete latency includes `glFinish`.
- Persistent reports: `reports/text-calculation-<datetime>.json`,
  `reports/nanovg-text-<datetime>.json`, and `reports/index.html`. The archive is machine-local,
  ignored by Git, and retained by `clean`; regenerate it with `:spinygui.benchmark:benchmarkReport`.

## CPU Results

Latency is average microseconds per operation; allocation is normalized bytes per operation.

| Benchmark | Latency (us/op) | Allocation (B/op) |
| --- | ---: | ---: |
| Caret near beginning | 0.055 | 144 |
| Caret near end | 487.290 | 230,511 |
| Text-dense inline layout | 393.931 | 930,630 |
| Latin measure | 15.773 | 45,744 |
| Long single-font measure | 37,577.193 | 368,076,629 |
| Missing-glyph measure | 12.429 | 29,656 |
| Mixed CJK measure | 17.805 | 31,688 |
| Supplementary Unicode measure | 8.785 | 19,256 |
| Wrapped paragraph measure | 98.312 | 212,953 |

## Rendering Results

Values are median / p95 / p99 microseconds. Budget use is the median percentage of the frame
budget, with CPU submission listed before synchronized GPU completion.

| Scene | CPU (us) | GPU complete (us) | CPU 60/120 Hz | GPU 60/120 Hz |
| --- | ---: | ---: | ---: | ---: |
| 100 fragments | 477.0 / 609.3 / 661.5 | 855.2 / 1,079.8 / 1,239.9 | 2.86% / 5.72% | 5.13% / 10.26% |
| 1,000 fragments | 4,692.3 / 5,388.0 / 6,005.1 | 6,310.1 / 7,121.4 / 7,524.9 | 28.15% / 56.31% | 37.86% / 75.72% |

The rendering report passed pixel validation. The 100/1,000-fragment scenes contain
3,800/38,000 code points and resolved glyphs, with 300/3,000 resolved runs.

## Observations

- Long single-font measurement is the dominant CPU and allocation hotspot; profile resolved-run
  construction before considering any optimization.
- Text-dense inline layout and wrapped text also allocate substantially; retain them as regression
  indicators when changing layout or text-run behavior.
- The 1,000-fragment scene consumes roughly 80% of the 120 Hz GPU-complete budget. Treat rendering
  changes near this scene size as hardware-sensitive and compare synchronized results first.
