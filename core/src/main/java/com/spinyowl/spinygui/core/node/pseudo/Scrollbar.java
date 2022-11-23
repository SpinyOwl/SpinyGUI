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
  public static final String NAME = "::scrollbar";

  @NonNull private final Orientation orientation;

  public Scrollbar(@NonNull Orientation orientation, Element scrollbarHolder) {
    super(NAME, scrollbarHolder);
    this.orientation = orientation;

    this.addChild(new ScrollbarButton(ActionType.DECREMENT, scrollbarHolder));
    ScrollbarTrack scrollbarTrack = new ScrollbarTrack(scrollbarHolder);
    scrollbarTrack.addChild(new ScrollbarTrackPiece(ActionType.DECREMENT, scrollbarHolder));
    scrollbarTrack.addChild(new ScrollbarThumb(scrollbarHolder));
    scrollbarTrack.addChild(new ScrollbarTrackPiece(ActionType.INCREMENT, scrollbarHolder));
    this.addChild(scrollbarTrack);
    this.addChild(new ScrollbarButton(ActionType.INCREMENT, scrollbarHolder));
  }

  public static class ScrollbarThumb extends PseudoElement {
    public static final String NAME = "::scrollbar-thumb";

    public ScrollbarThumb(Element scrollbarHolder) {
      super(NAME, scrollbarHolder);
    }
  }

  @Getter
  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class ScrollbarButton extends PseudoElement {
    public static final String NAME = "::scrollbar-button";

    private final ActionType action;

    public ScrollbarButton(ActionType action, Element scrollbarHolder) {
      super(NAME, scrollbarHolder);
      this.action = action;
    }
  }

  @Getter
  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class ScrollbarTrackPiece extends PseudoElement {

    public static final String NAME = "::scrollbar-track-piece";
    private final ActionType action;

    public ScrollbarTrackPiece(ActionType action, Element scrollbarHolder) {
      super(NAME, scrollbarHolder);
      this.action = action;
    }
  }

  public static class ScrollbarTrack extends PseudoElement {
    public static final String NAME = "::scrollbar-track";

    public ScrollbarTrack(Element scrollbarHolder) {
      super(NAME, scrollbarHolder);
    }
  }

  public static class ScrollbarCorner extends PseudoElement {
    public static final String NAME = "::scrollbar-corner";

    public ScrollbarCorner(Element scrollbarHolder) {
      super(NAME, scrollbarHolder);
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
