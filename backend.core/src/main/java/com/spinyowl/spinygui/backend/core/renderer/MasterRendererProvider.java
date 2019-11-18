package com.spinyowl.spinygui.backend.core.renderer;

import java.util.Objects;

public final class MasterRendererProvider {
    private static MasterRenderer renderer;

    private MasterRendererProvider() {
    }

    public static MasterRenderer getRenderer() {
        return renderer;
    }

    public static void setRenderer(MasterRenderer renderer) {
        Objects.requireNonNull(renderer);
        if (MasterRendererProvider.renderer != null) {
            MasterRendererProvider.renderer.destroy();
        }
        MasterRendererProvider.renderer = renderer;
    }
}
