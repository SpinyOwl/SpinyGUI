package com.spinyowl.spinygui.core.binding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.event.MouseClickEvent;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.event.processor.DefaultEventProcessor;
import com.spinyowl.spinygui.core.event.processor.InputImpact;
import com.spinyowl.spinygui.core.event.processor.InputProcessingBatch;
import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.MouseButton;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

class XmlEventBindingDispatchTest {
  /** Real parser used to construct XML-bound dispatcher targets. */
  private final DefaultNodeParser parser = new DefaultNodeParser();

  @Test
  void dispatchesBothSupportedDeclarationsThroughDefaultEventProcessor() {
    HandlerRegistry registry = new HandlerRegistry();
    AtomicInteger actions = new AtomicInteger();
    AtomicInteger clicks = new AtomicInteger();
    registry.register("activate", ActionEvent.class, event -> actions.incrementAndGet());
    registry.register("inspect", MouseClickEvent.class, event -> clicks.incrementAndGet());
    Element button =
        load(
            registry,
            XmlEventBindingOptions.defaults(),
            "<button on-action=\"activate\" on-click=\"inspect\">Open</button>");
    DefaultEventProcessor processor = new DefaultEventProcessor();

    processor.push(action(button));
    processor.push(click(button));
    InputImpact impact = processor.processEvents();

    assertEquals(1, actions.get());
    assertEquals(1, clicks.get());
    assertEquals(InputImpact.FULL_UNKNOWN, impact);
  }

  @Test
  void dispatchesOneSharedRegistrationOnceForEachTarget() {
    HandlerRegistry registry = new HandlerRegistry();
    AtomicInteger actions = new AtomicInteger();
    registry.register("save", ActionEvent.class, event -> actions.incrementAndGet());
    Element root =
        load(
            registry,
            XmlEventBindingOptions.defaults(),
            "<section><button on-action=\"save\">One</button>"
                + "<button on-action=\"save\">Two</button></section>");
    Element first = assertInstanceOf(Element.class, root.childNodes().get(0));
    Element second = assertInstanceOf(Element.class, root.childNodes().get(1));
    DefaultEventProcessor processor = new DefaultEventProcessor();

    processor.push(action(first));
    processor.push(action(second));
    processor.processEvents();

    assertEquals(2, actions.get());
  }

  @Test
  void dynamicAttributeAndRegistryMutationsAffectEveryTargetWithoutReattachment() {
    HandlerRegistry registry = new HandlerRegistry();
    AtomicInteger firstHandler = new AtomicInteger();
    AtomicInteger secondHandler = new AtomicInteger();
    AtomicInteger replacement = new AtomicInteger();
    registry.register("first", ActionEvent.class, event -> firstHandler.incrementAndGet());
    registry.register("second", ActionEvent.class, event -> secondHandler.incrementAndGet());
    Element root =
        load(
            registry,
            new XmlEventBindingOptions(MissingHandlerPolicy.SILENT, diagnostic -> {}),
            "<section><button on-action=\"first\">One</button>"
                + "<button on-action=\"second\">Two</button></section>");
    Element first = assertInstanceOf(Element.class, root.childNodes().get(0));
    Element second = assertInstanceOf(Element.class, root.childNodes().get(1));
    EventListener<ActionEvent> firstProxy = first.getListeners(ActionEvent.class).getFirst();
    EventListener<ActionEvent> secondProxy = second.getListeners(ActionEvent.class).getFirst();

    dispatch(action(first));
    dispatch(action(second));
    first.setAttribute("on-action", "second");
    dispatch(action(first));
    registry.replace("second", ActionEvent.class, event -> replacement.incrementAndGet());
    dispatch(action(first), action(second));
    registry.remove("second");
    assertEquals(InputImpact.FULL_UNKNOWN, dispatch(action(first), action(second)));

    assertEquals(1, firstHandler.get());
    assertEquals(2, secondHandler.get());
    assertEquals(2, replacement.get());
    assertSame(firstProxy, first.getListeners(ActionEvent.class).getFirst());
    assertSame(secondProxy, second.getListeners(ActionEvent.class).getFirst());
    assertEquals(1, first.getListeners(ActionEvent.class).size());
    assertEquals(1, second.getListeners(ActionEvent.class).size());
  }

