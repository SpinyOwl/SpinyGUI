# Epic Status

Last reviewed: 2026-08-22

This document tracks the current status of the top-level epic plans under
[`docs/work`](work/). Status is based on the epic's milestone documents and checked
implementation/acceptance evidence, not on the presence of a plan alone.

| Epic | Goal | Status | Current boundary and next work |
| --- | --- | --- | --- |
| [E1: CSS Animation Support](work/E1%20-%20CSS%20animation%20support.md) | Deliver bounded CSS transforms, transitions, and keyframe animation support. | In progress | Transform and transition work has delivered bounded implementation evidence; keyframes and hardening/documentation remain open. |
| [E2: Frame runtime integration](work/E2%20-%20Frame%20runtime%20integration.md) | Consolidate owner-native frame contracts and provide an optional backend-independent pipeline plus reusable LWJGL host loop. | Complete with verification caveat | Owner-native contracts, invalidation, `FramePipeline`, the reusable LWJGL host, manual Demo, tests, and fresh benchmark artifacts are complete. Native visual verification was not run, and the repository-wide build is blocked by an externally locked generated Demo build directory. |
| [E3: CSS Grid support](work/E3%20-%20CSS%20Grid%20support.md) | Deliver a first-class, bounded CSS Grid Level 1 formatting context. | In progress | A substantial typed Grid Level 1 subset is implemented and tested; container alignment, intrinsic sizing, edge-case grammar, broader interaction proof, and final documentation remain. |
| [E3.5: Chart.js Benchmark Charts](work/E3.5%20-%20Chart.js%20Benchmark%20Charts.md) | Provide offline benchmark reports with typed Chart.js visualizations. | Complete | Typed offline Chart.js reporting, supported artifact regeneration, structural inspection, and direct-file browser verification are complete. |
| [E4: Text Performance Benchmarks](work/E4%20-%20Text%20performance%20benchmarks.md) | Establish reproducible text measurement, layout, allocation, and rendering benchmarks. | Complete | The full benchmark suite and a fresh paired CPU/rendering report run pass; the current manifest selects complete comparable evidence. |
| [E5: Text Performance Improvements](work/E5%20-%20Text%20performance%20improvements.md) | Improve text-path measurement, font lifecycle, controls, rendering submission, caches, and orchestration. | In progress | M1 evidence repair, M2 approved-contract/linear uncached measurement, and M3 font identity/generation/lifecycle are complete. M4 and M5 are next and can proceed under their documented dependency boundary; M6-M8 remain planned. |
| [E6: Frame Pipeline Performance](work/E6%20-%20Frame%20pipeline%20performance.md) | Reduce non-text frame CPU cost and transient allocation while preserving ownership boundaries. | In progress | M1 and M1.5 are implemented; traversal, selector, property-storage, incremental-boundary, and mutation work remains. |
| [E7: Skija Renderer Backends](work/E7%20-%20Skija%20renderer%20backends.md) | Add opt-in Skija OpenGL and Vulkan renderers behind the backend-neutral renderer SPI. | Planned | Renderer-host and Skija backend milestones are defined but not yet started in this checkout. |

## Status conventions

- **Complete** means the tracked epic work has no remaining open acceptance/task items in its current plan set.
- **Complete with verification caveat** means the implementation boundary is complete, but a known
  verification failure or manual check remains explicitly recorded.
- **In progress** means implementation or evidence exists, but one or more planned boundaries remain open.
- **Planned** means the epic is documented but has no completed implementation boundary recorded here.

Update this index and the matching `**Status:**` field in each epic document when a milestone
crosses a verified boundary. Do not infer completion from a commit or an unchecked proposal alone.
