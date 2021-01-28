package com.spinyowl.spinygui.core.node.style;

import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.node.style.types.Background;
import com.spinyowl.spinygui.core.node.style.types.BorderRadius;
import com.spinyowl.spinygui.core.node.style.types.BoxShadow;
import com.spinyowl.spinygui.core.node.style.types.Color;
import com.spinyowl.spinygui.core.node.style.types.Display;
import com.spinyowl.spinygui.core.node.style.types.HorizontalAlign;
import com.spinyowl.spinygui.core.node.style.types.Margin;
import com.spinyowl.spinygui.core.node.style.types.Padding;
import com.spinyowl.spinygui.core.node.style.types.PointerEvents;
import com.spinyowl.spinygui.core.node.style.types.Position;
import com.spinyowl.spinygui.core.node.style.types.VerticalAlign;
import com.spinyowl.spinygui.core.node.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.node.style.types.border.Border;
import com.spinyowl.spinygui.core.node.style.types.flex.Flex;
import com.spinyowl.spinygui.core.node.style.types.length.Length;
import com.spinyowl.spinygui.core.node.style.types.length.Unit;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class NodeStyle {

  @NonNull
  private Flex flex = new Flex();

  @NonNull
  private Background background = new Background();

  @NonNull
  private Border border = new Border();

  @NonNull
  private BorderRadius borderRadius = new BorderRadius();

  @NonNull
  private Padding padding = new Padding();

  @NonNull
  private Margin margin = new Margin();

  @NonNull
  private Display display = Display.FLEX;

  @NonNull
  private Position position = Position.RELATIVE;

  @NonNull
  private Color color;

  @NonNull
  private Unit width;
  @NonNull
  private Unit height;

  @NonNull
  private Length<?> minWidth;
  @NonNull
  private Length<?> minHeight;

  @NonNull
  private Length<?> maxWidth;
  @NonNull
  private Length<?> maxHeight;

  @NonNull
  private Unit top;
  @NonNull
  private Unit bottom;
  @NonNull
  private Unit right;
  @NonNull
  private Unit left;

  @NonNull
  private WhiteSpace whiteSpace = WhiteSpace.NORMAL;

  @NonNull
  private Set<String> fontFamily = new LinkedHashSet<>();
  @NonNull
  private Length<?> fontSize;
  @NonNull
  private FontStyle fontStyle;
  @NonNull
  private FontWeight fontWeight;
  @NonNull
  private FontStretch fontStretch;

  @NonNull
  private BoxShadow boxShadow;

  /**
   * Horizontal alignment. By default used {@link HorizontalAlign#CENTER}.
   */
  private HorizontalAlign horizontalAlign;

  /**
   * Vertical alignment.
   */
  private VerticalAlign verticalAlign;

  /**
   * Pointer events. By default used {@link PointerEvents#AUTO}.
   */
  private PointerEvents pointerEvents;

  private int tabSize = 4;
  private int zIndex = 0;
  private float opacity = 1;

}
