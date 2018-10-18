package org.liquidengine.legui.core.component.base;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.liquidengine.legui.core.component.Button;
import org.liquidengine.legui.core.component.Panel;
import org.liquidengine.legui.core.component.Input;
import org.liquidengine.legui.core.component.Label;

public final class ComponentMapping {
    private static final BiMap<Class<? extends ComponentBase>, String> tagMapping = HashBiMap.create();

    static {
        tagMapping.put(Button.class, "button");
        tagMapping.put(Input.class, "input");
        tagMapping.put(Label.class, "label");
        tagMapping.put(Panel.class, "div");
    }

    private ComponentMapping() {
    }

    public static BiMap<Class<? extends ComponentBase>, String> getTagMapping() {
        return tagMapping;
    }
}
