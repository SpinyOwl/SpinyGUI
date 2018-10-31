package org.spinyowl.spinygui.core.component.base;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.spinyowl.spinygui.core.component.*;
import org.spinyowl.spinygui.core.component.*;

public final class ComponentMapping {
    private static final BiMap<Class<? extends Component>, String> tagMapping = HashBiMap.create();

    static {
        tagMapping.put(Button.class, "button");
        tagMapping.put(Input.class, "input");
        tagMapping.put(Label.class, "label");
        tagMapping.put(Panel.class, "div");
        tagMapping.put(Pre.class, "pre");
    }

    private ComponentMapping() {
    }

    public static BiMap<Class<? extends Component>, String> getTagMapping() {
        return tagMapping;
    }
}
