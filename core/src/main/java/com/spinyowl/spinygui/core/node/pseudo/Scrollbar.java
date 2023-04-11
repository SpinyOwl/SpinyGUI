package com.spinyowl.spinygui.core.node.pseudo;

import com.spinyowl.spinygui.core.node.Element;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Scrollbar extends PseudoElement {

  @NonNull private final Orientation orientation;

  public Scrollbar(@NonNull Orientation orientation, Element scrollbarHolder) {
    super("::scrollbar", scrollbarHolder::resolvedStyle);
    this.orientation = orientation;

    this.parent = scrollbarHolder;

    this.addChild(new ScrollbarButton(ActionType.DECREMENT, scrollbarHolder));
    ScrollbarTrack scrollbarTrack = new ScrollbarTrack(scrollbarHolder);
    scrollbarTrack.addChild(new ScrollbarTrackPiece(ActionType.DECREMENT, scrollbarHolder));
    scrollbarTrack.addChild(new ScrollbarThumb(scrollbarHolder));
    scrollbarTrack.addChild(new ScrollbarTrackPiece(ActionType.INCREMENT, scrollbarHolder));
    this.addChild(scrollbarTrack);
    this.addChild(new ScrollbarButton(ActionType.INCREMENT, scrollbarHolder));
  }

  public static class ScrollbarThumb extends PseudoElement {

    public ScrollbarThumb(Element scrollbarHolder) {
      super("::scrollbar-thumb", scrollbarHolder::resolvedStyle);
    }
  }

  @Getter
  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class ScrollbarButton extends PseudoElement {

    private final ActionType action;

    public ScrollbarButton(ActionType action, Element scrollbarHolder) {
      super("::scrollbar-button", scrollbarHolder::resolvedStyle);
      this.action = action;
    }
  }

  @Getter
  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class ScrollbarTrackPiece extends PseudoElement {

    private final ActionType action;

    public ScrollbarTrackPiece(ActionType action, Element scrollbarHolder) {
      super("::scrollbar-track-piece", scrollbarHolder::resolvedStyle);
      this.action = action;
    }
  }

  public static class ScrollbarTrack extends PseudoElement {

    public ScrollbarTrack(Element scrollbarHolder) {
      super("::scrollbar-track", scrollbarHolder::resolvedStyle);
    }
  }

  public static class ScrollbarCorner extends PseudoElement {

    public ScrollbarCorner(Element scrollbarHolder) {
      super("::scrollbar-corner", scrollbarHolder::resolvedStyle);
    }
  }

  public enum Orientation {
    HORIZONTAL,
    VERTICAL
  }

  public enum ActionType {
    INCREMENT,
    DECREMENT
  }
}
