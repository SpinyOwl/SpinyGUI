package com.spinyowl.spinygui.core.style.manager;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.*;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Conservative classifier for an already-computed resolved-style map delta. */
final class ResolvedStyleImpactClassifier {
  private static final Set<String> PAINT_ONLY =
      Set.of(
          COLOR,
          BACKGROUND_COLOR,
          BACKGROUND_IMAGE,
          BACKGROUND_POSITION_X,
          BACKGROUND_POSITION_Y,
          BACKGROUND_SIZE,
          BACKGROUND_REPEAT,
          BACKGROUND_ORIGIN,
          BORDER_BOTTOM_LEFT_RADIUS,
          BORDER_BOTTOM_RIGHT_RADIUS,
          BORDER_TOP_LEFT_RADIUS,
          BORDER_TOP_RIGHT_RADIUS,
          BOX_SHADOW,
          BORDER_LEFT_COLOR,
          BORDER_RIGHT_COLOR,
          BORDER_TOP_COLOR,
          BORDER_BOTTOM_COLOR,
          POINTER_EVENTS,
          Z_INDEX,
          OPACITY);

  private static final Set<String> TRANSFORM_ONLY = Set.of(TRANSFORM, TRANSFORM_ORIGIN);

  private static final Set<String> LAYOUT_AFFECTING =
      Set.of(
          FONT_FAMILY,
          FONT_SIZE,
          FONT_STYLE,
          FONT_WEIGHT,
          PADDING_TOP,
          PADDING_RIGHT,
          PADDING_BOTTOM,
          PADDING_LEFT,
          MARGIN_TOP,
          MARGIN_RIGHT,
          MARGIN_BOTTOM,
          MARGIN_LEFT,
          BORDER_LEFT_WIDTH,
          BORDER_RIGHT_WIDTH,
          BORDER_TOP_WIDTH,
          BORDER_BOTTOM_WIDTH,
          BORDER_LEFT_STYLE,
          BORDER_RIGHT_STYLE,
          BORDER_TOP_STYLE,
          BORDER_BOTTOM_STYLE,
          ALIGN_CONTENT,
          ALIGN_ITEMS,
          ALIGN_SELF,
          FLEX_BASIS,
          FLEX_DIRECTION,
          FLEX_GROW,
          FLEX_SHRINK,
          FLEX_WRAP,
          JUSTIFY_CONTENT,
          JUSTIFY_ITEMS,
          JUSTIFY_SELF,
          GRID_AUTO_COLUMNS,
          GRID_AUTO_FLOW,
          GRID_AUTO_ROWS,
          GRID_COLUMN_END,
          GRID_COLUMN_GAP,
          GRID_COLUMN_START,
          GRID_ROW_END,
          GRID_ROW_GAP,
          GRID_ROW_START,
          GRID_TEMPLATE_AREAS,
          GRID_TEMPLATE_COLUMNS,
          GRID_TEMPLATE_ROWS,
          WIDTH,
          HEIGHT,
          LINE_HEIGHT,
          TEXT_ALIGN,
          MIN_WIDTH,
          MIN_HEIGHT,
          MAX_WIDTH,
          MAX_HEIGHT,
          DISPLAY,
          POSITION,
          TOP,
          BOTTOM,
          RIGHT,
          LEFT,
          WHITE_SPACE,
          TAB_SIZE,
          OVERFLOW,
          OVERFLOW_X,
          OVERFLOW_Y,
          OVERFLOW_WRAP,
          WORD_BREAK);

  private ResolvedStyleImpactClassifier() {}

  static StyleImpact classify(Map<String, Object> before, Map<String, Object> after) {
    StyleImpact impact = StyleImpact.NO_CHANGE;
    for (Map.Entry<String, Object> entry : before.entrySet()) {
      if (!Objects.equals(entry.getValue(), after.get(entry.getKey()))) {
        impact = impact.combine(classify(entry.getKey()));
      }
    }
    for (Map.Entry<String, Object> entry : after.entrySet()) {
      if (!before.containsKey(entry.getKey())) {
        impact = impact.combine(classify(entry.getKey()));
      }
    }
    return impact;
  }

  private static StyleImpact classify(String property) {
    if (PAINT_ONLY.contains(property)) return StyleImpact.PAINT_ONLY;
    if (TRANSFORM_ONLY.contains(property)) return StyleImpact.TRANSFORM;
    if (LAYOUT_AFFECTING.contains(property)) return StyleImpact.LAYOUT;
    return StyleImpact.FULL_UNKNOWN;
  }
}
