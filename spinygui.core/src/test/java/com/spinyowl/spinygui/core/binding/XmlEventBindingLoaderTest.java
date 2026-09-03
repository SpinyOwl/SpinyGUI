package com.spinyowl.spinygui.core.binding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.event.MouseClickEvent;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.event.processor.InputImpact;
import com.spinyowl.spinygui.core.event.processor.InputProcessingBatch;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.parser.NodeParser;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class XmlEventBindingLoaderTest {
  /** Real parser used for binding and tree-shape fixtures. */
  private final DefaultNodeParser parser = new DefaultNodeParser();

  @Test
  void attachesOneProxyPerSupportedDeclarationWithoutARegistry() {
    XmlEventBindingLoader loader = new XmlEventBindingLoader(parser);

    Element button =
        assertInstanceOf(
            Element.class,
            loader.fromHtml("<button on-action=\"save\" on-click=\"inspect\">Save</button>"));
    Element plain =
        assertInstanceOf(Element.class, loader.fromHtml("<button data-handler=\"save\">Save</button>"));

    assertEquals(1, button.getListeners(ActionEvent.class).size());
    assertEquals(1, button.getListeners(MouseClickEvent.class).size());
    assertTrue(plain.getListeners(ActionEvent.class).isEmpty());
    assertTrue(plain.getListeners(MouseClickEvent.class).isEmpty());
  }

  @Test
  void preventsDuplicateAttachmentWhenADelegateReturnsTheSameTree() {
    Element button =
        assertInstanceOf(Element.class, parser.fromHtml("<button on-action=\"save\">Save</button>"));
    NodeParser sameTreeParser = new SameTreeParser(button, parser);
    XmlEventBindingLoader first = new XmlEventBindingLoader(sameTreeParser);
    XmlEventBindingLoader second = new XmlEventBindingLoader(sameTreeParser);

    assertSame(button, first.fromHtml("ignored"));
    EventListener<ActionEvent> proxy = button.getListeners(ActionEvent.class).getFirst();
    assertSame(button, first.fromHtml("ignored"));
    assertSame(button, second.fromHtml("ignored"));

    assertEquals(1, button.getListeners(ActionEvent.class).size());
    assertSame(proxy, button.getListeners(ActionEvent.class).getFirst());
  }

  @Test
  void oneRegistrationServesMultipleElementsExactlyOnce() {
    HandlerRegistry registry = new HandlerRegistry();
    AtomicInteger invocations = new AtomicInteger();
    registry.register("save", ActionEvent.class, event -> invocations.incrementAndGet());
    XmlEventBindingLoader loader =
        new XmlEventBindingLoader(parser, registry, XmlEventBindingOptions.defaults());

    Element root =
        assertInstanceOf(
            Element.class,
            loader.fromHtml(
                "<section><button on-action=\"save\">One</button>"
                    + "<button on-action=\"save\">Two</button></section>"));
    Element first = assertInstanceOf(Element.class, root.childNodes().get(0));
    Element second = assertInstanceOf(Element.class, root.childNodes().get(1));

    first.getListeners(ActionEvent.class).getFirst().process(action(first));
    second.getListeners(ActionEvent.class).getFirst().process(action(second));

    assertEquals(2, invocations.get());
  }

  @Test
  void appliesAllMissingRegistryAndHandlerPolicies() {
    for (MissingHandlerPolicy policy : MissingHandlerPolicy.values()) {
      List<BindingDiagnostic> diagnostics = new ArrayList<>();
      XmlEventBindingOptions options = new XmlEventBindingOptions(policy, diagnostics::add);
      AtomicReference<Optional<HandlerRegistry>> source = new AtomicReference<>(Optional.empty());
      XmlEventBindingLoader loader = new XmlEventBindingLoader(parser, source::get, options);
      Element button =
          assertInstanceOf(
              Element.class, loader.fromHtml("<button id=\"save\" on-action=\"save\">Save</button>"));
      EventListener<ActionEvent> proxy = button.getListeners(ActionEvent.class).getFirst();

      if (policy == MissingHandlerPolicy.ERROR) {
        IllegalStateException unavailable =
            assertThrows(IllegalStateException.class, () -> proxy.process(action(button)));
        assertTrue(unavailable.getMessage().contains("attribute=on-action"));
        assertTrue(unavailable.getMessage().contains("handler='save'"));
        assertTrue(unavailable.getMessage().contains("tag=button"));
        assertTrue(unavailable.getMessage().contains("element=button#save"));
      } else {
        proxy.process(action(button));
        proxy.process(action(button));
        assertEquals(policy == MissingHandlerPolicy.WARNING ? 1 : 0, diagnostics.size());
      }

      source.set(Optional.of(new HandlerRegistry()));
      if (policy == MissingHandlerPolicy.ERROR) {
        assertThrows(IllegalStateException.class, () -> proxy.process(action(button)));
      } else {
        proxy.process(action(button));
        proxy.process(action(button));
        assertEquals(policy == MissingHandlerPolicy.WARNING ? 2 : 0, diagnostics.size());
      }
    }
  }

  @Test
  void warningDedupeTracksAttributeAndRegistryIdentityAndRevision() {
    List<BindingDiagnostic> diagnostics = new ArrayList<>();
    AtomicReference<Optional<HandlerRegistry>> source = new AtomicReference<>(Optional.empty());
    XmlEventBindingLoader loader =
        new XmlEventBindingLoader(
            parser,
            source::get,
            new XmlEventBindingOptions(MissingHandlerPolicy.WARNING, diagnostics::add));
    Element button =
        assertInstanceOf(
            Element.class, loader.fromHtml("<button on-action=\"save\">Save</button>"));
    EventListener<ActionEvent> proxy = button.getListeners(ActionEvent.class).getFirst();

    proxy.process(action(button));
    proxy.process(action(button));
    HandlerRegistry first = new HandlerRegistry();
    source.set(Optional.of(first));
    proxy.process(action(button));
    proxy.process(action(button));
    first.register("other", ActionEvent.class, event -> {});
    proxy.process(action(button));
    HandlerRegistry second = new HandlerRegistry();
    source.set(Optional.of(second));
    proxy.process(action(button));
    button.setAttribute("on-action", "alternate");
    proxy.process(action(button));

    assertEquals(5, diagnostics.size());
    assertEquals(BindingDiagnostic.Reason.REGISTRY_UNAVAILABLE, diagnostics.get(0).reason());
    assertEquals("on-action", diagnostics.get(0).eventAttribute());
    assertEquals(ActionEvent.class, diagnostics.get(0).eventClass());
    assertEquals("/button[1]", diagnostics.get(0).elementReference());
    assertEquals(-1, diagnostics.get(0).registryRevision());
    assertEquals(0, diagnostics.get(1).registryRevision());
    assertEquals(1, diagnostics.get(2).registryRevision());
    assertEquals("alternate", diagnostics.get(4).handlerName());
  }

  @Test
  void readsCurrentAttributeAndRegistryContentsWithoutReattachment() {
    HandlerRegistry registry = new HandlerRegistry();
    AtomicInteger first = new AtomicInteger();
    AtomicInteger second = new AtomicInteger();
    AtomicInteger replacement = new AtomicInteger();
    registry.register("first", ActionEvent.class, event -> first.incrementAndGet());
    registry.register("second", ActionEvent.class, event -> second.incrementAndGet());
    XmlEventBindingLoader loader =
        new XmlEventBindingLoader(
            parser,
            registry,
            new XmlEventBindingOptions(MissingHandlerPolicy.SILENT, diagnostic -> {}));
    Element root =
        assertInstanceOf(
            Element.class,
            loader.fromHtml(
                "<section><button on-action=\"first\">One</button>"
                    + "<button on-action=\"second\">Two</button></section>"));
    Element firstButton = assertInstanceOf(Element.class, root.childNodes().get(0));
    Element secondButton = assertInstanceOf(Element.class, root.childNodes().get(1));
    EventListener<ActionEvent> firstProxy =
        firstButton.getListeners(ActionEvent.class).getFirst();
    EventListener<ActionEvent> secondProxy =
        secondButton.getListeners(ActionEvent.class).getFirst();

    firstProxy.process(action(firstButton));
    secondProxy.process(action(secondButton));
    firstButton.setAttribute("on-action", "second");
    firstProxy.process(action(firstButton));
    registry.replace("second", ActionEvent.class, event -> replacement.incrementAndGet());
    firstProxy.process(action(firstButton));
    secondProxy.process(action(secondButton));
    registry.remove("second");
    firstProxy.process(action(firstButton));
    secondProxy.process(action(secondButton));

    assertEquals(1, first.get());
    assertEquals(2, second.get());
    assertEquals(2, replacement.get());
    assertSame(firstProxy, firstButton.getListeners(ActionEvent.class).getFirst());
    assertSame(secondProxy, secondButton.getListeners(ActionEvent.class).getFirst());
    assertEquals(1, firstButton.getListeners(ActionEvent.class).size());
    assertEquals(1, secondButton.getListeners(ActionEvent.class).size());
  }

  @Test
  void malformedDeclarationsAndTypeMismatchesAreHardErrorsInEveryPolicy() {
    for (MissingHandlerPolicy policy : MissingHandlerPolicy.values()) {
      XmlEventBindingOptions options = new XmlEventBindingOptions(policy, diagnostic -> {});
      HandlerRegistry registry = new HandlerRegistry();
      registry.register("save", MouseClickEvent.class, event -> {});
      XmlEventBindingLoader loader = new XmlEventBindingLoader(parser, registry, options);

      IllegalArgumentException initialInvalid =
          assertThrows(
              IllegalArgumentException.class,
              () -> loader.fromHtml("<button on-action=\"  \">Save</button>"));
      assertTrue(initialInvalid.getMessage().contains("attribute=on-action"));
      assertTrue(initialInvalid.getMessage().contains("handler='  '"));
      assertTrue(initialInvalid.getMessage().contains("tag=button"));
      assertTrue(initialInvalid.getMessage().contains("element=/button[1]"));

      Element button =
          assertInstanceOf(
              Element.class, loader.fromHtml("<button on-action=\"save\">Save</button>"));
      EventListener<ActionEvent> proxy = button.getListeners(ActionEvent.class).getFirst();
      assertThrows(IllegalStateException.class, () -> proxy.process(action(button)));
      button.setAttribute("on-action", " ");
      assertThrows(IllegalArgumentException.class, () -> proxy.process(action(button)));
      button.removeAttribute("on-action");
      assertThrows(IllegalArgumentException.class, () -> proxy.process(action(button)));
    }
  }

  @Test
  void delegatesCustomImpactAndMarksSkippedResolutionUnknown() {
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
    XmlEventBindingLoader resolvedLoader =
        new XmlEventBindingLoader(parser, registry, XmlEventBindingOptions.defaults());
    Element resolved =
        assertInstanceOf(
            Element.class, resolvedLoader.fromHtml("<button on-action=\"save\">Save</button>"));
    InputProcessingBatch resolvedBatch = new InputProcessingBatch();

    resolved
        .getListeners(ActionEvent.class)
        .getFirst()
        .processWithImpact(action(resolved), resolvedBatch);

    assertEquals(InputImpact.FULL_REFRESH, resolvedBatch.impact());
    assertEquals(0, ordinaryCalls.get());

    for (MissingHandlerPolicy policy :
        List.of(MissingHandlerPolicy.WARNING, MissingHandlerPolicy.SILENT)) {
      XmlEventBindingLoader skippedLoader =
          new XmlEventBindingLoader(
              parser,
              Optional::empty,
              new XmlEventBindingOptions(policy, diagnostic -> {}));
      Element skipped =
          assertInstanceOf(
              Element.class, skippedLoader.fromHtml("<button on-action=\"save\">Save</button>"));
      InputProcessingBatch skippedBatch = new InputProcessingBatch();

      skipped
          .getListeners(ActionEvent.class)
          .getFirst()
          .processWithImpact(action(skipped), skippedBatch);

      assertEquals(InputImpact.FULL_UNKNOWN, skippedBatch.impact());
    }
  }

  private static ActionEvent action(Element target) {
    return ActionEvent.builder().source(target).target(target).timestamp(0).build();
  }

  /**
   * Minimal delegate that returns one stable tree to exercise duplicate-proxy prevention.
   *
   * @param tree stable parsed tree
   * @param serializer real parser used only for serialization delegation
   */
  private record SameTreeParser(Node tree, NodeParser serializer) implements NodeParser {
    @Override
    public Node fromHtml(String xml) {
      return tree;
    }

    @Override
    public String toHtml(Node node) {
      return serializer.toHtml(node);
    }

    @Override
    public String toHtml(Node node, boolean pretty) {
      return serializer.toHtml(node, pretty);
    }
  }
}
