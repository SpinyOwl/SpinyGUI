package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgTextOutcomeDiagnostics.TextPath;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSnapshot;
import java.util.List;

final class NvgTextRecordingAssertions {
  private NvgTextRecordingAssertions() {}

  static void assertReconciled(
      List<NvgTextCommand> commands, DiagnosticSnapshot diagnostics) {
    assertCommandCount(
        commands, NvgTextCommand.Text.class, diagnostics, NvgDiagnosticCounter.TEXT_CALLS);
    assertCommandCount(
        commands,
        NvgTextCommand.Face.class,
        true,
        diagnostics,
        NvgDiagnosticCounter.FONT_FACE_CALLS);
    assertCommandCount(
        commands, NvgTextCommand.FontSize.class, diagnostics, NvgDiagnosticCounter.FONT_SIZE_CALLS);
    assertCommandCount(
        commands, NvgTextCommand.FillColor.class, diagnostics, NvgDiagnosticCounter.FILL_COLOR_CALLS);
    assertCommandCount(
        commands, NvgTextCommand.Alignment.class, diagnostics, NvgDiagnosticCounter.TEXT_ALIGN_CALLS);
    assertEquals(
        count(commands, command -> command instanceof NvgTextCommand.Scope scope && scope.begin())
            + count(
                commands,
                command ->
                    command instanceof NvgTextCommand.TransformScope scope && scope.begin()),
        diagnostics.value(NvgDiagnosticCounter.SAVE_CALLS));
    assertEquals(
        count(commands, command -> command instanceof NvgTextCommand.Scope scope && !scope.begin())
            + count(
                commands,
                command ->
                    command instanceof NvgTextCommand.TransformScope scope && !scope.begin()),
        diagnostics.value(NvgDiagnosticCounter.RESTORE_CALLS));
    assertEquals(
        count(
            commands,
            command ->
                command instanceof NvgTextCommand.Clip clip
                    && clip.operation() == NvgTextCommand.ClipOperation.SCISSOR),
        diagnostics.value(NvgDiagnosticCounter.SCISSOR_CALLS));
    assertEquals(
        count(
            commands,
            command ->
                command instanceof NvgTextCommand.Clip clip
                    && clip.operation() == NvgTextCommand.ClipOperation.INTERSECT),
        diagnostics.value(NvgDiagnosticCounter.INTERSECT_SCISSOR_CALLS));
    assertEquals(
        count(
            commands,
            command ->
                command instanceof NvgTextCommand.Clip clip
                    && clip.operation() == NvgTextCommand.ClipOperation.RESET),
        diagnostics.value(NvgDiagnosticCounter.RESET_SCISSOR_CALLS));
    assertCommandCount(
        commands, NvgTextCommand.Transform.class, diagnostics, NvgDiagnosticCounter.TRANSFORM_CALLS);
    assertCommandCount(
        commands, NvgTextCommand.Translate.class, diagnostics, NvgDiagnosticCounter.TRANSLATE_CALLS);

    for (TextPath path : TextPath.values()) {
      NvgTextCommand.TextPath commandPath = NvgTextCommand.TextPath.valueOf(path.name());
      assertOutcomeCount(commands, commandPath, path.consideredCounter(), diagnostics);
      assertOutcomeCount(commands, commandPath, path.submittedCounter(), diagnostics);
      assertOutcomeCount(commands, commandPath, path.faceSelectionFailedCounter(), diagnostics);
      assertEquals(0, diagnostics.value(path.culledCounter()));
      assertEquals(
          diagnostics.value(path.consideredCounter()),
          diagnostics.value(path.submittedCounter())
              + diagnostics.value(path.culledCounter())
              + diagnostics.value(path.faceSelectionFailedCounter()));
      assertFailedFacesCorrelate(commands, commandPath, path, diagnostics);
    }
    assertOutcomeCount(
        commands,
        NvgTextCommand.TextPath.TEXTAREA,
        NvgDiagnosticCounter.TEXTAREA_LINES_CONSIDERED,
        diagnostics);
    assertOutcomeCount(
        commands,
        NvgTextCommand.TextPath.TEXTAREA,
        NvgDiagnosticCounter.TEXTAREA_LINES_SUBMITTED,
        diagnostics);
    assertEquals(0, diagnostics.value(NvgDiagnosticCounter.TEXTAREA_LINES_CULLED));
    assertEquals(
        0,
        diagnostics.value(
            NvgDiagnosticCounter.TEXTAREA_LINES_CULLED_OUTSIDE_EFFECTIVE_CLIP));
    assertEquals(
        0,
        diagnostics.value(
            NvgDiagnosticCounter.NORMAL_TEXT_CULLED_OUTSIDE_EFFECTIVE_CLIP));
    assertEquals(
        0,
        diagnostics.value(
            NvgDiagnosticCounter.INPUT_TEXT_CULLED_OUTSIDE_EFFECTIVE_CLIP));
    assertEquals(
        0,
        diagnostics.value(
            NvgDiagnosticCounter.TEXTAREA_TEXT_CULLED_OUTSIDE_EFFECTIVE_CLIP));
    assertEquals(
        diagnostics.value(NvgDiagnosticCounter.TEXTAREA_LINES_CONSIDERED),
        diagnostics.value(NvgDiagnosticCounter.TEXTAREA_LINES_SUBMITTED));
    assertEquals(0, commands.stream().filter(NvgTextCommand.Cull.class::isInstance).count());
    assertEquals(
        diagnostics.value(NvgDiagnosticCounter.FONT_FACE_FAILURES),
        diagnostics.value(NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_FACE_SELECTION_FAILED)
            + diagnostics.value(NvgDiagnosticCounter.INPUT_TEXT_ITEMS_FACE_SELECTION_FAILED)
            + diagnostics.value(NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_FACE_SELECTION_FAILED));
  }

