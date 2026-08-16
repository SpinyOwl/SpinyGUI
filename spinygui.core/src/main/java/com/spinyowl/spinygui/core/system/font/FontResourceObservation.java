package com.spinyowl.spinygui.core.system.font;

import java.util.Objects;

/**
 * Immutable inspection of resources currently retained by the core font-service aggregate.
 * Caller-retained aliases are not observable; only the cumulative number of read-only views issued
 * by the storage API is reported.
 *
 * @param ownerByteEntries current owner-controlled byte-buffer entries
 * @param ownerByteCapacity current aggregate capacity of owner-controlled byte buffers
 * @param ownerStbInfoEntries current owner-controlled native STB font-info entries
 * @param issuedExternalAliasViews cumulative read-only storage views issued
 * @param aliasLifetime lifetime policy for issued views
 */
public record FontResourceObservation(
    int ownerByteEntries,
    long ownerByteCapacity,
    int ownerStbInfoEntries,
    long issuedExternalAliasViews,
    AliasLifetime aliasLifetime) {

  public FontResourceObservation {
    if (ownerByteEntries < 0
        || ownerByteCapacity < 0
        || ownerStbInfoEntries < 0
        || issuedExternalAliasViews < 0) {
      throw new IllegalArgumentException("Font resource observation values must be non-negative");
    }
    Objects.requireNonNull(aliasLifetime, "aliasLifetime");
  }

  /** Compatibility lifetime for aliases returned by {@link FontStorage#getFontData(String)}. */
  public enum AliasLifetime {
    /**
     * Aliases share JVM-managed direct backing and may remain reachable after owner clear or close.
     * They are never claimed as deterministically freed by the service.
     */
    JVM_MANAGED_CALLER_RETAINABLE
  }
}
