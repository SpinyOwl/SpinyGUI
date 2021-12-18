package com.spinyowl.spinygui.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IOUtilTest {

  @Test
  void inputStream_asString() {
    String input = "Test text.";
    InputStream inputStream = IOUtils.toInputStream(input, StandardCharsets.UTF_8);
    String result = IOUtil.asString(inputStream);
    Assertions.assertEquals(input, result);
  }

  @Test
  void resourceAsByteBuffer_successfullyRead() throws IOException {
    // Arrange
    String input = "Test text.";
    Path tempFile = Files.createTempFile("temp_test_file", "text");
    Files.writeString(tempFile, input);

    // Act
    ByteBuffer result = IOUtil.resourceAsByteBuffer(tempFile.toAbsolutePath().toString());

    // Assert
    ByteBuffer expected = ByteBuffer.wrap(input.getBytes());
    Assertions.assertEquals(expected, result);
  }

  @Test
  void resourceAsString_successfullyRead() throws IOException {
    // Arrange
    String input = "Test text.";
    Path tempFile = Files.createTempFile("temp_test_file", "text");
    Files.writeString(tempFile, input);

    // Act
    String result = IOUtil.resourceAsString(tempFile.toAbsolutePath().toString());

    // Assert
    Assertions.assertEquals(input, result);
  }

  @Test
  void file_asString() throws IOException {
    String input = "Test text.";
    Path tempFile = Files.createTempFile("temp_test_file", "text");
    Files.writeString(tempFile, input);
    String result = IOUtil.asString(tempFile.toFile());
    Assertions.assertEquals(input, result);
  }

  @Test
  void url_asString() throws IOException {
    String input = "Test text.";
    Path tempFile = Files.createTempFile("temp_test_file_url", "text");
    Files.writeString(tempFile, input);
    String result = IOUtil.asString(tempFile.toUri().toURL());
    Assertions.assertEquals(input, result);
  }

  @Test
  void byteBuffer_asString() {
    String input = "Test text.";
    ByteBuffer byteBuffer = ByteBuffer.wrap(input.getBytes());
    String result = IOUtil.asString(byteBuffer);
    Assertions.assertEquals(input, result);
  }
}
