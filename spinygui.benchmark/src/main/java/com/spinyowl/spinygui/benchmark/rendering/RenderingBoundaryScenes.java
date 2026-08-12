package com.spinyowl.spinygui.benchmark.rendering;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgStructuralValidation;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgStructuralValidation.Evidence;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgStructuralValidation.Path;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgStructuralValidation.Requirements;
import com.spinyowl.spinygui.core.layout.impl.InlineFormattingContext;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.AffineTransform;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import java.util.ArrayList;
import java.util.List;

/** Source-bound approved boundary scenes shared by structural and optional image evidence. */
public final class RenderingBoundaryScenes {
  private static final int WIDTH = 400;
  private static final int HEIGHT = 200;

  private RenderingBoundaryScenes() {}

  public static List<Evidence> validateAll(FontServiceImpl fontService) {
    return scenes(fontService).stream()
        .map(
            scene ->
                NvgStructuralValidation.validate(
                    scene.frame(), fontService, scene.requirements()))
        .toList();
  }

  public static Evidence synchronizedSmallFixtureEvidence(FontServiceImpl fontService) {
    var specification = RenderingWorkloadSpecifications.CURRENT;
    var scene = specification.scene("small");
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
    frame.layoutChildNodes(List.of(container));
    List<Node> nodes = new ArrayList<>();
    for (int index = 0; index < scene.textNodeCount(); index++) {
      String content = specification.transformedContent(index);
      Text text = NodeBuilder.text(content);
      text.offsetParent(container);
      container.addChild(text);
      nodes.add(text);
    }
    container.layoutChildNodes(nodes);
    new InlineFormattingContext(fontService).layout(
        container, nodes, specification.inlineLayoutStartYPx());
    return NvgStructuralValidation.validate(
        frame,
        fontService,
        synchronizedSmallRequirements(frame));
  }

  /**
   * Builds the source-bound command contract for the executable small benchmark scene.
   *
   * <p>The raw node text and rendered run text intentionally remain separate: the former binds the
   * declared scene source, while the latter is the exact sequence submitted to NanoVG.</p>
   */
  public static Requirements synchronizedSmallRequirements(Frame frame) {
    List<Text> textNodes = new ArrayList<>();
    collectTextNodes(frame, textNodes);
    List<String> source = textNodes.stream().map(Text::content).toList();
    List<String> submitted = new ArrayList<>();
    for (Text text : textNodes) {
      text.inlineFragments().stream()
          .filter(fragment -> fragment.textFragment())
          .forEach(
              fragment -> {
                if (fragment.runs().isEmpty()) submitted.add(fragment.text());
                else fragment.runs().forEach(run -> submitted.add(run.renderedText()));
              });
    }
    return new Requirements(
        "benchmark-small",
        submitted,
        List.of(Path.NORMAL),
        false,
        false,
        false,
        false,
        List.of(),
        false,
        null,
        source);
  }

  private static void collectTextNodes(Node node, List<Text> textNodes) {
    if (node instanceof Text text) textNodes.add(text);
    if (node.layoutChildNodes() != null) {
      node.layoutChildNodes().forEach(child -> collectTextNodes(child, textNodes));
    }
  }

  static List<BoundaryScene> scenes(FontServiceImpl fontService) {
    return List.of(
        fallbackOverhang(fontService),
        nestedClipping(fontService),
        selectionCaret(fontService),
        transformedText(fontService));
  }

  private static BoundaryScene fallbackOverhang(FontServiceImpl fontService) {
    Frame frame = frame();
    Element container = container(frame, 12, 12, 92, 18);
    Text text = normalText(container, "A雪\uFDD0", fontService);
    return normalScene(
        "fallback-overhang",
        frame,
        text,
        List.of("A", "雪", "\uFFFD"),
        false,
        false,
        List.of(
            NvgStructuralValidation.fontId(com.spinyowl.spinygui.core.font.Font.ROBOTO_REGULAR),
            NvgStructuralValidation.fontId(
                com.spinyowl.spinygui.core.font.Font.NOTO_SANS_CJK_SC_REGULAR)),
        true,
        18f);
  }

