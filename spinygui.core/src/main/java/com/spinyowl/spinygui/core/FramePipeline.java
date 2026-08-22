package com.spinyowl.spinygui.core;

import com.spinyowl.spinygui.core.animation.TransitionImpact;
import com.spinyowl.spinygui.core.animation.TransitionService;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.event.processor.InputImpact;
import com.spinyowl.spinygui.core.layout.LayoutResult;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.FrameInvalidation;
import com.spinyowl.spinygui.core.style.manager.StyleImpact;
import com.spinyowl.spinygui.core.style.manager.StyleManager;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import java.util.Objects;

/**
 * Backend-independent orchestration for input, style, transitions, layout, and transforms.
 * Polling, user updates, rendering, swapping, and resource lifecycle remain host-owned.
 */
public final class FramePipeline {
  private final SystemEventProcessor systemEvents;
  private final EventProcessor guiEvents;
  private final StyleManager styles;
  private final TransitionService transitions;
  private final LayoutService layout;
  private final Thread owner = Thread.currentThread();
  private boolean running;
  private InputImpact pendingInputImpact = InputImpact.NO_IMPACT;

  public FramePipeline(
      SystemEventProcessor systemEvents,
      EventProcessor guiEvents,
      StyleManager styles,
      TransitionService transitions,
      LayoutService layout) {
    this.systemEvents = Objects.requireNonNull(systemEvents, "systemEvents");
    this.guiEvents = Objects.requireNonNull(guiEvents, "guiEvents");
    this.styles = Objects.requireNonNull(styles, "styles");
    this.transitions = Objects.requireNonNull(transitions, "transitions");
    this.layout = Objects.requireNonNull(layout, "layout");
  }

  /** Processes system events first so GUI events emitted by them run in the same input phase. */
  public InputImpact processInput() {
    enter();
    try {
      InputImpact impact = systemEvents.processEvents().combine(guiEvents.processEvents());
      pendingInputImpact = pendingInputImpact.combine(impact);
      return impact;
    } finally {
      exit();
    }
  }

  /** Prepares a frame after the host's update and size synchronization. */
  public FramePreparation prepareFrame(Frame frame) {
    Objects.requireNonNull(frame, "frame");
    enter();
    FrameInvalidation source = frame.invalidation();
    InputImpact inputImpact = pendingInputImpact;
    pendingInputImpact = InputImpact.NO_IMPACT;
    boolean styleRan = false;
    boolean transitionRan = false;
    boolean layoutRan = false;
    boolean transformRan = false;
    StyleImpact styleImpact = StyleImpact.NO_CHANGE;
    TransitionImpact transitionImpact = TransitionImpact.NO_CHANGE;
    LayoutResult layoutResult = null;
    try {
      boolean styleNeeded = source.styleDirty() || inputImpact != InputImpact.NO_IMPACT;
      boolean layoutNeeded = source.layoutDirty()
          || inputImpact == InputImpact.FULL_REFRESH
          || inputImpact == InputImpact.FULL_UNKNOWN;
      boolean transformNeeded = source.transformDirty();

      if (styleNeeded) {
        styleRan = true;
        styleImpact = Objects.requireNonNull(styles.recalculate(frame), "style impact");
        switch (styleImpact) {
          case LAYOUT, FULL_UNKNOWN -> layoutNeeded = true;
          case TRANSFORM -> transformNeeded = true;
          case NO_CHANGE, PAINT_ONLY -> { }
        }
      }

      transitionRan = true;
      transitionImpact = Objects.requireNonNull(transitions.tick(), "transition impact");
      if (transitionImpact == TransitionImpact.TRANSFORM) transformNeeded = true;

      if (layoutNeeded) {
        layoutRan = true;
        layoutResult = Objects.requireNonNull(layout.layout(frame), "layout result");
        if (!layoutResult.successful()) {
          FramePreparation.Status status =
              layoutResult.status() == LayoutResult.Status.UNCONVERGED
                  ? FramePreparation.Status.UNCONVERGED
                  : FramePreparation.Status.FAILED;
          return result(status, source.revision(), styleRan, transitionRan, true, false,
              false, styleImpact, transitionImpact, layoutResult, layoutResult.failure());
        }
        transformNeeded = true;
      }

      if (transformNeeded) {
        transformRan = true;
        layout.resolveTransforms(frame);
      }

      boolean renderRequired = source.paintDirty()
          || styleImpact != StyleImpact.NO_CHANGE
          || transitionImpact != TransitionImpact.NO_CHANGE
          || layoutRan
          || transformRan;
      if (!frame.completePreparation(source.revision(), styleRan, layoutRan, transformRan)) {
        return result(FramePreparation.Status.SUPERSEDED, source.revision(), styleRan,
            transitionRan, layoutRan, transformRan, false, styleImpact, transitionImpact,
            layoutResult, null);
      }
      return result(FramePreparation.Status.READY, source.revision(), styleRan, transitionRan,
          layoutRan, transformRan, renderRequired, styleImpact, transitionImpact, layoutResult,
          null);
    } catch (RuntimeException failure) {
      return result(FramePreparation.Status.FAILED, source.revision(), styleRan, transitionRan,
          layoutRan, transformRan, false, styleImpact, transitionImpact, layoutResult, failure);
    } finally {
      exit();
    }
  }

  /** Publishes paint after a successful host render. */
  public boolean publishRendered(Frame frame, FramePreparation preparation) {
    Objects.requireNonNull(frame, "frame");
    Objects.requireNonNull(preparation, "preparation");
    checkOwner();
    if (!preparation.renderable()) {
      throw new IllegalArgumentException("Only a renderable preparation can be published");
    }
    return frame.markPainted(preparation.revision());
  }

  private FramePreparation result(
      FramePreparation.Status status,
      long revision,
      boolean style,
      boolean transition,
      boolean layout,
      boolean transform,
      boolean renderRequired,
      StyleImpact styleImpact,
      TransitionImpact transitionImpact,
      LayoutResult layoutResult,
      Throwable failure) {
    return new FramePreparation(status, revision, style, transition, layout, transform,
        renderRequired, styleImpact, transitionImpact, layoutResult, failure);
  }

  private void enter() {
    checkOwner();
    if (running) throw new IllegalStateException("FramePipeline is non-reentrant");
    running = true;
  }

  private void exit() {
    running = false;
  }

  private void checkOwner() {
    if (Thread.currentThread() != owner) {
      throw new IllegalStateException("FramePipeline is owner-thread confined");
    }
  }
}
