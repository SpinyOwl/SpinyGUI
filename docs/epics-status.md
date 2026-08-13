# Epic Status

Last reviewed: 2026-08-13

This document tracks the current status of the top-level epic plans under
[`docs/work`](work/). Status is based on the epic's milestone documents and checked
implementation/acceptance evidence, not on the presence of a plan alone.

| Epic | Status | Current boundary and next work |
| --- | --- | --- |
| [E1: CSS Animation Support](work/E1%20-%20CSS%20animation%20support.md) | In progress | Transform and transition work has delivered bounded implementation evidence; keyframes and hardening/documentation remain open. |
| [E2: Frame runtime integration](work/E2%20-%20Frame%20runtime%20integration.md) | Planned | No frame-runtime implementation was found. The checked E2 child documents describe font-family resolution and need reclassification. |
| [E3: CSS Grid support](work/E3%20-%20CSS%20Grid%20support.md) | In progress | A substantial typed Grid Level 1 subset is implemented and tested; container alignment, intrinsic sizing, edge-case grammar, broader interaction proof, and final documentation remain. |
| [E3.5: Chart.js Benchmark Charts](work/E3.5%20-%20Chart.js%20Benchmark%20Charts.md) | In progress | Chart.js implementation is present; the task documentation still leaves browser/manual verification as follow-up. |
| [E4: Text Performance Benchmarks](work/E4%20-%20Text%20performance%20benchmarks.md) | Complete with verification caveat | The benchmark baseline implementation and child acceptance work are complete; one known diagnostic fixture mismatch still prevents a clean full benchmark-suite result. |
| [E5: Text Performance Improvements](work/E5%20-%20Text%20performance%20improvements.md) | In progress | Evidence, text-path, cache, and orchestration work is partially complete; remaining milestones and tasks are open. |
| [E6: Frame Pipeline Performance](work/E6%20-%20Frame%20pipeline%20performance.md) | In progress | M1 and M1.5 are implemented; traversal, selector, property-storage, incremental-boundary, and mutation work remains. |
| [E7: Skija Renderer Backends](work/E7%20-%20Skija%20renderer%20backends.md) | Planned | Renderer-host and Skija backend milestones are defined but not yet started in this checkout. |

## Status conventions

- **Complete** means the tracked epic work has no remaining open acceptance/task items in its current plan set.
- **Complete with verification caveat** means the implementation boundary is complete, but a known
  verification failure or manual check remains explicitly recorded.
- **In progress** means implementation or evidence exists, but one or more planned boundaries remain open.
- **Planned** means the epic is documented but has no completed implementation boundary recorded here.

Update this index and the matching `**Status:**` field in each epic document when a milestone
crosses a verified boundary. Do not infer completion from a commit or an unchecked proposal alone.
