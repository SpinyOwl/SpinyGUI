# T2: Define Shared Font-Chain Resolver

Parent plan: `docs/features/font-family-resolution-plan.md`

## Scope

Implement only T2 from the parent plan, after T1's ordered family migration. Add one core resolver for ordered families and migrate the existing independent single-font lookup sites to it. Do not implement glyph runs or change NanoVG text drawing in this task.

## Dependencies

- Depends on: T1 complete.
- Enables: T3, T4, T5.
- Parallelizable with: None.

## Required Changes

- [x] Add a core font-chain contract accepting ordered family names plus style, weight, and stretch, returning available faces in deterministic order.
- [x] Specify and test face matching with exact requested style/weight/stretch preferred and a documented deterministic fallback.
- [x] Migrate duplicated font selection in text layout, inline/block layout, input viewport/mouse behavior, textarea metrics, and NanoVG input/debug paths to the shared resolver.
- [x] Keep an unavailable family in the chain but skip it when resolving available faces; do not select arbitrary system fonts.

## Acceptance Checks

- [x] Resolver tests prove CSS order wins over registry insertion/hash order and unavailable names are skipped.
- [x] Current Latin, icon, and emoji family selection retains its primary face.
- [x] Core and NanoVG focused tests pass.
- [x] `git diff --check` passes.

## Constraints

- Preserve T1 changes and unrelated untracked `.worktrees/` and parent plan documents.
- Do not implement T3 glyph runs or T4 drawing changes.
- Do not commit.
- Report files changed, behavior changed, tests run/not run, model/fallback status, and risks.
