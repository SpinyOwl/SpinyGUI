package com.spinyowl.spinygui.core.style;

import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
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
import com.spinyowl.spinygui.core.style.types.background.BackgroundOrigin;
import com.spinyowl.spinygui.core.style.types.background.BackgroundSize;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.flex.AlignContent;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class NodeStyle {

  // -------------------------- FLEX STYLE GROUP -------------------------------
  /** Specifies the direction of the flexible items */
  @NonNull private FlexDirection flexDirection = FlexDirection.ROW;

  /**
   * Specifies the alignment between the items inside a flexible container when the items do not use
   * all available space.
   */
  @NonNull private JustifyContent justifyContent = JustifyContent.FLEX_START;

  /** Specifies the alignment for items inside a flexible container. */
  @NonNull private AlignItems alignItems = AlignItems.STRETCH;

  /** Specifies whether the flexible items should wrap or not. */
  @NonNull private FlexWrap flexWrap = FlexWrap.NOWRAP;

  /**
   * Specifies the alignment between the lines inside a flexible container when the items do not use
   * all available space.
   */
  @NonNull private AlignContent alignContent = AlignContent.STRETCH;

  /** Specifies the alignment for selected items inside a flexible container. */
  @NonNull private AlignSelf alignSelf = AlignSelf.AUTO;

  /** A number specifying how much the item will grow relative to the rest of the flexible items. */
  private int flexGrow;

  /**
   * A number specifying how much the item will shrink relative to the rest of the flexible items.
   */
  private int flexShrink;

  /** The length of the item. Legal values: a number in px. */
  @NonNull private Unit flexBasis = Length.ZERO;

  // -------------------------- BACKGROUND STYLE GROUP -------------------------------
  @NonNull private Color backgroundColor = Color.TRANSPARENT;
  private String backgroundImage;
  private Length<?> backgroundPositionX;
  private Length<?> backgroundPositionY;
  private BackgroundSize backgroundSize;
  private BackgroundOrigin backgroundOrigin = BackgroundOrigin.PADDING_BOX;

  @NonNull private Color borderTopColor = Color.TRANSPARENT;
  @NonNull private BorderStyle borderTopStyle = BorderStyle.NONE;
  @NonNull private Length<?> borderTopWidth = Length.ZERO;
  @NonNull private Color borderRightColor = Color.TRANSPARENT;
  @NonNull private BorderStyle borderRightStyle = BorderStyle.NONE;
  @NonNull private Length<?> borderRightWidth = Length.ZERO;
  @NonNull private Color borderBottomColor = Color.TRANSPARENT;
  @NonNull private BorderStyle borderBottomStyle = BorderStyle.NONE;
  @NonNull private Length<?> borderBottomWidth = Length.ZERO;
  @NonNull private Color borderLeftColor = Color.TRANSPARENT;
  @NonNull private BorderStyle borderLeftStyle = BorderStyle.NONE;
  @NonNull private Length<?> borderLeftWidth = Length.ZERO;

  @NonNull private BorderRadius borderRadius = new BorderRadius();

  @NonNull private Padding padding = new Padding();

  // -------------------------- MARGIN STYLE GROUP -------------------------------
  private Unit marginTop;
  private Unit marginBottom;
  private Unit marginLeft;
  private Unit marginRight;

  @NonNull private Display display = Display.FLEX;

  @NonNull private Position position = Position.RELATIVE;

  @NonNull private Color color;

  // -------------------------- SIZE STYLE GROUP -------------------------------
  @NonNull private Unit width;
  @NonNull private Unit height;

  @NonNull private Length<?> minWidth;
  @NonNull private Length<?> minHeight;

  @NonNull private Length<?> maxWidth;
  @NonNull private Length<?> maxHeight;

  // -------------------------- POSITION STYLE GROUP -------------------------------
  @NonNull private Unit top;
  @NonNull private Unit bottom;
  @NonNull private Unit right;
  @NonNull private Unit left;

  // -------------------------- TEXT-RELATED STYLE GROUP -------------------------------
  @NonNull private WhiteSpace whiteSpace = WhiteSpace.NORMAL;

  @NonNull private Set<String> fontFamily = new LinkedHashSet<>();
  @NonNull private Length<?> fontSize;
  @NonNull private FontStyle fontStyle;
  @NonNull private FontWeight fontWeight;
  @NonNull private FontStretch fontStretch;
  /** Horizontal alignment. By default used {@link HorizontalAlign#CENTER}. */
  private HorizontalAlign horizontalAlign;
  /** Vertical alignment. */
  private VerticalAlign verticalAlign;

  private int tabSize = 4;

  @NonNull private BoxShadow boxShadow;

  /** Pointer events. By default used {@link PointerEvents#AUTO}. */
  private PointerEvents pointerEvents;

  private int zIndex = 0;
  private float opacity = 1;

  public Unit[] margin() {
    return new Unit[] {marginTop, marginRight, marginBottom, marginLeft};
  }

  public void margin(Unit... margin) {
    setOneFourArray(
        margin, this::marginTop, this::marginRight, this::marginBottom, this::marginLeft);
  }

  public Color[] borderColor() {
    return new Color[] {borderTopColor, borderRightColor, borderBottomColor, borderLeftColor};
  }

  public void borderColor(Color... colors) {
    setOneFourArray(
        colors,
        this::borderTopColor,
        this::borderRightColor,
        this::borderBottomColor,
        this::borderLeftColor);
  }

  public BorderStyle[] borderStyle() {
    return new BorderStyle[] {borderTopStyle, borderRightStyle, borderBottomStyle, borderLeftStyle};
  }

  public void borderStyle(BorderStyle[] styles) {
    setOneFourArray(
        styles,
        this::borderTopStyle,
        this::borderRightStyle,
        this::borderBottomStyle,
        this::borderLeftStyle);
  }

  public Length[] borderWidth() {
    return new Length[] {borderTopWidth, borderRightWidth, borderBottomWidth, borderLeftWidth};
  }

  public void borderWidth(Length... widths) {
    setOneFourArray(
        widths,
        this::borderTopWidth,
        this::borderRightWidth,
        this::borderBottomWidth,
        this::borderLeftWidth);
  }

  public void flex(int flexGrow, int flexShrink, Unit flexBasis) {
    flexGrow(flexGrow);
    flexShrink(flexShrink);
    flexBasis(flexBasis);
  }

  public void flexFlow(FlexDirection flexDirection, FlexWrap flexWrap) {
    flexDirection(flexDirection);
    flexWrap(flexWrap);
  }

  private static <T> void setOneFourArray(
      @NonNull T[] values,
      @NonNull Consumer<T> topSetter,
      @NonNull Consumer<T> rightSetter,
      @NonNull Consumer<T> bottomSetter,
      @NonNull Consumer<T> leftSetter) {
    switch (values.length) {
      case 0:
        break;
      case 1:
        {
          topSetter.accept(values[0]);
          rightSetter.accept(values[0]);
          bottomSetter.accept(values[0]);
          leftSetter.accept(values[0]);
          break;
        }
      case 2:
        {
          topSetter.accept(values[0]);
          rightSetter.accept(values[1]);
          bottomSetter.accept(values[0]);
          leftSetter.accept(values[1]);
          break;
        }
      case 3:
        {
          topSetter.accept(values[0]);
          rightSetter.accept(values[1]);
          bottomSetter.accept(values[2]);
          leftSetter.accept(values[1]);
          break;
        }
      case 4:
      default:
        {
          topSetter.accept(values[0]);
          rightSetter.accept(values[1]);
          bottomSetter.accept(values[2]);
          leftSetter.accept(values[3]);
        }
    }
  }
}
