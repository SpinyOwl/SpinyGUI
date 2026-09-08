# Browser / SpinyGUI visual tests

This explicit Gradle subproject renders the same XML and CSS in Chromium and the real NanoVG
backend, compares element geometry and screenshots, and reports discrepancies. It is separate
from benchmarks and is **not** invoked by `test` or `check`. Those tasks run the runner's unit
tests and static analysis without launching Chromium or opening a graphics context.

## Setup and execution

Use the repository's Java 25 toolchain and wrapper. Install the pinned browser once:

```shell
./gradlew :spinygui.visual-tests:installChromium
./gradlew :spinygui.visual-tests:compareViews
```

On Windows use `gradlew.bat`. Run selected cases with:

```shell
./gradlew :spinygui.visual-tests:compareViews -PvisualCases=layout,text,scrolling-offset
./gradlew :spinygui.visual-tests:check
```

No Node/npm setup is needed. Playwright Java 1.58.0 pins its Chromium build. Browser installation
requires network access; subsequent captures use only a loopback HTTP server and local resources.
See the official [Playwright browser setup](https://playwright.dev/java/docs/browsers) for proxy,
cache location and Linux system dependencies. By default the browser lives in
`build/playwright-browsers`; `clean` removes it. `PLAYWRIGHT_BROWSERS_PATH` may select a shared cache.

Windows, Linux and macOS launch paths are provided for x64 and ARM64 JVMs, using matching LWJGL
natives. A working OpenGL 3.2 context and installed Chromium system dependencies are required.
On Linux CI, provision a display (for example Xvfb) and Mesa or a GPU driver, then run the same
command under that display. macOS native capture starts in a child JVM with `-XstartOnFirstThread`.
Cross-platform code paths do not imply all platforms have been exercised; see the execution record.
Missing native libraries, browser dependencies or graphics support are failures, never skipped passes.

## Cases and shared input

### Open demos side by side

After installing Chromium, open a native window on the left and Chromium in app mode on the right
(no tabs, address bar, or browser toolbar):

```shell
./gradlew :spinygui.visual-tests:launchDemos
./gradlew :spinygui.visual-tests:launchDemos -Pdemo=overflow-demo
```

The default is `button-demo`. `-Pdemo` accepts a resource filename without its extension
(`button-demo`, `grid-style-demo`, `main-menu`, `overflow-demo`, `text-input-demo`,
`textarea-demo`, `transform-demo`, `transition-demo`) or a full comparison case ID, including
focused fixtures such as `scrolling-offset`. Invalid names print the available choices and fail.

Both windows share the same viewport, sized to fit side by side on the primary display.
The task stays running until either window closes; Escape closes the native window.
The native window uses the standard input callbacks, so scrolling and text editing are available.
These are the shared XML/CSS views with the comparison font and static animation settings;
demo-specific Java actions are not attached. Window positioning is subject to desktop/window-manager
policy. Session inputs, actual window positions, and the native log live under `build/demo-sessions`.
For a timed launch smoke check, add `-PdemoSeconds=5`; the default `0` keeps the pair open.

### Comparison suite

- Every `.xml` demo under `spinygui.demo.complex/src/main/resources/com/spinyowl/spinygui/demo`
  is loaded with its matching `.css`, named `demo-<filename>` (currently eight demos).
- Focused cases: `layout`, `text`, `borders`, `clipping`, `scrolling`, `scrolling-offset`,
  and `scrolling-clamped`. Scrolled states set both offsets; the last requests offsets beyond
  the content so clamping can be compared.
- Demos use a fixed 960×720 viewport; focused fixtures use 480×320. Pixel ratio is 1.
- `gui-test-html-app/app.html?compare` loads `comparison.js` under the automated server.
  The existing manual `app.html` / `app.cmd` workflow remains available.
- The shared `comparison.css` makes defaults explicit. Both engines consume the same normalized
  XML with generated IDs for unnamed elements; source demos are never rewritten.
- Both engines receive bundled Roboto, a forced Roboto family, fixed viewport dimensions,
  and disabled transitions/animations. Chromium waits for the fonts and two animation frames;
  native preparation must converge. These are initial static states, not animation or input tests.
- Browser control defaults and unsupported CSS can differ from SpinyGUI. Such differences remain
  visible failures; this suite does not silently replace controls with mock elements.

## Results and tolerances

Capture defaults v2 explicitly style spans as inline and controls without browser-native
appearance, borders, centered text or resize handles; demo rules override these shared defaults.
Headless Chromium keeps scrollbars visible. Shared scrollbars are 12 px, with gray tracks/thumbs;
demo-specific dimensions and colors still apply. The browser root is absolutely positioned at
the viewport origin to isolate first-child margin collapse, matching a native Frame. Nested
margins and all declared layout dimensions remain observable. Browser metadata records effective
launch arguments, viewport/DPR and the policy version. Native element defaults are unchanged.

Native text accepts positive pixel and unitless line heights. Pixel values remain absolute when
inherited; unitless values scale with the child's font size. Line advances retain fractional
precision and glyph ink may exceed a short line box. Native `normal` still uses the configured
default multiplier (1.2); differences from Chromium's font-dependent `normal` remain visible.
Numeric font weights select supported static weights and the nearest available bundled face.

Open `build/reports/view-comparison/index.html`. Every invocation creates a new `run-*` directory
containing per-case input JSON, browser/native PNGs, a magenta difference image, geometry JSON,
runtime metadata, native process log, results and a run summary. Earlier run data is retained;
`clean` removes it. No stored golden image or auto-accept operation is involved.

A case passes only when **both** checks pass:

- Geometry: matching IDs; viewport border-box x/y/width/height (including transforms and ancestor
  scrolling), client width/height, scroll width/height and actual scroll offsets within 1 CSS pixel.
  Hidden elements have zero bounds. Missing/duplicate IDs and non-finite measurements fail.
- Pixels: maximum absolute delta of 16 per RGBA channel; at most 0.5% of pixels may exceed that
  delta. Image-size mismatches fail. The diff highlights exceeding pixels, including those within
  the allowed fraction. Geometry tolerance never grows in response to screenshot differences.

These explicit starting tolerances accommodate some rasterization variation but do not guarantee
that every font rasterizer matches. Failures require inspection; changing tolerance is a reviewed
policy change, not an automatic recovery step. Small pixel-only defects below the stated fraction
may pass. Text glyphs are checked by pixels and enclosing element geometry, not glyph-box metrics.

`compareViews` returns nonzero for any mismatch, capture/setup error or invalid selection. One
native process per case is limited to 60 seconds and its output is isolated, so stale images cannot
produce a pass. Read `native.log` for process failures. CI should retain the whole report directory
even on nonzero exit. Existing browser discrepancies are test findings, not reasons to mark a
capture `unvalidated` or to alter the existing benchmark image-comparison policy.

Scroll cases compare programmatically positioned states and bounds; they do not claim to verify
wheel routing, drag behavior, keyboard navigation or focus. Existing Java-assembled demo logic
is not executed: this suite covers the shared XML/CSS view resources.

Implementation and verification: [T1 execution record](../docs/work/T1%20-%20Browser%20visual%20comparison.md).
