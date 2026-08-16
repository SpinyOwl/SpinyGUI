package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.AffineTransform;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NvgTextCommandRecorderTest {
  private FontServiceImpl fontService;

  @BeforeEach
  void installFontOwner() {
    fontService = NvgFontTestOwner.install();
  }

  @AfterEach
  void closeFontOwner() {
    fontService.close();
  }

  @Test
  void normalTextRecordsReplacementFaceFailureAndTheActualAdvanceBranch() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));
    NvgTextCommandRecorder recorder =
        new NvgTextCommandRecorder(font -> !font.equals(Font.ROBOTO_REGULAR));
    NvgTextRenderer renderer = new NvgTextRenderer(recorder, diagnostics);
    Element parent = new Element("div");
    Text text = new Text("\uD83D\uDE00a");
    parent.addChild(text);
    text.inlineFragments(
        List.of(
            InlineFragment.builder()
                .text("\uD83D\uDE00a")
                .font(Font.NOTO_SANS_CJK_SC_REGULAR)
                .fontSize(16)
                .color(Color.BLACK)
                .baseline(12)
                .runs(
                    List.of(
                        new ResolvedTextRun(
                            0,
                            2,
                            Font.NOTO_SANS_CJK_SC_REGULAR,
                            List.of(
                                new ResolvedGlyph(
                                    0,
                                    2,
                                    0x1F600,
                                    0xFFFD,
                                    Font.NOTO_SANS_CJK_SC_REGULAR,
                                    true)),
                            9),
                        new ResolvedTextRun(
                            2,
                            3,
                            Font.ROBOTO_REGULAR,
                            List.of(
                                new ResolvedGlyph(
                                    2, 3, 'a', 'a', Font.ROBOTO_REGULAR, false)),
                            7)))
                .build()));

    renderer.render(text, 1);

    assertEquals(
        List.of(
            new NvgTextCommand.Scope(NvgTextCommand.TextPath.NORMAL, true),
            new NvgTextCommand.Alignment(65),
            new NvgTextCommand.Outcome(
                NvgTextCommand.TextPath.NORMAL,
                NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_CONSIDERED.id()),
            new NvgTextCommand.Face(
                NvgTextCommand.TextPath.NORMAL, Font.NOTO_SANS_CJK_SC_REGULAR, true),
            new NvgTextCommand.FontSize(16),
            new NvgTextCommand.FillColor(Color.BLACK),
            new NvgTextCommand.Outcome(
                NvgTextCommand.TextPath.NORMAL,
                NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_SUBMITTED.id()),
            new NvgTextCommand.Text(NvgTextCommand.TextPath.NORMAL, "\uFFFD", 3, 0, 12),
            new NvgTextCommand.Advance(NvgTextCommand.TextPath.NORMAL, 0, 9),
            new NvgTextCommand.Outcome(
                NvgTextCommand.TextPath.NORMAL,
                NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_CONSIDERED.id()),
            new NvgTextCommand.Face(NvgTextCommand.TextPath.NORMAL, Font.ROBOTO_REGULAR, false),
            new NvgTextCommand.Outcome(
                NvgTextCommand.TextPath.NORMAL,
                NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_FACE_SELECTION_FAILED.id()),
            new NvgTextCommand.Advance(NvgTextCommand.TextPath.NORMAL, 9, 7),
            new NvgTextCommand.Scope(NvgTextCommand.TextPath.NORMAL, false)),
        recorder.commands());
    assertEquals(
        0, recorder.commands().stream().filter(NvgTextCommand.Cull.class::isInstance).count());
    assertEquals(2, diagnostics.snapshot().value(NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_CONSIDERED));
    assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_SUBMITTED));
    assertEquals(
        1,
        diagnostics
            .snapshot()
            .value(NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_FACE_SELECTION_FAILED));
  }

  @Test
  void normalNoRunTextUsesTheProductionDisplayTextConversion() {
    NvgTextCommandRecorder recorder = new NvgTextCommandRecorder();
    NvgTextRenderer renderer = new NvgTextRenderer(recorder);
    Element parent = new Element("div");
    Text text = new Text("\u96EA");
    parent.addChild(text);
    text.inlineFragments(
        List.of(
            InlineFragment.builder()
                .text("\u96EA")
                .font(Font.DEFAULT)
                .fontSize(16)
                .color(Color.BLACK)
                .baseline(12)
                .build()));

    renderer.render(text, 0);

    assertEquals(
        List.of(
            new NvgTextCommand.Text(NvgTextCommand.TextPath.NORMAL, "\uFFFD", 3, 0, 12)),
        recorder.commands().stream().filter(NvgTextCommand.Text.class::isInstance).toList());
  }

  @Test
  void transformAndNestedClipScopesAreRecordedAtTheSameStateBoundaries() {
    NvgTextCommandRecorder recorder = new NvgTextCommandRecorder();
    Element outer = clippedElement(10, 20, 100, 80);
    Element inner = clippedElement(30, 40, 60, 50);
    inner.scrollLeft(4);
    inner.scrollTop(5);
    outer.addChild(inner);
    inner.offsetParent(outer);

    try (var transform =
            NvgTransformState.apply(1, AffineTransform.translation(3, 4), recorder);
        var outerContent = NvgSubtreeContentState.apply(1, outer, recorder);
        var content = NvgSubtreeContentState.apply(1, inner, recorder)) {}

    assertEquals(
        List.of(
            new NvgTextCommand.TransformScope(true),
            new NvgTextCommand.Transform(1, 0, 0, 1, 3, 4),
            new NvgTextCommand.TransformScope(true),
            new NvgTextCommand.Clip(NvgTextCommand.ClipOperation.INTERSECT, 10, 20, 100, 80),
            new NvgTextCommand.Translate(-0f, -0f),
            new NvgTextCommand.TransformScope(true),
            new NvgTextCommand.Clip(NvgTextCommand.ClipOperation.INTERSECT, 40, 60, 60, 50),
            new NvgTextCommand.Translate(-4, -5),
            new NvgTextCommand.TransformScope(false),
            new NvgTextCommand.TransformScope(false),
            new NvgTextCommand.TransformScope(false)),
        recorder.commands());
  }

  private Element clippedElement(float x, float y, float width, float height) {
    Element element = new Element("div");
    element.box().contentPosition(x, y);
    element.box().contentSize(width, height);
    element.resolvedStyle().overflowX(Overflow.HIDDEN);
    element.resolvedStyle().overflowY(Overflow.HIDDEN);
    return element;
  }
}
