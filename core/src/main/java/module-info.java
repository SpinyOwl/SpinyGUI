open module com.spinyowl.spinygui.core {

    requires jdom2;
    requires transitive java.xml;
    requires guava.base.r03;
    requires guava.collections.r03;
    requires io.github.classgraph;
    requires org.slf4j;

    exports com.spinyowl.spinygui.core.animation;
    exports com.spinyowl.spinygui.core.api;
    exports com.spinyowl.spinygui.core.component.base;
    exports com.spinyowl.spinygui.core.component;
    exports com.spinyowl.spinygui.core.converter;
    exports com.spinyowl.spinygui.core.event.listener.impl;
    exports com.spinyowl.spinygui.core.event.listener;
    exports com.spinyowl.spinygui.core.event.processor;
    exports com.spinyowl.spinygui.core.event;
    exports com.spinyowl.spinygui.core.service;
    exports com.spinyowl.spinygui.core.style;
    exports com.spinyowl.spinygui.core.util;
    exports com.spinyowl.spinygui.core;

}