package com.spinyowl.spinygui.benchmark.identity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Pattern;

/** Separately versioned, unambiguous manifests for immutable benchmark inputs. */
public final class BenchmarkInputManifests {
  public static final int CONTENT_SCHEMA_VERSION = 1;
  public static final int SHAPE_SCHEMA_VERSION = 1;
  public static final int FONT_SCHEMA_VERSION = 1;

  private static final Pattern FIELD_NAME = Pattern.compile("[a-z][a-z0-9-]*");

  private BenchmarkInputManifests() {
  }

  /** Hashes exact UTF-8 content without trimming or Unicode normalization. */
  public static Manifest content(Map<String, String> exactContent) {
    return manifest("workload-content", CONTENT_SCHEMA_VERSION, exactContent);
  }

  /** Hashes only immutable construction/shape fields supplied by the producer. */
  public static Manifest shape(Map<String, String> shape) {
    return manifest("workload-shape", SHAPE_SCHEMA_VERSION, shape);
  }

  /** Hashes ordered font descriptors and the exact bytes loaded from every font resource. */
  public static Manifest fonts(List<FontInput> fonts) {
    return fonts(fonts, Map.of(), BenchmarkInputManifests::readResource);
  }

  /** Hashes ordered font resources plus explicit typography configuration fields. */
  public static Manifest fonts(List<FontInput> fonts, Map<String, String> configuration) {
    return fonts(fonts, configuration, BenchmarkInputManifests::readResource);
  }

  static Manifest fonts(List<FontInput> fonts, ResourceLoader resourceLoader) {
    return fonts(fonts, Map.of(), resourceLoader);
  }

  static Manifest fonts(
      List<FontInput> fonts,
      Map<String, String> configuration,
      ResourceLoader resourceLoader) {
    Objects.requireNonNull(fonts, "fonts");
    Objects.requireNonNull(configuration, "configuration");
    Objects.requireNonNull(resourceLoader, "resourceLoader");
    if (fonts.isEmpty()) throw new IllegalArgumentException("font manifest must not be empty");
    Map<String, String> fields = new LinkedHashMap<>();
    configuration.forEach((key, value) -> fields.put("configuration-" + key, value));
    for (int index = 0; index < fonts.size(); index++) {
      FontInput font = Objects.requireNonNull(fonts.get(index), "font");
      String prefix = "font-" + String.format(java.util.Locale.ROOT, "%04d", index) + '-';
      fields.put(prefix + "role", font.role());
      fields.put(prefix + "descriptor", font.descriptor());
      fields.put(prefix + "resource-path", font.resourcePath());
      fields.put(
          prefix + "resource-sha256",
          sha256(Objects.requireNonNull(resourceLoader.read(font.resourcePath()), "font bytes")));
    }
    return manifest("font-inputs", FONT_SCHEMA_VERSION, fields);
  }

  private static Manifest manifest(String kind, int version, Map<String, String> source) {
    Objects.requireNonNull(source, "source");
    if (source.isEmpty()) throw new IllegalArgumentException(kind + " manifest must not be empty");
    StringBuilder serialization =
        new StringBuilder("spinygui-benchmark-input:")
            .append(kind)
            .append(":v")
            .append(version)
            .append('\n');
    for (Map.Entry<String, String> entry : new TreeMap<>(source).entrySet()) {
      String key = Objects.requireNonNull(entry.getKey(), "manifest field");
      String value = Objects.requireNonNull(entry.getValue(), "manifest value");
      if (!FIELD_NAME.matcher(key).matches()) {
        throw new IllegalArgumentException("Invalid manifest field: " + key);
      }
      byte[] keyBytes = utf8(key);
      byte[] valueBytes = utf8(value);
      serialization
          .append("field=")
          .append(keyBytes.length)
          .append(':')
          .append(key)
          .append('=')
          .append(valueBytes.length)
          .append(':')
          .append(value)
          .append('\n');
    }
    String canonical = serialization.toString();
    return new Manifest(kind + "-v" + version, canonical, sha256(utf8(canonical)));
  }

  private static byte[] readResource(String resourcePath) {
    try {
      Path file = Path.of(resourcePath);
      if (Files.isRegularFile(file)) return Files.readAllBytes(file);
      try (InputStream stream =
          Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
        if (stream == null) {
          throw new IllegalArgumentException("Font resource is unavailable: " + resourcePath);
        }
        return stream.readAllBytes();
      }
    } catch (IOException exception) {
      throw new IllegalStateException("Unable to hash font resource: " + resourcePath, exception);
    }
  }

  private static byte[] utf8(String value) {
    try {
      ByteBuffer encoded =
          StandardCharsets.UTF_8
              .newEncoder()
              .onMalformedInput(CodingErrorAction.REPORT)
              .onUnmappableCharacter(CodingErrorAction.REPORT)
              .encode(java.nio.CharBuffer.wrap(value));
      byte[] bytes = new byte[encoded.remaining()];
      encoded.get(bytes);
      return bytes;
    } catch (CharacterCodingException exception) {
      throw new IllegalArgumentException("Manifest contains invalid Unicode", exception);
    }
  }

  private static String sha256(byte[] bytes) {
    try {
      byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
      StringBuilder hexadecimal = new StringBuilder("sha256:");
      for (byte value : digest) {
        hexadecimal.append(String.format(java.util.Locale.ROOT, "%02x", value & 0xff));
      }
      return hexadecimal.toString();
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("Required SHA-256 digest is unavailable", exception);
    }
  }

  public record Manifest(String schema, String canonicalSerialization, String sha256) {
    public Manifest {
      Objects.requireNonNull(schema, "schema");
      Objects.requireNonNull(canonicalSerialization, "canonicalSerialization");
      Objects.requireNonNull(sha256, "sha256");
    }
  }

  public record InputSet(Manifest content, Manifest shape, Manifest fonts) {
    public InputSet {
      Objects.requireNonNull(content, "content");
      Objects.requireNonNull(shape, "shape");
      Objects.requireNonNull(fonts, "fonts");
    }
  }

  public record FontInput(String role, String descriptor, String resourcePath) {
    public FontInput {
      Objects.requireNonNull(role, "role");
      Objects.requireNonNull(descriptor, "descriptor");
      Objects.requireNonNull(resourcePath, "resourcePath");
    }
  }

  @FunctionalInterface
  interface ResourceLoader {
    byte[] read(String resourcePath);
  }
}
