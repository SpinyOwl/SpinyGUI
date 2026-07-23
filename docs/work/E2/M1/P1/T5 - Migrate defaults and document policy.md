# T5: Migrate Defaults and Document Policy

Parent plan: `docs/features/font-family-resolution-plan.md`

## Scope

Complete T5 after accepted T1-T4 work. Remove the temporary global fallback chain as the runtime behavior source, retain only style-derived ordered family chains, document system-font policy, and run final verification. A demo is optional only if a concise existing text-input demo can show the behavior without scope creep.

## Dependencies

- Depends on: T3, T4 complete.
- Enables: None.
- Parallelizable with: None.

## Required Changes

- [x] Remove `Font.fallbackFonts(...)` and all remaining production dependence on its hardcoded global chain; preserve direct single-font compatibility behavior with its visible marker.
- [x] Confirm default style family order is the bundled `Roboto`, then `Noto Sans CJK SC` chain and is used by core and NanoVG.
- [x] Document explicit system-font loading/selection, unavailable family skipping, deterministic bundled defaults, and missing-marker behavior.
- [x] Update relevant package or feature documentation; no demo was added because the existing renderer/core regression surface is sufficient and a new demo would add scope.

## Acceptance Checks

- [x] No production path uses a global hardcoded fallback list in place of a resolved style chain.
- [x] Default rendering remains deterministic when system fonts differ between hosts.
- [x] Existing Latin, icon, emoji, CJK, unsupported-marker, input caret, selection, and supplementary-plane tests pass.
- [x] `./gradlew build` and `git diff --check` pass.

## Constraints

- Preserve accepted T1-T4 changes and unrelated untracked `.worktrees/` and plan documents.
- Do not commit.
- Report files changed, behavior changed, tests run/not run, model/fallback status, and risks.
