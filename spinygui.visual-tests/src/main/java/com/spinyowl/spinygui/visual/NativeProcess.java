package com.spinyowl.spinygui.visual;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Starts a native host in an isolated JVM, with the first-thread requirement on macOS. */
final class NativeProcess {
  private NativeProcess() {}

  static Process start(Class<?> main, Path output, String... args) throws IOException {
    var command = new ArrayList<String>();
    boolean windows = System.getProperty("os.name").startsWith("Windows");
    command.add(Path.of(System.getProperty("java.home"), "bin", windows ? "java.exe" : "java").toString());
    if (System.getProperty("os.name").startsWith("Mac")) command.add("-XstartOnFirstThread");
    command.add("--enable-native-access=ALL-UNNAMED");
    command.add("-Djava.awt.headless=true");
    command.add("-cp");
    command.add(System.getProperty("java.class.path"));
    command.add(main.getName());
    command.addAll(List.of(args));
    return new ProcessBuilder(command).redirectErrorStream(true)
        .redirectOutput(output.resolve("native.log").toFile()).start();
  }
}
