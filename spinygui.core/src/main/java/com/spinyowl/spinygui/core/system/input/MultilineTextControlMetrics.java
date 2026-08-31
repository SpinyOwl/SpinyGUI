package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import java.util.List;
import lombok.NonNull;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/** Compatibility facade over the shared immutable editable-control snapshot. */
public class MultilineTextControlMetrics {
  @NonNull private final ControlTextLayoutService layoutService;

  public MultilineTextControlMetrics(@NonNull TextMeasurer textMeasurer) {
    this(new ControlTextLayoutService(textMeasurer));
  }

  public MultilineTextControlMetrics(@NonNull ControlTextLayoutService layoutService) {
    this.layoutService = layoutService;
  }

  public ControlTextLayoutService layoutService() {
    return layoutService;
  }

  public List<Line> lines(TextareaElement textarea) {
    return layoutService.query(textarea).lines().stream()
        .map(line -> new Line(line.text(), line.startIndex(), line.endIndex(), line.width(),
            line.height(), line.baseline(), line.runs(), line.y()))
        .toList();
  }

  public Caret caret(TextareaElement textarea, int index) {
    ControlTextLayoutSnapshot.Caret caret =
        layoutService.query(textarea).caret(index, layoutService.diagnostics());
    return new Caret(caret.index(), caret.x(), caret.y(), caret.height());
  }

  public int indexAt(TextareaElement textarea, Vector2fc cursorPosition) {
    Vector2f contentPosition = contentPosition(textarea);
    float localX = cursorPosition.x() - contentPosition.x() + textarea.textScrollLeft();
    float localY = cursorPosition.y() - contentPosition.y() + textarea.textScrollTop();
    return layoutService.query(textarea).indexAt(localX, localY, layoutService.diagnostics());
  }

  public int lineStart(TextareaElement textarea, int index) {
    return layoutService.query(textarea).lineForIndex(index).startIndex();
  }

  public int lineEnd(TextareaElement textarea, int index) {
    return layoutService.query(textarea).lineForIndex(index).endIndex();
  }

  public int verticalCaretIndex(TextareaElement textarea, int index, int direction) {
    ControlTextLayoutSnapshot snapshot = layoutService.query(textarea);
    ControlTextLayoutSnapshot.Line current = snapshot.lineForIndex(index);
    int currentLineIndex = snapshot.lines().indexOf(current);
    int nextLineIndex =
        Math.max(0, Math.min(currentLineIndex + direction, snapshot.lines().size() - 1));
    if (nextLineIndex == currentLineIndex) {
      return index;
    }
    float desiredX = snapshot.caret(index, layoutService.diagnostics()).x();
    return snapshot.lines().get(nextLineIndex).caretStops()
        .caretAt(desiredX, layoutService.diagnostics()).charIndex();
  }

  public Vector2f contentPosition(TextareaElement textarea) {
    Vector2f position = textarea.layoutAbsolutePosition();
    position.add(textarea.box().border().left() + textarea.box().padding().left(),
        textarea.box().border().top() + textarea.box().padding().top());
    return position;
  }

  public TextStyle textStyle(TextareaElement textarea) {
    ControlTextLayoutSnapshot.Key key = layoutService.query(textarea).key();
    return new TextStyle(key.resolvedFonts(), key.fontSize(), key.lineHeight());
  }

  public record Line(String text, int startIndex, int endIndex, float width, float height,
      float baseline, List<ResolvedTextRun> runs, float y) {}

  public record Caret(int index, float x, float y, float height) {}

  public record TextStyle(List<Font> fonts, float fontSize, float lineHeight) {
    public TextStyle { fonts = List.copyOf(fonts); }
  }
}
