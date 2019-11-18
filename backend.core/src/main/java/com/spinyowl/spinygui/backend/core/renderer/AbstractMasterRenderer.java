package com.spinyowl.spinygui.backend.core.renderer;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.api.Layer;
import com.spinyowl.spinygui.core.api.LayerContainer;
import com.spinyowl.spinygui.core.system.context.Context;

public abstract class AbstractMasterRenderer implements MasterRenderer {


    protected abstract boolean isInitialized();

    protected abstract boolean isDestroyed();

    protected abstract void preRender(Context c);

    @Override
    public void render(Frame frame, Context context) {
        if (!isInitialized()) {
            throw new IllegalStateException("Renderer should be initialized before rendering!");
        }
        if (isDestroyed()) {
            throw new IllegalStateException("Renderer is already destroyed!");
        }

        if (frame == null) return;

        preRender(context);
        for (Layer layer : frame.getAllLayers()) {
            LayerContainer container = layer.getContainer();
            container.getRenderer().render(container, context);
        }
        postRender(context);
    }

    protected abstract void postRender(Context c);
}
