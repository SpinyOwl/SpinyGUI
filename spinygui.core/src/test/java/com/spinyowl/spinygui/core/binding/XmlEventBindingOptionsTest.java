package com.spinyowl.spinygui.core.binding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.spinyowl.spinygui.core.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class XmlEventBindingOptionsTest {

  @Test
  void omittedConfigurationDefaultsToError() {
    assertEquals(MissingHandlerPolicy.ERROR, new XmlEventBindingOptions().missingHandlerPolicy());
    assertEquals(MissingHandlerPolicy.ERROR, XmlEventBindingOptions.defaults().missingHandlerPolicy());
  }

  @Test
  void supportsExplicitWarningAndSilentPolicies() {
    assertEquals(
        MissingHandlerPolicy.WARNING,
        new XmlEventBindingOptions(MissingHandlerPolicy.WARNING).missingHandlerPolicy());
    assertEquals(
        MissingHandlerPolicy.SILENT,
        new XmlEventBindingOptions(MissingHandlerPolicy.SILENT).missingHandlerPolicy());
  }

  @Test
  void deliversStructuredWarningsToTheInjectedSink() {
    List<BindingDiagnostic> diagnostics = new ArrayList<>();
    XmlEventBindingOptions options =
        new XmlEventBindingOptions(MissingHandlerPolicy.WARNING, diagnostics::add);
    BindingDiagnostic diagnostic =
        new BindingDiagnostic(
            BindingDiagnostic.Reason.HANDLER_MISSING,
            "on-action",
            "save",
            ActionEvent.class,
            "button#save",
            3);

    options.diagnosticSink().report(diagnostic);

    assertEquals(List.of(diagnostic), diagnostics);
  }

  @Test
  void retainsItsInitializationState() {
    BindingDiagnosticSink sink = diagnostic -> {};
    XmlEventBindingOptions options =
        new XmlEventBindingOptions(MissingHandlerPolicy.SILENT, sink);

    assertEquals(MissingHandlerPolicy.SILENT, options.missingHandlerPolicy());
    assertSame(sink, options.diagnosticSink());
  }
}
