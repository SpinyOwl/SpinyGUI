package org.liquidengine.legui.core.system.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node<P extends Node, T extends Node> implements Serializable {
    private Map<String, Attribute<?>> attributes;

    private float x;
    private float y;
    private float width;
    private float height;

    private P parent;
    private List<T> childComponents;

    {
        attributes = new HashMap<>();
        childComponents = new ArrayList<>();
    }

    protected P getParent() {
        return parent;
    }

    public void setParent(P parent) {
        this.parent = parent;
    }

    protected List<T> getChildComponents() {
        return childComponents;
    }

    public void setChildComponents(List<T> childComponents) {
        this.childComponents = childComponents;
    }

    public Attribute<?> getAttribute(String attribute) {
        return attributes.get(attribute);
    }

    public Class<?> getAttributeType(String attribute) {
        Attribute<?> value = attributes.get(attribute);
        if (value == null) throw new IllegalStateException("Can't find specified attribute: '" + attribute + "'.");
        return value.getType();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean hasAttribute(String attribute) {
        return attributes.containsKey(attribute);
    }

    public void setAttribute(String attribute, Attribute<?> value) {
        attributes.put(attribute, value);
    }

    public void setAttribute(String attribute, Integer value) {
        attributes.put(attribute, new Attribute<>(value, Integer.class));
    }

    public void setAttribute(String attribute, Boolean value) {
        attributes.put(attribute, new Attribute<>(value, Boolean.class));
    }

    public void setAttribute(String attribute, Long value) {
        attributes.put(attribute, new Attribute<>(value, Long.class));
    }

    public void setAttribute(String attribute, Short value) {
        attributes.put(attribute, new Attribute<>(value, Short.class));
    }

    public void setAttribute(String attribute, Byte value) {
        attributes.put(attribute, new Attribute<>(value, Byte.class));
    }

    public void setAttribute(String attribute, Float value) {
        attributes.put(attribute, new Attribute<>(value, Float.class));
    }

    public void setAttribute(String attribute, Double value) {
        attributes.put(attribute, new Attribute<>(value, Double.class));
    }

    public void setAttribute(String attribute, String value) {
        attributes.put(attribute, new Attribute<>(value, String.class));
    }

    public Integer getAttributeInteger(String attribute) {
        return (Integer) attributes.get(attribute).getValue();
    }

    public Boolean getAttributeBoolean(String attribute) {
        return (Boolean) attributes.get(attribute).getValue();
    }

    public Long getAttributeLong(String attribute) {
        return (Long) attributes.get(attribute).getValue();
    }

    public Short getAttributeShort(String attribute) {
        return (Short) attributes.get(attribute).getValue();
    }

    public Byte getAttributeByte(String attribute) {
        return (Byte) attributes.get(attribute).getValue();
    }

    public Float getAttributeFloat(String attribute) {
        return (Float) attributes.get(attribute).getValue();
    }

    public Double getAttributeDouble(String attribute) {
        return (Double) attributes.get(attribute).getValue();
    }

    public String getAttributeString(String attribute) {
        return (String) attributes.get(attribute).getValue();
    }
}
