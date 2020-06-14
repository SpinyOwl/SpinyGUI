open module com.spinyowl.spinygui.core.window {

  requires transitive com.spinyowl.spinygui.core;

  requires transitive org.slf4j;
  requires transitive ch.qos.logback.core;
  requires transitive ch.qos.logback.classic;

  exports com.spinyowl.spinygui.core.window;

}