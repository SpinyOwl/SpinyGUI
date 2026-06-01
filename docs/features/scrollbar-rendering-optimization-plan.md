# Scrollbar Rendering Optimization Plan

## Goal
Reduce the remaining scrollbar-specific rendering overhead on `feature/scrollbar` without starting the broader dirty style/layout invalidation work. The target is local NanoVG scrollbar cost: fewer per-frame allocations, fewer no-op draw calls, and cheaper scissor setup where safe.

## Non-Goals
- Dirty style/layout invalidation.
- Changing scrollbar CSS behavior.
- Changing scrollbar geometry or hit-testing semantics unless required by tests.
- Reworking the general renderer traversal or global clipping model.

## Context
- Current branch: `feature/scrollbar`.
- Current baseline comparison:
  - `agent-dev` comparable demo: approximately `1319 FPS`.
  - `feature/scrollbar` after first performance pass: approximately `1074 FPS`.
- Main files:
  - `spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/NvgScrollbarRenderer.java`
  - `spinygui.core/src/main/java/com/spinyowl/spinygui/core/util/ScrollbarGeometry.java`
  - `spinygui.core.backend.lwjgl.nanovg/src/test/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/NvgScrollbarRendererTest.java`
- Current hot path still allocates `Vector2f`, `Vector4f`, `Color.withA(...)` results, and calls fill even when a pseudo style could be transparent.

## Assumptions and Open Questions
- Assumption: preserving current rendered output is more important than merging shapes aggressively.
- Assumption: FPS in `OverflowExample` is acceptable as a coarse manual benchmark, but tests should validate behavior.
- Question: Should temporary timing instrumentation be committed, or only used locally and reverted? This affects Step 6.

## Step-by-Step Plan

### Step 1: Add Transparent-Paint Early-Outs
**Purpose:** Avoid NanoVG calls for scrollbar parts that resolve to fully or effectively transparent paint.

**Changes:**
- [x] Add a small alpha threshold constant in `NvgScrollbarRenderer`, matching or reusing `NvgShapes.MIN_ALPHA` if access is appropriate.
- [x] Skip `shapeSink.fill(...)` when resolved background color is null or alpha is below threshold.
- [x] Skip `shapeSink.stroke(...)` when resolved border color is null or alpha is below threshold, in addition to existing border-style and width checks.
- [x] Add renderer tests for transparent track/thumb/corner backgrounds and transparent border color.

**Acceptance Checks:**
- [x] `NvgScrollbarRendererTest` verifies transparent fill parts do not record fill calls.
- [x] Existing default scrollbar rendering tests still pass unchanged or with only expected call-list updates.
- [x] Run:
  ```powershell
  .\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test --tests "*NvgScrollbarRendererTest"
  ```

**Dependencies:** None.

**Risks:** Low. Risk is accidentally skipping default fallback colors; mitigate by testing default rendering remains opaque.

### Step 2: Avoid Color Copies for Default Opacity
**Purpose:** Remove unnecessary `Color.withA(...)` allocations for the common case where opacity is absent or `1`.

**Changes:**
- [x] Refactor `color(Color color, ResolvedStyle style)` so it returns the original `Color` when opacity is null or exactly `1`.
- [x] Clamp and allocate only when opacity changes effective alpha.
- [x] Add or adjust tests to cover opacity `1`, opacity `0.5`, and null style.

**Acceptance Checks:**
- [x] Existing opacity test still records alpha `0.5` for styled thumb.
- [x] A new or updated test confirms opacity `1` keeps expected alpha without changing visible output.
- [x] Run:
  ```powershell
  .\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test --tests "*NvgScrollbarRendererTest"
  ```

**Dependencies:** Step 1 can be done before or after this step.

**Risks:** Low. Floating-point equality for `1f` should be explicit and simple.

### Step 3: Replace Shape Sink Vector Arguments with Primitive Geometry
**Purpose:** Remove per-part `Vector2f` allocations in NanoVG scrollbar fill/stroke paths.

**Changes:**
- [ ] Change `ScrollbarShapeSink.fill(...)` from `Rect + Color + Vector4f` to primitive rect coordinates plus color and radius.
- [ ] Change `ScrollbarShapeSink.stroke(...)` similarly.
- [ ] Update `NanoVgScrollbarShapeSink` to call new primitive overloads or add primitive overloads to `NvgShapes`.
- [ ] Update `RecordingShapeSink` in tests to record primitive values.
- [ ] Keep `Rect` in `ScrollbarGeometry.Metrics`; do not broaden this step into geometry storage refactoring.

**Acceptance Checks:**
- [ ] Renderer tests pass and produce the same recorded geometry values.
- [ ] Diff does not introduce new allocations in `NvgScrollbarRenderer.fill/stroke`.
- [ ] Run:
  ```powershell
  .\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test --tests "*NvgScrollbarRendererTest"
  ```

