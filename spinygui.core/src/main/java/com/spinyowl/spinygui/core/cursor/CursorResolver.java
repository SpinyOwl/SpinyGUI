package com.spinyowl.spinygui.core.cursor;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.CURSOR;

import com.spinyowl.spinygui.core.node.ButtonElement;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.types.CursorType;
import com.spinyowl.spinygui.core.system.input.ScrollbarInteraction;
import com.spinyowl.spinygui.core.util.NodeUtilities;
import org.joml.Vector2fc;

/** Selects the effective pointer shape from authored style and current control semantics. */
public final class CursorResolver {
  private CursorResolver() {}

  /** Resolves the cursor for the topmost element under a pointer position. */
  public static CursorType resolve(Element root, Vector2fc position) {
    Element target = NodeUtilities.getTargetElement(root, position);
    CursorType targetCursor = styled(target);
    if (targetCursor != null) return targetCursor;
    ScrollbarInteraction.Hit scrollbarHit =
        new ScrollbarInteraction().hit(NodeUtilities.getTargetElementList(root, position), position);
    if (scrollbarHit != null && ScrollbarInteraction.HitPart.THUMB.equals(scrollbarHit.part())) {
      CursorType scrollbarCursor = styled(scrollbarHit.element());
      if (scrollbarCursor != null) return scrollbarCursor;
      return ScrollbarInteraction.Axis.VERTICAL.equals(scrollbarHit.axis())
          ? CursorType.NS_RESIZE
          : CursorType.EW_RESIZE;
    }
    return automatic(target);
  }

  /** Resolves an explicit cursor first, then the bounded default for known controls. */
  public static CursorType resolve(Element target) {
    CursorType styled = styled(target);
    return styled == null ? automatic(target) : styled;
  }

  private static CursorType styled(Element target) {
    if (target == null) return null;
    CursorType styled = target.resolvedStyle().get(CURSOR, CursorType.AUTO);
    return CursorType.AUTO.equals(styled) ? null : styled;
  }

  private static CursorType automatic(Element target) {
    if (target == null) return CursorType.DEFAULT;
    if (target.disabled()) return CursorType.DEFAULT;
    if (target instanceof ButtonElement) return CursorType.POINTER;
    if (target instanceof InputElement input) {
      return input.textInput() ? CursorType.TEXT : CursorType.POINTER;
    }
    return target instanceof TextareaElement ? CursorType.TEXT : CursorType.DEFAULT;
  }
}
