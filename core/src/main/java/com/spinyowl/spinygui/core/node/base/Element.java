package com.spinyowl.spinygui.core.node.base;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.EventTarget;
import com.spinyowl.spinygui.core.event.listener.EventListener;
import com.spinyowl.spinygui.core.event.listener.EventListenerMap;
import com.spinyowl.spinygui.core.style.NodeStyle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class Element extends Node implements EventTarget {

    /**
     * Used to overload styles from stylesheet.
     */
    private final NodeStyle style = new NodeStyle();
    /**
     * Styles from stylesheet.
     * Updated by style manager every frame state changes.
     */
    private final NodeStyle calculatedStyle = new NodeStyle();
    /**
     * Node attributes.
     */
    private Map<String, String> attributes = new ConcurrentHashMap<>();

    private EventListenerMap eventListenerMap = new EventListenerMap();

    /**
     * Used to remove child node.
     *
     * @param node node to remove.
     */
    public abstract void removeChild(Node node);

    /**
     * Used to add a new child node, to an element, as the last child node.
     *
     * @param node node to add.
     */
    public abstract void addChild(Node node);

    /**
     * Used to get child nodes.
     *
     * @return list of child nodes.
     */
    public abstract List<Node> getChildNodes();

    /**
     * Used to get child nodes.
     *
     * @return list of child nodes.
     */
    public List<Element> getChildElements() {
        return getChildNodes().stream().filter(n -> n instanceof Element)
                .map(n -> (Element) n).collect(Collectors.toUnmodifiableList());
    }


    /**
     * Returns unmodifiable collection of node attributes.
     *
     * @return unmodifiable collection of node attributes.
     */
    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Used to set attributes.
     *
     * @param attributes attributes map.
     */
    public void setAttributes(Map<String, String> attributes) {
        if (attributes != null && this.attributes != attributes) {
            this.attributes.putAll(attributes);
        }
    }

    /**
     * Used to set attribute.
     *
     * @param key   attribute name.
     * @param value attribute value.
     */
    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    /**
     * Used to get attribute.
     *
     * @param key attribute name.
     * @return attribute value.
     */
    public String getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Used to remove attribute.
     *
     * @param key attribute name.
     */
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    /**
     * Returns node style that used to override calculated styles from stylesheet.
     *
     * @return node style that used to override calculated styles from stylesheet.
     */
    public NodeStyle getStyle() {
        return style;
    }

    /**
     * Returns node style that calculated from stylesheet.
     *
     * @return node style that calculated from stylesheet.
     */
    public NodeStyle getCalculatedStyle() {
        return calculatedStyle;
    }


    /**
     * Returns the number of elements in this node.  If this node contains
     * more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * @return the number of elements in this node
     */
    public abstract int count();

    /**
     * Returns true if there is no child nodes in this node.
     *
     * @return true if there is no child nodes in this node.
     */
    public abstract boolean isEmpty();

    @Override
    public <T extends Event> void addListener(Class<T> eventClass, EventListener<T> listener) {
        eventListenerMap.addEventListener(eventClass, listener);
    }

    @Override
    public <T extends Event> void removeListener(Class<T> eventClass, EventListener<T> listener) {
        eventListenerMap.removeEventListener(eventClass, listener);
    }

    @Override
    public <T extends Event> List<EventListener<T>> getListeners(Class<T> eventClass) {
        return eventListenerMap.getListeners(eventClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Element element = (Element) o;
        return Objects.equals(style, element.style) &&
                Objects.equals(calculatedStyle, element.calculatedStyle) &&
                Objects.equals(attributes, element.attributes) &&
                Objects.equals(eventListenerMap, element.eventListenerMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(style, calculatedStyle, attributes, eventListenerMap);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("position=" + getPosition())
                .add("size=" + getSize())
                .add("visible=" + isVisible())
                .add("hovered=" + isHovered())
                .add("focused=" + isFocused())
                .add("pressed=" + isPressed())
                .add("attributes=" + attributes)
                .toString();
    }
}
