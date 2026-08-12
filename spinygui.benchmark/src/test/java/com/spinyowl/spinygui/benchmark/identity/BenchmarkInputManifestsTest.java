package com.spinyowl.spinygui.benchmark.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.spinyowl.spinygui.benchmark.identity.BenchmarkInputManifests.FontInput;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class BenchmarkInputManifestsTest {
  @Test
  void contentManifestPreservesExactUnicodeBytesWithoutNfcNormalization() {
    BenchmarkInputManifests.Manifest composed =
        BenchmarkInputManifests.content(Map.of("text", "Caf\u00e9"));
    BenchmarkInputManifests.Manifest decomposed =
        BenchmarkInputManifests.content(Map.of("text", "Cafe\u0301"));

    assertEquals(
        "spinygui-benchmark-input:workload-content:v1\nfield=4:text=5:Caf\u00e9\n",
        composed.canonicalSerialization());
    assertNotEquals(composed.sha256(), decomposed.sha256());
  }

  @Test
  void lengthPrefixesKeepDelimiterRichFieldsUnambiguous() {
    BenchmarkInputManifests.Manifest first =
        BenchmarkInputManifests.shape(Map.of("shape-kind", "a\nfield=1:x=1:y"));
    BenchmarkInputManifests.Manifest second =
        BenchmarkInputManifests.shape(Map.of("shape-kind", "a", "x", "y"));

    assertNotEquals(first.canonicalSerialization(), second.canonicalSerialization());
    assertNotEquals(first.sha256(), second.sha256());
  }

  @Test
  void orderedFontManifestChangesWhenResourceBytesChange() {
    List<FontInput> fonts =
        List.of(new FontInput("measurement", "Family|normal|regular|font.ttf", "font.ttf"));

    BenchmarkInputManifests.Manifest first =
        BenchmarkInputManifests.fonts(fonts, ignored -> new byte[] {1, 2, 3});
    BenchmarkInputManifests.Manifest changedResource =
        BenchmarkInputManifests.fonts(fonts, ignored -> new byte[] {1, 2, 4});
    BenchmarkInputManifests.Manifest changedOrder =
        BenchmarkInputManifests.fonts(
            List.of(
                new FontInput("first", "A|a.ttf", "a.ttf"),
                new FontInput("second", "B|b.ttf", "b.ttf")),
            path -> path.equals("a.ttf") ? new byte[] {1} : new byte[] {2});
    BenchmarkInputManifests.Manifest reversedOrder =
        BenchmarkInputManifests.fonts(
            List.of(
                new FontInput("second", "B|b.ttf", "b.ttf"),
                new FontInput("first", "A|a.ttf", "a.ttf")),
            path -> path.equals("a.ttf") ? new byte[] {1} : new byte[] {2});

    assertNotEquals(first.sha256(), changedResource.sha256());
    assertNotEquals(changedOrder.sha256(), reversedOrder.sha256());
  }
}
