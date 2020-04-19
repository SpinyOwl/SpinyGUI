open module com.spinyowl.spinygui.core.window {

  requires transitive com.spinyowl.spinygui.core;

  requires transitive jdom2;
  requires transitive java.xml;
  requires transitive org.joml;
  requires io.github.classgraph;
  requires org.antlr.antlr4.runtime;
  requires transitive commons.logging;

  exports com.spinyowl.spinygui.core.window;

}