package com.spinyowl.spinygui.core.system.font;

import com.spinyowl.spinygui.core.font.Font;
import lombok.NonNull;

/** Font service responsible for parsing font descriptors and serving font metrics. */
public interface FontService extends AutoCloseable {

  /**
   * Explicitly installs or joins the process semantic font owner on the current UI thread and
   * atomically bootstraps the built-in descriptors.
   *
   * @return the installed semantic owner
   * @throws IllegalStateException if the service storage cannot support staged atomic publication,
   *     an incompatible owner is already installed, or the call is made off the owner thread
   */
  SemanticFontOwner installSemanticOwner();

  /**
   * Returns the resolver owned by this service's installed production semantic owner.
   *
   * @return the installed owner's single resolver
   * @throws IllegalStateException before owner installation or off the owner thread
   */
  FontChainResolver fontChainResolver();

  /**
   * Returns one immutable backend-neutral identity/generation observation from this service's
   * installed production semantic owner.
   *
   * <p>The observation is suitable for downstream generation validation and cache-style keys. It
   * exposes no mutable registry, raw font bytes, parser state, owner implementation, or renderer
   * context state.
   *
   * @return current immutable semantic font observation
   * @throws IllegalStateException before owner installation or off the owner thread
   * @throws UnsupportedOperationException for a legacy implementation without semantic ownership
   */
  default FontSemanticObservation semanticObservation() {
    throw new UnsupportedOperationException(
        "This font service does not expose production semantic font state");
  }

  /**
   * Returns an immutable owner-thread inspection of currently retained core resources. The
   * observation counts issued public aliases without claiming that the service can observe their
   * reachability or deterministically free their JVM-managed backing.
   *
   * @return current core resource observation
   * @throws UnsupportedOperationException when an implementation has no coordinated resource owner
   * @throws IllegalStateException when production use is before installation, off the owner thread,
   *     or after close
   */
  default FontResourceObservation resourceObservation() {
    throw new UnsupportedOperationException(
        "Font resource observation requires a coordinated lifecycle aggregate");
  }

  /**
   * Clears current semantic registrations and their owner-controlled core resources. Previously
   * returned read-only font-data aliases remain readable because their direct backing allocations
   * are JVM-managed.
   *
   * @return the semantic clear result; an empty/repeated clear is unchanged
   * @throws IllegalStateException before owner installation, off the owner thread, during active
   *     read/use, or after close
   * @throws UnsupportedOperationException for a legacy implementation without coordinated resource
   *     lifecycle
   */
  default SemanticFontOwner.Mutation clear() {
    throw new UnsupportedOperationException(
        "This font service does not coordinate semantic font resource lifecycle");
  }

  /**
   * Closes owner-controlled core resources and the semantic owner on its install thread. Production
   * implementations reject active read/use and all later service use. The default is a source-
   * compatible no-op for legacy service implementations that do not own native font resources.
   *
   * @throws IllegalStateException for production implementations before installation, off the owner
   *     thread, or during active read/use
   */
  @Override
  default void close() {}

  /**
   * Loads, parses, validates, and atomically publishes one font descriptor and byte identity through
   * the installed semantic owner. An exact duplicate is a generation no-op; a changed locator or
   * byte revision replaces the matching semantic face and advances generation exactly once.
   *
   * @param path path to font file
   * @return loaded font
   * @throws FontLoadingException in case of font loading or parsing failure
   * @throws IllegalStateException before owner installation or off the owner thread
   */
  Font loadFont(String path) throws FontLoadingException;

  /**
   * Verifies if font exists and available to use.
   *
   * @param font font to verify.
   * @return true if font exists, false otherwise.
   */
  boolean isFontAvailable(@NonNull Font font);

  /**
   * Calculates font vertical metrics.
   *
   * @param font font to use.
   * @param fontSize font size.
   * @param lineHeight requested CSS line-height multiplier.
   * @return font metrics in pixels.
   */
  FontMetrics getFontMetrics(@NonNull Font font, float fontSize, float lineHeight);
}
