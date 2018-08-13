/**
 * Created by ShchAlexander on 08.08.2018.
 */
module org.liquidengine.legui.backend {
    requires transitive org.apache.logging.log4j;
    requires io.github.classgraph;
    exports org.liquidengine.legui.backend;
    exports org.liquidengine.legui.backend.annotation;
}