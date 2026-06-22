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
import com.spinyowl.spinygui.core.style.types.length.Length;
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
        rawLonghand(GRID_TEMPLATE_COLUMNS, NONE, GridPropertyProvider::testTrackListOrNone),
        rawLonghand(GRID_TEMPLATE_ROWS, NONE, GridPropertyProvider::testTrackListOrNone),
        rawLonghand(GRID_TEMPLATE_AREAS, NONE, GridPropertyProvider::testTemplateAreas),
        rawLonghand(GRID_AUTO_COLUMNS, AUTO, GridPropertyProvider::testTrackList),
        rawLonghand(GRID_AUTO_ROWS, AUTO, GridPropertyProvider::testTrackList),
        rawLonghand(GRID_AUTO_FLOW, ROW, GridPropertyProvider::testAutoFlow),
        rawLonghand(GRID_COLUMN_START, AUTO, GridPropertyProvider::testPlacement),
        rawLonghand(GRID_COLUMN_END, AUTO, GridPropertyProvider::testPlacement),
        rawLonghand(GRID_ROW_START, AUTO, GridPropertyProvider::testPlacement),
        rawLonghand(GRID_ROW_END, AUTO, GridPropertyProvider::testPlacement),
        gapLonghand(GRID_COLUMN_GAP),
        gapLonghand(GRID_ROW_GAP),
        gapLonghand(COLUMN_GAP),
        gapLonghand(ROW_GAP),
        axisPlacementShorthand(GRID_COLUMN, GRID_COLUMN_START, GRID_COLUMN_END),
        axisPlacementShorthand(GRID_ROW, GRID_ROW_START, GRID_ROW_END),
        gridGapShorthand(),
        rawShorthand(GRID_AREA, GridPropertyProvider::testArea),
        rawShorthand(GRID_TEMPLATE, GridPropertyProvider::testTemplate),
        rawShorthand(GRID, GridPropertyProvider::testGrid));
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

  private static void updateAxisPlacement(
      Term<?> term, String start, String end, Map<String, Object> styles) {
    if (term instanceof TermList termList && Operator.SLASH.equals(termList.operator())) {
      if (startsWithSpanPlacement(termList)) {
        styles.put(start, new TermList(Operator.SPACE, termList.get(0), termList.get(1)));
        styles.put(end, afterSpanStart(termList));
      } else {
        styles.put(start, termList.get(0));
        styles.put(end, afterSlash(termList));
      }
    } else {
      styles.put(start, term);
      styles.put(end, AUTO);
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
    return isIdent(term, "none") || term instanceof TermList;
  }

  private static boolean testGrid(Term<?> term) {
    return isIdent(term, "none") || term instanceof TermList;
  }

  private static boolean isIdent(Term<?> term, String expected) {
    return term instanceof TermIdent termIdent && expected.equalsIgnoreCase(termIdent.value());
  }
}
