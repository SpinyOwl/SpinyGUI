package com.spinyowl.spinygui.benchmark.frame;

import com.google.gson.GsonBuilder;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata.Pairing;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Command-line producer for the matched E6 frame-path baseline archive. */
public final class FrameBaselineMain {
  private FrameBaselineMain() {}

  public static void main(String[] args) throws IOException {
    if (args.length != 3 || Pairing.fromJson(args[2]) != Pairing.PAIRED_REPORT) {
      throw new IllegalArgumentException(
          "Expected output path, run ID, and paired-report eligibility");
    }
    long durationMillis =
        Long.getLong(
            "spinygui.e6.frame.durationMillis",
            FrameScenarioSpecifications.SCENARIOS.get(0).measurementDurationMillis());
    boolean profiles =
        Boolean.parseBoolean(System.getProperty("spinygui.e6.frame.profiles", "true"));
    FrameBaselineArtifact artifact = FrameBaselineRecorder.recordAll(args[1], durationMillis, profiles);
    Path output = Path.of(args[0]);
    Path parent = output.toAbsolutePath().getParent();
    if (parent != null) Files.createDirectories(parent);
    Files.writeString(output, new GsonBuilder().setPrettyPrinting().create().toJson(artifact));
  }
}
