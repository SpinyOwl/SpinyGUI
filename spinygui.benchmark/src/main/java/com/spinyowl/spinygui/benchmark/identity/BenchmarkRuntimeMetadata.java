package com.spinyowl.spinygui.benchmark.identity;

/** Captures explicit runtime and implementation provenance for benchmark result producers. */
public final class BenchmarkRuntimeMetadata {
  private static final String UNAVAILABLE = "unavailable";

  private BenchmarkRuntimeMetadata() {
  }

  public static ComparabilityMetadata.Environment cpuEnvironment() {
    return new ComparabilityMetadata.Environment(
        ComparabilityMetadata.Scope.CPU,
        property("java.vendor"),
        property("java.version"),
        property("os.name"),
        property("os.version"),
        property("os.arch"),
        cpuModel(),
        null,
        null,
        null,
        null);
  }

  public static ComparabilityMetadata.Environment renderingEnvironment(
      String glVendor, String glRenderer, String glDriverVersion, String glVersion) {
    return new ComparabilityMetadata.Environment(
        ComparabilityMetadata.Scope.RENDERING,
        property("java.vendor"),
        property("java.version"),
        property("os.name"),
        property("os.version"),
        property("os.arch"),
        cpuModel(),
        glVendor,
        glRenderer,
        glDriverVersion,
        glVersion);
  }

  public static ComparabilityMetadata.Implementation implementation() {
    String packagedVersion = BenchmarkRuntimeMetadata.class.getPackage().getImplementationVersion();
    return new ComparabilityMetadata.Implementation(
        benchmarkProperty(
            "implementationRevision",
            packagedVersion == null ? "working-tree" : packagedVersion,
            "SPINYGUI_IMPLEMENTATION_REVISION"),
        benchmarkProperty("buildRevision", "local-build", "BUILD_ID", "BUILD_NUMBER"),
        benchmarkProperty(
            "commitRevision", UNAVAILABLE, "GIT_COMMIT", "GITHUB_SHA", "CI_COMMIT_SHA"));
  }

  private static String cpuModel() {
    String override = System.getProperty("spinygui.benchmark.cpuModel");
    if (override != null && !override.isBlank()) return override;
    for (String variable : new String[] {"PROCESSOR_IDENTIFIER", "HOSTTYPE"}) {
      String value = System.getenv(variable);
      if (value != null && !value.isBlank()) return value;
    }
    return UNAVAILABLE;
  }

  private static String property(String name) {
    String value = System.getProperty(name);
    return value == null || value.isBlank() ? UNAVAILABLE : value;
  }

  private static String benchmarkProperty(
      String name, String fallback, String... environmentVariables) {
    String value = System.getProperty("spinygui.benchmark." + name);
    if (value != null && !value.isBlank()) return value;
    for (String environmentVariable : environmentVariables) {
      value = System.getenv(environmentVariable);
      if (value != null && !value.isBlank()) return value;
    }
    return fallback;
  }
}
