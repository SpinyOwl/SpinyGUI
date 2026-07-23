# T4: Render Resolved Runs in NanoVG

Parent plan: `docs/features/font-family-resolution-plan.md`

## Scope

Implement only T4 after accepted T1-T3 work. NanoVG must draw the exact resolved font runs from core at advances compatible with core measurement. Do not perform T5 documentation/demo/migration cleanup beyond what is essential to avoid duplicated runtime fallback behavior.

## Dependencies

- Depends on: T2, T3 complete.
- Enables: T5.
- Parallelizable with: None.

## Required Changes

- [x] Give NanoVG text, input, and textarea paths access to resolved runs from the core text measurer or line metrics.
- [x] Set each run's face and draw its rendered code points at core-compatible cumulative advances.
- [x] Retain source text indices for controls while rendering replacement marker glyphs for unsupported code points.
- [x] Remove or bypass `NvgFontRegistry`'s independent hardcoded fallback and `displayText` re-resolution where run data is used.
- [x] Preserve existing clipping, scissor, baseline, icon, emoji, and plain Latin behavior.

## Acceptance Checks

- [x] The injected renderer sink records Roboto then Noto CJK faces and cumulative x positions for mixed text.
- [x] Renderer positions agree with core measured run advances under the existing pixel-rounding contract.
- [x] Unsupported code points pass a visible U+FFFD marker run rather than becoming blank.
- [x] NanoVG backend tests and `git diff --check` pass.

## Constraints

- Preserve accepted T1-T3 changes and unrelated untracked `.worktrees/` and plan documents.
- Do not implement T5 documentation/demo work.
- Do not commit.
- Report files changed, behavior changed, tests run/not run, model/fallback status, and risks.
