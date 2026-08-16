package com.spinyowl.spinygui.core.system.font;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Immutable backend-neutral semantic font state suitable for generation checks and cache keys.
 *
 * <p>The observation contains no font bytes, mutable registry state, parser information, or
 * renderer/context identity.
 *
 * @param generation monotonic semantic generation
 * @param identities immutable ordered semantic identities published under the generation
 */
public record FontSemanticObservation(long generation, List<Identity> identities) {
  public FontSemanticObservation {
    if (generation < 0) {
      throw new IllegalArgumentException("generation must be non-negative");
    }
    identities = List.copyOf(identities);
  }

  /**
   * Immutable normalized identity for one semantic font face.
   *
   * @param family normalized family
   * @param style normalized style
   * @param weight normalized weight
   * @param stretch normalized stretch
   * @param normalizedLocator normalized resource locator
   * @param byteRevision SHA-256 byte-content revision
   */
  public record Identity(
      String family,
      String style,
      String weight,
      String stretch,
      String normalizedLocator,
      String byteRevision) {
    public Identity {
      family = normalized(family, "family");
      style = normalized(style, "style");
      weight = normalized(weight, "weight");
      stretch = normalized(stretch, "stretch");
      normalizedLocator =
          SemanticFontOwner.normalizeLocator(required(normalizedLocator, "normalizedLocator"));
      byteRevision = required(byteRevision, "byteRevision");
      if (!byteRevision.matches("sha256:[0-9a-f]{64}")) {
        throw new IllegalArgumentException("byteRevision must contain a SHA-256 revision");
      }
    }

    private static String required(String value, String name) {
      Objects.requireNonNull(value, name);
      if (value.isBlank()) {
        throw new IllegalArgumentException(name + " must not be blank");
      }
      return value;
    }

    private static String normalized(String value, String name) {
      return required(value, name).trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
  }
}
