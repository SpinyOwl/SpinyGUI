package com.spinyowl.spinygui.benchmark.frame;

import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.event.processor.DefaultEventProcessor;
import com.spinyowl.spinygui.core.event.processor.InputProcessingCounters;
import com.spinyowl.spinygui.core.event.processor.InputImpact;
import com.spinyowl.spinygui.core.input.Keyboard;
import com.spinyowl.spinygui.core.input.impl.KeyboardLayoutImpl;
import com.spinyowl.spinygui.core.input.impl.MouseServiceImpl;
import com.spinyowl.spinygui.core.input.impl.ShortcutRegistryImpl;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.system.event.listener.SystemCharEventListener;
import com.spinyowl.spinygui.core.system.event.listener.SystemCursorPosEventListener;
import com.spinyowl.spinygui.core.system.event.listener.SystemEventListener;
import com.spinyowl.spinygui.core.system.event.listener.SystemKeyEventListener;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessorImpl;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProviderImpl;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.time.TimeService;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/** Records input-processing decisions without coupling hosts to gameplay key policy. */
public final class InputImpactEvidenceRecorder {
  private static final TimeService TIME = () -> 1D;

  private InputImpactEvidenceRecorder() {}

  public enum RatePolicy {
    UNCAPPED("uncapped", 0),
    FPS_120("120hz", 120),
    FPS_60("60hz", 60);

    private final String id;
    private final int hertz;

    RatePolicy(String id, int hertz) {
      this.id = id;
      this.hertz = hertz;
    }

    long periodNanos() {
      return hertz == 0 ? 0 : 1_000_000_000L / hertz;
    }
  }

  public enum Scenario {
    IDLE("idle"),
    POINTER_ACTIVE("pointer-active"),
    KEYBOARD_ACTIVE("keyboard-active"),
    MIXED_INPUT("mixed-input");

    private final String id;

    Scenario(String id) {
      this.id = id;
    }
  }

  public static InputImpactEvidenceArtifact recordAll(
      String runId, long durationMillis) {
    if (durationMillis <= 0) throw new IllegalArgumentException("Duration must be positive");
    List<InputImpactEvidenceArtifact.Recording> recordings = new ArrayList<>();
    for (Scenario scenario : Scenario.values()) {
      for (RatePolicy rate : RatePolicy.values()) {
        recordings.add(record(scenario, rate, durationMillis));
      }
    }
    return InputImpactEvidenceArtifact.create(runId, recordings);
  }

