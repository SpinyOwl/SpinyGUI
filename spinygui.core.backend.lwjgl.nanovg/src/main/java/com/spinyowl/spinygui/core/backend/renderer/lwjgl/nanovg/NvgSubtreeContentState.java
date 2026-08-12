package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.nanovg.NanoVG.nvgIntersectScissor;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgTranslate;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.util.OverflowUtils;

/**
 * Balanced render state for an element's child content.
 *
 * <p>The state is entered after the element background/border is painted and before descendants
 * are traversed. Its clip is therefore transformed with the element, while scroll translation is
 * limited to descendants and never affects the element's scrollbar paint.
 */
final class NvgSubtreeContentState implements NvgRenderer.SubtreeContentState {

  static final NvgRenderer.SubtreeContentState.Factory FACTORY = NvgSubtreeContentState::apply;

  private final long context;
  private final DiagnosticSession diagnostics;
  private final NvgTextCommandSink commands;

  private NvgSubtreeContentState(long context, DiagnosticSession diagnostics) {
    this.context = context;
    this.diagnostics = diagnostics;
    commands = null;
  }

  private NvgSubtreeContentState(long context, NvgTextCommandSink commands) {
    this.context = context;
    diagnostics = null;
    this.commands = commands;
  }

  private static NvgSubtreeContentState apply(long context, Element element) {
    return apply(context, element, DiagnosticSession.disabled());
  }

  static NvgSubtreeContentState apply(
      long context, Element element, DiagnosticSession diagnostics) {
    diagnostics.increment(NvgDiagnosticCounter.SAVE_CALLS);
    nvgSave(context);
    if (OverflowUtils.clipsAny(element)) {
      var position = element.layoutAbsolutePosition();
      var box = element.box();
      var paddingBox = box.paddingBox();
      var borderBox = box.borderBox();
      diagnostics.increment(NvgDiagnosticCounter.INTERSECT_SCISSOR_CALLS);
      nvgIntersectScissor(
          context,
          position.x + paddingBox.x() - borderBox.x(),
          position.y + paddingBox.y() - borderBox.y(),
          paddingBox.width(),
          paddingBox.height());
    }
    diagnostics.increment(NvgDiagnosticCounter.TRANSLATE_CALLS);
    nvgTranslate(context, -element.scrollLeft(), -element.scrollTop());
    return new NvgSubtreeContentState(context, diagnostics);
  }

  static NvgSubtreeContentState apply(
      long context, Element element, NvgTextCommandSink commands) {
    commands.beginTransform(context);
    if (OverflowUtils.clipsAny(element)) {
      var position = element.layoutAbsolutePosition();
      var box = element.box();
      var paddingBox = box.paddingBox();
      var borderBox = box.borderBox();
      commands.intersectScissor(
          context,
          position.x + paddingBox.x() - borderBox.x(),
          position.y + paddingBox.y() - borderBox.y(),
          paddingBox.width(),
          paddingBox.height());
    }
    commands.translate(context, -element.scrollLeft(), -element.scrollTop());
    return new NvgSubtreeContentState(context, commands);
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
