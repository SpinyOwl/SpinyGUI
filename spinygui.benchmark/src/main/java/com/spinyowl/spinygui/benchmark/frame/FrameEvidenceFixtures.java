package com.spinyowl.spinygui.benchmark.frame;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.FrameDiagnosticCounter;
import com.spinyowl.spinygui.core.layout.impl.LayoutServiceImpl;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.AffineTransform;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.style.types.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Deterministic reference scenes used by E6 frame-path evidence and equivalence tests. */
public final class FrameEvidenceFixtures {
  private FrameEvidenceFixtures() {}

  public static Fixture build(FrameScenarioSpecifications.Scenario scenario) {
    Objects.requireNonNull(scenario, "scenario");
    Frame frame = new Frame();
    frame.frameSize(scenario.frameWidthPx(), scenario.frameHeightPx());
    frame.resolvedStyle().display(Display.BLOCK);
    frame.resolvedStyle().position(Position.STATIC);
    frame.box().contentSize(scenario.frameWidthPx(), scenario.frameHeightPx());
    List<Element> elements = new ArrayList<>();
    for (int index = 0; index < scenario.nodeCount(); index++) {
      Element element = NodeBuilder.div();
      element.resolvedStyle().display(Display.BLOCK);
      element.resolvedStyle().position(Position.STATIC);
      element.setAttribute("id", scenario.name() + "-" + index);
      element.setAttribute("class", "e6-panel e6-panel-" + scenario.kindId());
      float y =
          scenario.kind() == FrameScenarioSpecifications.Kind.SCROLL
              ? scenario.frameHeightPx() + (index / 16) * 28
              : (index / 16) * 28;
      element.box().contentPosition((index % 16) * 72, y);
      element.box().contentSize(64, 20);
      frame.addChild(element);
      elements.add(element);
    }

    if (scenario.kind() == FrameScenarioSpecifications.Kind.POINTER_ACTIVE
        || scenario.kind() == FrameScenarioSpecifications.Kind.TRANSFORM) {
      elements.get(scenario.activeNodeIndex()).hovered(true);
    }
    if (scenario.kind() == FrameScenarioSpecifications.Kind.SCROLL) {
      frame.scrollHeight(scenario.frameHeightPx() + 256);
      frame.clientHeight(scenario.frameHeightPx());
      frame.scrollTop(64);
    }
    if (scenario.kind() == FrameScenarioSpecifications.Kind.TRANSFORM) {
      elements
          .get(scenario.activeNodeIndex())
          .presentationState()
          .transform(AffineTransform.translation(7, 3).multiply(AffineTransform.rotationDegrees(6)));
    }
    return new Fixture(frame, List.copyOf(elements));
  }

  /** Reapplies scenario-owned interaction state after a force-full style pass. */
  public static void applyRuntimeState(
      FrameScenarioSpecifications.Scenario scenario, Fixture fixture) {
    Frame frame = fixture.frame();
    if (scenario.kind() == FrameScenarioSpecifications.Kind.SCROLL) {
      frame.resolvedStyle().overflowX(Overflow.HIDDEN);
      frame.resolvedStyle().overflowY(Overflow.AUTO);
      frame.scrollTop(64);
    }
    if (scenario.kind() == FrameScenarioSpecifications.Kind.POINTER_ACTIVE
        || scenario.kind() == FrameScenarioSpecifications.Kind.TRANSFORM) {
      fixture.elements().get(scenario.activeNodeIndex()).hovered(true);
    }
    if (scenario.kind() == FrameScenarioSpecifications.Kind.TRANSFORM) {
      fixture
          .elements()
          .get(scenario.activeNodeIndex())
          .presentationState()
          .transform(AffineTransform.translation(7, 3).multiply(AffineTransform.rotationDegrees(6)));
    }
  }

  public static void validateDeterministicReference(FrameScenarioSpecifications.Scenario scenario) {
    Fixture first = build(scenario);
    Fixture second = build(scenario);
    validateFixtureShape(scenario, first);
    validateFixtureShape(scenario, second);
    if (!first.signature().equals(second.signature())) {
      throw new IllegalStateException("Reference fixture is not deterministic: " + scenario.name());
    }
  }

  /** Runs the force-full/reference invariants owned by the P1 evidence contract. */
  public static void validateReferenceContracts() {
    for (FrameScenarioSpecifications.Scenario scenario : FrameScenarioSpecifications.SCENARIOS) {
      validateDeterministicReference(scenario);
    }
    validateCascadeReference();
    validateScrollConvergenceReference();
    validateTreeMutationReference();
  }

