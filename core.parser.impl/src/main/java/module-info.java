open module com.spinyowl.spinygui.core.parser.impl {
  requires com.spinyowl.spinygui.core;
  requires com.spinyowl.spinygui.core.parser;

  requires transitive java.xml;
  requires static lombok;
  requires io.github.classgraph;

  requires org.antlr.antlr4.runtime;

  requires org.apache.commons.lang3;

  exports com.spinyowl.spinygui.core.parser.impl.css.parser.antlr;
  exports com.spinyowl.spinygui.core.parser.impl.css.parser.visitor;

  exports com.spinyowl.spinygui.core.parser.impl;
}