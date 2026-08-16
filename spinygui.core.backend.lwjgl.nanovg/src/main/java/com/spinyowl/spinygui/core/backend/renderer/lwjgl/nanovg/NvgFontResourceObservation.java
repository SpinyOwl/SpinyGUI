package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import com.spinyowl.spinygui.core.system.font.SemanticFontOwner;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable inspection of font resources retained by one NanoVG renderer/context owner.
 *
 * <p>The renderer has a hard limit of one live context. Face, backing-buffer, font-info, and
 * retryable-failure entries have a natural bound of one entry per distinct current semantic font
 * identity used by that context, plus explicitly supplied unregistered compatibility descriptors.
 * Submitted duplicate buffer views are call-local and are never retained by the registry.
 *
 * @param semanticGeneration last core semantic generation reconciled by the registry
 * @param contextIdentity exact bound NanoVG context, or zero before context binding
 * @param contextCount zero or one bound context
 * @param faceEntries successfully created context-local faces
 * @param bufferEntries retained {@code freeData=false} backing buffers
 * @param fontInfoEntries retained backend STB font-info views
 * @param retryableFaceFailures failed face keys retained only to describe retry state
 * @param faceCreationFailures cumulative failed native face-creation attempts
 * @param retainedSubmittedViews submitted duplicate views retained by the registry; always zero
 * @param retainedSemanticIdentities immutable semantic identities represented by current entries
 */
public record NvgFontResourceObservation(
    long semanticGeneration,
    long contextIdentity,
    int contextCount,
    int faceEntries,
    int bufferEntries,
    int fontInfoEntries,
    int retryableFaceFailures,
    long faceCreationFailures,
    int retainedSubmittedViews,
    Set<SemanticFontOwner.Identity> retainedSemanticIdentities) {

  /** One renderer is permanently bound to at most one native context. */
  public static final int HARD_CONTEXT_LIMIT = 1;

  /** Validates that the reported counts obey the documented one-context resource bounds. */
  public NvgFontResourceObservation {
    retainedSemanticIdentities = Set.copyOf(Objects.requireNonNull(retainedSemanticIdentities));
    if (semanticGeneration < 0
        || contextCount < 0
        || contextCount > HARD_CONTEXT_LIMIT
        || faceEntries < 0
        || bufferEntries < 0
        || fontInfoEntries < 0
        || retryableFaceFailures < 0
        || faceCreationFailures < 0
        || retainedSubmittedViews != 0) {
      throw new IllegalArgumentException("Invalid NanoVG font resource observation");
    }
    if ((contextIdentity == 0) != (contextCount == 0)
        || faceEntries > bufferEntries
        || fontInfoEntries > bufferEntries
        || retryableFaceFailures > bufferEntries
        || (contextCount == 0
            && (faceEntries != 0
                || bufferEntries != 0
                || fontInfoEntries != 0
                || retryableFaceFailures != 0
                || faceCreationFailures != 0
                || !retainedSemanticIdentities.isEmpty()))) {
      throw new IllegalArgumentException("NanoVG font resource bounds are inconsistent");
    }
  }
}