  private static InputImpactEvidenceArtifact.Recording record(
      Scenario scenario, RatePolicy rate, long durationMillis) {
    Harness harness = new Harness();
    for (int i = 0; i < 3; i++) harness.process(scenario);
    long threadId = Thread.currentThread().threadId();
    com.sun.management.ThreadMXBean threadBean =
        (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
    if (threadBean.isThreadAllocatedMemorySupported()
        && !threadBean.isThreadAllocatedMemoryEnabled()) {
      threadBean.setThreadAllocatedMemoryEnabled(true);
    }
    long beforeCpu = threadBean.getThreadCpuTime(threadId);
    long beforeAllocation = threadBean.getThreadAllocatedBytes(threadId);
    GcTotals beforeGc = GcTotals.capture();
    InputProcessingCounters.Snapshot beforeCounters = harness.counters();
    long start = System.nanoTime();
    long deadline = start + TimeUnit.MILLISECONDS.toNanos(durationMillis);
    long frames = 0;
    do {
      long frameStart = System.nanoTime();
      harness.process(scenario);
      frames++;
      long period = rate.periodNanos();
      if (period > 0) {
        long remaining = period - (System.nanoTime() - frameStart);
        if (remaining > 0) LockSupport.parkNanos(remaining);
      }
    } while (System.nanoTime() < deadline || frames == 0);
    long elapsed = Math.max(1, System.nanoTime() - start);
    long cpu = Math.max(0, threadBean.getThreadCpuTime(threadId) - beforeCpu);
    long allocated = Math.max(0, threadBean.getThreadAllocatedBytes(threadId) - beforeAllocation);
    GcTotals afterGc = GcTotals.capture();
    InputProcessingCounters.Snapshot afterCounters = harness.counters();
    long inputBatches = frames * 2;
    long unchanged =
        afterCounters.unchangedBatches() - beforeCounters.unchangedBatches();
    long refresh = inputBatches - unchanged;
    return new InputImpactEvidenceArtifact.Recording(
        scenario.id,
        rate.id,
        frames,
        inputBatches,
        unchanged,
        refresh,
        allocated,
        cpu,
        Math.max(0, afterGc.collections - beforeGc.collections),
        Math.max(0, afterGc.timeMillis - beforeGc.timeMillis),
        (double) allocated / frames,
        allocated * 1_000_000_000.0 / elapsed,
        (double) cpu / frames,
        cpu * 1_000_000_000.0 / elapsed,
        frames * 1_000_000_000.0 / elapsed);
  }

  private static final class Harness {
    private final Frame frame = new Frame();
    private final MouseServiceImpl mouse = new MouseServiceImpl();
    private final DefaultEventProcessor gui = new DefaultEventProcessor();
    private final SystemEventProcessorImpl system;

    private Harness() {
      frame.box().contentSize(100, 100);
      com.spinyowl.spinygui.core.node.Element inert =
          new com.spinyowl.spinygui.core.node.Element("inert");
      inert.box().contentPosition(10, 10);
      inert.box().contentSize(20, 20);
      frame.addChild(inert);
      mouse.setCursorPositions(
          frame,
          new com.spinyowl.spinygui.core.input.MouseService.CursorPositions(
              new org.joml.Vector2f(16, 16), new org.joml.Vector2f(16, 16)));
      SystemEventListenerProviderImpl provider = new SystemEventListenerProviderImpl();
      provider.listener(
          SystemCursorPosEvent.class,
          SystemCursorPosEventListener.builder()
              .eventProcessor(gui)
              .timeService(TIME)
              .mouseService(mouse)
              .build());
      provider.listener(
          SystemKeyEvent.class,
          SystemKeyEventListener.builder()
              .eventProcessor(gui)
              .timeService(TIME)
              .keyboard(
                  new Keyboard(
                      new KeyboardLayoutImpl(Map.of(com.spinyowl.spinygui.core.input.KeyCode.KEY_A, 65)),
                      new ShortcutRegistryImpl()))
              .build());
      provider.listener(
          SystemCharEvent.class,
          SystemCharEventListener.builder().eventProcessor(gui).timeService(TIME).build());
      system = SystemEventProcessorImpl.builder().eventListenerProvider(provider).build();
    }

    private void process(Scenario scenario) {
      switch (scenario) {
        case IDLE -> {}
        case POINTER_ACTIVE -> system.push(pointer());
        case KEYBOARD_ACTIVE -> system.push(key(65));
        case MIXED_INPUT -> {
          system.push(pointer());
          system.push(key(999));
        }
      }
      InputImpact systemImpact = system.processEvents();
      systemImpact.combine(gui.processEvents());
    }

    private InputProcessingCounters.Snapshot counters() {
      InputProcessingCounters.Snapshot systemCounters = system.inputProcessingCounters();
      InputProcessingCounters.Snapshot guiCounters = gui.inputProcessingCounters();
      return new InputProcessingCounters.Snapshot(
          systemCounters.unchangedBatches() + guiCounters.unchangedBatches(),
          systemCounters.knownEffectBatches() + guiCounters.knownEffectBatches(),
          systemCounters.unknownFallbackBatches() + guiCounters.unknownFallbackBatches());
    }

    private SystemCursorPosEvent pointer() {
      return SystemCursorPosEvent.builder().frame(frame).posX(16).posY(16).build();
    }

    private SystemKeyEvent key(int keyCode) {
      return SystemKeyEvent.builder()
          .frame(frame)
          .keyCode(keyCode)
          .scancode(keyCode)
          .action(SystemKeyAction.PRESS)
          .mods(ImmutableSet.of())
          .build();
    }
  }

  private record GcTotals(long collections, long timeMillis) {
    static GcTotals capture() {
      long collections = 0;
      long timeMillis = 0;
      for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
        if (bean.getCollectionCount() >= 0) collections += bean.getCollectionCount();
        if (bean.getCollectionTime() >= 0) timeMillis += bean.getCollectionTime();
      }
      return new GcTotals(collections, timeMillis);
    }
  }
}
