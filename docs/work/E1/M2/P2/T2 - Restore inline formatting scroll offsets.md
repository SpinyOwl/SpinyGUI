# T2 Follow-up: Restore Inline Formatting Scroll Offsets

Amend only M2/P2/T2 after combined verification failure.

## Failure

`NvgInlineFormattingOffsetTest` fails for element, border, text, and inline-block offsets because the rendering-coordinate refactor stopped subtracting the containing block scroll offset.

## Requirements

- Restore correct containing-block scroll subtraction for inline formatting while preserving the renderer-owned subtree scroll state introduced by M2/P2/T2.
- Update affected focused tests only as required by the correct coordinate contract; do not weaken assertions or revert transform/clip behavior.
- Run `:spinygui.core.backend.lwjgl.nanovg:test --tests *NvgInlineFormattingOffsetTest` plus relevant transform/clip tests.

## Limits

- No new features, no input/demo changes, no commit.
