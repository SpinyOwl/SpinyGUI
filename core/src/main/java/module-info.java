open module org.liquidengine.legui.core {

    requires transitive jdom2;
    requires transitive java.xml;
    requires transitive guava.base.r03;
    requires transitive guava.collections.r03;
    requires io.github.classgraph;
    requires org.apache.logging.log4j;

    exports org.liquidengine.legui.core;
    exports org.liquidengine.legui.core.api;
    exports org.liquidengine.legui.core.component;
    exports org.liquidengine.legui.core.component.base;
    exports org.liquidengine.legui.core.style;
    exports org.liquidengine.legui.core.converter;
    exports org.liquidengine.legui.core.event;
    exports org.liquidengine.legui.core.event.processor;

}