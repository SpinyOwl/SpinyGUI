package com.spinyowl.spinygui.benchmark.report;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;

class BenchmarkChartAssetsTest {
  @Test
  void pinsChartJsAndItsLicenseNotices() throws IOException, NoSuchAlgorithmException {
    String chartJs = resource("chart.umd.min.js");
    String licenses = resource("THIRD-PARTY-LICENSES.txt");

    assertTrue(chartJs.contains("Chart.js v4.5.1"));
    assertTrue(chartJs.contains("@kurkle/color v0.3.2"));
    assertFalse(chartJs.contains("sourceMappingURL"));
    assertTrue(licenses.contains("Copyright (c) 2014-2024 Chart.js Contributors"));
    assertTrue(licenses.contains("Copyright (c) 2018-2021 Jukka Kurkela"));
    assertTrue(licenses.split("Permission is hereby granted", -1).length - 1 == 2);
    assertTrue(sha256(chartJs).equals("84d0e233daba702b8f77d669d8c137cad36d441a10f200b6f2d3ab553bdfcf6b"));
  }

  private static String resource(String name) throws IOException {
    InputStream stream = BenchmarkChartAssetsTest.class.getResourceAsStream("/com/spinyowl/spinygui/benchmark/report/" + name);
    assertNotNull(stream);
    try (stream) {
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  private static String sha256(String value) throws NoSuchAlgorithmException {
    return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
  }
}
