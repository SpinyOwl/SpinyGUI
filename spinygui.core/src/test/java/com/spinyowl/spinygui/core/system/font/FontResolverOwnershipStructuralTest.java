package com.spinyowl.spinygui.core.system.font;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.InputStream;
import java.lang.classfile.ClassFile;
import java.lang.classfile.Opcode;
import java.lang.classfile.instruction.FieldInstruction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class FontResolverOwnershipStructuralTest {
  private static final String RESOLVER_OWNER =
      "com/spinyowl/spinygui/core/system/font/FontChainResolver";

  @Test
  void productionSourcesRetainOnlyTheStatelessCompatibilityDefaultDeclaration() throws Exception {
    Path root = repositoryRoot();
    List<String> defaultAccesses = new ArrayList<>();
    List<String> defaultConstructions = new ArrayList<>();

    try (var paths = Files.walk(root)) {
      for (Path path :
          paths
              .filter(Files::isRegularFile)
              .filter(candidate -> candidate.toString().endsWith(".java"))
              .filter(
                  candidate ->
                      candidate.toString().replace('\\', '/').contains("/src/main/java/"))
              .filter(candidate -> !candidate.startsWith(root.resolve(".worktrees")))
              .toList()) {
        String source = Files.readString(path);
        if (source.contains("FontChainResolver.DEFAULT")) {
          defaultAccesses.add(root.relativize(path).toString().replace('\\', '/'));
        }
        if (source.contains("new DefaultFontChainResolver")) {
          defaultConstructions.add(root.relativize(path).toString().replace('\\', '/'));
        }
      }
    }

    String declaration =
        "spinygui.core/src/main/java/com/spinyowl/spinygui/core/system/font/FontChainResolver.java";
    assertEquals(List.of(), defaultAccesses);
    assertEquals(List.of(declaration), defaultConstructions);
  }

  @Test
  void compiledCoreConsumersDoNotReadTheCompatibilityDefaultField() throws Exception {
    for (String className :
        List.of(
            "com.spinyowl.spinygui.core.layout.impl.TextLayoutImpl",
            "com.spinyowl.spinygui.core.layout.impl.BlockLayout",
            "com.spinyowl.spinygui.core.layout.impl.InlineFormattingContext",
            "com.spinyowl.spinygui.core.system.input.MultilineTextControlMetrics",
            "com.spinyowl.spinygui.core.system.input.TextInputMouseCaretBehavior",
            "com.spinyowl.spinygui.core.system.input.TextInputViewportBehavior",
            "com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl")) {
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

  private static Path repositoryRoot() {
    Path root = Path.of("").toAbsolutePath();
    while (root != null && !Files.exists(root.resolve("settings.gradle.kts"))) {
      root = root.getParent();
    }
    if (root == null) {
      throw new IllegalStateException("Could not locate the SpinyGUI repository root");
    }
    return root;
  }
}
