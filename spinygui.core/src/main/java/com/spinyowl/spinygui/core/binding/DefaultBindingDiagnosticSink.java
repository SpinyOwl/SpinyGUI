package com.spinyowl.spinygui.core.binding;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Package-private implementation of the documented default structured diagnostic sink. */
final class DefaultBindingDiagnosticSink {
  /** Shared stateless sink instance used by default binding options. */
  static final BindingDiagnosticSink INSTANCE = DefaultBindingDiagnosticSink::log;

  /** Logger that receives structured warning fields at the existing core logging boundary. */
  private static final Logger LOGGER = LoggerFactory.getLogger(BindingDiagnosticSink.class);

  private DefaultBindingDiagnosticSink() {}

  private static void log(BindingDiagnostic diagnostic) {
    BindingDiagnostic value = Objects.requireNonNull(diagnostic, "diagnostic");
    LOGGER.warn(
        "Unresolved XML event binding: reason={}, attribute={}, handler={}, eventClass={}, element={}, registryRevision={}",
        value.reason(),
        value.eventAttribute(),
        value.handlerName(),
        value.eventClass().getName(),
        value.elementReference(),
        value.registryRevision());
  }
}
