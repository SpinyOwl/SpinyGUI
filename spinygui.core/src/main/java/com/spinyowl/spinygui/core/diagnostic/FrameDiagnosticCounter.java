package com.spinyowl.spinygui.core.diagnostic;

/** Backend-neutral structural counters for the non-text frame pipeline. */
public enum FrameDiagnosticCounter implements DiagnosticCounter {
  CHILD_NODE_VIEW_READS(
      "core.frame.traversal.child-node-view-reads",
      DiagnosticUnit.CALLS,
      "Reads of the public child-node view during frame processing."),
  ELEMENT_VIEW_READS(
      "core.frame.traversal.element-view-reads",
      DiagnosticUnit.CALLS,
      "Reads of the derived element-child view during frame processing."),
  GEOMETRY_POSITION_READS(
      "core.frame.geometry.position-reads",
      DiagnosticUnit.CALLS,
      "Layout or absolute position reads used by frame processing."),
  GEOMETRY_SIZE_READS(
      "core.frame.geometry.size-reads",
      DiagnosticUnit.CALLS,
      "Border-box size reads used by frame processing."),
  TRANSFORM_COMPOSITIONS(
      "core.frame.transform.compositions",
      DiagnosticUnit.CALLS,
      "Presentation transform compositions resolved after layout."),
  SELECTOR_TESTS(
      "core.frame.selector.tests",
      DiagnosticUnit.CALLS,
      "Selector and scrollbar-selector tests performed during style resolution."),
  PROPERTY_APPLICATIONS(
      "core.frame.property.applications",
      DiagnosticUnit.CALLS,
      "Style declarations and default properties applied during resolution."),
  STYLE_RECALCULATIONS(
      "core.frame.style.recalculations",
      DiagnosticUnit.CALLS,
      "Full-frame style recalculation entry points."),
  LAYOUT_PASSES(
      "core.frame.layout.passes",
      DiagnosticUnit.CALLS,
      "Layout convergence passes, including repeated scrollbar-gutter passes."),
  LAYOUT_NODE_VISITS(
      "core.frame.layout.node-visits",
      DiagnosticUnit.ITEMS,
      "Nodes visited by the layout pipeline."),
  SCROLL_CONVERGENCE_CHECKS(
      "core.frame.layout.scroll-convergence-checks",
      DiagnosticUnit.CALLS,
      "Scroll and client-size convergence checks."),
  LOOKUP_NODE_VISITS(
      "core.frame.lookup.node-visits",
      DiagnosticUnit.ITEMS,
      "Elements visited by frame lookup operations."),
  MUTATION_ATTACHMENTS(
      "core.frame.mutation.attachments",
      DiagnosticUnit.ITEMS,
      "Successful child attachments."),
  MUTATION_DETACHMENTS(
      "core.frame.mutation.detachments",
      DiagnosticUnit.ITEMS,
      "Successful child detachments.");

  public static final String VOCABULARY_VERSION = "core-frame-diagnostics-1";

  private final String id;
  private final DiagnosticUnit unit;
  private final String description;

  FrameDiagnosticCounter(String id, DiagnosticUnit unit, String description) {
    this.id = id;
    this.unit = unit;
    this.description = description;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public DiagnosticUnit unit() {
    return unit;
  }

  @Override
  public String description() {
    return description;
  }
}
