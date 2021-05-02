package com.spinyowl.spinygui.core.style.stylesheet;

import java.util.List;
import java.util.Map;

public interface PropertyStore {

  /**
   * Returns property by property name.
   *
   * @param propertyName property name.
   * @return property.
   */
  @SuppressWarnings("rawtypes")
  Property getProperty(String propertyName);

  /**
   * Used to add property support.
   *
   * @param name property to support (will be converted to lower-case string).
   * @param property property supplier which will be used to create new {@link Property} instance.
   */
  void addProperty(String name, Property<?> property);

  /**
   * Used to remove property binding.
   *
   * @param property property to remove.
   */
  void removeProperty(String property);

  /**
   * Returns unmodifiable list of supported properties.
   *
   * @return unmodifiable list of supported properties.
   */
  List<String> getPropertiesNames();

  /**
   * Returns unmodifiable map of supported properties.
   *
   * @return unmodifiable map of supported properties.
   */
  @SuppressWarnings("rawtypes")
  Map<String, Property> getProperties();
}
