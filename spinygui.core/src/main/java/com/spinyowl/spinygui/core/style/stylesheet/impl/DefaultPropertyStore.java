package com.spinyowl.spinygui.core.style.stylesheet.impl;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultPropertyStore implements PropertyStore {
  private final Map<String, Property> propertyMap = new HashMap<>();

  @Override
  public Property getProperty(@NonNull String propertyName) {
    return propertyMap.get(propertyName.toLowerCase());
  }

  @Override
  public void addProperty(@NonNull String name, @NonNull Property property) {
    String propertyName = name.toLowerCase();
    if (!Objects.equals(propertyName, property.name())) {
      throw new IllegalArgumentException("Name should be the same as the property name.");
    }

    if (log.isWarnEnabled() && propertyMap.containsKey(propertyName)) {
      log.warn(
          "There is already exist property handler for {} : {}. Would be replaced by {}.",
          propertyName,
          propertyMap.get(propertyName).getClass().getName(),
          property.getClass().getName());
    }

    propertyMap.put(propertyName, property);
  }

  @Override
  public void removeProperty(@NonNull String property) {
    propertyMap.remove(property.toLowerCase());
  }

  @Override
  public List<String> getPropertyNames() {
    return List.copyOf(propertyMap.keySet());
  }

  @Override
  public Map<String, Property> getPropertyMap() {
    return Map.copyOf(propertyMap);
  }

  @Override
  public List<Property> getProperties() {
    return List.copyOf(propertyMap.values());
  }
}
