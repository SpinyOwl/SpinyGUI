package com.spinyowl.spinygui.core.util;

import static java.util.Objects.requireNonNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import lombok.NonNull;

/**
 * Used to store key-value structure, where key is class of type C and value is parameterized
 * instance V, where generic arg is {@code <? extends T>}.
 *
 * <p>Used for such structures as class and processor for this class. Provides method to search
 * value recursively by key class hierarchy in case if there is no value for provided key.
 *
 * @param <C> class type.
 * @param <V> value type.
 */
public class ClassKeyMap<C, V> {

  private final Map<Class<? extends C>, V> map;
  private final Class<C> rootClass;

  public ClassKeyMap(
      @NonNull Supplier<Map<Class<? extends C>, V>> mapSupplier, @NonNull Class<C> rootClass) {
    this.map = requireNonNull(mapSupplier.get());
    this.rootClass = rootClass;
  }

  public ClassKeyMap(@NonNull Class<C> rootClass) {
    this(ConcurrentHashMap::new, rootClass);
  }

  public void put(@NonNull Class<? extends C> key, @NonNull V value) {
    map.put(key, value);
  }

  public V get(@NonNull Class<? extends C> key) {
    return map.get(key);
  }

  public V get(@NonNull Class<? extends C> key, V defaultValue) {
    return map.getOrDefault(key, defaultValue);
  }

  public V recursiveGet(@NonNull Class<? extends C> key) {
    return recursiveGet(key, null);
  }

  public V recursiveGet(@NonNull Class<? extends C> key, V defaultValue) {
    return cycledSearch(key, map, defaultValue);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private V cycledSearch(Class<? extends C> keyClass, Map map, V defaultValue) {
    V value = null;
    Class<? extends C> currentKeyClass = keyClass;

    while (value == null) {
      value = (V) map.get(currentKeyClass);
      // if current key class is root class
      if (currentKeyClass.equals(rootClass)) {
        break;
      }
      currentKeyClass = (Class<? extends C>) currentKeyClass.getSuperclass();
    }
    if (value == null) {
      value = defaultValue;
    }
    return value;
  }
}
