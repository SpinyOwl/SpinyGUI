package com.spinyowl.spinygui.benchmark.frame;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Command-line producer for E6/M1.5 input-impact evidence. */
public final class InputImpactEvidenceMain {
  private InputImpactEvidenceMain() {}

  public static void main(String[] args) throws IOException {
    if (args.length != 3 || !"paired-report".equals(args[2])) {
      throw new IllegalArgumentException("Expected output path, run ID, and paired-report");
    }
    long duration = Long.getLong("spinygui.e6.input.durationMillis", 1000L);
    InputImpactEvidenceArtifact artifact =
        InputImpactEvidenceRecorder.recordAll(args[1], duration);
    Path output = Path.of(args[0]);
    if (output.toAbsolutePath().getParent() != null) {
      Files.createDirectories(output.toAbsolutePath().getParent());
    }
    Files.writeString(output, new GsonBuilder().setPrettyPrinting().create().toJson(artifact));
  }
}
