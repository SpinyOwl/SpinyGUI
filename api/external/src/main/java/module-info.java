/**
 * Created by ShchAlexander on 08.08.2018.
 */
open module org.liquidengine.legui.api.external {
    requires org.liquidengine.legui.backend.external;
    requires transitive org.liquidengine.legui.core.external;
    exports org.liquidengine.legui.api;
//    exports org.liquidengine.legui.api.component;
}