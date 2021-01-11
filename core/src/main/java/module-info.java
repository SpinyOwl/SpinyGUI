open module com.spinyowl.spinygui.core {

  requires transitive java.xml;
  requires transitive org.joml;
  requires static lombok;

  requires org.lwjgl;
  requires org.lwjgl.natives;

  requires org.lwjgl.yoga;
  requires org.lwjgl.yoga.natives;

  requires io.github.classgraph;
  requires org.antlr.antlr4.runtime;

  requires transitive org.slf4j;
  requires transitive ch.qos.logback.core;
  requires transitive ch.qos.logback.classic;

  requires org.apache.commons.collections4;
  requires org.apache.commons.lang3;

  exports com.spinyowl.spinygui.core.animation;

  exports com.spinyowl.spinygui.core.node;
  exports com.spinyowl.spinygui.core.node.intersection;

  exports com.spinyowl.spinygui.core.converter;
  exports com.spinyowl.spinygui.core.converter.annotation;

  exports com.spinyowl.spinygui.core.converter.css;
  exports com.spinyowl.spinygui.core.converter.css.extractor;
  exports com.spinyowl.spinygui.core.converter.css.extractor.impl;
  exports com.spinyowl.spinygui.core.converter.css.model;
  exports com.spinyowl.spinygui.core.converter.css.parser;
  exports com.spinyowl.spinygui.core.converter.css.parser.antlr;
  exports com.spinyowl.spinygui.core.converter.css.parser.visitor;
  exports com.spinyowl.spinygui.core.converter.css.model.property;
  exports com.spinyowl.spinygui.core.converter.css.model.property.border;
  exports com.spinyowl.spinygui.core.converter.css.model.property.border.radius;
  exports com.spinyowl.spinygui.core.converter.css.model.property.dimension;
  exports com.spinyowl.spinygui.core.converter.css.model.property.margin;
  exports com.spinyowl.spinygui.core.converter.css.model.property.padding;
  exports com.spinyowl.spinygui.core.converter.css.model.property.position;
  exports com.spinyowl.spinygui.core.converter.css.model.selector;
  exports com.spinyowl.spinygui.core.converter.css.model.selector.combinator;
  exports com.spinyowl.spinygui.core.converter.css.model.selector.pseudo_class;
  exports com.spinyowl.spinygui.core.converter.css.model.selector.simple;

  exports com.spinyowl.spinygui.core.event;
  exports com.spinyowl.spinygui.core.event.listener;
  exports com.spinyowl.spinygui.core.event.processor;

  exports com.spinyowl.spinygui.core.font;
  exports com.spinyowl.spinygui.core.input;

  exports com.spinyowl.spinygui.core.layout;

  exports com.spinyowl.spinygui.core.style;

  exports com.spinyowl.spinygui.core.style.manager;

  exports com.spinyowl.spinygui.core.style.types;
  exports com.spinyowl.spinygui.core.style.types.background;
  exports com.spinyowl.spinygui.core.style.types.border;
  exports com.spinyowl.spinygui.core.style.types.flex;
  exports com.spinyowl.spinygui.core.style.types.length;

  exports com.spinyowl.spinygui.core.util;
  exports com.spinyowl.spinygui.core;

}