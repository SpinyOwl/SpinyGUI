# T11: Prevent Historical Run Overwrite

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Scope

Guarantee that a repeated or backward-moving local clock cannot reuse an existing archived benchmark identifier after its transient reservation lock has been removed.

## Dependencies

- Depends on: T9 complete.
- Enables: T10.
- Parallelizable with: None.

## Required Changes

- [x] Check existing CPU and rendering archive files as part of atomic run-ID reservation.
- [x] Preserve the datetime prefix and append a sortable numeric collision suffix when the base datetime is already archived or reserved.
- [x] Keep existing timestamp-only T9 files readable and paired with new suffixed identifiers.
- [x] Sort runs with the same datetime by collision sequence and select the newest sequence as current.
- [x] Add focused parser/order tests for base and collision-suffixed identifiers.

## Acceptance Checks

- [x] Existing files cannot be overwritten by a newly reserved ID even after lock cleanup.
- [x] Parallel reservations and repeated datetime candidates produce distinct sortable identifiers.
- [x] Report tests, configuration-cache probes, clean retention, and whitespace checks pass.

## Constraints

- Do not modify benchmark calculations or report presentation.
- Preserve T1-T9 and unrelated worktree changes.
- Do not commit.
- Report files changed, tests run/not run, model/fallback status, and risks.