  private static void assertFailedFacesCorrelate(
      List<NvgTextCommand> commands,
      NvgTextCommand.TextPath commandPath,
      TextPath diagnosticPath,
      DiagnosticSnapshot diagnostics) {
    long failedFaces = 0;
    for (int index = 0; index < commands.size(); index++) {
      NvgTextCommand command = commands.get(index);
      if (command instanceof NvgTextCommand.Face face
          && face.path() == commandPath
          && !face.selected()) {
        failedFaces++;
        assertEquals(
            new NvgTextCommand.Outcome(
                commandPath, diagnosticPath.faceSelectionFailedCounter().id()),
            commands.get(index + 1));
      }
    }
    assertEquals(failedFaces, diagnostics.value(diagnosticPath.faceSelectionFailedCounter()));
  }

  private static void assertOutcomeCount(
      List<NvgTextCommand> commands,
      NvgTextCommand.TextPath path,
      NvgDiagnosticCounter counter,
      DiagnosticSnapshot diagnostics) {
    assertEquals(
        count(
            commands,
            command ->
                command instanceof NvgTextCommand.Outcome outcome
                    && outcome.path() == path
                    && outcome.diagnosticId().equals(counter.id())),
        diagnostics.value(counter));
  }

  private static void assertCommandCount(
      List<NvgTextCommand> commands,
      Class<? extends NvgTextCommand> type,
      DiagnosticSnapshot diagnostics,
      NvgDiagnosticCounter counter) {
    assertEquals(commands.stream().filter(type::isInstance).count(), diagnostics.value(counter));
  }

  private static void assertCommandCount(
      List<NvgTextCommand> commands,
      Class<? extends NvgTextCommand> type,
      boolean selected,
      DiagnosticSnapshot diagnostics,
      NvgDiagnosticCounter counter) {
    assertEquals(
        count(
            commands,
            command ->
                type.isInstance(command)
                    && ((NvgTextCommand.Face) command).selected() == selected),
        diagnostics.value(counter));
  }

  private static long count(
      List<NvgTextCommand> commands,
      java.util.function.Predicate<NvgTextCommand> predicate) {
    return commands.stream().filter(predicate).count();
  }
}
