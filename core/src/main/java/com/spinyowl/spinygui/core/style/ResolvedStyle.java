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
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.LINE_HEIGHT;
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
import com.spinyowl.spinygui.core.style.stylesheet.Ruleset;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResolvedStyle {

  /** List of rules that applicable to element, sorted by specificity. */
  private List<Ruleset> rules = List.of();

  /** Map of styles, where key is property name and value is calculated property value. */
  @Getter private final Map<String, Object> styles = new TreeMap<>();

  /**
   * Used to update list of rules.
   *
   * @param rules new list of rules.
   */
  public void rules(@NonNull List<Ruleset> rules) {
    this.rules = List.copyOf(rules);
  }

  /**
   * Returns list of rules.
   *
   * @return list of rules.
   */
  public List<Ruleset> rules() {
    return Collections.unmodifiableList(rules);
  }

  /**
   * Used to get property value by property key with automatic cast to specific type.
   *
   * @param property property name to get.
   * @param <T> type to cast.
   * @return property value.
   */
  public <T> T get(String property) {
    return get(property, null);
  }

  /**
   * Used to set property value by property key.
   *
   * @param property property name to set.
   * @param value value to set.
   * @param <T> type of value.
   */
  public <T> void set(String property, T value) {
    styles.put(property, value);
  }

  /**
   * Used to get property value by property key with automatic cast to specific type.
   *
   * @param property property name to get.
   * @param defaultValue default value if not found.
   * @param <T> type to cast.
   * @return property value.
   */
  @SuppressWarnings("unchecked")
  public <T> T get(String property, T defaultValue) {
    return (T) styles().getOrDefault(property, defaultValue);
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

  public void color(Color color) {
    set(COLOR, color);
  }

  public Color backgroundColor() {
    return get(BACKGROUND_COLOR);
  }

  public void backgroundColor(Color color) {
    set(BACKGROUND_COLOR, color);
  }

  public String backgroundImage() {
    return get(BACKGROUND_IMAGE);
  }

  public void backgroundImage(String backgroundImage) {
    set(BACKGROUND_IMAGE, backgroundImage);
  }

  @SuppressWarnings("rawtypes")
  public Length backgroundPositionX() {
    return get(BACKGROUND_POSITION_X);
  }

  public void backgroundPositionX(Length<?> length) {
    set(BACKGROUND_POSITION_X, length);
  }

  @SuppressWarnings("rawtypes")
  public Length backgroundPositionY() {
    return get(BACKGROUND_POSITION_Y);
  }

  public void backgroundPositionY(Length<?> length) {
    set(BACKGROUND_POSITION_Y, length);
  }

  public BackgroundSize backgroundSize() {
    return get(BACKGROUND_SIZE);
  }

  public void backgroundSize(BackgroundSize backgroundSize) {
    set(BACKGROUND_SIZE, backgroundSize);
  }

  public BackgroundRepeat backgroundRepeat() {
    return get(BACKGROUND_REPEAT);
  }

  public void backgroundRepeat(BackgroundRepeat backgroundRepeat) {
    set(BACKGROUND_REPEAT, backgroundRepeat);
  }

  public BackgroundOrigin backgroundOrigin() {
    return get(BACKGROUND_ORIGIN);
  }

  public void backgroundOrigin(BackgroundOrigin backgroundOrigin) {
    set(BACKGROUND_ORIGIN, backgroundOrigin);
  }

  @SuppressWarnings("rawtypes")
  public Length borderBottomLeftRadius() {
    return get(BORDER_BOTTOM_LEFT_RADIUS);
  }

  public void borderBottomLeftRadius(Length<?> length) {
    set(BORDER_BOTTOM_LEFT_RADIUS, length);
  }

  @SuppressWarnings("rawtypes")
  public Length borderBottomRightRadius() {
    return get(BORDER_BOTTOM_RIGHT_RADIUS);
  }

  public void borderBottomRightRadius(Length<?> length) {
    set(BORDER_BOTTOM_RIGHT_RADIUS, length);
  }

  @SuppressWarnings("rawtypes")
  public Length borderTopLeftRadius() {
    return get(BORDER_TOP_LEFT_RADIUS);
  }

  public void borderTopLeftRadius(Length<?> length) {
    set(BORDER_TOP_LEFT_RADIUS, length);
  }

  @SuppressWarnings("rawtypes")
  public Length borderTopRightRadius() {
    return get(BORDER_TOP_RIGHT_RADIUS);
  }

  public void borderTopRightRadius(Length<?> length) {
    set(BORDER_TOP_RIGHT_RADIUS, length);
  }

  public BoxShadow boxShadow() {
    return get(BOX_SHADOW);
  }

  public void boxShadow(BoxShadow boxShadow) {
    set(BOX_SHADOW, boxShadow);
  }

  public Set<String> fontFamilies() {
    return get(FONT_FAMILY);
  }

  public void fontFamilies(Set<String> fontFamilies) {
    set(FONT_FAMILY, fontFamilies);
  }

  @SuppressWarnings("rawtypes")
  public Length fontSize() {
    return get(FONT_SIZE);
  }

  public void fontSize(Length<?> length) {
    set(FONT_SIZE, length);
  }

  public FontStyle fontStyle() {
    return get(FONT_STYLE);
  }

  public void fontStyle(FontStyle fontStyle) {
    set(FONT_STYLE, fontStyle);
  }

  public FontWeight fontWeight() {
    return get(FONT_WEIGHT);
  }

  public void fontWeight(FontWeight fontWeight) {
    set(FONT_WEIGHT, fontWeight);
  }

  @SuppressWarnings("rawtypes")
  public Length paddingTop() {
    return get(PADDING_TOP);
  }

  public void paddingTop(Length<?> length) {
    set(PADDING_TOP, length);
  }

  @SuppressWarnings("rawtypes")
  public Length paddingRight() {
    return get(PADDING_RIGHT);
  }

  public void paddingRight(Length<?> length) {
    set(PADDING_RIGHT, length);
  }

  @SuppressWarnings("rawtypes")
  public Length paddingBottom() {
    return get(PADDING_BOTTOM);
  }

  public void paddingBottom(Length<?> length) {
    set(PADDING_BOTTOM, length);
  }

  @SuppressWarnings("rawtypes")
  public Length paddingLeft() {
    return get(PADDING_LEFT);
  }

  public void paddingLeft(Length<?> length) {
    set(PADDING_LEFT, length);
  }

  public Unit marginTop() {
    return get(MARGIN_TOP);
  }

  public void marginTop(Unit unit) {
    set(MARGIN_TOP, unit);
  }

  public Unit marginRight() {
    return get(MARGIN_RIGHT);
  }

  public void marginRight(Unit unit) {
    set(MARGIN_RIGHT, unit);
  }

  public Unit marginBottom() {
    return get(MARGIN_BOTTOM);
  }

  public void marginBottom(Unit unit) {
    set(MARGIN_BOTTOM, unit);
  }

  public Unit marginLeft() {
    return get(MARGIN_LEFT);
  }

  public void marginLeft(Unit unit) {
    set(MARGIN_LEFT, unit);
  }

  public PixelLength borderLeftWidth() {
    return get(BORDER_LEFT_WIDTH);
  }

  public void borderLeftWidth(PixelLength length) {
    set(BORDER_LEFT_WIDTH, length);
  }

  public PixelLength borderRightWidth() {
    return get(BORDER_RIGHT_WIDTH);
  }

  public void borderRightWidth(PixelLength length) {
    set(BORDER_RIGHT_WIDTH, length);
  }

  public PixelLength borderTopWidth() {
    return get(BORDER_TOP_WIDTH);
  }

  public void borderTopWidth(PixelLength length) {
    set(BORDER_TOP_WIDTH, length);
  }

  public PixelLength borderBottomWidth() {
    return get(BORDER_BOTTOM_WIDTH);
  }

  public void borderBottomWidth(PixelLength length) {
    set(BORDER_BOTTOM_WIDTH, length);
  }

  public Color borderLeftColor() {
    return get(BORDER_LEFT_COLOR);
  }

  public void borderLeftColor(Color color) {
    set(BORDER_LEFT_COLOR, color);
  }

  public Color borderRightColor() {
    return get(BORDER_RIGHT_COLOR);
  }

  public void borderRightColor(Color color) {
    set(BORDER_RIGHT_COLOR, color);
  }

  public Color borderTopColor() {
    return get(BORDER_TOP_COLOR);
  }

  public void borderTopColor(Color color) {
    set(BORDER_TOP_COLOR, color);
  }

  public Color borderBottomColor() {
    return get(BORDER_BOTTOM_COLOR);
  }

  public void borderBottomColor(Color color) {
    set(BORDER_BOTTOM_COLOR, color);
  }

  public BorderStyle borderLeftStyle() {
    return get(BORDER_LEFT_STYLE);
  }

  public void borderLeftStyle(BorderStyle borderStyle) {
    set(BORDER_LEFT_STYLE, borderStyle);
  }

  public BorderStyle borderRightStyle() {
    return get(BORDER_RIGHT_STYLE);
  }

  public void borderRightStyle(BorderStyle borderStyle) {
    set(BORDER_RIGHT_STYLE, borderStyle);
  }

  public BorderStyle borderTopStyle() {
    return get(BORDER_TOP_STYLE);
  }

  public void borderTopStyle(BorderStyle borderStyle) {
    set(BORDER_TOP_STYLE, borderStyle);
  }

  public BorderStyle borderBottomStyle() {
    return get(BORDER_BOTTOM_STYLE);
  }

  public void borderBottomStyle(BorderStyle borderStyle) {
    set(BORDER_BOTTOM_STYLE, borderStyle);
  }

  public AlignContent alignContent() {
    return get(ALIGN_CONTENT);
  }

  public void alignContent(AlignContent alignContent) {
    set(ALIGN_CONTENT, alignContent);
  }

  public AlignItems alignItems() {
    return get(ALIGN_ITEMS);
  }

  public void alignItems(AlignItems alignItems) {
    set(ALIGN_ITEMS, alignItems);
  }

  public AlignSelf alignSelf() {
    return get(ALIGN_SELF);
  }

  public void alignSelf(AlignSelf alignSelf) {
    set(ALIGN_SELF, alignSelf);
  }

  public Unit flexBasis() {
    return get(FLEX_BASIS);
  }

  public void flexBasis(Unit unit) {
    set(FLEX_BASIS, unit);
  }

  public FlexDirection flexDirection() {
    return get(FLEX_DIRECTION);
  }

  public void flexDirection(FlexDirection flexDirection) {
    set(FLEX_DIRECTION, flexDirection);
  }

  public Integer flexGrow() {
    return get(FLEX_GROW);
  }

  public void flexGrow(Integer flexGrow) {
    set(FLEX_GROW, flexGrow);
  }

  public Integer flexShrink() {
    return get(FLEX_SHRINK);
  }

  public void flexShrink(Integer flexShrink) {
    set(FLEX_SHRINK, flexShrink);
  }

  public FlexWrap flexWrap() {
    return get(FLEX_WRAP);
  }

  public void flexWrap(FlexWrap flexWrap) {
    set(FLEX_WRAP, flexWrap);
  }

  public JustifyContent justifyContent() {
    return get(JUSTIFY_CONTENT);
  }

  public void justifyContent(JustifyContent justifyContent) {
    set(JUSTIFY_CONTENT, justifyContent);
  }

  public Unit width() {
    return get(WIDTH);
  }

  public void width(Unit unit) {
    set(WIDTH, unit);
  }

  public Unit height() {
    return get(HEIGHT);
  }

  public void height(Unit unit) {
    set(HEIGHT, unit);
  }

  @SuppressWarnings("rawtypes")
  public Length minWidth() {
    return get(MIN_WIDTH);
  }

  @SuppressWarnings("rawtypes")
  public void minWidth(Length length) {
    set(MIN_WIDTH, length);
  }

  @SuppressWarnings("rawtypes")
  public Length minHeight() {
    return get(MIN_HEIGHT);
  }

  @SuppressWarnings("rawtypes")
  public void minHeight(Length length) {
    set(MIN_HEIGHT, length);
  }

  @SuppressWarnings("rawtypes")
  public Length maxWidth() {
    return get(MAX_WIDTH);
  }

  @SuppressWarnings("rawtypes")
  public void maxWidth(Length length) {
    set(MAX_WIDTH, length);
  }

  @SuppressWarnings("rawtypes")
  public Length maxHeight() {
    return get(MAX_HEIGHT);
  }

  @SuppressWarnings("rawtypes")
  public void maxHeight(Length length) {
    set(MAX_HEIGHT, length);
  }

  public Display display() {
    return get(DISPLAY);
  }

  public void display(Display display) {
    set(DISPLAY, display);
  }

  public Position position() {
    return get(POSITION, Position.RELATIVE);
  }

  public void position(Position position) {
    set(POSITION, position);
  }

  public Unit top() {
    return get(TOP);
  }

  public void top(Unit unit) {
    set(TOP, unit);
  }

  public Unit bottom() {
    return get(BOTTOM);
  }

  public void bottom(Unit unit) {
    set(BOTTOM, unit);
  }

  public Unit right() {
    return get(RIGHT);
  }

  public void right(Unit unit) {
    set(RIGHT, unit);
  }

  public Unit left() {
    return get(LEFT);
  }

  public void left(Unit unit) {
    set(LEFT, unit);
  }

  public WhiteSpace whiteSpace() {
    return get(WHITE_SPACE);
  }

  public void whiteSpace(WhiteSpace whiteSpace) {
    set(WHITE_SPACE, whiteSpace);
  }

  public PointerEvents pointerEvents() {
    return get(POINTER_EVENTS);
  }

  public void pointerEvents(PointerEvents pointerEvents) {
    set(POINTER_EVENTS, pointerEvents);
  }

  public Integer zIndex() {
    return get(Z_INDEX);
  }

  public void zIndex(Integer zIndex) {
    set(Z_INDEX, zIndex);
  }

  public Integer tabSize() {
    return get(TAB_SIZE);
  }

  public void tabSize(Integer tabSize) {
    set(TAB_SIZE, tabSize);
  }

  public Float opacity() {
    return get(OPACITY);
  }

  public void opacity(Float opacity) {
    set(OPACITY, opacity);
  }

  public Float lineHeight() {
    return get(LINE_HEIGHT);
  }

  public void lineHeight(Float lineHeight) {
    set(LINE_HEIGHT, lineHeight);
  }
}
