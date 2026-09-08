package com.spinyowl.spinygui.visual;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RunnerFailureTest {
  @TempDir Path output;

  @Test
  void invalidSelectionFailsAndWritesANewReportWithoutLaunchingBrowsers() throws Exception {
    Files.writeString(output.resolve("index.html"), "old passing report");
    assertThrows(IllegalStateException.class, () -> CompareViewsMain.main(new String[] {
        Path.of("..").toAbsolutePath().toString(), output.toString(), "no-such-case"}));
    String report = Files.readString(output.resolve("index.html"));
    assertTrue(report.contains("setup-error"));
    assertFalse(report.contains("old passing report"));
    try (var files = Files.list(output)) {
      Path run = files.filter(Files::isDirectory).findFirst().orElseThrow();
      assertTrue(Files.readString(run.resolve("summary.json")).contains("no-such-case"));
    }
  }

  @Test
  void previewServerExposesOnlySharedAssetsAndSelectedCase() throws Exception {
    Path root = Path.of("..").toAbsolutePath().normalize();
    ViewCase view = ViewCase.load(root, "layout").getFirst();
    try (var server = new PreviewServer(root, view); var client = HttpClient.newHttpClient()) {
      URI base = URI.create(server.url());
      var json = client.send(HttpRequest.newBuilder(base.resolve("/case.json")).build(),
          HttpResponse.BodyHandlers.ofString());
      assertEquals(200, json.statusCode());
      assertTrue(json.body().contains("layout"));
      var secret = client.send(HttpRequest.newBuilder(base.resolve("/.git/config")).build(),
          HttpResponse.BodyHandlers.ofString());
      assertEquals(404, secret.statusCode());
    }
  }
}
