package com.spinyowl.spinygui.core.diagnostic;

/** One stable, semantically indivisible diagnostic count. */
public interface DiagnosticCounter {
  String id();

  DiagnosticUnit unit();

  String description();
}
