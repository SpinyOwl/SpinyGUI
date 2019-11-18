package com.spinyowl.spinygui.backend.opengl32.renderer.component;

import com.spinyowl.spinygui.core.system.context.Context;
import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.system.render.NodeRenderer;

import static com.spinyowl.spinygui.backend.opengl32.renderer.NvgMasterRenderer.NVG_CONTEXT;

/**
 * Abstract node renderer based on NanoVG library.
 */
public abstract class AbstractNvgNodeRenderer implements NodeRenderer<Node> {
    @Override
    public void render(Node element, Context context) {
        long nvgContext = (long) context.getContextData().get(NVG_CONTEXT);
        render(element, context, nvgContext);
    }

    protected abstract void render(Node element, Context context, long nanovg);
}
