# P3 - Prove complex Grid panels

## Document Context
- Status: Blocked
- Dependencies: P2
- Parent: [M7 - Complete predictable Grid panels](../M7%20-%20Complete%20predictable%20Grid%20panels.md)
- Children: [T1 - Add Grid regression fixtures](P3/T1%20-%20Add%20Grid%20regression%20fixtures.md), [T2 - Extend Grid demo and manual script](P3/T2%20-%20Extend%20Grid%20demo%20and%20manual%20script.md), [T3 - Reconcile support docs and final regression](P3/T3%20-%20Reconcile%20support%20docs%20and%20final%20regression.md), [T4 - Repair demo test output directory](P3/T4%20-%20Repair%20demo%20test%20output%20directory.md)
- Related: [Grid proof and documentation](../../M6%20-%20Grid%20proof%20and%20documentation.md)
- Next: Add a compact complex-demo panel that exercises the delivered M7 behavior.

**Depends on:** P2 — completed alignment and bounded intrinsic-sizing behavior.

## Goal

Provide repeatable automated and native evidence that complex Grid panels preserve alignment, dimensions, clipping, and interaction.

## Context

- M6 already plans representative Grid proof and documentation for the existing subset.
- M7 needs evidence for the new container alignment and intrinsic sizing behavior before the support matrix is broadened.

## Phase Tasks

### T1: Add core and backend regression fixtures
See [T1 - Add Grid regression fixtures](P3/T1%20-%20Add%20Grid%20regression%20fixtures.md).

### T2: Extend the complex Grid demo and manual script
See [T2 - Extend Grid demo and manual script](P3/T2%20-%20Extend%20Grid%20demo%20and%20manual%20script.md).

### T3: Reconcile support documentation and run final regression
See [T3 - Reconcile support docs and final regression](P3/T3%20-%20Reconcile%20support%20docs%20and%20final%20regression.md).

### T4: Repair demo test output directory
See [T4 - Repair demo test output directory](P3/T4%20-%20Repair%20demo%20test%20output%20directory.md).

## Verification Strategy

- Focused core/backend tests per fixture, then the affected aggregate command and native demo check.

## Deferred Work

- `subgrid`, masonry, baseline alignment, negative indexes, and advanced named-line grammar.
