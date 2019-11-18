open module com.spinyowl.spinygui.core.window {

    requires transitive com.spinyowl.spinygui.core;

    requires transitive jdom2;
    requires transitive java.xml;
    requires transitive org.joml;
    requires guava.base.r03;
    requires guava.collections.r03;
    requires io.github.classgraph;
    requires org.antlr.antlr4.runtime;
    requires transitive commons.logging;

    exports com.spinyowl.spinygui.core.window;

}