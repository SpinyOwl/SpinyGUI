/**
 * Created by ShchAlexander on 09.08.2018.
 */
module com.spinyowl.spinygui.backend {
    requires transitive com.spinyowl.spinygui.core;

    requires commons.lang3;

    exports com.spinyowl.spinygui.backend.renderer;
    exports com.spinyowl.spinygui.backend.event;
    exports com.spinyowl.spinygui.backend.event.processor;
    exports com.spinyowl.spinygui.backend.context;
}