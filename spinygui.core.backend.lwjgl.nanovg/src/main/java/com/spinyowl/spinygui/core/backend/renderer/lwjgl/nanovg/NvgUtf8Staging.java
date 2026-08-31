package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memRealloc;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import java.nio.ByteBuffer;
import java.util.Objects;

/** Renderer-owned, hard-capped UTF-8 staging for synchronous NanoVG text calls. */
final class NvgUtf8Staging implements AutoCloseable {
  static final int DEFAULT_INITIAL_CAPACITY = 1_024;
  static final int DEFAULT_MAX_RETAINED_CAPACITY = 64 * 1_024;

  private final DiagnosticSession diagnostics;
  private final int initialCapacity;
  private final int maximumRetainedCapacity;
  private ByteBuffer retained;
  private boolean closed;
  private long allocationCalls;
  private long reuseCalls;
  private long oversizedAllocationCalls;
  private long oversizedFreedBytes;
  private long payloadBytes;

  NvgUtf8Staging(DiagnosticSession diagnostics) {
    this(diagnostics, DEFAULT_INITIAL_CAPACITY, DEFAULT_MAX_RETAINED_CAPACITY);
  }

  NvgUtf8Staging(
      DiagnosticSession diagnostics, int initialCapacity, int maximumRetainedCapacity) {
    this.diagnostics = Objects.requireNonNull(diagnostics, "diagnostics");
    if (initialCapacity <= 0 || maximumRetainedCapacity < initialCapacity) {
      throw new IllegalArgumentException("UTF-8 staging requires 0 < initial <= maximum");
    }
    this.initialCapacity = initialCapacity;
    this.maximumRetainedCapacity = maximumRetainedCapacity;
  }

  void submit(String text, NativeCall nativeCall) {
    submit(NvgRenderedText.literal(text), nativeCall);
  }

  void submit(NvgRenderedText text, NativeCall nativeCall) {
    Objects.requireNonNull(text, "text");
    Objects.requireNonNull(nativeCall, "nativeCall");
    requireOpen();
    int required = text.utf8Length();
    payloadBytes += required;
    diagnostics.add(NvgDiagnosticCounter.UTF8_PAYLOAD_BYTES, required);
    if (required > maximumRetainedCapacity) {
      submitOversized(text, required, nativeCall);
      return;
    }
    ByteBuffer target = retained(required);
    encode(text, required, target);
    reuseCalls++;
    diagnostics.increment(NvgDiagnosticCounter.UTF8_REUSE_CALLS);
    nativeCall.accept(target);
  }

  private void submitOversized(NvgRenderedText text, int required, NativeCall nativeCall) {
    ByteBuffer oneShot = allocate(required);
    oversizedAllocationCalls++;
    diagnostics.increment(NvgDiagnosticCounter.UTF8_OVERSIZED_ALLOCATION_CALLS);
    try {
      encode(text, required, oneShot);
      nativeCall.accept(oneShot);
    } finally {
      memFree(oneShot);
      oversizedFreedBytes += required;
      diagnostics.add(NvgDiagnosticCounter.UTF8_OVERSIZED_FREED_BYTES, required);
    }
  }

  private ByteBuffer retained(int required) {
    int wanted = Math.max(1, required);
    if (retained == null) {
      retained = allocate(growthCapacity(wanted));
    } else if (retained.capacity() < wanted) {
      int capacity = growthCapacity(wanted);
      retained = memRealloc(retained, capacity);
      recordAllocation(capacity);
    }
    return retained;
  }

  private int growthCapacity(int required) {
    int capacity = retained == null ? initialCapacity : retained.capacity();
    while (capacity < required && capacity < maximumRetainedCapacity) {
      capacity = Math.min(maximumRetainedCapacity, capacity * 2);
    }
    return capacity;
  }

  private ByteBuffer allocate(int capacity) {
    ByteBuffer allocated = memAlloc(Math.max(1, capacity));
    recordAllocation(allocated.capacity());
    return allocated;
  }

  private void recordAllocation(int capacity) {
    allocationCalls++;
    diagnostics.increment(NvgDiagnosticCounter.UTF8_ALLOCATION_CALLS);
    diagnostics.add(NvgDiagnosticCounter.UTF8_ALLOCATED_BYTES, capacity);
  }

  private static void encode(NvgRenderedText text, int required, ByteBuffer target) {
    target.clear();
    int written = text.encode(target);
    if (written != required) {
      throw new IllegalStateException(
          "LWJGL UTF-8 length mismatch: expected " + required + ", encoded " + written);
    }
    target.position(0);
    target.limit(required);
  }

  NvgTextStagingObservation observation() {
    return new NvgTextStagingObservation(
        retained == null ? 0 : retained.capacity(),
        maximumRetainedCapacity,
        allocationCalls,
        reuseCalls,
        oversizedAllocationCalls,
        oversizedFreedBytes,
        payloadBytes,
        closed);
  }

  void resetFrame() {
    requireOpen();
    if (retained != null) retained.clear();
  }

  private void requireOpen() {
    if (closed) throw new IllegalStateException("UTF-8 staging is closed");
  }

  @Override
  public void close() {
    if (closed) return;
    if (retained != null) {
      memFree(retained);
      retained = null;
    }
    closed = true;
  }

  @FunctionalInterface
  interface NativeCall {
    void accept(ByteBuffer utf8);
  }
}
