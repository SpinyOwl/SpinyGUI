package com.spinyowl.spinygui.core;

import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration class that used to define configuration for GUI system.
 *
 * @param <T> type of configuration value.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public final class Configuration<T> {

  public static final Configuration<Boolean> INITIALIZE_SERVICES =
      new Configuration<>("spinygui.service.initialize.provider", Initializer.BOOLEAN);

  public static final Configuration<String> MONITOR_SERVICE =
      new Configuration<>("spinygui.service.monitor", Initializer.STRING);
  public static final Configuration<String> WINDOW_SERVICE =
      new Configuration<>("spinygui.service.window", Initializer.STRING);
  public static final Configuration<String> CLIPBOARD_SERVICE =
      new Configuration<>("spinygui.service.clipboard", Initializer.STRING);
  public static final Configuration<String> RENDERER_PROVIDER_SERVICE =
      new Configuration<>("spinygui.service.renderer", Initializer.STRING);
  public static final Configuration<String> TIME_SERVICE =
      new Configuration<>("spinygui.service.time", Initializer.STRING);

  public static final Configuration<Float> DEFAULT_LINE_HEIGHT =
      new Configuration<>("spinygui.font.lineHeight.default", Initializer.FLOAT, 1.2F);

  private final String name;
  private final T defaultValue;
  private T value;

  private Configuration(String name, Initializer<T> initializer) {
    this(name, initializer, null);
  }

  private Configuration(String name, Initializer<T> initializer, T defaultValue) {
    this.name = name;
    this.defaultValue = defaultValue;
    this.value = initializer.initialize(name, defaultValue);
  }

  protected interface Initializer<T> {

    Initializer<Boolean> BOOLEAN = fromSystemProperties(Boolean::parseBoolean);
    Initializer<Integer> INT = fromSystemProperties(Integer::parseInt);
    Initializer<String> STRING = fromSystemProperties(Function.identity());
    Initializer<Float> FLOAT = fromSystemProperties(Float::parseFloat);

    static <T> Initializer<T> fromSystemProperties(Function<String, T> convert) {
      return (propert, defaultValue) -> {
        String value = System.getProperty(propert);
        return value == null ? defaultValue : convert.apply(value);
      };
    }

    T initialize(String configuration, T defaultValue);
  }
}
