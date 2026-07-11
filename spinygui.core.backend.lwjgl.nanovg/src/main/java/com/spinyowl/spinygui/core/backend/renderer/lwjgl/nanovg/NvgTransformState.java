package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgTransform;

import com.spinyowl.spinygui.core.style.types.AffineTransform;

/** Balanced NanoVG state for one backend-neutral affine transform. */
final class NvgTransformState implements NvgTransformStateScope {

  interface Factory {
    NvgTransformStateScope apply(long context, AffineTransform transform);
  }

  static final Factory FACTORY = NvgTransformState::apply;

  private final long context;

  private NvgTransformState(long context) {
    this.context = context;
  }

  static NvgTransformState apply(long context, AffineTransform transform) {
    nvgSave(context);
    nvgTransform(context, transform.a(), transform.b(), transform.c(), transform.d(), transform.tx(), transform.ty());
    return new NvgTransformState(context);
  }

  @Override
  public void close() {
    nvgRestore(context);
  }
}

interface NvgTransformStateScope extends AutoCloseable {
  @Override
  void close();
}
