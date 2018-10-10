package org.liquidengine.legui.core.system.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Attributes {

    private Map<String, String> attributes = new ConcurrentHashMap<>();

    public String getAttribute(String attribute) {
        return attributes.get(attribute);
    }

    public boolean hasAttribute(String attribute) {
        return attributes.containsKey(attribute);
    }

    public void setAttribute(String attribute, Integer value) {
        attributes.put(attribute, value.toString());
    }

    public void setAttribute(String attribute, Boolean value) {
        attributes.put(attribute, value.toString());
    }

    public void setAttribute(String attribute, Long value) {
        attributes.put(attribute, value.toString());
    }

    public void setAttribute(String attribute, Short value) {
        attributes.put(attribute, value.toString());
    }

    public void setAttribute(String attribute, Byte value) {
        attributes.put(attribute, value.toString());
    }

    public void setAttribute(String attribute, Float value) {
        attributes.put(attribute, value.toString());
    }

    public void setAttribute(String attribute, Double value) {
        attributes.put(attribute, value.toString());
    }

    public void setAttribute(String attribute, String value) {
        attributes.put(attribute, value.toString());
    }

    public Integer getAttributeInteger(String attribute) {
        return Integer.valueOf(attributes.get(attribute));
    }

    public Boolean getAttributeBoolean(String attribute) {
        return Boolean.valueOf(attributes.get(attribute));
    }

    public Long getAttributeLong(String attribute) {
        return Long.valueOf(attributes.get(attribute));
    }

    public Short getAttributeShort(String attribute) {
        return Short.valueOf(attributes.get(attribute));
    }

    public Byte getAttributeByte(String attribute) {
        return Byte.valueOf(attributes.get(attribute));
    }

    public Float getAttributeFloat(String attribute) {
        return Float.valueOf(attributes.get(attribute));
    }

    public Double getAttributeDouble(String attribute) {
        return Double.valueOf(attributes.get(attribute));
    }

    public String getAttributeString(String attribute) {
        return String.valueOf(attributes.get(attribute));
    }

}
