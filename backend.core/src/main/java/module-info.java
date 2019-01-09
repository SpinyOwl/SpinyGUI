/**
 * Created by ShchAlexander on 09.08.2018.
 */
module com.spinyowl.spinygui.backend.core {
    requires transitive com.spinyowl.spinygui.core;

    requires commons.lang3;

    exports com.spinyowl.spinygui.backend.core.renderer;
    exports com.spinyowl.spinygui.backend.core.event;
    exports com.spinyowl.spinygui.backend.core.event.handler;
    exports com.spinyowl.spinygui.backend.core.event.processor;
    exports com.spinyowl.spinygui.backend.core.context;
}