**Dependencies:** None, but easier after Steps 1-2.

**Risks:** Medium-low. Test fixtures will need mechanical updates. Keep the public impact contained to package-private renderer internals.

### Step 4: Reuse Zero Radius and Avoid Radius Allocation for Unstyled Parts
**Purpose:** Avoid creating `new Vector4f(0)` for default scrollbar parts and reduce radius object churn.

**Changes:**
- [ ] Add a static zero-radius constant in `NvgScrollbarRenderer` or reuse `NvgShapes.ZERO_CORNERS` if type compatibility is clean.
- [ ] Make `borderRadius(...)` return the zero constant when style is null or all radius values resolve to zero.
- [ ] Only allocate a new radius vector when at least one radius is non-zero.
- [ ] Adjust tests so they do not depend on radius object identity, only values.

**Acceptance Checks:**
- [ ] Default rendering still records radius `0.0`.
- [ ] Styled radius test still records `3,4,5,6`.
- [ ] Code inspection shows default scrollbar parts do not allocate radius vectors.
- [ ] Run:
  ```powershell
  .\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test --tests "*NvgScrollbarRendererTest"
  ```

**Dependencies:** Step 3 if shape sink signature changes radius type.

**Risks:** Low. Avoid mutating shared radius constants.

### Step 5: Add a Cheap Scissor Fast Path
**Purpose:** Avoid walking parent clipping ancestors and resetting scissor when the scrollbar element has no clipping ancestors.

**Changes:**
- [ ] Add a helper in `NvgClipStack` or `NvgRenderUtils` to answer whether a node has clipping ancestors.
- [ ] In `NanoVgScrollbarShapeSink.begin(...)`, skip `createScissor(context, element)` when no parent clip applies.
- [ ] Preserve `nvgSave`, element border-box `nvgIntersectScissor`, `nvgRestore`, and `resetScissor` semantics where a scissor was actually created.
- [ ] Add focused unit tests around `NvgClipStack` if existing seams allow it; otherwise test through a sink abstraction if practical.

**Acceptance Checks:**
- [ ] Existing renderer tests pass.
- [ ] A new test or code inspection confirms parent clip traversal is skipped for unclipped elements.
- [ ] Manual check: `OverflowExample` still clips scrollbar rendering correctly inside scrollable/nested panels.
- [ ] Run:
  ```powershell
  .\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test --tests "*NvgScrollbarRendererTest"
  .\gradlew.bat :spinygui.core:test --tests "*OverflowLayoutTest"
  ```

**Dependencies:** Steps 1-4 independent, but do this after allocation cleanup because it is more behavior-sensitive.

**Risks:** Medium. Scissor state mistakes can cause rendering leaks. Stop if visual clipping changes in nested overflow cases.

### Step 6: Measure and Decide Whether to Continue
**Purpose:** Verify the local optimizations moved the actual demo number before planning broader renderer or invalidation work.

**Changes:**
- [ ] Run `OverflowExample` on `feature/scrollbar` and record FPS after Steps 1-5.
- [ ] Compare against current `feature/scrollbar` value of approximately `1074 FPS` and `agent-dev` comparable value of approximately `1319 FPS`.
- [ ] If improvement is small, add temporary local timing around scrollbar render vs total render, then revert timing code before commit unless instrumentation is explicitly requested.

**Acceptance Checks:**
- [ ] FPS comparison is documented in the final implementation note.
- [ ] Temporary instrumentation is not left in committed code unless explicitly approved.
- [ ] Working tree contains only intentional optimization/test changes.

**Dependencies:** Steps 1-5.

**Risks:** Manual FPS is noisy. Use it only to guide direction, not as a strict pass/fail gate.

## Verification Strategy
- Narrow renderer tests after each renderer step:
  ```powershell
  .\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test --tests "*NvgScrollbarRendererTest"
  ```
- Core safety tests when geometry/interactions/clipping are touched:
  ```powershell
  .\gradlew.bat :spinygui.core:test --tests "*ScrollbarGeometryTest" --tests "*OverflowLayoutTest" --tests "*SystemMouseClickEventListenerTest" --tests "*SystemCursorPosEventListenerTest"
  ```
- Manual demo check:
  ```powershell
  .\gradlew.bat :spinygui.demo.complex:run
  ```
  or the project's existing run configuration for `OverflowExample`.

## Review Boundaries
- Commit 1: transparent/no-op draw skips and color opacity allocation cleanup.
- Commit 2: primitive shape sink and radius allocation cleanup.
- Commit 3: scissor fast path, if visual checks pass.
- Commit 4: optional measurement note or benchmark-only follow-up if requested.

## Deferred Work
- Dirty style/layout invalidation.
- Cached scrollbar render-style objects stored on `Element`.
- Global renderer clip-stack traversal instead of per-renderer scissor reconstruction.
- Shape batching or merging track/corner fills.
