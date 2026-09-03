package com.spinyowl.spinygui.core.binding;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.event.processor.InputProcessingBatch;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.parser.NodeParser;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Optional {@link NodeParser} composition that installs dispatch-time XML event-handler proxies.
 *
 * <p>The delegate parser remains unchanged. Each parsed tree is traversed once, and declarations
 * present during that traversal receive stable listeners which read current attribute and registry
 * state for every event.
 */
public final class XmlEventBindingLoader implements NodeParser {
  /** Parser that owns the existing XML-to-node and node-to-XML behavior. */
  private final NodeParser parser;

  /** Owner-provided source whose current optional registry is read for every event. */
  private final Supplier<Optional<HandlerRegistry>> registrySource;

  /** Immutable resolution policy and diagnostic destination for every installed proxy. */
  private final XmlEventBindingOptions options;

  /** Creates a loader with no available registry and the safe default error policy. */
  public XmlEventBindingLoader(NodeParser parser) {
    this(parser, Optional::empty, XmlEventBindingOptions.defaults());
  }

  /** Creates a loader with one fixed registry and the supplied immutable options. */
  public XmlEventBindingLoader(
      NodeParser parser, HandlerRegistry registry, XmlEventBindingOptions options) {
    this(parser, fixedRegistrySource(registry), options);
  }

  /**
   * Creates a loader whose registry may be replaced, removed, or supplied after tree loading.
   *
   * @param parser delegate parser retained for parsing and serialization
   * @param registrySource owner-thread source of the current optional registry
   * @param options immutable missing-handler configuration
   */
  public XmlEventBindingLoader(
      NodeParser parser,
      Supplier<Optional<HandlerRegistry>> registrySource,
      XmlEventBindingOptions options) {
    this.parser = Objects.requireNonNull(parser, "parser");
    this.registrySource = Objects.requireNonNull(registrySource, "registrySource");
    this.options = Objects.requireNonNull(options, "options");
  }

  /** Parses one tree and automatically installs proxies for declarations present in that tree. */
  @Override
  public Node fromHtml(String xml) {
    Node root = parser.fromHtml(xml);
    if (root != null) {
      bind(root, "/" + root.nodeName() + "[1]");
    }
    return root;
  }

  /** Delegates serialization unchanged to the injected parser. */
  @Override
  public String toHtml(Node node) {
    return parser.toHtml(node);
  }

  /** Delegates serialization unchanged to the injected parser. */
  @Override
  public String toHtml(Node node, boolean pretty) {
    return parser.toHtml(node, pretty);
  }

  private void bind(Node node, String path) {
    if (!(node instanceof Element element)) {
      return;
    }
    String elementReference = elementReference(element, path);
    for (var attribute : element.attributes().entrySet()) {
      Optional<XmlEventDeclaration> declaration =
          validatedDeclaration(element, attribute.getKey(), attribute.getValue(), elementReference);
      declaration.ifPresent(value -> attach(element, value, elementReference));
    }

    int elementIndex = 0;
    for (Node child : element.childNodes()) {
      if (child instanceof Element) {
        elementIndex++;
        bind(child, path + "/" + child.nodeName() + "[" + elementIndex + "]");
      }
    }
  }

  private Optional<XmlEventDeclaration> validatedDeclaration(
      Element element, String attributeName, String handlerName, String elementReference) {
    try {
      return XmlEventDeclaration.fromAttribute(attributeName, handlerName);
    } catch (IllegalArgumentException | NullPointerException exception) {
      throw invalidDeclaration(element, attributeName, handlerName, elementReference, exception);
    }
  }

  private static IllegalArgumentException invalidDeclaration(
      Element element,
      String attributeName,
      String handlerName,
      String elementReference,
      RuntimeException cause) {
    return new IllegalArgumentException(
        "Invalid XML event declaration [attribute="
            + attributeName
            + ", handler="
            + displayHandler(handlerName)
            + ", tag="
            + element.nodeName()
            + ", element="
            + elementReference
            + "]",
        cause);
  }

  private void attach(
      Element element, XmlEventDeclaration declaration, String elementReference) {
    attachTyped(element, declaration, declaration.eventClass(), elementReference);
  }

  @SuppressWarnings("unchecked")
  private <T extends Event> void attachTyped(
      Element element,
      XmlEventDeclaration declaration,
      Class<? extends Event> untypedEventClass,
      String elementReference) {
    Class<T> eventClass = (Class<T>) untypedEventClass;
    List<EventListener<T>> listeners = element.getListeners(eventClass);
    boolean alreadyAttached =
        listeners.stream()
            .filter(ResolvingEventListener.class::isInstance)
            .map(ResolvingEventListener.class::cast)
            .anyMatch(proxy -> proxy.attributeName.equals(declaration.attributeName()));
    if (!alreadyAttached) {
      listeners.add(
          new ResolvingEventListener<>(
              element,
              declaration.attributeName(),
              eventClass,
              elementReference,
              registrySource,
              options));
    }
  }

  private static String elementReference(Element element, String path) {
    String id = element.getIdAttribute();
    return id == null || id.isBlank() ? path : element.nodeName() + "#" + id;
  }

  private static String displayHandler(String handlerName) {
    return handlerName == null ? "<missing>" : "'" + handlerName + "'";
  }

