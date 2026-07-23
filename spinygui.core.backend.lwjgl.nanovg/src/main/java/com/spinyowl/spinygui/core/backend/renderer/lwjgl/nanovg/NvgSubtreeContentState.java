package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.nanovg.NanoVG.nvgIntersectScissor;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgTranslate;

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

  private NvgSubtreeContentState(long context) {
    this.context = context;
  }

  private static NvgSubtreeContentState apply(long context, Element element) {
    nvgSave(context);
    if (OverflowUtils.clipsAny(element)) {
      var position = element.layoutAbsolutePosition();
      var box = element.box();
      var paddingBox = box.paddingBox();
      var borderBox = box.borderBox();
      nvgIntersectScissor(
          context,
          position.x + paddingBox.x() - borderBox.x(),
          position.y + paddingBox.y() - borderBox.y(),
          paddingBox.width(),
          paddingBox.height());
    }
    nvgTranslate(context, -element.scrollLeft(), -element.scrollTop());
    return new NvgSubtreeContentState(context);
  }

  @Override
  public void close() {
    nvgRestore(context);
  }
}
