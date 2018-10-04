/**
 * Created by ShchAlexander on 08.08.2018.
 */
module org.liquidengine.legui.core.system {
    requires org.apache.logging.log4j;
    requires io.github.classgraph;

    exports org.liquidengine.legui.core.system.component;
    exports org.liquidengine.legui.core.api;
    exports org.liquidengine.legui.core;
}