  @Test
  void missingPoliciesPreserveManualListenersAndWarningDedupeAcrossBatches() {
    for (MissingHandlerPolicy policy : MissingHandlerPolicy.values()) {
      List<BindingDiagnostic> diagnostics = new ArrayList<>();
      AtomicReference<Optional<HandlerRegistry>> source = new AtomicReference<>(Optional.empty());
      XmlEventBindingLoader loader =
          new XmlEventBindingLoader(
              parser,
              source::get,
              new XmlEventBindingOptions(policy, diagnostics::add));
      Element button =
          assertInstanceOf(
              Element.class, loader.fromHtml("<button on-action=\"save\">Save</button>"));
      AtomicInteger manualCalls = new AtomicInteger();
      button.addListener(ActionEvent.class, event -> manualCalls.incrementAndGet());

      if (policy == MissingHandlerPolicy.ERROR) {
        assertThrows(IllegalStateException.class, () -> dispatch(action(button)));
        assertEquals(0, manualCalls.get());
      } else {
        assertEquals(InputImpact.FULL_UNKNOWN, dispatch(action(button)));
        assertEquals(InputImpact.FULL_UNKNOWN, dispatch(action(button)));
        assertEquals(2, manualCalls.get());
        assertEquals(policy == MissingHandlerPolicy.WARNING ? 1 : 0, diagnostics.size());
      }

      source.set(Optional.of(new HandlerRegistry()));
      if (policy == MissingHandlerPolicy.ERROR) {
        assertThrows(IllegalStateException.class, () -> dispatch(action(button)));
      } else {
        dispatch(action(button));
        dispatch(action(button));
        assertEquals(4, manualCalls.get());
        assertEquals(policy == MissingHandlerPolicy.WARNING ? 2 : 0, diagnostics.size());
      }
      assertEquals(2, button.getListeners(ActionEvent.class).size());
    }
  }

  @Test
  void preservesCustomImpactOverrideThroughDefaultEventProcessor() {
    HandlerRegistry registry = new HandlerRegistry();
    AtomicInteger ordinaryCalls = new AtomicInteger();
    registry.register(
        "save",
        ActionEvent.class,
        new EventListener<>() {
          @Override
          public void process(ActionEvent event) {
            ordinaryCalls.incrementAndGet();
          }

          @Override
          public void processWithImpact(ActionEvent event, InputProcessingBatch batch) {
            batch.markKnownEffect();
          }
        });
    Element button =
        load(
            registry,
            XmlEventBindingOptions.defaults(),
            "<button on-action=\"save\">Save</button>");

    InputImpact impact = dispatch(action(button));

    assertEquals(InputImpact.FULL_REFRESH, impact);
    assertEquals(0, ordinaryCalls.get());
  }

  @Test
  void preservesExactClassDispatchAndHardMismatchFailure() {
    HandlerRegistry actionRegistry = new HandlerRegistry();
    AtomicInteger actions = new AtomicInteger();
    actionRegistry.register("activate", ActionEvent.class, event -> actions.incrementAndGet());
    Element actionOnly =
        load(
            actionRegistry,
            XmlEventBindingOptions.defaults(),
            "<button on-action=\"activate\">Open</button>");

    assertEquals(InputImpact.FULL_UNKNOWN, dispatch(click(actionOnly)));
    assertEquals(0, actions.get());

    HandlerRegistry mismatched = new HandlerRegistry();
    mismatched.register("shared", ActionEvent.class, event -> {});
    Element clickTarget =
        load(
            mismatched,
            new XmlEventBindingOptions(MissingHandlerPolicy.SILENT, diagnostic -> {}),
            "<button on-click=\"shared\">Open</button>");

    assertThrows(IllegalStateException.class, () -> dispatch(click(clickTarget)));
  }

  private Element load(
      HandlerRegistry registry, XmlEventBindingOptions options, String xml) {
    XmlEventBindingLoader loader = new XmlEventBindingLoader(parser, registry, options);
    return assertInstanceOf(Element.class, loader.fromHtml(xml));
  }

  private static InputImpact dispatch(com.spinyowl.spinygui.core.event.Event... events) {
    DefaultEventProcessor processor = new DefaultEventProcessor();
    for (var event : events) {
      processor.push(event);
    }
    return processor.processEvents();
  }

  private static ActionEvent action(Element target) {
    return ActionEvent.builder().source(target).target(target).timestamp(0).build();
  }

  private static MouseClickEvent click(Element target) {
    return MouseClickEvent.builder()
        .source(target)
        .target(target)
        .timestamp(0)
        .action(KeyAction.CLICK)
        .mouseButton(MouseButton.LEFT)
        .position(new Vector2f())
        .absolutePosition(new Vector2f())
        .mods(ImmutableSet.of())
        .build();
  }
}
