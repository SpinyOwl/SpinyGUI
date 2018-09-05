/**
 * Created by ShchAlexander on 08.08.2018.
 */
module org.liquidengine.legui.backend {
    requires org.liquidengine.legui.backend.internal;
    requires transitive org.apache.logging.log4j;
    requires io.github.classgraph;
    requires org.liquidengine.legui.core;
    exports org.liquidengine.legui.backend;
}