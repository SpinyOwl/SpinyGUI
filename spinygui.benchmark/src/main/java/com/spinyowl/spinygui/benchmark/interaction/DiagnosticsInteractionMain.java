package com.spinyowl.spinygui.benchmark.interaction;

import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;

/** CLI entry point for the headless E6/M1.6 diagnostics interaction baseline. */
public final class DiagnosticsInteractionMain {
  private DiagnosticsInteractionMain() {}

  public static void main(String[] args) throws Exception {
    if (args.length != 3 || !"paired-report".equals(args[2])) {
      throw new IllegalArgumentException("Expected output path, run ID, and paired-report");
    }
    int warmup = Integer.getInteger("spinygui.e6.interaction.warmupOperations", 20);
    int samples = Integer.getInteger("spinygui.e6.interaction.measuredOperations", 100);
    DiagnosticsInteractionArtifact artifact =
        DiagnosticsInteractionRecorder.recordAll(args[1], args[2], warmup, samples);
    Path output = Path.of(args[0]);
    if (output.getParent() != null) Files.createDirectories(output.getParent());
    Files.writeString(output, new GsonBuilder().setPrettyPrinting().create().toJson(artifact));
    System.out.println("Wrote diagnostics interaction evidence: " + output.toAbsolutePath());
  }
}
