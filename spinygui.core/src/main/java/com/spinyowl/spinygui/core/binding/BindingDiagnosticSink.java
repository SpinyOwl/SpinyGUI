package com.spinyowl.spinygui.core.binding;

/** Receives structured diagnostics produced by warning-mode XML event binding. */
@FunctionalInterface
public interface BindingDiagnosticSink {

  /** Reports one unresolved binding state. */
  void report(BindingDiagnostic diagnostic);

  /**
   * Returns the documented default sink, which writes one structured warning through SLF4J.
   *
   * <p>Binding code controls deduplication; this sink reports every diagnostic it receives.
   */
  static BindingDiagnosticSink logging() {
    return DefaultBindingDiagnosticSink.INSTANCE;
  }
}
