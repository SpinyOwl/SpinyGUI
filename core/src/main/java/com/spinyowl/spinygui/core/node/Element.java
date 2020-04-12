package com.spinyowl.spinygui.core.node;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.EventTarget;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.style.NodeStyle;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class Element extends Node implements EventTarget {

    /**
     * Used to overload styles from stylesheet.
     */
    @Setter(AccessLevel.NONE)
    private NodeStyle style = new NodeStyle();

    /**
     * Styles from stylesheet. Updated by style manager every frame state changes.
     */
    @Setter(AccessLevel.NONE)
    private NodeStyle calculatedStyle = new NodeStyle();

    /**
     * Node attributes.
     */
    @Setter(AccessLevel.NONE)
    private Map<String, String> attributes = new ConcurrentHashMap<>();

    /**
     * Map of listeners attached that should be attached for node and processed if any event
     * performed.
     */
    @Setter(AccessLevel.NONE)
    private Map<Class<? extends Event>, List<? extends EventListener>> listenerMap = new ConcurrentHashMap<>();

    /**
     * Used to add a new child node, to an element, as the last child node.
     *
     * @param node node to add.
     */
    public abstract void addChild(Node node);

    /**
     * Used to remove child node.
     *
     * @param node node to remove.
     */
    public abstract void removeChild(Node node);

    /**
     * The {@link #childNodes()} method returns a collection of a node's child nodes, as {@code
     * List<Node>} object.
     * <p>
     * The nodes in the collection are sorted as they was added to the element.
     * <p>
     * Tip: To return a collection of a node's element nodes (excluding text and comment nodes), use
     * the {@link #children()} method.
     *
     * @return list of child nodes.
     */
    public abstract List<Node> childNodes();

    /**
     * Returns true if an element has any child nodes, otherwise false.
     *
     * @return true if an element has any child nodes, otherwise false.
     */
    public abstract boolean hasChildNodes();

    /**
     * The {@link #children()} method returns a collection of an element's child elements, as an
     * {@code List<Element>} object.
     * <p>
     * The elements in the collection are sorted as they was added tp the element.
     *
     * @return list of child elements.
     */
    public List<Element> children() {
        return childNodes().stream().filter(n -> n instanceof Element)
            .map(n -> (Element) n).collect(Collectors.toUnmodifiableList());
    }

    /**
     * Shorthand to set attribute.
     *
     * @param key   attribute name.
     * @param value attribute value.
     */
    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    /**
     * Shorthand to get attribute.
     *
     * @param key attribute name.
     * @return attribute value.
     */
    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public boolean hasAttribute(String attribute) {
        return attributes.containsKey(attribute);
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    /**
     * Used to add event listener for specified event class.
     *
     * @param eventClass event class.
     * @param listener   event listener to add.
     * @param <T>        type of event for which adding event listener.
     */
    public <T extends Event> void addListener(Class<T> eventClass, EventListener<T> listener) {
        getOrCreate(eventClass).add(listener);
    }

    public <T extends Event> void removeListener(Class<T> eventClass,
        EventListener<T> listener) {
        getOrCreate(eventClass).remove(listener);
    }

    /**
     * Returns listener list for specified event class.
     *
     * @param eventClass event class.
     * @param <T>        type of event.
     * @return list of event listeners for specified event class.
     */
    public <T extends Event> List<EventListener<T>> getListeners(Class<T> eventClass) {
        return getOrCreate(eventClass);
    }

    private <T extends Event> List<EventListener<T>> getOrCreate(Class<T> eventClass) {
        return (List<EventListener<T>>) listenerMap
            .computeIfAbsent(eventClass, aClass -> new CopyOnWriteArrayList<>());
    }

    /**
     * Returns true if there is at least one event listener for specified event class.
     *
     * @param eventClass event class.
     * @return true if there is at least one event listener for specified event class.
     */
    public boolean hasListenersFor(Class<? extends Event> eventClass) {
        return listenerMap.containsKey(eventClass) && !listenerMap.get(eventClass).isEmpty();
    }
}
