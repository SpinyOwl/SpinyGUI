package com.spinyowl.spinygui.visual;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/** Loopback-only server exposing explicit preview inputs, never the whole working tree. */
final class PreviewServer implements AutoCloseable {
  private final HttpServer server;

  PreviewServer(Path root, ViewCase view) throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    Map<String, Path> assets = Map.of(
        "/app.html", root.resolve("gui-test-html-app/app.html"),
        "/comparison.js", root.resolve("gui-test-html-app/comparison.js"),
        "/fonts/Roboto-Regular.ttf", root.resolve("spinygui.core/src/main/resources/fonts/Roboto-Regular.ttf"),
        "/fonts/Roboto-Light.ttf", root.resolve("spinygui.core/src/main/resources/fonts/Roboto-Light.ttf"),
        "/fonts/Roboto-Bold.ttf", root.resolve("spinygui.core/src/main/resources/fonts/Roboto-Bold.ttf"));
    byte[] json = new Gson().toJson(view).getBytes(StandardCharsets.UTF_8);
    server.createContext("/", exchange -> {
      try (exchange) {
        String path = exchange.getRequestURI().getPath();
        if (!exchange.getRequestMethod().equals("GET")
            || !(path.equals("/case.json") || assets.containsKey(path))) {
          exchange.sendResponseHeaders(404, -1);
          return;
        }
        byte[] bytes = path.equals("/case.json") ? json : Files.readAllBytes(assets.get(path));
        String type = path.endsWith(".json") ? "application/json" : path.endsWith(".js")
            ? "text/javascript" : path.endsWith(".ttf") ? "font/ttf" : "text/html";
        exchange.getResponseHeaders().set("Content-Type", type);
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
      }
    });
    server.start();
  }

  String url() { return "http://127.0.0.1:" + server.getAddress().getPort() + "/app.html?compare"; }

  @Override
  public void close() { server.stop(0); }
}
