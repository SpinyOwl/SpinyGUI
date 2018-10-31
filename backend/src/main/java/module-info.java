/**
 * Created by ShchAlexander on 09.08.2018.
 */
module org.spinyowl.spinygui.backend {
    requires transitive org.spinyowl.spinygui.core;

    requires commons.lang3;

    exports org.spinyowl.spinygui.backend.renderer;
    exports org.spinyowl.spinygui.backend.event;
    exports org.spinyowl.spinygui.backend.context;
}