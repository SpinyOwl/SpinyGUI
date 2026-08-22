package com.spinyowl.spinygui.core.layout;

import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Immutable provenance from one durable inline fragment's UTF-16 text back to its original owning
 * text-node source. A collapsed fragment code point may cover a larger source span, while multiple
 * expanded fragment code points may share one source span.
 */
public final class InlineSourceMapping {

  private static final InlineSourceMapping UNMAPPED =
      new InlineSourceMapping(null, "", 0, 0, false);

  private final PreparedProvenance provenance;
  private final String renderedText;
  private final int preparedStart;
  private final int preparedEnd;
  private final boolean mapped;

  private InlineSourceMapping(
      PreparedProvenance provenance,
      String renderedText,
      int preparedStart,
      int preparedEnd,
      boolean mapped) {
    this.provenance = provenance;
    this.renderedText = renderedText;
    this.preparedStart = preparedStart;
    this.preparedEnd = preparedEnd;
    this.mapped = mapped;
  }

  /** Returns the singleton provenance value for null-text or externally constructed fragments. */
  public static InlineSourceMapping unmapped() {
    return UNMAPPED;
  }

  /**
   * Freezes one prepared source mapping. The returned whole-value mapping can create immutable
   * fragment views without copying the contribution arrays again.
   */
  public static InlineSourceMapping forPreparedText(
      String originalSource,
      String preparedText,
      int[] contributionSourceStarts,
      int[] contributionSourceEnds) {
    PreparedProvenance provenance =
        new PreparedProvenance(
            originalSource, preparedText, contributionSourceStarts, contributionSourceEnds);
    if (preparedText.isEmpty()) return UNMAPPED;
    return new InlineSourceMapping(provenance, preparedText, 0, preparedText.length(), true);
  }

  /** Freezes provenance whose local coordinate space is replacement-aware rendered text. */
  public static InlineSourceMapping forRenderedText(
      String originalSource,
      String renderedText,
      int[] contributionSourceStarts,
      int[] contributionSourceEnds) {
    PreparedProvenance provenance =
        new PreparedProvenance(
            originalSource, renderedText, contributionSourceStarts, contributionSourceEnds);
    if (renderedText.isEmpty()) return UNMAPPED;
    return new InlineSourceMapping(provenance, renderedText, 0, renderedText.length(), true);
  }

  /** Creates a durable fragment-local view over this prepared provenance. */
  public InlineSourceMapping fragment(String text, int start, int end) {
    requireMapped();
    validateBoundary(provenance.preparedText, start, "prepared start");
    validateBoundary(provenance.preparedText, end, "prepared end");
    if (start >= end) {
      throw new IllegalArgumentException("Mapped durable fragments must be non-empty");
    }
    Objects.requireNonNull(text, "text");
    if (text.length() != end - start
        || !provenance.preparedText.regionMatches(start, text, 0, text.length())) {
      throw new IllegalArgumentException("Fragment text does not match its prepared range");
    }
    return new InlineSourceMapping(provenance, text, start, end, true);
  }

  public boolean mapped() {
    return mapped;
  }

  public int fragmentLength() {
    return mapped ? preparedEnd - preparedStart : 0;
  }

  public int sourceStart() {
    requireMapped();
    return provenance.sourceStarts[preparedStart];
  }

  public int sourceEnd() {
    requireMapped();
    return provenance.sourceEnds[preparedEnd - 1];
  }

  /** Returns the original source span that contributed one fragment UTF-16 code unit. */
  public SourceSpan sourceSpanAt(int fragmentUtf16Index) {
    requireMapped();
    if (fragmentUtf16Index < 0 || fragmentUtf16Index >= fragmentLength()) {
      throw new IndexOutOfBoundsException("Fragment UTF-16 index is outside its mapping");
    }
    int preparedIndex = preparedStart + fragmentUtf16Index;
    return new SourceSpan(
        provenance.sourceStarts[preparedIndex], provenance.sourceEnds[preparedIndex]);
  }

  /** Maps a fragment-local boundary to the inclusive set of original source boundaries. */
  public BoundarySpan sourceBoundariesForFragment(int fragmentBoundary) {
    requireMapped();
    validateBoundary(renderedText, fragmentBoundary, "fragment boundary");
    int preparedBoundary = preparedStart + fragmentBoundary;
    int before =
        fragmentBoundary == 0
            ? sourceStart()
            : provenance.sourceEnds[preparedBoundary - 1];
    int after =
        fragmentBoundary == fragmentLength()
            ? sourceEnd()
            : provenance.sourceStarts[preparedBoundary];
    return new BoundarySpan(Math.min(before, after), Math.max(before, after));
  }

  /** Maps an original source boundary to the inclusive set of fragment-local boundaries. */
  public BoundarySpan fragmentBoundariesForSource(int sourceBoundary) {
    requireMapped();
    validateSourceBoundary(provenance.originalSource, sourceBoundary);
    int before = 0;
    int after = 0;
    for (int preparedIndex = preparedStart; preparedIndex < preparedEnd; preparedIndex++) {
      int localBoundary = preparedIndex - preparedStart + 1;
      if (provenance.sourceEnds[preparedIndex] <= sourceBoundary) before = localBoundary;
      if (provenance.sourceStarts[preparedIndex] < sourceBoundary) after = localBoundary;
    }
    return new BoundarySpan(before, Math.max(before, after));
  }

