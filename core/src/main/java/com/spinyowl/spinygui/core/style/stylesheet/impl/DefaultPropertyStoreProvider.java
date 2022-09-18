package com.spinyowl.spinygui.core.style.stylesheet.impl;

import static java.util.Comparator.comparingInt;
import static org.slf4j.LoggerFactory.getLogger;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStoreProvider;
import com.spinyowl.spinygui.core.style.stylesheet.annotation.Priority;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

@NoArgsConstructor()
public final class DefaultPropertyStoreProvider implements PropertyStoreProvider {
  private static final Logger LOG = getLogger(DefaultPropertyStoreProvider.class);

  @Override
  public PropertyStore createPropertyStore() {
    PropertyStore propertyStore = new DefaultPropertyStore();
    fillPropertyStore(propertyStore);
    return propertyStore;
  }

  public void fillPropertyStore(@NonNull PropertyStore propertyStore) {
    var scanResult = new ClassGraph().enableAllInfo().scan();
    var propertyProviders = new ArrayList<Pair<PropertyProvider, Integer>>();

    findPropertyProviders(scanResult, propertyProviders);
    fillInPropertyStore(propertyStore, propertyProviders);
  }

  private void findPropertyProviders(
      ScanResult scanResult, List<Pair<PropertyProvider, Integer>> propertyProviders) {
    for (ClassInfo classInfo :
        scanResult.getClassesImplementing(PropertyProvider.class.getName())) {
      @SuppressWarnings("unchecked")
      Class<PropertyProvider> clazz = (Class<PropertyProvider>) classInfo.loadClass();
      Priority annotation = clazz.getAnnotation(Priority.class);
      var priority = 0;
      if (annotation != null) {
        priority = annotation.value();
      }
      PropertyProvider propertyProvider = null;
      try {
        propertyProvider = clazz.getConstructor().newInstance();

      } catch (Exception e) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Can't initialize property provider {}.", clazz.getName());
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug(e.getMessage(), e);
        }
      }
      if (propertyProvider != null) {
        propertyProviders.add(Pair.of(propertyProvider, priority));
      }
    }
  }

  private void fillInPropertyStore(
      PropertyStore propertyStore, List<Pair<PropertyProvider, Integer>> propertyProviders) {
    propertyProviders.sort(comparingInt(Pair::getRight));
    for (var propertyProvider : propertyProviders) {
      for (Property property : propertyProvider.getLeft().getProperties()) {
        String name = property.name();
        if (propertyStore.getProperty(name) != null && LOG.isWarnEnabled()) {
          LOG.warn("Property with name {} already exists. Property will be overridden.", name);
        }
        propertyStore.addProperty(name, property);
      }
    }
  }
}
