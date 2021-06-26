package com.spinyowl.spinygui.core.style;

import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.style.types.Background;
import com.spinyowl.spinygui.core.style.types.BorderRadius;
import com.spinyowl.spinygui.core.style.types.BoxShadow;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.HorizontalAlign;
import com.spinyowl.spinygui.core.style.types.Padding;
import com.spinyowl.spinygui.core.style.types.PointerEvents;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.VerticalAlign;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.border.Border;
import com.spinyowl.spinygui.core.style.types.flex.Flex;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class NodeStyle {

  @NonNull private Flex flex = new Flex();

  @NonNull private Background background = new Background();

  @NonNull private Border border = new Border();

  @NonNull private BorderRadius borderRadius = new BorderRadius();

  @NonNull private Padding padding = new Padding();

  private Unit marginTop;
  private Unit marginBottom;
  private Unit marginLeft;
  private Unit marginRight;

  @NonNull private Display display = Display.FLEX;

  @NonNull private Position position = Position.RELATIVE;

  @NonNull private Color color;

  @NonNull private Unit width;
  @NonNull private Unit height;

  @NonNull private Length<?> minWidth;
  @NonNull private Length<?> minHeight;

  @NonNull private Length<?> maxWidth;
  @NonNull private Length<?> maxHeight;

  @NonNull private Unit top;
  @NonNull private Unit bottom;
  @NonNull private Unit right;
  @NonNull private Unit left;

  @NonNull private WhiteSpace whiteSpace = WhiteSpace.NORMAL;

  @NonNull private Set<String> fontFamily = new LinkedHashSet<>();
  @NonNull private Length<?> fontSize;
  @NonNull private FontStyle fontStyle;
  @NonNull private FontWeight fontWeight;
  @NonNull private FontStretch fontStretch;

  @NonNull private BoxShadow boxShadow;

  /** Horizontal alignment. By default used {@link HorizontalAlign#CENTER}. */
  private HorizontalAlign horizontalAlign;

  /** Vertical alignment. */
  private VerticalAlign verticalAlign;

  /** Pointer events. By default used {@link PointerEvents#AUTO}. */
  private PointerEvents pointerEvents;

  private int tabSize = 4;
  private int zIndex = 0;
  private float opacity = 1;

  public NodeStyle margin(Unit... margin) {
    switch (margin.length) {
      case 0:
        break;
      case 1:
        {
          marginTop = marginRight = marginBottom = marginLeft = margin[0];
          break;
        }
      case 2:
        {
          marginTop = marginBottom = margin[0];
          marginRight = marginLeft = margin[1];
          break;
        }
      case 3:
        {
          marginTop = margin[0];
          marginRight = marginLeft = margin[1];
          marginBottom = margin[2];
          break;
        }
      case 4:
      default:
        {
          marginTop = margin[0];
          marginRight = margin[1];
          marginBottom = margin[2];
          marginLeft = margin[3];
          break;
        }
    }
    return this;
  }

  public Unit[] margin() {
    return new Unit[] {marginTop, marginRight, marginBottom, marginLeft};
  }
}
