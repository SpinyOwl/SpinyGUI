# Local Renderer Image Comparison Policy

Structural `NvgTextCommand` fixtures are the required portable rendering gate. A colored/non-black
frame is not evidence of correct rendering. Local image comparison is optional secondary boundary
evidence and never replaces structural assertions, identifies a benchmark, or supplies a performance
threshold.

## Opt-in and validation outcomes

- Comparison is disabled unless `-Dspinygui.rendering.localImageComparison=true` is explicitly set.
- Structural evidence runs first. If it fails, image comparison is forbidden even when pixels are
  non-black.
- Opt-out, a missing reference, a policy/reference-version mismatch, or any exact environment mismatch
  is `unvalidated`, never passed. No pixels are compared in those states.
- The normal CI gate runs structural fixtures only. Image comparison requires a separately approved
  local invocation with opt-in and an exact compatible reference.

## References and environment

Policy `local-text-image-policy-v1` uses reference schema `reference-v1`. Files are named
`reference-v1--<scene-id>--<environment-id>.png`; the sidecar manifest stores every unhashed field.
`<environment-id>` is the lowercase 64-character SHA-256 digest of the canonical JSON environment
fingerprint. References live under `<reference-root>/<scene-id>/`, with the sidecar named by adding
`.json` to the PNG filename.
Compatibility requires exact equality of JVM vendor/version, OS name/version/architecture, GL vendor,
renderer, driver version and API version, NanoVG backend, antialias setting, framebuffer width/height,
and pixel ratio. There is no universal reference.

Approved boundary scene IDs are `fallback-overhang`, `nested-clipping`, `selection-caret`, and
`transformed-text`. Together they cover fallback/overhang, clipping, selection/caret, and transformed
text. Adding or changing a scene requires structural fixture review first.

## Tolerance and artifacts

Outside an antialias edge fringe, RGB channel delta is at most 2 and alpha delta at most 2. Within a
one-pixel fringe around reference edges, both RGB and alpha channel delta are at most 6. At most 0.5% of pixels may differ
within those limits; a pixel over its applicable channel limit always fails. The comparator must retain
actual, expected, amplified-diff, edge-mask, environment manifest, and summary artifacts under
`mismatches/local-text-image-policy-v1/<scene-id>/`; artifacts are never accepted automatically.

## Authoring and updates

1. Pass all structural fixtures and record the exact environment manifest.
2. Run the approved boundary scene locally with explicit opt-in.
3. Place the candidate under its versioned environment-specific name; never overwrite an unrelated
   environment reference.
4. Review the scene commands, candidate image, diff artifacts, tolerance summary, and environment
   manifest together. Reference updates require the same review as initial authoring.
5. Preserve old references when needed for still-supported environments; changing policy semantics
   requires a new policy/reference version.

Image comparison is not run by `test`, `jmhRendering`, or `benchmarkReport` implicitly.

## Executing the workflow

Run the source-bound capture harness explicitly with
`./gradlew -Dspinygui.rendering.localImageComparison=true :spinygui.benchmark:localImageComparison`.
It captures all four approved scenes to the build artifact directory, runs structural recording first,
and compares only references under `spinygui.benchmark/local-image-references/`. The lower-level
`LocalImageComparisonPolicy.compareConfigured(...)` accepts the approved scene ID, captured PNG,
reference root, exact runtime fingerprint, and artifact root. It does not decode
an image until opt-in, manifest, scene, version, and exact environment checks all succeed. It returns
`unvalidated` for opt-out or incompatible/missing references, `passed` only after the comparator runs,
and `failed-image-comparison` after retaining the required mismatch artifacts.
