# Named XML event handlers

SpinyGUI can optionally connect supported XML event attributes to caller-owned, named handlers.
This feature is a small composition over the existing `NodeParser`, `EventListener`, and event
processor APIs. It is not reflection-based controller discovery, data binding, or a template
framework.

## Supported declarations

The initial vocabulary is deliberately closed:

| XML attribute | Exact event class |
| --- | --- |
| `on-action` | `ActionEvent` |
| `on-click` | `MouseClickEvent` |

Attribute names are lowercase and handler names must be nonblank. Unsupported attributes remain
ordinary node attributes; they do not trigger class loading or listener discovery. A malformed
supported declaration or an event-type mismatch is always a hard error, regardless of the
missing-handler policy.

## Optional loader and registry

Existing parser and manual-listener code does not change. Opt in by composing a parser with an
`XmlEventBindingLoader` and a caller-owned `HandlerRegistry`:

```java
import com.spinyowl.spinygui.core.binding.HandlerRegistry;
import com.spinyowl.spinygui.core.binding.XmlEventBindingLoader;
import com.spinyowl.spinygui.core.binding.XmlEventBindingOptions;
import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.NodeParser;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import java.util.concurrent.atomic.AtomicInteger;

HandlerRegistry handlers = new HandlerRegistry();
AtomicInteger saveCount = new AtomicInteger();
handlers.register("save", ActionEvent.class, event -> saveCount.incrementAndGet());

NodeParser parser =
    new XmlEventBindingLoader(
        new DefaultNodeParser(), handlers, XmlEventBindingOptions.defaults());
Frame frame =
    parser.fromHtml(
            "<winframe><button id=\"save\" on-action=\"save\">Save</button></winframe>")
        .frame();
```

The loader traverses each parsed tree once and attaches one stable proxy for every supported
declaration present at load time. Resolution happens when an event is processed, not while the XML
is parsed. Therefore handlers may be registered after parsing but before dispatch, as demonstrated
by `ButtonExample` in the complex demo.

The registry itself is optional. `new XmlEventBindingLoader(new DefaultNodeParser())` still installs
proxies, but its default `ERROR` policy fails dispatch when a declaration is reached without an
available registry. Using `DefaultNodeParser` directly installs no declarative proxies.

## Missing-handler policies

`XmlEventBindingOptions.defaults()` and the no-registry loader constructor use
`MissingHandlerPolicy.ERROR`. Both an unavailable registry and a missing exact-type handler fail
event dispatch with an actionable `IllegalStateException`.

Callers may explicitly select a permissive policy:

```java
import com.spinyowl.spinygui.core.binding.BindingDiagnostic;
import com.spinyowl.spinygui.core.binding.MissingHandlerPolicy;
import com.spinyowl.spinygui.core.binding.XmlEventBindingOptions;
import java.util.ArrayList;
import java.util.List;

List<BindingDiagnostic> diagnostics = new ArrayList<>();
XmlEventBindingOptions warningOptions =
    new XmlEventBindingOptions(MissingHandlerPolicy.WARNING, diagnostics::add);
XmlEventBindingOptions silentOptions =
    new XmlEventBindingOptions(MissingHandlerPolicy.SILENT);
```

- `WARNING` skips the unresolved handler and reports a structured `BindingDiagnostic`.
- `SILENT` skips the unresolved handler without a diagnostic.
- Both permissive modes retain conservative unknown input impact and allow other listeners to
  continue.

Warnings are deduplicated per proxy while the unresolved state is unchanged. A different handler
name, registry identity, registry revision, or failure reason may produce a new warning. A
successful resolution clears the remembered warning state.

## Event-time changes

A proxy reads the current declaration value and registry state on every event. On the owning UI
thread, between event batches, callers can change an existing declaration or mutate the registry:

```java
import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.node.Element;

Element save = frame.getElementById("save");
handlers.register("save-v2", ActionEvent.class, event -> saveCount.addAndGet(10));
save.setAttribute("on-action", "save-v2");

handlers.replace("save-v2", ActionEvent.class, event -> saveCount.addAndGet(100));
handlers.remove("save-v2");
```

Each operation affects the next event without re-traversing the tree or changing proxy identity.
`setAttribute(...)` only changes node state: it does not invoke a mutation callback, resolve a
handler, or attach a proxy. Consequently, adding a supported attribute to an element that had no
such declaration when loaded does not automatically install a listener.

An owner that needs to replace or temporarily remove the entire registry can supply its current
value explicitly:

```java
import com.spinyowl.spinygui.core.binding.HandlerRegistry;
import com.spinyowl.spinygui.core.binding.XmlEventBindingLoader;
import com.spinyowl.spinygui.core.binding.XmlEventBindingOptions;
import com.spinyowl.spinygui.core.parser.NodeParser;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

HandlerRegistry initialRegistry = new HandlerRegistry();
AtomicReference<Optional<HandlerRegistry>> currentRegistry =
    new AtomicReference<>(Optional.of(initialRegistry));
NodeParser parser =
    new XmlEventBindingLoader(
        new DefaultNodeParser(), currentRegistry::get, XmlEventBindingOptions.defaults());

currentRegistry.set(Optional.of(new HandlerRegistry()));
currentRegistry.set(Optional.empty());
```

`HandlerRegistry`, its supplier, node attributes, and dispatch are owner-thread APIs. They provide no
thread-safety guarantee for mid-dispatch mutation; update them between event batches.

## Manual listeners remain supported

Declarative binding is opt-in. Applications can keep the direct parser and listener path:

```java
import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;

Frame frame =
    new DefaultNodeParser()
        .fromHtml("<winframe><button id=\"save\">Save</button></winframe>")
        .frame();
frame.getElementById("save").addListener(ActionEvent.class, event -> {});
```

The complex demo's `MainMenuExample` intentionally remains on this manual path.

## Lifecycle and deferred scope

There is no `BindingSession`, controller lifecycle, rebinding pass, or disposal API. The parsed
elements own their stable proxy listeners for their normal lifetime, and those proxies consult the
current declaration and registry source at dispatch time.

Deferred work includes additional explicit event mappings, measurement before binding
high-frequency pointer or scroll events, optional controller or annotation adapters outside core,
and reactive values, collections, expressions, or template features. These are not implied by the
named-handler API.
