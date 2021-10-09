package com.spinyowl.spinygui.core.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_CONTENT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_ITEMS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_SELF;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_IMAGE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_ORIGIN;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_POSITION_X;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_POSITION_Y;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_REPEAT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_SIZE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_LEFT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_RIGHT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_LEFT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_RIGHT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BOTTOM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BOX_SHADOW;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.DISPLAY;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_BASIS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_DIRECTION;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_GROW;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_SHRINK;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_WRAP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_FAMILY;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_SIZE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_WEIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.HEIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.JUSTIFY_CONTENT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.LEFT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_BOTTOM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_LEFT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_RIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_TOP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MAX_HEIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MAX_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MIN_HEIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MIN_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OPACITY;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_BOTTOM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_LEFT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_RIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_TOP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.POINTER_EVENTS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.POSITION;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.RIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TAB_SIZE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TOP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WHITE_SPACE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.Z_INDEX;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.Declaration;
import com.spinyowl.spinygui.core.style.stylesheet.RuleSet;
import com.spinyowl.spinygui.core.style.types.BoxShadow;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.PointerEvents;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.background.BackgroundOrigin;
import com.spinyowl.spinygui.core.style.types.background.BackgroundRepeat;
import com.spinyowl.spinygui.core.style.types.background.BackgroundSize;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.flex.AlignContent;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Length.PixelLength;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CalculatedStyle {

  @NonNull private final Element element;
  /** List of rulesets that applicable to element, sorted by specificity */
  private List<RuleSet> rules = List.of();

  private final Map<String, Object> styles = new HashMap<>();

  /**
   * Used to update list of rules.
   *
   * @param rules new list of rules.
   */
  public void rules(@NonNull List<RuleSet> rules) {
    if (!this.rules.equals(rules)) {
      this.rules = List.copyOf(rules);

      for (RuleSet rule : rules) {
        for (Declaration declaration : rule.declarations()) {
          declaration.compute(element, styles);
        }
      }
    }
  }

  /**
   * Returns list of rules.
   *
   * @return list of rules.
   */
  public List<RuleSet> rules() {
    return Collections.unmodifiableList(rules);
  }

  /**
   * Returns map of styles, where key is property name and value is calculated property value.
   *
   * @return map of property key to calculated property values.
   */
  public Map<String, Object> styles() {
    if (styles.isEmpty()) {
      rules(rules);
    }
    return styles;
  }

  /**
   * Used to get property value by property key with automatic cast to specific type.
   *
   * @param property property name to get.
   * @param <T> type to cast.
   * @return property value.
   */
  @SuppressWarnings("unchecked")
  public <T> T get(String property) {
    return (T) styles().get(property);
  }

  /**
   * Used to get property value by property key with automatic cast to specific type.
   *
   * @param property property name to get.
   * @return property value.
   */
  public Object getSafe(String property) {
    return styles().get(property);
  }

  public Color color() {
    return get(COLOR);
  }

  public Color backgroundColor() {
    return get(BACKGROUND_COLOR);
  }

  public String backgroundImage() {
    return get(BACKGROUND_IMAGE);
  }

  public Length backgroundPositionX() {
    return get(BACKGROUND_POSITION_X);
  }

  public Length backgroundPositionY() {
    return get(BACKGROUND_POSITION_Y);
  }

  public BackgroundSize backgroundSize() {
    return get(BACKGROUND_SIZE);
  }

  public BackgroundRepeat backgroundRepeat() {
    return get(BACKGROUND_REPEAT);
  }

  public BackgroundOrigin backgroundOrigin() {
    return get(BACKGROUND_ORIGIN);
  }

  public Length borderBottomLeftRadius() {
    return get(BORDER_BOTTOM_LEFT_RADIUS);
  }

  public Length borderBottomRightRadius() {
    return get(BORDER_BOTTOM_RIGHT_RADIUS);
  }

  public Length borderTopLeftRadius() {
    return get(BORDER_TOP_LEFT_RADIUS);
  }

  public Length borderTopRightRadius() {
    return get(BORDER_TOP_RIGHT_RADIUS);
  }

  public BoxShadow boxShadow() {
    return get(BOX_SHADOW);
  }

  public String fontFamily() {
    return get(FONT_FAMILY);
  }

  public Length fontSize() {
    return get(FONT_SIZE);
  }

  public FontStyle fontStyle() {
    return get(FONT_STYLE);
  }

  public FontWeight fontWeight() {
    return get(FONT_WEIGHT);
  }

  public Length paddingTop() {
    return get(PADDING_TOP);
  }

  public Length paddingRight() {
    return get(PADDING_RIGHT);
  }

  public Length paddingBottom() {
    return get(PADDING_BOTTOM);
  }

  public Length paddingLeft() {
    return get(PADDING_LEFT);
  }

  public Unit marginTop() {
    return get(MARGIN_TOP);
  }

  public Unit marginRight() {
    return get(MARGIN_RIGHT);
  }

  public Unit marginBottom() {
    return get(MARGIN_BOTTOM);
  }

  public Unit marginLeft() {
    return get(MARGIN_LEFT);
  }

  public PixelLength borderLeftWidth() {
    return get(BORDER_LEFT_WIDTH);
  }

  public PixelLength borderRightWidth() {
    return get(BORDER_RIGHT_WIDTH);
  }

  public PixelLength borderTopWidth() {
    return get(BORDER_TOP_WIDTH);
  }

  public PixelLength borderBottomWidth() {
    return get(BORDER_BOTTOM_WIDTH);
  }

  public Color borderLeftColor() {
    return get(BORDER_LEFT_COLOR);
  }

  public Color borderRightColor() {
    return get(BORDER_RIGHT_COLOR);
  }

  public Color borderTopColor() {
    return get(BORDER_TOP_COLOR);
  }

  public Color borderBottomColor() {
    return get(BORDER_BOTTOM_COLOR);
  }

  public BorderStyle borderLeftStyle() {
    return get(BORDER_LEFT_STYLE);
  }

  public BorderStyle borderRightStyle() {
    return get(BORDER_RIGHT_STYLE);
  }

  public BorderStyle borderTopStyle() {
    return get(BORDER_TOP_STYLE);
  }

  public BorderStyle borderBottomStyle() {
    return get(BORDER_BOTTOM_STYLE);
  }

  public AlignContent alignContent() {
    return get(ALIGN_CONTENT);
  }

  public AlignItems alignItems() {
    return get(ALIGN_ITEMS);
  }

  public AlignSelf alignSelf() {
    return get(ALIGN_SELF);
  }

  public Unit flexBasis() {
    return get(FLEX_BASIS);
  }

  public FlexDirection flexDirection() {
    return get(FLEX_DIRECTION);
  }

  public Integer flexGrow() {
    return get(FLEX_GROW);
  }

  public Integer flexShrink() {
    return get(FLEX_SHRINK);
  }

  public FlexWrap flexWrap() {
    return get(FLEX_WRAP);
  }

  public JustifyContent justifyContent() {
    return get(JUSTIFY_CONTENT);
  }

  public Unit width() {
    return get(WIDTH);
  }

  public Unit height() {
    return get(HEIGHT);
  }

  public Length minWidth() {
    return get(MIN_WIDTH);
  }

  public Length minHeight() {
    return get(MIN_HEIGHT);
  }

  public Length maxWidth() {
    return get(MAX_WIDTH);
  }

  public Length maxHeight() {
    return get(MAX_HEIGHT);
  }

  public Display display() {
    return get(DISPLAY);
  }

  public Position position() {
    return get(POSITION);
  }

  public Unit top() {
    return get(TOP);
  }

  public Unit bottom() {
    return get(BOTTOM);
  }

  public Unit right() {
    return get(RIGHT);
  }

  public Unit left() {
    return get(LEFT);
  }

  public WhiteSpace whiteSpace() {
    return get(WHITE_SPACE);
  }

  public PointerEvents pointerEvents() {
    return get(POINTER_EVENTS);
  }

  public Integer zIndex() {
    return get(Z_INDEX);
  }

  public Integer tabSize() {
    return get(TAB_SIZE);
  }

  public Float opacity() {
    return get(OPACITY);
  }
}
