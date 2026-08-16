package com.spinyowl.spinygui.benchmark;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.InputStream;
import java.lang.classfile.ClassFile;
import java.lang.classfile.Opcode;
import java.lang.classfile.instruction.FieldInstruction;
import java.util.List;
import org.junit.jupiter.api.Test;

class BenchmarkFontResolverOwnershipTest {
  private static final String RESOLVER_OWNER =
      "com/spinyowl/spinygui/core/system/font/FontChainResolver";

  @Test
  void benchmarkCompositionBytecodeDoesNotReadTheCompatibilityDefaultField() throws Exception {
    for (String className :
        List.of(
            "com.spinyowl.spinygui.benchmark.TextStyleSpecification",
            "com.spinyowl.spinygui.benchmark.cpu.CpuWorkloadSpecifications",
            "com.spinyowl.spinygui.benchmark.diagnostic.CounterDiagnosticsMain",
            "com.spinyowl.spinygui.benchmark.frame.FrameBaselineRecorder",
            "com.spinyowl.spinygui.benchmark.rendering.RenderingWorkloadSpecifications")) {
      assertNoDefaultFieldReference(className);
    }
  }

  private static void assertNoDefaultFieldReference(String className) throws Exception {
    String resource = className.replace('.', '/') + ".class";
    try (InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
      byte[] bytecode = java.util.Objects.requireNonNull(stream, resource).readAllBytes();
      boolean readsDefault =
          ClassFile.of().parse(bytecode).methods().stream()
              .flatMap(method -> method.code().stream())
              .flatMap(code -> code.elementList().stream())
              .filter(FieldInstruction.class::isInstance)
              .map(FieldInstruction.class::cast)
              .anyMatch(
                  field ->
                      field.opcode() == Opcode.GETSTATIC
                          && field.owner().asInternalName().equals(RESOLVER_OWNER)
                          && field.name().equalsString("DEFAULT"));
      assertFalse(readsDefault, className + " must not read FontChainResolver.DEFAULT");
    }
  }
}
