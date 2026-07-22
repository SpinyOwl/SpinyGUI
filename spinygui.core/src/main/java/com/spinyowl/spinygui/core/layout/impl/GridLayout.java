package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.layout.impl.LayoutUtils.hasPosition;
import static com.spinyowl.spinygui.core.style.types.Position.ABSOLUTE;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;

import com.spinyowl.spinygui.core.layout.ElementLayout;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.layout.Edges;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;
import com.spinyowl.spinygui.core.style.types.grid.GridAutoFlow;
import com.spinyowl.spinygui.core.style.types.grid.GridPlacement;
import com.spinyowl.spinygui.core.style.types.grid.GridTemplateAreas;
import com.spinyowl.spinygui.core.style.types.grid.GridTrack;
import com.spinyowl.spinygui.core.style.types.grid.GridTrackList;
import com.spinyowl.spinygui.core.style.types.grid.GridTrackSize;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GridLayout implements ElementLayout {
  private static final float EPSILON = 0.0001f;

  @NonNull private final BlockLayout blockLayout;
  @NonNull private final LayoutService layoutService;

  @Override
  public void layout(Element parent, LayoutContext context) {
    blockLayout.layout(parent, true, context);
    layoutService.layoutChildNodes(parent, context);

    List<Element> items = gridItems(parent);
    if (items.isEmpty()) {
      return;
    }

    ResolvedStyle style = parent.resolvedStyle();
    Rect content = localContentArea(parent);
    float columnGap = resolveLength(style.gridColumnGap(), content.width());
    float rowGap = resolveLength(style.gridRowGap(), content.height());
    PlacementResult placement = placeItems(items, style);
    List<Float> columns =
        resolveTracks(
            style.gridTemplateColumns(),
            style.gridAutoColumns(),
            placement.columnCount(),
            content.width(),
            columnGap,
            placement.items(),
            Axis.COLUMN);
    List<Float> rows =
        resolveTracks(
            style.gridTemplateRows(),
            style.gridAutoRows(),
            placement.rowCount(),
            content.height(),
            rowGap,
            placement.items(),
            Axis.ROW);
    applyItemBoxes(content, columns, rows, columnGap, rowGap, placement.items(), style);
    if (parent.resolvedStyle().height().isAuto()) {
      float height = sum(rows) + rowGap * Math.max(0, rows.size() - 1);
      parent.box().content().height(height);
    }
  }

  private Rect localContentArea(Element element) {
    Edges border = element.box().border();
    Edges padding = element.box().padding();
    Rect content = element.box().content();
    return new Rect(
        border.left() + padding.left(),
        border.top() + padding.top(),
        content.width(),
        content.height());
  }

  private List<Element> gridItems(Element parent) {
    List<Element> items = new ArrayList<>();
    for (Node node : parent.childNodes()) {
      if (node instanceof Element element && visible(element) && !hasPosition(element, ABSOLUTE)) {
        items.add(element);
      }
    }
    return items;
  }

  private PlacementResult placeItems(List<Element> elements, ResolvedStyle style) {
    Map<Element, GridItem> items = new HashMap<>();
    Occupancy occupancy = new Occupancy(Math.max(1, style.gridTemplateRows().tracks().size()), Math.max(1, style.gridTemplateColumns().tracks().size()));
    for (Element element : elements) {
      GridItem requested = requestedPlacement(element, style.gridTemplateAreas());
      if (requested.definiteRow() && requested.definiteColumn()) {
        occupancy.occupy(requested);
        items.put(element, requested);
      }
    }

    GridAutoFlow flow = style.gridAutoFlow();
    boolean columnFlow = flow.direction() == GridAutoFlow.Direction.COLUMN;
    boolean dense = flow.dense();
    Cursor cursor = new Cursor();
    for (Element element : elements) {
      if (items.containsKey(element)) {
        continue;
      }
      GridItem requested = requestedPlacement(element, style.gridTemplateAreas());
      GridItem placed = occupancy.place(requested, columnFlow, dense ? new Cursor() : cursor);
      items.put(element, placed);
    }

    int rows = 1;
    int columns = 1;
    for (GridItem item : items.values()) {
      rows = Math.max(rows, item.rowEnd());
      columns = Math.max(columns, item.columnEnd());
    }
    return new PlacementResult(
        elements.stream().map(element -> new PlacedItem(element, items.get(element))).toList(),
        rows,
        columns);
  }

  private GridItem requestedPlacement(Element element, GridTemplateAreas templateAreas) {
    ResolvedStyle style = element.resolvedStyle();
    GridTemplateAreas.AreaRange area = namedArea(style, templateAreas);
    if (area != null) {
      return new GridItem(area.rowStart(), area.rowEnd(), area.columnStart(), area.columnEnd());
    }
    Range rows = range(style.gridRowStart(), style.gridRowEnd());
    Range columns = range(style.gridColumnStart(), style.gridColumnEnd());
    return new GridItem(rows.start(), rows.end(), columns.start(), columns.end());
  }

  private GridTemplateAreas.AreaRange namedArea(ResolvedStyle style, GridTemplateAreas templateAreas) {
    if (style.gridRowStart() instanceof GridPlacement.Line line
        && line.name() != null
        && style.gridRowEnd() == GridPlacement.AUTO
        && style.gridColumnStart() == GridPlacement.AUTO
        && style.gridColumnEnd() == GridPlacement.AUTO) {
      return templateAreas.areas().get(line.name());
    }
    return null;
  }

  private Range range(GridPlacement startPlacement, GridPlacement endPlacement) {
    Integer start = lineIndex(startPlacement);
    Integer end = lineIndex(endPlacement);
    int span = spanCount(startPlacement, endPlacement);
    if (start != null && end != null) {
      int normalizedStart = Math.min(start, end);
      int normalizedEnd = Math.max(start, end);
      return new Range(normalizedStart, Math.max(normalizedStart + 1, normalizedEnd));
    }
    if (start != null) {
      return new Range(start, start + span);
    }
    if (end != null) {
      return new Range(end - span, end);
    }
    return new Range(null, null, span);
  }

  private Integer lineIndex(GridPlacement placement) {
    if (placement instanceof GridPlacement.Line line && line.index() != null) {
      int index = line.index();
      return index > 0 ? index - 1 : index;
    }
    return null;
  }

  private int spanCount(GridPlacement startPlacement, GridPlacement endPlacement) {
    if (startPlacement instanceof GridPlacement.Span span && span.count() != null) {
      return span.count();
    }
    if (endPlacement instanceof GridPlacement.Span span && span.count() != null) {
      return span.count();
    }
    return 1;
  }

  private List<Float> resolveTracks(
      GridTrackList explicit,
      GridTrackList implicit,
      int required,
      float available,
      float gap,
      List<PlacedItem> items,
      Axis axis) {
    List<GridTrackSize> sizes = new ArrayList<>(explicit.tracks().stream().map(GridTrack::size).toList());
    GridTrackSize implicitSize =
        implicit.tracks().isEmpty() ? GridTrackSize.AUTO : implicit.tracks().getFirst().size();
    while (sizes.size() < required) {
      sizes.add(implicitSize);
    }

    List<Float> resolved = new ArrayList<>();
    float fixed = gap * Math.max(0, sizes.size() - 1);
    float flexTotal = 0;
    for (int i = 0; i < sizes.size(); i++) {
      GridTrackSize size = sizes.get(i);
      float flex = flexibleFactor(size);
      if (flex > 0f) {
        float base = flexibleBase(size, available, items, axis, i);
        flexTotal += flex;
        resolved.add(base);
        fixed += base;
      } else {
        float value = resolveTrack(size, available, items, axis, i);
        resolved.add(value);
        fixed += value;
      }
    }
    float free = Math.max(0, available - fixed);
    if (flexTotal > EPSILON) {
      for (int i = 0; i < sizes.size(); i++) {
        float flex = flexibleFactor(sizes.get(i));
        if (flex > 0f) {
          resolved.set(i, resolved.get(i) + free * flex / flexTotal);
        }
      }
    }
    return resolved;
  }

  private float flexibleFactor(GridTrackSize size) {
    if (size instanceof GridTrackSize.Flexible flexible) {
      return flexible.fraction().value();
    }
    if (size instanceof GridTrackSize.MinMax minMax
        && minMax.max() instanceof GridTrackSize.Flexible flexible) {
      return flexible.fraction().value();
    }
    return 0f;
  }

  private float flexibleBase(
      GridTrackSize size, float available, List<PlacedItem> items, Axis axis, int index) {
    if (size instanceof GridTrackSize.MinMax minMax) {
      return resolveTrack(minMax.min(), available, items, axis, index);
    }
    return 0f;
  }

  private float resolveTrack(
      GridTrackSize size, float available, List<PlacedItem> items, Axis axis, int index) {
    if (size instanceof GridTrackSize.Fixed fixed) {
      return resolveLength(fixed.length(), available);
    }
    if (size instanceof GridTrackSize.MinMax minMax) {
      float min = resolveTrack(minMax.min(), available, items, axis, index);
      float max = resolveTrack(minMax.max(), available, items, axis, index);
      return Math.max(min, max);
    }
    if (size instanceof GridTrackSize.FitContent fitContent) {
      return Math.min(resolveAutoTrack(items, axis, index), resolveLength(fitContent.limit(), available));
    }
    return resolveAutoTrack(items, axis, index);
  }

  private float resolveAutoTrack(List<PlacedItem> items, Axis axis, int index) {
    return items.stream()
        .filter(item -> axis.start(item.range()) <= index && axis.end(item.range()) > index)
        .filter(item -> axis.span(item.range()) == 1)
        .map(item -> axis == Axis.COLUMN ? item.element().box().borderBox().width() : item.element().box().borderBox().height())
        .max(Comparator.naturalOrder())
        .orElse(0f);
  }

  private void applyItemBoxes(
      Rect content,
      List<Float> columns,
      List<Float> rows,
      float columnGap,
      float rowGap,
      List<PlacedItem> items,
      ResolvedStyle parentStyle) {
    for (PlacedItem item : items) {
      GridItem range = item.range();
      float areaX = content.x() + offset(columns, columnGap, range.columnStart());
      float areaY = content.y() + offset(rows, rowGap, range.rowStart());
      float areaWidth = spanSize(columns, columnGap, range.columnStart(), range.columnEnd());
      float areaHeight = spanSize(rows, rowGap, range.rowStart(), range.rowEnd());
      applyItemBox(item.element(), areaX, areaY, areaWidth, areaHeight, parentStyle);
      layoutService.layoutChildNodes(item.element(), new LayoutContext());
    }
  }

  private void applyItemBox(
      Element element, float areaX, float areaY, float areaWidth, float areaHeight, ResolvedStyle parentStyle) {
    Edges padding = element.box().padding();
    Edges border = element.box().border();
    float horizontalExtras = padding.left() + padding.right() + border.left() + border.right();
    float verticalExtras = padding.top() + padding.bottom() + border.top() + border.bottom();
    boolean horizontalStretch = stretchesHorizontal(parentStyle, element);
    boolean verticalStretch = stretchesVertical(parentStyle, element);
    float width =
        horizontalStretch && element.resolvedStyle().width().isAuto()
            ? Math.max(0, areaWidth - horizontalExtras)
            : element.box().content().width();
    float height =
        verticalStretch && element.resolvedStyle().height().isAuto()
            ? Math.max(0, areaHeight - verticalExtras)
            : element.box().content().height();
    element.box().contentSize(width, height);
    float borderBoxWidth = width + horizontalExtras;
    float borderBoxHeight = height + verticalExtras;
    float x = areaX + alignmentOffset(areaWidth, borderBoxWidth, horizontalAlignment(parentStyle, element));
    float y = areaY + alignmentOffset(areaHeight, borderBoxHeight, verticalAlignment(parentStyle, element));
    element.box().contentPosition(x + border.left() + padding.left(), y + border.top() + padding.top());
  }

  private boolean stretchesVertical(ResolvedStyle parentStyle, Element element) {
    AlignSelf self = element.resolvedStyle().alignSelf();
    if (AlignSelf.STRETCH.equals(self)) {
      return true;
    }
    return (self == null || AlignSelf.AUTO.equals(self))
        && (parentStyle.alignItems() == null || AlignItems.STRETCH.equals(parentStyle.alignItems()));
  }

  private boolean stretchesHorizontal(ResolvedStyle parentStyle, Element element) {
    AlignSelf self = element.resolvedStyle().justifySelf();
    if (AlignSelf.STRETCH.equals(self)) {
      return true;
    }
    return (self == null || AlignSelf.AUTO.equals(self))
        && (parentStyle.justifyItems() == null || AlignItems.STRETCH.equals(parentStyle.justifyItems()));
  }

  private AlignSelf verticalAlignment(ResolvedStyle parentStyle, Element element) {
    AlignSelf self = element.resolvedStyle().alignSelf();
    if (self != null && !AlignSelf.AUTO.equals(self)) {
      return self;
    }
    AlignItems items = parentStyle.alignItems();
    if (AlignItems.CENTER.equals(items)) {
      return AlignSelf.CENTER;
    }
    if (AlignItems.FLEX_END.equals(items)) {
      return AlignSelf.FLEX_END;
    }
    if (AlignItems.STRETCH.equals(items)) {
      return AlignSelf.STRETCH;
    }
    return AlignSelf.FLEX_START;
  }

  private AlignSelf horizontalAlignment(ResolvedStyle parentStyle, Element element) {
    AlignSelf self = element.resolvedStyle().justifySelf();
    if (self != null && !AlignSelf.AUTO.equals(self)) {
      return self;
    }
    AlignItems items = parentStyle.justifyItems();
    if (AlignItems.CENTER.equals(items)) {
      return AlignSelf.CENTER;
    }
    if (AlignItems.FLEX_END.equals(items)) {
      return AlignSelf.FLEX_END;
    }
    if (AlignItems.STRETCH.equals(items)) {
      return AlignSelf.STRETCH;
    }
    return AlignSelf.FLEX_START;
  }

  private float alignmentOffset(float areaSize, float itemSize, AlignSelf alignment) {
    if (AlignSelf.CENTER.equals(alignment)) {
      return Math.max(0, (areaSize - itemSize) / 2f);
    }
    if (AlignSelf.FLEX_END.equals(alignment)) {
      return Math.max(0, areaSize - itemSize);
    }
    return 0f;
  }

  private float offset(List<Float> tracks, float gap, int index) {
    float offset = 0;
    for (int i = 0; i < index && i < tracks.size(); i++) {
      offset += tracks.get(i) + gap;
    }
    return offset;
  }

  private float spanSize(List<Float> tracks, float gap, int start, int end) {
    float size = 0;
    for (int i = start; i < end && i < tracks.size(); i++) {
      size += tracks.get(i);
    }
    size += gap * Math.max(0, end - start - 1);
    return size;
  }

  private float sum(List<Float> values) {
    float sum = 0;
    for (Float value : values) {
      sum += value;
    }
    return sum;
  }

  private float resolveLength(Length<?> length, float base) {
    if (length == null) {
      return 0f;
    }
    return length.convert(base);
  }

  private enum Axis {
    ROW {
      @Override
      int start(GridItem item) {
        return item.rowStart();
      }

      @Override
      int end(GridItem item) {
        return item.rowEnd();
      }
    },
    COLUMN {
      @Override
      int start(GridItem item) {
        return item.columnStart();
      }

      @Override
      int end(GridItem item) {
        return item.columnEnd();
      }
    };

    abstract int start(GridItem item);

    abstract int end(GridItem item);

    int span(GridItem item) {
      return end(item) - start(item);
    }
  }

  private record Range(Integer start, Integer end, int span) {
    private Range(Integer start, Integer end) {
      this(start, end, Math.max(1, end - start));
    }
  }

  private static final class Cursor {
    private int row;
    private int column;
  }

  private record GridItem(Integer requestedRowStart, Integer requestedRowEnd, Integer requestedColumnStart, Integer requestedColumnEnd) {
    private GridItem(int rowStart, int rowEnd, int columnStart, int columnEnd) {
      this(Integer.valueOf(rowStart), Integer.valueOf(rowEnd), Integer.valueOf(columnStart), Integer.valueOf(columnEnd));
    }

    private boolean definiteRow() {
      return requestedRowStart != null && requestedRowEnd != null;
    }

    private boolean definiteColumn() {
      return requestedColumnStart != null && requestedColumnEnd != null;
    }

    private int rowStart() {
      return requestedRowStart == null ? 0 : Math.max(0, requestedRowStart);
    }

    private int rowEnd() {
      return requestedRowEnd == null ? rowStart() + 1 : Math.max(rowStart() + 1, requestedRowEnd);
    }

    private int columnStart() {
      return requestedColumnStart == null ? 0 : Math.max(0, requestedColumnStart);
    }

    private int columnEnd() {
      return requestedColumnEnd == null ? columnStart() + 1 : Math.max(columnStart() + 1, requestedColumnEnd);
    }
  }

  private record PlacedItem(Element element, GridItem range) {}

  private record PlacementResult(List<PlacedItem> items, int rowCount, int columnCount) {}

  private static final class Occupancy {
    private final List<List<Boolean>> cells = new ArrayList<>();

    private Occupancy(int rows, int columns) {
      ensure(rows, columns);
    }

    private GridItem place(GridItem requested, boolean columnFlow, Cursor cursor) {
      int row = requested.requestedRowStart() == null ? cursor.row : requested.rowStart();
      int column = requested.requestedColumnStart() == null ? cursor.column : requested.columnStart();
      int rowSpan = Math.max(1, requested.rowEnd() - requested.rowStart());
      int columnSpan = Math.max(1, requested.columnEnd() - requested.columnStart());
      while (true) {
        GridItem candidate = new GridItem(row, row + rowSpan, column, column + columnSpan);
        if (fits(candidate)) {
          occupy(candidate);
          cursor.row = row;
          cursor.column = column;
          return candidate;
        }
        if (columnFlow) {
          row++;
          if (row >= cells.size()) {
            row = 0;
            column++;
          }
        } else {
          column++;
          if (!cells.isEmpty() && column >= cells.getFirst().size()) {
            column = 0;
            row++;
          }
        }
      }
    }

    private boolean fits(GridItem item) {
      ensure(item.rowEnd(), item.columnEnd());
      for (int row = item.rowStart(); row < item.rowEnd(); row++) {
        for (int column = item.columnStart(); column < item.columnEnd(); column++) {
          if (cells.get(row).get(column)) {
            return false;
          }
        }
      }
      return true;
    }

    private void occupy(GridItem item) {
      ensure(item.rowEnd(), item.columnEnd());
      for (int row = item.rowStart(); row < item.rowEnd(); row++) {
        for (int column = item.columnStart(); column < item.columnEnd(); column++) {
          cells.get(row).set(column, true);
        }
      }
    }

    private void ensure(int rows, int columns) {
      int width = Math.max(columns, cells.isEmpty() ? 0 : cells.getFirst().size());
      while (cells.size() < rows) {
        cells.add(new ArrayList<>());
      }
      for (List<Boolean> row : cells) {
        while (row.size() < width) {
          row.add(false);
        }
      }
    }
  }
}
