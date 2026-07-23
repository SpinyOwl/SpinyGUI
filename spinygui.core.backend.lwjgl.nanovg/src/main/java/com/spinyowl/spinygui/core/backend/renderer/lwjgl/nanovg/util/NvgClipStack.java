package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util;

import static org.lwjgl.nanovg.NanoVG.nvgIntersectScissor;
import static org.lwjgl.nanovg.NanoVG.nvgResetScissor;
import static org.lwjgl.nanovg.NanoVG.nvgScissor;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.util.OverflowUtils;
import java.util.Iterator;
import java.util.LinkedList;
import lombok.NonNull;

public class NvgClipStack {

  private final ClipSink sink;

  public NvgClipStack(@NonNull ClipSink sink) {
    this.sink = sink;
  }

  public void create(long context, Node node) {
    if (node != null) {
      createByParent(context, node.offsetParent());
    }
  }

  public void createByParent(long context, Node parent) {
    if (parent == null) {
      return;
    }

    var parents = new LinkedList<Element>();
    Node current = parent;
    while (current != null) {
      if (current instanceof Element element && OverflowUtils.clipsAny(element)) {
        parents.add(element);
      }
      current = current.offsetParent();
    }

    if (parents.isEmpty()) {
      return;
    }

    Iterator<Element> descendingIterator = parents.descendingIterator();
    clip(context, descendingIterator.next(), true);
    while (descendingIterator.hasNext()) {
      clip(context, descendingIterator.next(), false);
    }
  }

  public void reset(long context) {
    sink.reset(context);
  }

  private void clip(long context, Element element, boolean first) {
    var absolutePosition = element.layoutAbsolutePosition();
    var box = element.box();
    var paddingBox = box.paddingBox();
    var borderBox = box.borderBox();
    float x = absolutePosition.x() + paddingBox.x() - borderBox.x();
    float y = absolutePosition.y() + paddingBox.y() - borderBox.y();
    if (first) {
      sink.scissor(context, x, y, paddingBox.width(), paddingBox.height());
    } else {
      sink.intersectScissor(context, x, y, paddingBox.width(), paddingBox.height());
    }
  }

  public interface ClipSink {
    void scissor(long context, float x, float y, float width, float height);

    void intersectScissor(long context, float x, float y, float width, float height);

    void reset(long context);
  }

  public static final class NanoVgClipSink implements ClipSink {

    @Override
    public void scissor(long context, float x, float y, float width, float height) {
      nvgScissor(context, x, y, width, height);
    }

    @Override
    public void intersectScissor(long context, float x, float y, float width, float height) {
      nvgIntersectScissor(context, x, y, width, height);
    }

    @Override
    public void reset(long context) {
      nvgResetScissor(context);
    }
  }
}
