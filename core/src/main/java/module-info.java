open module org.liquidengine.legui.core {

    requires org.jsoup;
    requires io.github.classgraph;
    requires org.apache.logging.log4j;

    exports org.liquidengine.legui.core;
    exports org.liquidengine.legui.core.api;
    exports org.liquidengine.legui.core.component;
    exports org.liquidengine.legui.core.style;
    exports org.liquidengine.legui.core.system.component;

}