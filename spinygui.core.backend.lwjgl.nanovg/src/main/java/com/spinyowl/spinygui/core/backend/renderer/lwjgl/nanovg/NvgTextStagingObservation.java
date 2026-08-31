package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

/**
 * Immutable accounting for one renderer/context's bounded UTF-8 text staging.
 *
 * @param retainedCapacityBytes native bytes retained for reuse by the renderer
 * @param maximumRetainedCapacityBytes hard retention cap for this owner
 * @param allocationCalls retained and one-shot allocation calls
 * @param reuseCalls submissions encoded into retained storage
 * @param oversizedAllocationCalls one-shot submissions larger than the retention cap
 * @param oversizedFreedBytes one-shot bytes synchronously freed after submission
 * @param payloadBytes total submitted UTF-8 payload bytes
 * @param closed whether renderer teardown released the retained staging
 */
public record NvgTextStagingObservation(
    int retainedCapacityBytes,
    int maximumRetainedCapacityBytes,
    long allocationCalls,
    long reuseCalls,
    long oversizedAllocationCalls,
    long oversizedFreedBytes,
    long payloadBytes,
    boolean closed) {}
