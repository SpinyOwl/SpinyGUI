package com.spinyowl.spinygui.benchmark.rendering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.layout.impl.InlineFormattingContext;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgStructuralValidation;
import java.util.List;
import org.junit.jupiter.api.Test;

class RenderingBoundaryScenesTest {
  @Test
  void approvedScenesExecuteSourceBoundProductionRecordingPaths() {
    var specification = RenderingWorkloadSpecifications.CURRENT;
    var evidence =
        RenderingBoundaryScenes.validateAll(
            specification.createFontService(DiagnosticSession.disabled()));

    assertEquals(StructuralValidationReport.APPROVED_SCENE_ORDER,
        evidence.stream().map(item -> item.sceneId()).toList());
    var fallback = evidence.get(0);
    assertEquals(List.of("A", "雪", "�"), fallback.submittedText());
    assertEquals(2, fallback.selectedFaceIds().stream().distinct().count());
    assertTrue(fallback.replacementSubmitted());
    assertTrue(fallback.overhangSubmitted());
    assertTrue(
        evidence.stream()
            .filter(item -> item.sceneId().equals("nested-clipping"))
            .allMatch(item -> item.clipCommands() > 0));
    assertTrue(
        evidence.stream()
            .filter(item -> item.sceneId().equals("selection-caret"))
            .allMatch(item -> item.selectionCommands() > 0 && item.caretCommands() > 0));
    assertTrue(
        evidence.stream()
            .filter(item -> item.sceneId().equals("transformed-text"))
            .allMatch(item -> item.nonIdentityTransform()));
    evidence.forEach(
        item -> {
          assertTrue(item.sourceExpectationSha256().startsWith("sha256:"));
          assertTrue(item.commandDigestSha256().startsWith("sha256:"));
          assertTrue(item.evidenceDigestSha256().startsWith("sha256:"));
          assertTrue(
              com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgStructuralValidation
                  .evidenceDigestValid(item));
        });
  }

  @Test
  void currentProductionSmallSceneMatchesItsSourceBoundSynchronizedFixture() {
    var specification = RenderingWorkloadSpecifications.CURRENT;
    var fontService = specification.createFontService(DiagnosticSession.disabled());
    Frame frame = new Frame();
    specification.style().apply(frame);
    frame.frameSize(specification.window().widthPx(), specification.window().heightPx());
    frame.box().contentSize(specification.window().widthPx(), specification.window().heightPx());
    Element container = NodeBuilder.div();
    specification.style().apply(container);
    container.box().contentPosition(
        specification.container().positionXPx(), specification.container().positionYPx());
    container.box().contentSize(
        specification.container().widthPx(), specification.container().heightPx());
    container.offsetParent(frame);
    frame.addChild(container);
    List<Node> textNodes = new java.util.ArrayList<>();
    for (int index = 0; index < specification.scene("small").textNodeCount(); index++) {
      Text text = NodeBuilder.text(specification.transformedContent(index));
      text.offsetParent(container);
      container.addChild(text);
      textNodes.add(text);
    }
    new InlineFormattingContext(fontService).layout(
        container, textNodes, specification.inlineLayoutStartYPx());
    frame.layoutChildNodes(List.of(container));
    container.layoutChildNodes(textNodes);

    var production = NvgStructuralValidation.validate(
        frame, fontService, RenderingBoundaryScenes.synchronizedSmallRequirements(frame));
    var fixture = RenderingBoundaryScenes.synchronizedSmallFixtureEvidence(fontService);

    assertEquals(fixture, production);
  }
}
