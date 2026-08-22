package com.spinyowl.spinygui.core.layout.impl;

import static com.spinyowl.spinygui.core.style.types.WhiteSpace.NORMAL;
import static com.spinyowl.spinygui.core.style.types.WhiteSpace.NOWRAP;
import static com.spinyowl.spinygui.core.style.types.WhiteSpace.PRE;
import static com.spinyowl.spinygui.core.style.types.WhiteSpace.PRE_LINE;
import static com.spinyowl.spinygui.core.style.types.WhiteSpace.PRE_WRAP;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.layout.InlineSourceMapping;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.system.font.internal.PreparedRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** Immutable, pass-local normalized text with explicit source spans and code-point-safe ranges. */
final class PreparedInlineText {

  enum UnitKind {
    TEXT,
    COLLAPSIBLE_SPACE,
    PRESERVED_SPACE,
    FORCED_BREAK
  }

  /** Inclusive boundary alternatives caused by collapsed or expanded source spans. */
  record BoundarySpan(int start, int end) {
    BoundarySpan {
      if (start < 0 || start > end) {
        throw new IllegalArgumentException("Invalid boundary span [%d, %d]".formatted(start, end));
      }
    }
  }

  record Unit(UnitKind kind, int preparedStart, int preparedEnd, int sourceStart, int sourceEnd) {
    Unit {
      Objects.requireNonNull(kind, "kind");
      if (preparedStart < 0
          || preparedStart > preparedEnd
          || sourceStart < 0
          || sourceStart > sourceEnd) {
        throw new IllegalArgumentException("Invalid prepared/source unit range");
      }
    }

    boolean textBearing() {
      return kind != UnitKind.FORCED_BREAK;
    }
  }

  private final String source;
  private final String prepared;
  private final int[] contributionSourceStarts;
  private final int[] contributionSourceEnds;
  private final InlineSourceMapping sourceMapping;
  private final List<Unit> units;

  private PreparedInlineText(
      String source,
      String prepared,
      int[] contributionSourceStarts,
      int[] contributionSourceEnds,
      InlineSourceMapping sourceMapping,
      List<Unit> units) {
    this.source = source;
    this.prepared = prepared;
    this.contributionSourceStarts = contributionSourceStarts;
    this.contributionSourceEnds = contributionSourceEnds;
    this.sourceMapping = sourceMapping;
    this.units = List.copyOf(units);
  }

  static PreparedInlineText prepare(
      String source, ResolvedStyle style, DiagnosticSession diagnostics) {
    Objects.requireNonNull(source, "source");
    Objects.requireNonNull(style, "style");
    Objects.requireNonNull(diagnostics, "diagnostics");
    diagnostics.increment(TextDiagnosticCounter.NORMALIZATION_SCANS);

    WhiteSpace policy = style.whiteSpace();
    boolean collapseAll = NORMAL.equals(policy) || NOWRAP.equals(policy);
    boolean collapseHorizontal = PRE_LINE.equals(policy);
    int tabSize = Math.max(1, style.tabSize() == null ? 4 : style.tabSize());
    StringBuilder prepared = new StringBuilder(source.length());
    IntArrayBuilder starts = new IntArrayBuilder(source.length());
    IntArrayBuilder ends = new IntArrayBuilder(source.length());
    boolean collapsedRun = false;

    for (int sourceStart = 0; sourceStart < source.length(); ) {
      int codePoint = source.codePointAt(sourceStart);
      int sourceEnd = sourceStart + Character.charCount(codePoint);
      int scannedCodePoints = 1;
      if (codePoint == '\r') {
        if (sourceEnd < source.length() && source.charAt(sourceEnd) == '\n') {
          sourceEnd++;
          scannedCodePoints++;
        }
        codePoint = '\n';
      }

      boolean collapsible =
          (collapseAll && asciiWhitespace(codePoint))
              || (collapseHorizontal && horizontalWhitespace(codePoint));
      if (collapsible) {
        if (!collapsedRun) {
          append(prepared, starts, ends, ' ', sourceStart, sourceEnd, diagnostics);
          collapsedRun = true;
        } else {
          ends.setLast(sourceEnd);
        }
      } else {
        collapsedRun = false;
        if (codePoint == '\t') {
          for (int index = 0; index < tabSize; index++) {
            append(prepared, starts, ends, ' ', sourceStart, sourceEnd, diagnostics);
          }
        } else {
          append(prepared, starts, ends, codePoint, sourceStart, sourceEnd, diagnostics);
        }
      }
      diagnostics.add(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED, scannedCodePoints);
      sourceStart = sourceEnd;
    }

    diagnostics.increment(TextDiagnosticCounter.INLINE_PREPARATION_FREEZES);
    String frozen = prepared.toString();
    int[] sourceStarts = starts.freeze();
    int[] sourceEnds = ends.freeze();
    List<Unit> units = units(frozen, sourceStarts, sourceEnds, policy, diagnostics);
    InlineSourceMapping sourceMapping =
        InlineSourceMapping.forPreparedText(source, frozen, sourceStarts, sourceEnds);
    return new PreparedInlineText(
        source, frozen, sourceStarts, sourceEnds, sourceMapping, units);
  }

