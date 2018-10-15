package org.liquidengine.legui.core.system.component;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Element extends Node {

    private Map<String, String> attributes = new ConcurrentHashMap<>();

    private List<Node> childComponents = new CopyOnWriteArrayList<>();


    @Override
    public void removeChild(Node node) {
        childComponents.remove(node);
    }

    @Override
    public void addChild(Node node) {
        if (node == null || node == this || isContainsByRef(node)) return;

        Node parent = node.getParent();
        if (parent != null) parent.removeChild(node);

        childComponents.add(node);

        node.setParent(this);
    }

    /**
     * Returns unmodifiable collection of node attributes.
     *
     * @return unmodifiable collection of node attributes.
     */
    @Override
    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Used to set attribute.
     *
     * @param key   attribute name.
     * @param value attribute value.
     */
    @Override
    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    /**
     * Used to get attribute.
     *
     * @param key attribute name.
     * @return attribute value.
     */
    @Override
    public String getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Used to remove attribute.
     *
     * @param key attribute name.
     */
    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    private boolean isContainsByRef(Node node) {
        return childComponents.stream().anyMatch(n -> n == node);
    }

    public String getClassAttribute() {
        return getAttribute("class");
    }

    public void setClassAttribute(String classAttribute) {
        setAttribute("class", classAttribute);
    }

    public String getId() {
        return getAttribute("id");
    }

    public void setId(String id) {
        setAttribute("id", id);
    }


}