  private static void validateCascadeReference() {
    Frame frame = new Frame();
    Element element = NodeBuilder.div();
    element.setAttribute("class", "e6-panel selected");
    frame.addChild(element);
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    var parser = StyleSheetParserFactory.createParser(propertyStore);
    frame.styleSheets().add(
        parser.parse(".e6-panel { opacity: 0.25; } .e6-panel.selected { opacity: 0.75; }"));
    var manager = new StyleManagerImpl(propertyStore, parser);
    manager.recalculate(frame);
    if (Float.compare(element.resolvedStyle().opacity(), 0.75f) != 0) {
      throw new IllegalStateException("Cascade reference did not select the more specific rule");
    }
    manager.recalculate(frame);
    if (Float.compare(element.resolvedStyle().opacity(), 0.75f) != 0) {
      throw new IllegalStateException("Repeated force-full cascade changed the resolved result");
    }
  }

  private static void validateTreeMutationReference() {
    Frame frame = new Frame();
    Element first = NodeBuilder.div();
    Element second = NodeBuilder.div();
    frame.addChild(first);
    frame.addChild(second);
    frame.removeChild(first);
    frame.addChild(first);
    if (frame.childNodes().size() != 2
        || frame.childNodes().get(0) != second
        || frame.childNodes().get(1) != first
        || first.parent() != frame
        || second.parent() != frame) {
      throw new IllegalStateException("Tree mutation reference violated child order or ownership");
    }
  }

  private static void validateScrollConvergenceReference() {
    var scenario =
        FrameScenarioSpecifications.SCENARIOS.stream()
            .filter(candidate -> candidate.kind() == FrameScenarioSpecifications.Kind.SCROLL)
            .findFirst()
            .orElseThrow();
    Frame frame = build(scenario).frame();
    frame.resolvedStyle().overflowX(Overflow.HIDDEN);
    frame.resolvedStyle().overflowY(Overflow.AUTO);
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(FrameDiagnosticCounter.values()));
    frame.diagnostics(diagnostics);
    var layout = new LayoutServiceImpl((text, context) -> {}, new java.util.HashMap<>());
    layout.layout(frame);
    float firstScrollHeight = frame.scrollHeight();
    float firstClientHeight = frame.clientHeight();
    layout.layout(frame);
    if (Float.compare(firstScrollHeight, frame.scrollHeight()) != 0
        || Float.compare(firstClientHeight, frame.clientHeight()) != 0
        || diagnostics.snapshot().value(FrameDiagnosticCounter.LAYOUT_PASSES) < 2) {
      throw new IllegalStateException("Scrollbar convergence reference is not stable");
    }
  }

  private static void validateFixtureShape(
      FrameScenarioSpecifications.Scenario scenario, Fixture fixture) {
    Frame frame = fixture.frame();
    if (frame.childNodes().size() != scenario.nodeCount()
        || fixture.elements().size() != scenario.nodeCount()) {
      throw new IllegalStateException("Fixture node count disagrees with scenario: " + scenario.name());
    }
    for (int index = 0; index < fixture.elements().size(); index++) {
      Element element = fixture.elements().get(index);
      if (element.parent() != frame || frame.getElementById(scenario.name() + "-" + index) != element) {
        throw new IllegalStateException("Fixture tree invariant failed: " + scenario.name());
      }
    }
    if (scenario.kind() == FrameScenarioSpecifications.Kind.SCROLL
        && Float.compare(frame.scrollTop(), 64) != 0) {
      throw new IllegalStateException("Scroll fixture lost its declared scroll offset");
    }
    if (scenario.kind() == FrameScenarioSpecifications.Kind.TRANSFORM
        && fixture.elements().get(scenario.activeNodeIndex()).presentationState().transform()
            == AffineTransform.IDENTITY) {
      throw new IllegalStateException("Transform fixture lost its non-identity transform");
    }
  }

  public record Fixture(Frame frame, List<Element> elements) {
    public Fixture {
      Objects.requireNonNull(frame, "frame");
      elements = List.copyOf(elements);
    }

    public String signature() {
      StringBuilder signature = new StringBuilder();
      signature
          .append(frame.frameSize().x)
          .append('x')
          .append(frame.frameSize().y)
          .append('|')
          .append(frame.scrollTop())
          .append('|')
          .append(frame.scrollHeight());
      for (Node node : frame.childNodes()) {
        Element element = node.asElement();
        signature
            .append('|')
            .append(element.getIdAttribute())
            .append(':')
            .append(element.getClassAttribute())
            .append(':')
            .append(element.box().contentPosition())
            .append(':')
            .append(element.hovered())
            .append(':')
            .append(element.presentationState().transform());
      }
      return signature.toString();
    }
  }
}
