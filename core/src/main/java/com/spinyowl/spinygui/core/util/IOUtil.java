package com.spinyowl.spinygui.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

/** IO utility. Used to read resource as {@link ByteBuffer} or as {@link String}. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IOUtil {

  /**
   * Aggregating function that creates {@link ByteBuffer} from:
   *
   * <ul>
   *   <li>file
   *   <li>resource
   *   <li>url (http/https) protocol
   * </ul>
   *
   * @param path path to file or resource.
   * @return file or resource data or null if resource was not found.
   */
  @SneakyThrows
  public static ByteBuffer resourceAsByteBuffer(String path) {
    String trimmedPath = path.trim();

    if (trimmedPath.startsWith("http://") || trimmedPath.startsWith("https://")) {
      return asByteBuffer(new URL(trimmedPath));
    } else {
      InputStream stream;
      File file = new File(trimmedPath);
      if (file.exists() && file.isFile()) {
        stream = new FileInputStream(file);
      } else {
        stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(trimmedPath);
      }
      if (stream == null) {
        return null;
      }
      return asByteBuffer(stream);
    }
  }

  /**
   * Aggregating function that creates {@link String} from:
   *
   * <ul>
   *   <li>file
   *   <li>resource
   *   <li>url (http/https) protocol
   * </ul>
   *
   * @param path path to file or resource.
   * @return file or resource data or null if resource was not found.
   */
  @SneakyThrows
  public static String resourceAsString(String path) {
    ByteBuffer resource = resourceAsByteBuffer(path);
    return resource == null ? null : asString(resource);
  }

  /**
   * Creates {@link ByteBuffer} from stream.
   *
   * @param url url to read.
   * @return stream data.
   */
  @SneakyThrows
  public static ByteBuffer asByteBuffer(@NonNull URL url) {
    return asByteBuffer(IOUtils.toByteArray(url));
  }

  /**
   * Creates {@link ByteBuffer} from stream.
   *
   * @param stream stream to read.
   * @return stream data.
   */
  @SneakyThrows
  public static ByteBuffer asByteBuffer(@NonNull InputStream stream) {
    return asByteBuffer(IOUtils.toByteArray(stream));
  }

  /**
   * Creates {@link ByteBuffer} from file.
   *
   * @param file file to read.
   * @return file.
   */
  @SneakyThrows
  public static ByteBuffer asByteBuffer(@NonNull File file) {
    if (!file.exists() || !file.isFile()) {
      throw new IllegalArgumentException("File is not exist or is not a file.");
    }
    return asByteBuffer(IOUtils.toByteArray(file.toURI()));
  }

  /**
   * Used to convert byte array to read-ready {@link ByteBuffer}.
   *
   * @param bytes source
   * @return ByteBuffer with specified data.
   */
  @SneakyThrows
  public static ByteBuffer asByteBuffer(byte[] bytes) {
    return ByteBuffer.allocateDirect(bytes.length).order(ByteOrder.nativeOrder()).put(bytes).flip();
  }

  /**
   * Creates {@link String} from url.
   *
   * @param url url to read.
   * @return String from url data.
   */
  @SneakyThrows
  public static String asString(@NonNull URL url) {
    return asString(asByteBuffer(url));
  }

  /**
   * Creates {@link String} from stream.
   *
   * @param stream stream to read.
   * @return String from stream data.
   */
  @SneakyThrows
  public static String asString(@NonNull InputStream stream) {
    return asString(asByteBuffer(stream));
  }

  /**
   * Creates {@link String} from file.
   *
   * @param file file to read.
   * @return String from file.
   */
  @SneakyThrows
  public static String asString(@NonNull File file) {
    return asString(asByteBuffer(file));
  }

  /**
   * Used to transfer buffer data to {@link String}.
   *
   * @param byteBuffer data to transfer
   * @return string.
   */
  @SneakyThrows
  public static String asString(@NonNull ByteBuffer byteBuffer) {
    if (byteBuffer.limit() == 0) {
      return "";
    }
    byte[] buffer = new byte[byteBuffer.limit()];
    byteBuffer.get(buffer);
    return new String(buffer);
  }
}
