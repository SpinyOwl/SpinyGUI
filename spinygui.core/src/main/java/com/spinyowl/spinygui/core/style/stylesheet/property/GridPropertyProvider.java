package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.COLUMN_GAP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_AREA;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_AUTO_COLUMNS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_AUTO_FLOW;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_AUTO_ROWS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_COLUMN;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_COLUMN_END;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_COLUMN_GAP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_COLUMN_START;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_GAP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_ROW;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_ROW_END;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_ROW_GAP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_ROW_START;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_TEMPLATE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_TEMPLATE_AREAS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_TEMPLATE_COLUMNS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_TEMPLATE_ROWS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ROW_GAP;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFunction;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermGridFraction;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermInteger;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import com.spinyowl.spinygui.core.style.types.grid.GridAutoFlow;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.grid.GridFraction;
import com.spinyowl.spinygui.core.style.types.grid.GridPlacement;
import com.spinyowl.spinygui.core.style.types.grid.GridTemplateAreas;
import com.spinyowl.spinygui.core.style.types.grid.GridTrack;
import com.spinyowl.spinygui.core.style.types.grid.GridTrackList;
import com.spinyowl.spinygui.core.style.types.grid.GridTrackRepeat;
import com.spinyowl.spinygui.core.style.types.grid.GridTrackSize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GridPropertyProvider implements PropertyProvider {
  private static final Term<?> AUTO = new TermIdent("auto");
  private static final Term<?> NONE = new TermIdent("none");
  private static final Term<?> ROW = new TermIdent("row");
  private static final Term<?> ZERO = new TermLength(Length.ZERO);
  private static final Set<String> TRACK_KEYWORDS = Set.of("auto", "min-content", "max-content");
  private static final Set<String> FLOW_KEYWORDS = Set.of("row", "column", "dense");
  private static final Set<String> GRID_FUNCTIONS = Set.of("repeat", "minmax", "fit-content");

  @Override
  public List<Property> getProperties() {
    return List.of(
        trackListLonghand(GRID_TEMPLATE_COLUMNS, NONE, true),
        trackListLonghand(GRID_TEMPLATE_ROWS, NONE, true),
        templateAreasLonghand(),
        trackListLonghand(GRID_AUTO_COLUMNS, AUTO, false),
        trackListLonghand(GRID_AUTO_ROWS, AUTO, false),
        autoFlowLonghand(),
        placementLonghand(GRID_COLUMN_START),
        placementLonghand(GRID_COLUMN_END),
        placementLonghand(GRID_ROW_START),
        placementLonghand(GRID_ROW_END),
        gapLonghand(GRID_COLUMN_GAP),
        gapLonghand(GRID_ROW_GAP),
        gapLonghand(COLUMN_GAP),
        gapLonghand(ROW_GAP),
        axisPlacementShorthand(GRID_COLUMN, GRID_COLUMN_START, GRID_COLUMN_END),
        axisPlacementShorthand(GRID_ROW, GRID_ROW_START, GRID_ROW_END),
        gridGapShorthand(),
        gridAreaShorthand(),
        gridTemplateShorthand(),
        gridShorthand());
  }

  private static Property rawLonghand(
      String name, Term<?> defaultValue, Property.Validator validator) {
    return Property.builder()
        .name(name)
        .defaultValue(defaultValue)
        .updater((term, styles) -> styles.put(name, term))
        .validator(validator)
        .build();
  }

  private static Property trackListLonghand(String name, Term<?> defaultValue, boolean allowNone) {
    return Property.builder()
        .name(name)
        .defaultValue(defaultValue)
        .updater((term, styles) -> styles.put(name, parseTrackList(term, allowNone)))
        .validator(term -> canParseTrackList(term, allowNone))
        .build();
  }

  private static Property templateAreasLonghand() {
    return Property.builder()
        .name(GRID_TEMPLATE_AREAS)
        .defaultValue(NONE)
        .updater((term, styles) -> styles.put(GRID_TEMPLATE_AREAS, parseTemplateAreas(term)))
        .validator(GridPropertyProvider::canParseTemplateAreas)
        .build();
  }

  private static Property autoFlowLonghand() {
    return Property.builder()
        .name(GRID_AUTO_FLOW)
        .defaultValue(ROW)
        .updater((term, styles) -> styles.put(GRID_AUTO_FLOW, parseAutoFlow(term)))
        .validator(term -> parseAutoFlow(term) != null)
        .build();
  }

  private static Property placementLonghand(String name) {
    return Property.builder()
        .name(name)
        .defaultValue(AUTO)
        .updater((term, styles) -> styles.put(name, parsePlacement(term)))
        .validator(term -> parsePlacement(term) != null)
        .build();
  }

  private static Property gapLonghand(String name) {
    return Property.builder()
        .name(name)
        .defaultValue(ZERO)
        .updater((term, styles) -> styles.put(name, term.value()))
        .validator(TermLength.class::isInstance)
        .build();
  }

  private static Property rawShorthand(String name, Property.Validator validator) {
    return Property.builder()
        .name(name)
        .defaultValue(NONE)
        .updater((term, styles) -> styles.put(name, term))
        .validator(validator)
        .shorthand(true)
        .build();
  }

  private static Property axisPlacementShorthand(String name, String start, String end) {
    return Property.builder()
        .name(name)
        .defaultValue(AUTO)
        .updater((term, styles) -> updateAxisPlacement(term, start, end, styles))
        .validator(GridPropertyProvider::testAxisPlacement)
        .shorthand(true)
        .build();
  }

  private static Property gridGapShorthand() {
    return Property.builder()
        .name(GRID_GAP)
        .defaultValue(ZERO)
        .updater(GridPropertyProvider::updateGridGap)
        .validator(GridPropertyProvider::testGapShorthand)
        .shorthand(true)
        .build();
  }

  private static Property gridAreaShorthand() {
    return Property.builder()
        .name(GRID_AREA)
        .defaultValue(AUTO)
        .updater(GridPropertyProvider::updateGridArea)
        .validator(GridPropertyProvider::testArea)
        .shorthand(true)
        .build();
  }

  private static Property gridTemplateShorthand() {
    return Property.builder()
        .name(GRID_TEMPLATE)
        .defaultValue(NONE)
        .updater(GridPropertyProvider::updateGridTemplate)
        .validator(GridPropertyProvider::testTemplate)
        .shorthand(true)
        .build();
  }

  private static Property gridShorthand() {
    return Property.builder()
        .name(GRID)
        .defaultValue(NONE)
        .updater(GridPropertyProvider::updateGridTemplate)
        .validator(GridPropertyProvider::testGrid)
        .shorthand(true)
        .build();
  }

  private static void updateAxisPlacement(
      Term<?> term, String start, String end, Map<String, Object> styles) {
    if (term instanceof TermList termList && Operator.SLASH.equals(termList.operator())) {
      if (startsWithSpanPlacement(termList)) {
        styles.put(start, parsePlacement(new TermList(Operator.SPACE, termList.get(0), termList.get(1))));
        styles.put(end, parsePlacement(afterSpanStart(termList)));
      } else {
        styles.put(start, parsePlacement(termList.get(0)));
        styles.put(end, parsePlacement(afterSlash(termList)));
      }
    } else {
      styles.put(start, parsePlacement(term));
      styles.put(end, GridPlacement.AUTO);
    }
  }

  private static boolean startsWithSpanPlacement(TermList termList) {
    return termList.size() >= 3
        && termList.get(0) instanceof TermIdent ident
        && "span".equalsIgnoreCase(ident.value());
  }

  private static Term<?> afterSpanStart(TermList termList) {
    if (termList.size() == 3) {
      return termList.get(2);
    }
    return new TermList(Operator.SPACE, termList.terms().subList(2, termList.terms().size()));
  }

  private static Term<?> afterSlash(TermList termList) {
    if (termList.size() == 2) {
      return termList.get(1);
    }
    return new TermList(Operator.SPACE, termList.terms().subList(1, termList.terms().size()));
  }

  private static void updateGridGap(Term<?> term, Map<String, Object> styles) {
    if (term instanceof TermList termList) {
      styles.put(GRID_ROW_GAP, termList.get(0).value());
      styles.put(ROW_GAP, termList.get(0).value());
      styles.put(GRID_COLUMN_GAP, termList.get(1).value());
      styles.put(COLUMN_GAP, termList.get(1).value());
    } else {
      styles.put(GRID_ROW_GAP, term.value());
      styles.put(ROW_GAP, term.value());
      styles.put(GRID_COLUMN_GAP, term.value());
      styles.put(COLUMN_GAP, term.value());
    }
  }

  private static void updateGridArea(Term<?> term, Map<String, Object> styles) {
    if (term instanceof TermList termList && Operator.SLASH.equals(termList.operator())) {
      styles.put(GRID_ROW_START, parsePlacement(termList.get(0)));
      styles.put(GRID_COLUMN_START, termList.size() > 1 ? parsePlacement(termList.get(1)) : GridPlacement.AUTO);
      styles.put(GRID_ROW_END, termList.size() > 2 ? parsePlacement(termList.get(2)) : GridPlacement.AUTO);
      styles.put(GRID_COLUMN_END, termList.size() > 3 ? parsePlacement(termList.get(3)) : GridPlacement.AUTO);
    } else {
      GridPlacement placement = parsePlacement(term);
      styles.put(GRID_ROW_START, placement);
      styles.put(GRID_COLUMN_START, GridPlacement.AUTO);
      styles.put(GRID_ROW_END, GridPlacement.AUTO);
      styles.put(GRID_COLUMN_END, GridPlacement.AUTO);
    }
  }

  private static void updateGridTemplate(Term<?> term, Map<String, Object> styles) {
    if (isIdent(term, "none")) {
      styles.put(GRID_TEMPLATE_ROWS, GridTrackList.NONE);
      styles.put(GRID_TEMPLATE_COLUMNS, GridTrackList.NONE);
      styles.put(GRID_TEMPLATE_AREAS, GridTemplateAreas.NONE);
      return;
    }
    if (term instanceof TermList termList && Operator.SLASH.equals(termList.operator()) && termList.size() == 2) {
      styles.put(GRID_TEMPLATE_ROWS, parseTrackList(termList.get(0), true));
      styles.put(GRID_TEMPLATE_COLUMNS, parseTrackList(termList.get(1), true));
      styles.put(GRID_TEMPLATE_AREAS, GridTemplateAreas.NONE);
    }
  }

  private static boolean testTrackListOrNone(Term<?> term) {
    return isIdent(term, "none") || testTrackList(term);
  }

  private static boolean testTrackList(Term<?> term) {
    if (testTrackSize(term)) {
      return true;
    }
    if (term instanceof TermList termList) {
      return Operator.SPACE.equals(termList.operator())
          && !termList.isEmpty()
          && termList.terms().stream().allMatch(GridPropertyProvider::testTrackSize);
    }
    return false;
  }

  private static boolean testTrackSize(Term<?> term) {
    if (term instanceof TermLength || term instanceof TermGridFraction) {
      return true;
    }
    if (term instanceof TermIdent termIdent) {
      return TRACK_KEYWORDS.contains(termIdent.value().toLowerCase());
    }
    if (term instanceof TermFunction function) {
      return GRID_FUNCTIONS.contains(function.name().toLowerCase());
    }
    return false;
  }

  private static boolean testTemplateAreas(Term<?> term) {
    if (isIdent(term, "none")) {
      return true;
    }
    if (term instanceof TermIdent) {
      return true;
    }
    if (term instanceof TermList termList) {
      return Operator.SPACE.equals(termList.operator())
          && !termList.isEmpty()
          && termList.terms().stream().allMatch(TermIdent.class::isInstance);
    }
    return false;
  }

  private static boolean testAutoFlow(Term<?> term) {
    if (term instanceof TermIdent termIdent) {
      return FLOW_KEYWORDS.contains(termIdent.value().toLowerCase());
    }
    if (term instanceof TermList termList) {
      return Operator.SPACE.equals(termList.operator())
          && termList.size() == 2
          && termList.terms().stream().allMatch(GridPropertyProvider::testAutoFlow);
    }
    return false;
  }

  private static boolean testAxisPlacement(Term<?> term) {
    if (testPlacement(term)) {
      return true;
    }
    if (term instanceof TermList termList) {
      return Operator.SLASH.equals(termList.operator())
          && termList.size() >= 2;
    }
    return false;
  }

  private static boolean testPlacement(Term<?> term) {
    if (term instanceof TermInteger) {
      return true;
    }
    if (term instanceof TermIdent termIdent) {
      String value = termIdent.value().toLowerCase();
      return "auto".equals(value) || !"span".equals(value);
    }
    if (term instanceof TermList termList) {
      return Operator.SPACE.equals(termList.operator())
          && termList.size() == 2
          && termList.get(0) instanceof TermIdent ident
          && "span".equalsIgnoreCase(ident.value())
          && (termList.get(1) instanceof TermInteger || termList.get(1) instanceof TermIdent);
    }
    return false;
  }

  private static boolean testGapShorthand(Term<?> term) {
    if (term instanceof TermLength) {
      return true;
    }
    if (term instanceof TermList termList) {
      return Operator.SPACE.equals(termList.operator())
          && termList.size() == 2
          && termList.terms().stream().allMatch(TermLength.class::isInstance);
    }
    return false;
  }

  private static boolean testArea(Term<?> term) {
    if (testPlacement(term)) {
      return true;
    }
    if (term instanceof TermList termList) {
      return Operator.SLASH.equals(termList.operator())
          && termList.size() >= 1
          && termList.size() <= 4
          && termList.terms().stream().allMatch(GridPropertyProvider::testPlacement);
    }
    return false;
  }

  private static boolean testTemplate(Term<?> term) {
    return isIdent(term, "none")
        || (term instanceof TermList termList
            && Operator.SLASH.equals(termList.operator())
            && termList.size() == 2
            && canParseTrackList(termList.get(0), true)
            && canParseTrackList(termList.get(1), true));
  }

  private static boolean testGrid(Term<?> term) {
    return testTemplate(term);
  }

  private static boolean isIdent(Term<?> term, String expected) {
    return term instanceof TermIdent termIdent && expected.equalsIgnoreCase(termIdent.value());
  }

  private static boolean canParseTrackList(Term<?> term, boolean allowNone) {
    try {
      parseTrackList(term, allowNone);
      return true;
    } catch (IllegalArgumentException ignored) {
      return false;
    }
  }

  private static GridTrackList parseTrackList(Term<?> term, boolean allowNone) {
    if (allowNone && isIdent(term, "none")) {
      return GridTrackList.NONE;
    }
    List<GridTrack> tracks = parseTracks(term);
    if (tracks.isEmpty()) {
      throw new IllegalArgumentException("Grid track list cannot be empty");
    }
    return GridTrackList.of(tracks);
  }

  private static List<GridTrack> parseTracks(Term<?> term) {
    if (term instanceof TermList termList && Operator.SPACE.equals(termList.operator())) {
      List<GridTrack> tracks = new ArrayList<>();
      for (Term<?> child : termList.terms()) {
        tracks.addAll(parseTracks(child));
      }
      return tracks;
    }
    if (term instanceof TermFunction function
        && "repeat".equalsIgnoreCase(function.name())
        && Operator.COMMA.equals(function.operator())
        && function.size() == 2
        && function.get(0) instanceof TermInteger repeatCount) {
      return GridTrackRepeat.expand(repeatCount.value(), parseTracks(function.get(1)));
    }
    return List.of(GridTrack.of(parseTrackSize(term)));
  }

  private static GridTrackSize parseTrackSize(Term<?> term) {
    if (term instanceof TermLength length) {
      return GridTrackSize.fixed(length.value());
    }
    if (term instanceof TermGridFraction fraction) {
      GridFraction value = fraction.value();
      return GridTrackSize.flexible(value);
    }
    if (isIdent(term, "auto") || isIdent(term, "min-content") || isIdent(term, "max-content")) {
      return GridTrackSize.AUTO;
    }
    if (term instanceof TermFunction function) {
      String name = function.name().toLowerCase();
      if ("minmax".equals(name)
          && Operator.COMMA.equals(function.operator())
          && function.size() == 2) {
        return GridTrackSize.minmax(parseTrackSize(function.get(0)), parseTrackSize(function.get(1)));
      }
      if ("fit-content".equals(name) && function.size() == 1 && function.get(0) instanceof TermLength limit) {
        return GridTrackSize.fitContent(limit.value());
      }
    }
    throw new IllegalArgumentException("Unsupported grid track size: " + term);
  }

  private static boolean canParseTemplateAreas(Term<?> term) {
    try {
      parseTemplateAreas(term);
      return true;
    } catch (IllegalArgumentException ignored) {
      return false;
    }
  }

  private static GridTemplateAreas parseTemplateAreas(Term<?> term) {
    if (isIdent(term, "none")) {
      return GridTemplateAreas.NONE;
    }
    if (term instanceof TermIdent ident) {
      return GridTemplateAreas.of(List.of(parseTemplateAreaRow(ident.value())));
    }
    if (term instanceof TermList termList && Operator.SPACE.equals(termList.operator())) {
      List<List<String>> rows = new ArrayList<>();
      for (Term<?> row : termList.terms()) {
        if (!(row instanceof TermIdent ident)) {
          throw new IllegalArgumentException("Grid template area rows must be strings");
        }
        rows.add(parseTemplateAreaRow(ident.value()));
      }
      return GridTemplateAreas.of(rows);
    }
    throw new IllegalArgumentException("Unsupported grid template areas: " + term);
  }

  private static List<String> parseTemplateAreaRow(String row) {
    String cleaned = row.replace('"', ' ').replace('\'', ' ').trim();
    if (cleaned.isEmpty()) {
      throw new IllegalArgumentException("Grid template area row cannot be empty");
    }
    return Arrays.stream(cleaned.split("\\s+")).toList();
  }

  private static GridAutoFlow parseAutoFlow(Term<?> term) {
    if (isIdent(term, "row")) {
      return GridAutoFlow.ROW;
    }
    if (isIdent(term, "column")) {
      return GridAutoFlow.COLUMN;
    }
    if (isIdent(term, "dense")) {
      return GridAutoFlow.ROW_DENSE;
    }
    if (term instanceof TermList termList
        && Operator.SPACE.equals(termList.operator())
        && termList.size() == 2) {
      boolean row = termList.terms().stream().anyMatch(child -> isIdent(child, "row"));
      boolean column = termList.terms().stream().anyMatch(child -> isIdent(child, "column"));
      boolean dense = termList.terms().stream().anyMatch(child -> isIdent(child, "dense"));
      if (dense && row != column) {
        return row ? GridAutoFlow.ROW_DENSE : GridAutoFlow.COLUMN_DENSE;
      }
    }
    return null;
  }

  private static GridPlacement parsePlacement(Term<?> term) {
    if (isIdent(term, "auto")) {
      return GridPlacement.AUTO;
    }
    if (term instanceof TermInteger integer) {
      return GridPlacement.line(integer.value());
    }
    if (term instanceof TermIdent ident && !"span".equalsIgnoreCase(ident.value())) {
      return GridPlacement.line(ident.value());
    }
    if (term instanceof TermList termList
        && Operator.SPACE.equals(termList.operator())
        && termList.size() == 2
        && isIdent(termList.get(0), "span")) {
      if (termList.get(1) instanceof TermInteger integer) {
        return GridPlacement.span(integer.value());
      }
      if (termList.get(1) instanceof TermIdent ident) {
        return GridPlacement.span(ident.value());
      }
    }
    return null;
  }
}
