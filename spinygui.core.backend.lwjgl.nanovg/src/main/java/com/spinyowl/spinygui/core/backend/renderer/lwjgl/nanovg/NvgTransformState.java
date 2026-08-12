package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgTransform;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.style.types.AffineTransform;

/** Balanced NanoVG state for one backend-neutral affine transform. */
final class NvgTransformState implements NvgTransformStateScope {

  interface Factory {
    NvgTransformStateScope apply(long context, AffineTransform transform);
  }

  static final Factory FACTORY = NvgTransformState::apply;

  private final long context;
  private final DiagnosticSession diagnostics;
  private final NvgTextCommandSink commands;

  private NvgTransformState(long context, DiagnosticSession diagnostics) {
    this.context = context;
    this.diagnostics = diagnostics;
    commands = null;
  }

  private NvgTransformState(long context, NvgTextCommandSink commands) {
    this.context = context;
    diagnostics = null;
    this.commands = commands;
  }

  static NvgTransformState apply(long context, AffineTransform transform) {
    return apply(context, transform, DiagnosticSession.disabled());
  }

  static NvgTransformState apply(
      long context, AffineTransform transform, DiagnosticSession diagnostics) {
    diagnostics.increment(NvgDiagnosticCounter.SAVE_CALLS);
    nvgSave(context);
    diagnostics.increment(NvgDiagnosticCounter.TRANSFORM_CALLS);
    nvgTransform(context, transform.a(), transform.b(), transform.c(), transform.d(), transform.tx(), transform.ty());
    return new NvgTransformState(context, diagnostics);
  }

  static NvgTransformState apply(
      long context, AffineTransform transform, NvgTextCommandSink commands) {
    commands.beginTransform(context);
    commands.transform(
        context,
        transform.a(),
        transform.b(),
        transform.c(),
        transform.d(),
        transform.tx(),
        transform.ty());
    return new NvgTransformState(context, commands);
  }

  @Override
  public void close() {
    if (commands == null) {
      diagnostics.increment(NvgDiagnosticCounter.RESTORE_CALLS);
      nvgRestore(context);
    } else {
      commands.endTransform(context);
    }
  }
}

interface NvgTransformStateScope extends AutoCloseable {
  @Override
  void close();
}
