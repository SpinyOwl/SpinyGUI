/**
 * Created by ShchAlexander on 09.08.2018.
 */
module org.liquidengine.legui.backend.opengl30 {
    requires org.liquidengine.legui.backend.external;
    requires transitive org.liquidengine.legui.core.external;
    exports org.liquidengine.legui.backend.opengl30;
    exports org.liquidengine.legui.backend.opengl30.toolkit to org.liquidengine.legui.backend.external;
}