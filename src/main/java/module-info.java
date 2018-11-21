open module com.spinyowl.spinygui {

    requires commons.lang3;

    requires jdom2;
    requires java.xml;
    requires guava.base.r03;
    requires guava.collections.r03;

    requires org.lwjgl;
    requires org.lwjgl.natives;
    requires org.lwjgl.glfw;
    requires org.lwjgl.glfw.natives;
    requires org.lwjgl.opengl;
    requires org.lwjgl.opengl.natives;

    requires org.slf4j;

    requires io.github.classgraph;

    exports com.spinyowl.spinygui.backend.core.renderer;
    exports com.spinyowl.spinygui.backend.core.event;
    exports com.spinyowl.spinygui.backend.core.event.processor;
    exports com.spinyowl.spinygui.backend.core.context;

    exports com.spinyowl.spinygui.backend.glfw.util;
    exports com.spinyowl.spinygui.backend.glfw.util.cbchain;
    exports com.spinyowl.spinygui.backend.glfw.util.cbchain.impl;

    exports com.spinyowl.spinygui.backend.glfw.opengl32.api;
    exports com.spinyowl.spinygui.backend.glfw.opengl32.event;
    exports com.spinyowl.spinygui.backend.glfw.opengl32.service;
    exports com.spinyowl.spinygui.backend.glfw.opengl32.service.internal;

    exports com.spinyowl.spinygui.core.api;
    exports com.spinyowl.spinygui.core.animation;
    exports com.spinyowl.spinygui.core.component;
    exports com.spinyowl.spinygui.core.component.base;
    exports com.spinyowl.spinygui.core.style;
    exports com.spinyowl.spinygui.core.converter;
    exports com.spinyowl.spinygui.core.event;
    exports com.spinyowl.spinygui.core.event.listener;
    exports com.spinyowl.spinygui.core.event.processor;

    exports com.spinyowl.spinygui.core.configuration;

    exports com.spinyowl.spinygui.core.service;

}