# Epic Status

Last reviewed: 2026-08-31

This document tracks the current status of the top-level epic plans under
[`docs/work`](work/). Status is based on the epic's milestone documents and checked
implementation/acceptance evidence, not on the presence of a plan alone.

| Epic | Goal | Status | Current boundary and next work |
| --- | --- | --- | --- |
| [E1: CSS Animation Support](work/E1%20-%20CSS%20animation%20support.md) | Deliver bounded CSS transforms, transitions, and keyframe animation support. | In progress | Transform and transition work has delivered bounded implementation evidence; keyframes and hardening/documentation remain open. |
| [E2: Frame runtime integration](work/E2%20-%20Frame%20runtime%20integration.md) | Consolidate owner-native frame contracts and provide an optional backend-independent pipeline plus reusable LWJGL application host. | In progress | M1-M3 provide the verified single-frame pipeline and host boundary. Planned M4 adds `FrameNavigator`, a frame-owned modal `TopLayer`, reusable default listeners, cbchain callback coexistence, and dynamic active-frame host composition. |
| [E3: CSS Grid support](work/E3%20-%20CSS%20Grid%20support.md) | Deliver a first-class, bounded CSS Grid Level 1 formatting context. | In progress | A substantial typed Grid Level 1 subset is implemented and tested; container alignment, intrinsic sizing, edge-case grammar, broader interaction proof, and final documentation remain. |
| [E3.5: Chart.js Benchmark Charts](work/E3.5%20-%20Chart.js%20Benchmark%20Charts.md) | Provide offline benchmark reports with typed Chart.js visualizations. | Complete | Typed offline Chart.js reporting, supported artifact regeneration, structural inspection, and direct-file browser verification are complete. |
| [E4: Text Performance Benchmarks](work/E4%20-%20Text%20performance%20benchmarks.md) | Establish reproducible text measurement, layout, allocation, and rendering benchmarks. | Complete | The full benchmark suite and a fresh paired CPU/rendering report run pass; the current manifest selects complete comparable evidence. |
| [E5: Text Performance Improvements](work/E5%20-%20Text%20performance%20improvements.md) | Improve text-path measurement, font lifecycle, controls, rendering submission, and caches. | Complete | M1-M7 remain complete. The experimental M8 session architecture was superseded by E2's owner-native `FramePipeline`. |
| [E6: Frame Pipeline Performance](work/E6%20-%20Frame%20pipeline%20performance.md) | Reduce non-text frame CPU cost and transient allocation while preserving ownership boundaries. | In progress | M1 and M1.5 are implemented; traversal, selector, property-storage, incremental-boundary, and mutation work remains. |
| [E7: Skija Renderer Backends](work/E7%20-%20Skija%20renderer%20backends.md) | Add opt-in Skija OpenGL and Vulkan renderers behind the backend-neutral renderer SPI. | Planned | Renderer-host and Skija backend milestones are defined but not yet started in this checkout. |
| [E8: Declarative XML Event Binding](work/E8%20-%20Declarative%20XML%20event%20binding.md) | Bind named XML event declarations to caller-owned Java listeners through an optional typed registry. | Planned | M1/P1 defines default resolving proxies, dispatch-time registry lookup, configurable diagnostics, compatibility coverage, demo adoption, and documentation. |

## Status conventions

- **Complete** means the tracked epic work has no remaining open acceptance/task items in its current plan set.
- **Complete with verification caveat** means the implementation boundary is complete, but a known
  verification failure or manual check remains explicitly recorded.
- **In progress** means implementation or evidence exists, but one or more planned boundaries remain open.
- **Planned** means the epic is documented but has no completed implementation boundary recorded here.

Update this index and the matching `**Status:**` field in each epic document when a milestone
crosses a verified boundary. Do not infer completion from a commit or an unchecked proposal alone.