  private static void append(
      StringBuilder prepared,
      IntArrayBuilder starts,
      IntArrayBuilder ends,
      int codePoint,
      int sourceStart,
      int sourceEnd,
      DiagnosticSession diagnostics) {
    int preparedStart = prepared.length();
    prepared.appendCodePoint(codePoint);
    for (int index = preparedStart; index < prepared.length(); index++) {
      starts.add(sourceStart);
      ends.add(sourceEnd);
    }
    diagnostics.increment(TextDiagnosticCounter.INLINE_PREPARED_CODE_POINTS_APPENDED);
  }

  private static List<Unit> units(
      String prepared,
      int[] sourceStarts,
      int[] sourceEnds,
      WhiteSpace policy,
      DiagnosticSession diagnostics) {
    List<Unit> result = new ArrayList<>();
    boolean preserveSpaces = PRE.equals(policy) || PRE_WRAP.equals(policy);
    UnitKind currentKind = null;
    int rangeStart = 0;
    for (int index = 0; index < prepared.length(); ) {
      int codePoint = prepared.codePointAt(index);
      int next = index + Character.charCount(codePoint);
      UnitKind kind =
          codePoint == '\n'
              ? UnitKind.FORCED_BREAK
              : codePoint == ' '
                  ? preserveSpaces ? UnitKind.PRESERVED_SPACE : UnitKind.COLLAPSIBLE_SPACE
                  : UnitKind.TEXT;
      if (currentKind != null && kind != currentKind) {
        addUnit(result, currentKind, prepared, sourceStarts, sourceEnds, rangeStart, index);
        rangeStart = index;
      }
      currentKind = kind;
      index = next;
    }
    if (currentKind != null) {
      addUnit(
          result,
          currentKind,
          prepared,
          sourceStarts,
          sourceEnds,
          rangeStart,
          prepared.length());
    }
    diagnostics.add(TextDiagnosticCounter.INLINE_PREPARED_RANGES, result.size());
    return result;
  }

  private static void addUnit(
      List<Unit> result,
      UnitKind kind,
      String prepared,
      int[] sourceStarts,
      int[] sourceEnds,
      int preparedStart,
      int preparedEnd) {
    PreparedRange.validateSourceRange(prepared, preparedStart, preparedEnd);
    int sourceStart = preparedStart == preparedEnd ? 0 : sourceStarts[preparedStart];
    int sourceEnd = preparedStart == preparedEnd ? sourceStart : sourceEnds[preparedEnd - 1];
    result.add(new Unit(kind, preparedStart, preparedEnd, sourceStart, sourceEnd));
  }

  private static boolean asciiWhitespace(int codePoint) {
    return codePoint == ' ' || codePoint == '\t' || codePoint == '\n'
        || codePoint == '\r' || codePoint == '\f' || codePoint == 0x0B;
  }

  private static boolean horizontalWhitespace(int codePoint) {
    return codePoint == ' ' || codePoint == '\t' || codePoint == '\f' || codePoint == 0x0B;
  }

