package org.liquidengine.legui.core.component.base;


import com.google.common.base.Objects;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Component extends ComponentBase {

    private Map<String, String> attributes = new ConcurrentHashMap<>();

    private List<ComponentBase> childBaseComponents = new CopyOnWriteArrayList<>();


    @Override
    public void removeChild(ComponentBase component) {
        childBaseComponents.remove(component);
    }

    @Override
    public void addChild(ComponentBase component) {
        if (component == null || component == this || isContainsByRef(component)) return;

        ComponentBase parent = component.getParent();
        if (parent != null) parent.removeChild(component);

        childBaseComponents.add(component);

        component.setParent(this);
    }

    @Override
    public List<ComponentBase> getChildBaseComponents() {
        return Collections.unmodifiableList(childBaseComponents);
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

    private boolean isContainsByRef(ComponentBase component) {
        return childBaseComponents.stream().anyMatch(n -> n == component);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Objects.equal(attributes, component.attributes) &&
                Objects.equal(childBaseComponents, component.childBaseComponents);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(attributes, childBaseComponents);
    }
}
