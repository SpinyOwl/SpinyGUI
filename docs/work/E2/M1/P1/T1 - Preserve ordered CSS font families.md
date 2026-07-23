# T1: Preserve Ordered CSS Font Families

Parent plan: `docs/features/font-family-resolution-plan.md`

## Scope

Implement only T1 from the parent plan. Preserve CSS `font-family` declaration order through parsing, style storage, inheritance, and `ResolvedStyle`. Do not introduce a shared resolver, glyph runs, or NanoVG changes in this task.

## Dependencies

- Depends on: None.
- Enables: T2, T3, T4.
- Parallelizable with: None.

## Required Changes

- [x] Replace `Set<String>` font-family values with immutable ordered `List<String>` values where they cross style APIs.
- [x] Update `FontPropertyProvider` so `TermList` order is retained and the default is Roboto followed by Noto Sans CJK SC.
- [x] Update affected style tests and fixtures for quoted names, comma-list order, inheritance, and unavailable-first-family preservation.

## Acceptance Checks

- [x] `ResolvedStyle` reports the exact CSS family order.
- [x] CSS default resolves to `Roboto`, then `Noto Sans CJK SC`.
- [x] Core tests pass for the changed style surface.
- [x] `git diff --check` passes.

## Constraints

- Preserve unrelated work, including untracked `.worktrees/` and the parent plan document.
- Do not commit.
- Report files changed, behavior changed, tests run/not run, model/fallback status, and risks.