  String source() {
    return source;
  }

  String text() {
    return prepared;
  }

  List<Unit> units() {
    return units;
  }

  BoundarySpan preparedBoundariesForSource(int sourceBoundary) {
    validateSourceBoundary(source, sourceBoundary);
    int before = 0;
    int after = 0;
    for (int index = 0; index < contributionSourceStarts.length; index++) {
      if (contributionSourceEnds[index] <= sourceBoundary) before = index + 1;
      if (contributionSourceStarts[index] < sourceBoundary) after = index + 1;
    }
    return new BoundarySpan(before, Math.max(before, after));
  }

  BoundarySpan sourceBoundariesForPrepared(int preparedBoundary) {
    validatePreparedBoundary(preparedBoundary);
    int before = preparedBoundary == 0 ? 0 : contributionSourceEnds[preparedBoundary - 1];
    int after =
        preparedBoundary == contributionSourceStarts.length
            ? source.length()
            : contributionSourceStarts[preparedBoundary];
    return new BoundarySpan(Math.min(before, after), Math.max(before, after));
  }

  int sourceStartForPreparedRange(int preparedStart, int preparedEnd) {
    PreparedRange.validateSourceRange(prepared, preparedStart, preparedEnd);
    return preparedStart == preparedEnd
        ? sourceBoundariesForPrepared(preparedStart).start()
        : contributionSourceStarts[preparedStart];
  }

  int sourceEndForPreparedRange(int preparedStart, int preparedEnd) {
    PreparedRange.validateSourceRange(prepared, preparedStart, preparedEnd);
    return preparedStart == preparedEnd
        ? sourceBoundariesForPrepared(preparedEnd).end()
        : contributionSourceEnds[preparedEnd - 1];
  }

  InlineSourceMapping sourceMappingForPreparedRange(
      String fragmentText, int preparedStart, int preparedEnd) {
    if (preparedStart == preparedEnd) return InlineSourceMapping.unmapped();
    return sourceMapping.fragment(fragmentText, preparedStart, preparedEnd);
  }

  Unit subrange(Unit unit, int preparedStart, int preparedEnd) {
    Objects.requireNonNull(unit, "unit");
    if (preparedStart < unit.preparedStart() || preparedEnd > unit.preparedEnd()) {
      throw new IllegalArgumentException("Subrange is outside its prepared unit");
    }
    PreparedRange.validateSourceRange(prepared, preparedStart, preparedEnd);
    if (preparedStart == preparedEnd) {
      BoundarySpan sourceSpan = sourceBoundariesForPrepared(preparedStart);
      return new Unit(
          unit.kind(), preparedStart, preparedEnd, sourceSpan.start(), sourceSpan.end());
    }
    return new Unit(
        unit.kind(),
        preparedStart,
        preparedEnd,
        contributionSourceStarts[preparedStart],
        contributionSourceEnds[preparedEnd - 1]);
  }

  private void validatePreparedBoundary(int boundary) {
    PreparedRange.validateSourceRange(prepared, boundary, boundary);
  }

  private static void validateSourceBoundary(String source, int boundary) {
    PreparedRange.validateSourceRange(source, boundary, boundary);
    if (boundary > 0
        && boundary < source.length()
        && source.charAt(boundary - 1) == '\r'
        && source.charAt(boundary) == '\n') {
      throw new IllegalArgumentException("Source boundary splits an atomic CRLF pair");
    }
  }

  private static final class IntArrayBuilder {
    private int[] values;
    private int size;

    private IntArrayBuilder(int expectedSize) {
      values = new int[Math.max(4, expectedSize)];
    }

    private void add(int value) {
      if (size == values.length) {
        values = Arrays.copyOf(values, values.length * 2);
      }
      values[size++] = value;
    }

    private void setLast(int value) {
      if (size == 0) {
        throw new IllegalStateException("Cannot update an empty mapping builder");
      }
      values[size - 1] = value;
    }

    private int[] freeze() {
      return Arrays.copyOf(values, size);
    }
  }
}
