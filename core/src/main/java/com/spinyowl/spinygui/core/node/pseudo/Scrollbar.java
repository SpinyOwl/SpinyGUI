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

  public Scrollbar(@NonNull Orientation orientation, Element pseudoParent) {
    super("::scrollbar", pseudoParent);
    this.orientation = orientation;

    this.parent = pseudoParent;

    this.addChild(new ScrollbarButton(ActionType.DECREMENT, pseudoParent));
    ScrollbarTrack scrollbarTrack = new ScrollbarTrack(pseudoParent);
    scrollbarTrack.addChild(new ScrollbarTrackPiece(ActionType.DECREMENT, pseudoParent));
    scrollbarTrack.addChild(new ScrollbarThumb(pseudoParent));
    scrollbarTrack.addChild(new ScrollbarTrackPiece(ActionType.INCREMENT, pseudoParent));
    this.addChild(scrollbarTrack);
    this.addChild(new ScrollbarButton(ActionType.INCREMENT, pseudoParent));
  }

  public static class ScrollbarThumb extends PseudoElement {

    public ScrollbarThumb(Element pseudoParent) {
      super("::scrollbar-thumb", pseudoParent);
    }
  }

  @Getter
  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class ScrollbarButton extends PseudoElement {

    private final ActionType action;

    public ScrollbarButton(ActionType action, Element pseudoParent) {
      super("::scrollbar-button", pseudoParent);
      this.action = action;
    }
  }

  @Getter
  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class ScrollbarTrackPiece extends PseudoElement {

    private final ActionType action;

    public ScrollbarTrackPiece(ActionType action, Element pseudoParent) {
      super("::scrollbar-track-piece", pseudoParent);
      this.action = action;
    }
  }

  public static class ScrollbarTrack extends PseudoElement {

    public ScrollbarTrack(Element pseudoParent) {
      super("::scrollbar-track", pseudoParent);
    }
  }

  public static class ScrollbarCorner extends PseudoElement {

    public ScrollbarCorner(Element pseudoParent) {
      super("::scrollbar-corner", pseudoParent);
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