  /** Maps a non-empty fragment-local range to its complete original source span. */
  public SourceSpan sourceSpanForFragmentRange(int fragmentStart, int fragmentEnd) {
    requireMapped();
    validateBoundary(renderedText, fragmentStart, "fragment start");
    validateBoundary(renderedText, fragmentEnd, "fragment end");
    if (fragmentStart >= fragmentEnd) {
      throw new IllegalArgumentException("Mapped fragment range must be non-empty");
    }
    return new SourceSpan(
        provenance.sourceStarts[preparedStart + fragmentStart],
        provenance.sourceEnds[preparedStart + fragmentEnd - 1]);
  }

  boolean matchesRenderedOutput(String fallbackText, List<ResolvedTextRun> runs) {
    if (!mapped) return true;
    if (runs.isEmpty()) return Objects.equals(renderedText, fallbackText);
    int local = 0;
    for (ResolvedTextRun run : runs) {
      if (run.sourceStart() != local) return false;
      for (ResolvedGlyph glyph : run.glyphs()) {
        if (glyph.sourceStart() != local
            || glyph.sourceEnd() != local + Character.charCount(glyph.renderedCodePoint())
            || renderedText.codePointAt(local) != glyph.renderedCodePoint()) {
          return false;
        }
        local = glyph.sourceEnd();
      }
      if (run.sourceEnd() != local) return false;
    }
    return local == fragmentLength();
  }

  private void requireMapped() {
    if (!mapped) throw new IllegalStateException("Inline fragment has no original-source mapping");
  }

  private static void validateSourceBoundary(String source, int boundary) {
    validateBoundary(source, boundary, "source boundary");
    if (boundary > 0
        && boundary < source.length()
        && source.charAt(boundary - 1) == '\r'
        && source.charAt(boundary) == '\n') {
      throw new IllegalArgumentException("Source boundary splits an atomic CRLF pair");
    }
  }

  private static void validateBoundary(String text, int boundary, String label) {
    if (boundary < 0 || boundary > text.length()) {
      throw new IndexOutOfBoundsException(label + " is outside UTF-16 text");
    }
    if (boundary > 0
        && boundary < text.length()
        && Character.isHighSurrogate(text.charAt(boundary - 1))
        && Character.isLowSurrogate(text.charAt(boundary))) {
      throw new IllegalArgumentException(label + " splits a surrogate pair");
    }
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (!(other instanceof InlineSourceMapping mapping)) return false;
    if (mapped != mapping.mapped) return false;
    if (!mapped) return true;
    if (!renderedText.equals(mapping.renderedText)
        || !provenance.originalSource.equals(mapping.provenance.originalSource)
        || fragmentLength() != mapping.fragmentLength()) {
      return false;
    }
    for (int index = 0; index < fragmentLength(); index++) {
      if (!sourceSpanAt(index).equals(mapping.sourceSpanAt(index))) return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    if (!mapped) return 0;
    int result = Objects.hash(provenance.originalSource, renderedText);
    for (int index = preparedStart; index < preparedEnd; index++) {
      result = 31 * result + provenance.sourceStarts[index];
      result = 31 * result + provenance.sourceEnds[index];
    }
    return result;
  }

  @Override
  public String toString() {
    return mapped
        ? "InlineSourceMapping[fragmentLength=%d, sourceStart=%d, sourceEnd=%d]"
            .formatted(fragmentLength(), sourceStart(), sourceEnd())
        : "InlineSourceMapping[unmapped]";
  }

  public record BoundarySpan(int start, int end) {
    public BoundarySpan {
      if (start < 0 || start > end) throw new IllegalArgumentException("Invalid boundary span");
    }
  }

  public record SourceSpan(int start, int end) {
    public SourceSpan {
      if (start < 0 || start >= end) throw new IllegalArgumentException("Invalid source span");
    }
  }

  private static final class PreparedProvenance {
    private final String originalSource;
    private final String preparedText;
    private final int[] sourceStarts;
    private final int[] sourceEnds;

    private PreparedProvenance(
        String originalSource, String preparedText, int[] sourceStarts, int[] sourceEnds) {
      this.originalSource = Objects.requireNonNull(originalSource, "originalSource");
      this.preparedText = Objects.requireNonNull(preparedText, "preparedText");
      Objects.requireNonNull(sourceStarts, "sourceStarts");
      Objects.requireNonNull(sourceEnds, "sourceEnds");
      if (sourceStarts.length != preparedText.length()
          || sourceEnds.length != preparedText.length()) {
        throw new IllegalArgumentException("Contribution arrays must match prepared UTF-16 length");
      }
      this.sourceStarts = Arrays.copyOf(sourceStarts, sourceStarts.length);
      this.sourceEnds = Arrays.copyOf(sourceEnds, sourceEnds.length);
      int previousStart = -1;
      int previousEnd = -1;
      for (int index = 0; index < this.sourceStarts.length; index++) {
        int start = this.sourceStarts[index];
        int end = this.sourceEnds[index];
        if (start < 0 || start >= end || end > originalSource.length()) {
          throw new IllegalArgumentException("Invalid original-source contribution span");
        }
        validateSourceBoundary(originalSource, start);
        validateSourceBoundary(originalSource, end);
        if (start < previousStart || end < previousEnd) {
          throw new IllegalArgumentException("Original-source contributions must be monotonic");
        }
        previousStart = start;
        previousEnd = end;
      }
    }
  }
}