  private static BoundaryScene nestedClipping(FontServiceImpl fontService) {
    Frame frame = frame();
    Element outer = container(frame, 10, 10, 180, 50);
    outer.resolvedStyle().overflowX(Overflow.HIDDEN);
    outer.resolvedStyle().overflowY(Overflow.HIDDEN);
    Element inner = NodeBuilder.div();
    RenderingWorkloadSpecifications.CURRENT.style().apply(inner);
    inner.box().contentPosition(18, 18);
    inner.box().contentSize(90, 24);
    inner.resolvedStyle().overflowX(Overflow.HIDDEN);
    inner.resolvedStyle().overflowY(Overflow.HIDDEN);
    inner.offsetParent(outer);
    outer.addChild(inner);
    outer.layoutChildNodes(List.of(inner));
    Text text = normalText(inner, "nested clipping boundary", fontService);
    return normalScene(
        "nested-clipping",
        frame,
        text,
        List.of("nested", "clipping", "boundary"),
        true,
        false,
        List.of(),
        false,
        null);
  }

  private static BoundaryScene selectionCaret(FontServiceImpl fontService) {
    Frame frame = frame();
    Element container = container(frame, 12, 12, 240, 48);
    InputElement input = new InputElement();
    RenderingWorkloadSpecifications.CURRENT.style().apply(input);
    input.value("selection caret");
    input.select(0, 9);
    input.focused(true);
    input.box().contentPosition(18, 18);
    input.box().contentSize(190, 28);
    input.offsetParent(container);
    container.addChild(input);
    container.layoutChildNodes(List.of(input));
    return new BoundaryScene(
        frame,
        new Requirements(
            "selection-caret",
            List.of("selection caret"),
            List.of(Path.INPUT),
            true,
            true,
            true,
            false,
            List.of(
                NvgStructuralValidation.fontId(
                    com.spinyowl.spinygui.core.font.Font.ROBOTO_REGULAR)),
            false,
            null,
            List.of(input.value())));
  }

  private static BoundaryScene transformedText(FontServiceImpl fontService) {
    Frame frame = frame();
    Element container = container(frame, 30, 30, 190, 50);
    container.presentationState().transform(
        AffineTransform.rotationDegrees(7).multiply(AffineTransform.translation(3, 2)));
    Text text = normalText(container, "transformed text", fontService);
    return normalScene(
        "transformed-text",
        frame,
        text,
        List.of("transformed", " ", "text"),
        false,
        true,
        List.of(),
        false,
        null);
  }

  private static BoundaryScene normalScene(
      String sceneId,
      Frame frame,
      Text text,
      List<String> expectedText,
      boolean requireClip,
      boolean requireNonIdentityTransform,
      List<String> requiredFaceIds,
      boolean requireReplacement,
      Float overhangBoundaryY) {
    return new BoundaryScene(
        frame,
        new Requirements(
            sceneId,
            expectedText,
            List.of(Path.NORMAL),
            requireClip,
            false,
            false,
            requireNonIdentityTransform,
            requiredFaceIds,
            requireReplacement,
            overhangBoundaryY,
            List.of(text.content())));
  }

  private static Frame frame() {
    Frame frame = new Frame();
    RenderingWorkloadSpecifications.CURRENT.style().apply(frame);
    frame.frameSize(WIDTH, HEIGHT);
    frame.box().contentSize(WIDTH, HEIGHT);
    return frame;
  }

  private static Element container(
      Frame frame, float x, float y, float width, float height) {
    Element container = NodeBuilder.div();
    RenderingWorkloadSpecifications.CURRENT.style().apply(container);
    container.box().contentPosition(x, y);
    container.box().contentSize(width, height);
    container.offsetParent(frame);
    frame.addChild(container);
    frame.layoutChildNodes(List.of(container));
    return container;
  }

  private static Text normalText(
      Element container, String source, FontServiceImpl fontService) {
    Text text = NodeBuilder.text(source);
    text.offsetParent(container);
    container.addChild(text);
    List<Node> nodes = List.of(text);
    container.layoutChildNodes(nodes);
    new InlineFormattingContext(fontService).layout(container, nodes, 0);
    return text;
  }

  record BoundaryScene(Frame frame, Requirements requirements) {
    String id() {
      return requirements.sceneId();
    }
  }
}
