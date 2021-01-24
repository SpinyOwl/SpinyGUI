package com.spinyowl.spinygui.core.style.stylesheet.extractor;

import com.spinyowl.spinygui.core.style.stylesheet.annotation.Priority;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Class keeps all existing value extractors.
 */
@Slf4j
public final class ValueExtractors {

  private static final Map<Class, ValueExtractor> valueExtractorMap = new ConcurrentHashMap<>();

  static {
    var scanResult = new ClassGraph().enableAllInfo().scan();
    var extractorsToAdd = new HashMap<Class<?>, List<Pair<ValueExtractor<?>, Integer>>>();
    for (ClassInfo classInfo : scanResult.getClassesImplementing(ValueExtractor.class.getName())) {
      @SuppressWarnings("unchecked")
      Class<ValueExtractor<?>> clazz = (Class<ValueExtractor<?>>) classInfo.loadClass();
      Priority annotation = clazz.getAnnotation(Priority.class);
      int priority = 0;
      if (annotation != null) {
        priority = annotation.value();
      }
      ValueExtractor<?> extractor = null;
      try {
        extractor = clazz.getConstructor().newInstance();

      } catch (Exception e) {
        log.error("Can't initialize value extractor {}.", clazz.getName());
        if (log.isDebugEnabled()) {
          log.debug(e.getMessage(), e);
        }
      }
      if (extractor != null) {
        extractorsToAdd.computeIfAbsent(extractor.getType(), p -> new ArrayList<>()).
            add(Pair.of(extractor, priority));
      }
    }

    for (var entry : extractorsToAdd.entrySet()) {
      Class clazz = entry.getKey();
      var list = entry.getValue();
      if (list.size() > 1) {
        list.sort(Comparator.comparingInt(o -> -o.getRight()));
        log.warn("Found several tag mappings for tag {} : {}. Using {}", clazz,
            Arrays.toString(
                list.stream().map(c -> c.getLeft().getClass().getName()).toArray()),
            list.get(0).getLeft().getClass().getName());
      }
      add(clazz, (ValueExtractor) list.get(0).getLeft());
    }

  }

  private ValueExtractors() {
  }

  public static <T> void add(Class<T> targetValueClass,
      ValueExtractor<T> valueExtractor) {
    valueExtractorMap.put(targetValueClass, valueExtractor);
  }

  public static void remove(Class<?> targetValueClass) {
    valueExtractorMap.remove(targetValueClass);
  }

  @SuppressWarnings("unchecked")
  public static <T> ValueExtractor<T> of(Class<T> targetValueClass) {
    return valueExtractorMap.get(targetValueClass);
  }


}
