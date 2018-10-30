/**
 * Created by ShchAlexander on 09.08.2018.
 */
module org.liquidengine.legui.backend {
    requires transitive org.liquidengine.legui.core;

    requires commons.lang3;

    exports org.liquidengine.legui.backend.renderer;
    exports org.liquidengine.legui.backend.event;
    exports org.liquidengine.legui.backend.context;
}