package org.liquidengine.legui.core.component;

import org.liquidengine.legui.core.component.base.EmptyComponent;

public class Input extends EmptyComponent {


    public String getValue() {
        return getAttribute("value");
    }

    public void setValue(String value) {
        setAttribute("value", value);
    }

    public String getName() {
        return getAttribute("name");
    }

    public void setName(String name) {
        setAttribute("name", name);
    }
}
