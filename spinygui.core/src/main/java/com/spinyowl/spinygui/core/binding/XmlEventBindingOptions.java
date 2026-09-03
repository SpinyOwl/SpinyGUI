package com.spinyowl.spinygui.core.binding;

import java.util.Objects;

/**
 * Immutable initialization options for XML event binding.
 *
 * @param missingHandlerPolicy policy for an unavailable registry or unresolved handler
 * @param diagnosticSink warning destination; ignored by silent policy and used only for warnings
 */
public record XmlEventBindingOptions(
    MissingHandlerPolicy missingHandlerPolicy, BindingDiagnosticSink diagnosticSink) {

  /** Creates safe default options using {@link MissingHandlerPolicy#ERROR} and the logging sink. */
  public XmlEventBindingOptions() {
    this(MissingHandlerPolicy.ERROR, BindingDiagnosticSink.logging());
  }

  /** Creates options for a policy using the documented default logging sink. */
  public XmlEventBindingOptions(MissingHandlerPolicy missingHandlerPolicy) {
    this(missingHandlerPolicy, BindingDiagnosticSink.logging());
  }

  /** Validates and creates immutable binding options. */
  public XmlEventBindingOptions {
    Objects.requireNonNull(missingHandlerPolicy, "missingHandlerPolicy");
    Objects.requireNonNull(diagnosticSink, "diagnosticSink");
  }

  /** Returns a new safe-default options value. */
  public static XmlEventBindingOptions defaults() {
    return new XmlEventBindingOptions();
  }
}
