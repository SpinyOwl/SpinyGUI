/**
 * Created by ShchAlexander on 09.08.2018.
 */
module org.spinyowl.spinygui.backend.opengl30 {
    requires org.spinyowl.spinygui.backend;
    requires org.spinyowl.spinygui.core;
    exports org.spinyowl.spinygui.backend.opengl30;
    exports org.spinyowl.spinygui.backend.opengl30.toolkit to org.spinyowl.spinygui.backend;
}