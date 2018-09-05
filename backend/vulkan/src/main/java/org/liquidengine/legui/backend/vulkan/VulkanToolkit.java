package org.liquidengine.legui.backend.vulkan;

import org.liquidengine.legui.backend.Toolkit;

public class VulkanToolkit extends Toolkit {
    static {
        System.out.println("VulkanToolkit");
    }
    protected void load() {
        System.out.println("VulkanToolkit LOADED");
    }
}
