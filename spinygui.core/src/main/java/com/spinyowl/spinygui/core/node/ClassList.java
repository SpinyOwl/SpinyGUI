package com.spinyowl.spinygui.core.node;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Live, ordered set of CSS class tokens backed by an element's {@code class} attribute.
 * Reads observe direct attribute changes; mutations use the element's style invalidation path.
 * Tokens must be non-null, non-empty, and contain no ASCII whitespace.
 */
public final class ClassList {

  /** Owning element; the attribute remains the sole source of class state. */
  private final Element element;

  ClassList(Element element) {
    this.element = element;
  }

  /** Returns whether the given token is present. */
  public boolean contains(String token) {
    validate(token);
    return tokens().contains(token);
  }

  /** Adds tokens in argument order, ignoring duplicates; validates all before changing state. */
  public void add(String... tokens) {
    validateAll(tokens);
    Set<String> current = tokens();
    if (current.addAll(Arrays.asList(tokens))) write(current);
  }

  /** Removes tokens; validates all arguments before changing state. */
  public void remove(String... tokens) {
    validateAll(tokens);
    Set<String> current = tokens();
    if (current.removeAll(Arrays.asList(tokens))) write(current);
  }

  /** Toggles a token and returns whether it is present afterward. */
  public boolean toggle(String token) {
    return toggle(token, !contains(token));
  }

  /** Ensures the token is present when force is true, absent otherwise; returns force. */
  public boolean toggle(String token, boolean force) {
    if (force) add(token);
    else remove(token);
    return force;
  }

  /** Removes all class tokens, leaving an empty attribute when classes were present. */
  public void clear() {
    if (!tokens().isEmpty()) element.setAttribute("class", "");
  }

  /** Returns the number of distinct class tokens. */
  public int size() {
    return tokens().size();
  }

  /** Returns an immutable snapshot of distinct tokens in attribute order. */
  public List<String> values() {
    return List.copyOf(tokens());
  }

  private Set<String> tokens() {
    Set<String> result = new LinkedHashSet<>();
    String value = element.getAttribute("class");
    if (value != null) {
      for (String token : value.split("[ \\t\\n\\r\\f]+")) {
        if (!token.isEmpty()) result.add(token);
      }
    }
    return result;
  }

  private void write(Set<String> tokens) {
    element.setAttribute("class", String.join(" ", tokens));
  }

  private static void validateAll(String[] tokens) {
    Objects.requireNonNull(tokens, "tokens");
    for (String token : tokens) validate(token);
  }

  private static void validate(String token) {
    Objects.requireNonNull(token, "token");
    if (token.isEmpty() || token.matches("(?s).*[ \\t\\n\\r\\f].*")) {
      throw new IllegalArgumentException("Class token must be non-empty and contain no ASCII whitespace");
    }
  }
}
