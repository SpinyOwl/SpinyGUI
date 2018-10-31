open module org.spinyowl.spinygui.core {

    requires transitive jdom2;
    requires transitive java.xml;
    requires transitive guava.base.r03;
    requires transitive guava.collections.r03;
    requires io.github.classgraph;
    requires org.apache.logging.log4j;

    exports org.spinyowl.spinygui.core;
    exports org.spinyowl.spinygui.core.api;
    exports org.spinyowl.spinygui.core.component;
    exports org.spinyowl.spinygui.core.component.base;
    exports org.spinyowl.spinygui.core.style;
    exports org.spinyowl.spinygui.core.converter;
    exports org.spinyowl.spinygui.core.event;
    exports org.spinyowl.spinygui.core.event.processor;

}