  private static Supplier<Optional<HandlerRegistry>> fixedRegistrySource(
      HandlerRegistry registry) {
    HandlerRegistry value = Objects.requireNonNull(registry, "registry");
    return () -> Optional.of(value);
  }

  /** Stable event listener that resolves current declaration and registry state per invocation. */
  private static final class ResolvingEventListener<T extends Event> implements EventListener<T> {
    /** Element whose current declaration value is read on every dispatch. */
    private final Element element;

    /** Supported attribute associated with this one proxy. */
    private final String attributeName;

    /** Exact GUI event class used for registry lookup and listener attachment. */
    private final Class<T> eventClass;

    /** Stable tag-and-id or deterministic path used by errors and diagnostics. */
    private final String elementReference;

    /** Dynamic source of the caller-owned optional registry. */
    private final Supplier<Optional<HandlerRegistry>> registrySource;

    /** Immutable resolution policy and warning sink. */
    private final XmlEventBindingOptions options;

    /** Last reported unresolved warning state, cleared after successful resolution. */
    private WarningState lastWarning;

    private ResolvingEventListener(
        Element element,
        String attributeName,
        Class<T> eventClass,
        String elementReference,
        Supplier<Optional<HandlerRegistry>> registrySource,
        XmlEventBindingOptions options) {
      this.element = element;
      this.attributeName = attributeName;
      this.eventClass = eventClass;
      this.elementReference = elementReference;
      this.registrySource = registrySource;
      this.options = options;
    }

    /** Resolves and invokes the current handler using its ordinary processing path. */
    @Override
    public void process(T event) {
      resolve().ifPresent(listener -> listener.process(event));
    }

    /** Resolves and delegates custom impact reporting, or records unknown impact when skipped. */
    @Override
    public void processWithImpact(T event, InputProcessingBatch batch) {
      Objects.requireNonNull(batch, "batch");
      Optional<EventListener<T>> listener = resolve();
      if (listener.isPresent()) {
        listener.orElseThrow().processWithImpact(event, batch);
      } else {
        batch.markUnknownFallback();
      }
    }

    private Optional<EventListener<T>> resolve() {
      String handlerName = element.getAttribute(attributeName);
      XmlEventDeclaration declaration;
      try {
        declaration =
            XmlEventDeclaration.fromAttribute(attributeName, handlerName).orElseThrow();
      } catch (IllegalArgumentException | NullPointerException exception) {
        throw invalidDeclaration(
            element, attributeName, handlerName, elementReference, exception);
      }
      if (!eventClass.equals(declaration.eventClass())) {
        throw resolutionFailure(handlerName, "event declaration type changed unexpectedly", null);
      }

      Optional<HandlerRegistry> registry =
          Objects.requireNonNull(registrySource.get(), "registrySource result");
      if (registry.isEmpty()) {
        return unresolved(
            BindingDiagnostic.Reason.REGISTRY_UNAVAILABLE, handlerName, null, -1);
      }

      HandlerRegistry currentRegistry = registry.orElseThrow();
      Optional<EventListener<T>> listener;
      try {
        listener = currentRegistry.lookup(handlerName, eventClass);
      } catch (IllegalArgumentException exception) {
        throw resolutionFailure(handlerName, exception.getMessage(), exception);
      }
      if (listener.isEmpty()) {
        return unresolved(
            BindingDiagnostic.Reason.HANDLER_MISSING,
            handlerName,
            currentRegistry,
            currentRegistry.revision());
      }
      lastWarning = null;
      return listener;
    }

    private Optional<EventListener<T>> unresolved(
        BindingDiagnostic.Reason reason,
        String handlerName,
        HandlerRegistry registry,
        long registryRevision) {
      if (options.missingHandlerPolicy() == MissingHandlerPolicy.ERROR) {
        String detail =
            reason == BindingDiagnostic.Reason.REGISTRY_UNAVAILABLE
                ? "registry is unavailable"
                : "handler is not registered";
        throw resolutionFailure(handlerName, detail, null);
      }
      if (options.missingHandlerPolicy() == MissingHandlerPolicy.WARNING) {
        WarningState warning = new WarningState(reason, handlerName, registry, registryRevision);
        if (!warning.equals(lastWarning)) {
          options
              .diagnosticSink()
              .report(
                  new BindingDiagnostic(
                      reason,
                      attributeName,
                      handlerName,
                      eventClass,
                      elementReference,
                      registryRevision));
          lastWarning = warning;
        }
      }
      return Optional.empty();
    }

    private IllegalStateException resolutionFailure(
        String handlerName, String detail, RuntimeException cause) {
      return new IllegalStateException(
          "Unable to resolve XML event binding [attribute="
              + attributeName
              + ", handler="
              + displayHandler(handlerName)
              + ", eventClass="
              + eventClass.getName()
              + ", tag="
              + element.nodeName()
              + ", element="
              + elementReference
              + "]: "
              + detail,
          cause);
    }
  }

  /**
   * Warning deduplication key for one proxy's last reported unresolved state.
   *
   * @param reason unresolved registry state
   * @param handlerName current declaration value
   * @param registry current registry identity, or null when unavailable
   * @param registryRevision current registry revision, or -1 when unavailable
   */
  private record WarningState(
      BindingDiagnostic.Reason reason,
      String handlerName,
      HandlerRegistry registry,
      long registryRevision) {}
}
