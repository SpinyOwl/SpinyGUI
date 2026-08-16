package com.spinyowl.spinygui.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BenchmarkFreshProcessInitializationTest {
  @ParameterizedTest
  @ValueSource(strings = {"rendering-startup", "report-static", "cpu-enrichment"})
  void benchmarkOuterProcessPathsDoNotRequireAnInstalledFontOwner(String mode) throws Exception {
    Process process =
        new ProcessBuilder(
                javaExecutable(),
                "-cp",
                System.getProperty("java.class.path"),
                BenchmarkFreshProcessProbe.class.getName(),
                mode)
            .redirectErrorStream(true)
            .start();

    boolean completed = process.waitFor(30, TimeUnit.SECONDS);
    if (!completed) {
      process.destroyForcibly();
    }
    assertTrue(completed, "fresh JVM timed out for " + mode);
    String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    assertEquals(0, process.exitValue(), output);
    assertTrue(output.contains("BENCHMARK_FRESH_PROCESS_OK " + mode), output);
  }

  private static String javaExecutable() throws IOException {
    Path java =
        Path.of(
            System.getProperty("java.home"),
            "bin",
            System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java");
    if (!java.toFile().isFile()) {
      throw new IOException("Java executable does not exist: " + java);
    }
    return java.toString();
  }
}
