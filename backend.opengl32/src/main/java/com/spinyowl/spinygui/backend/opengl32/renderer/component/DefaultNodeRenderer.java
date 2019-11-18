package com.spinyowl.spinygui.backend.opengl32.renderer.component;

import com.spinyowl.spinygui.core.system.context.Context;
import com.spinyowl.spinygui.core.node.base.Node;
import org.joml.Vector4f;
import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;

public class DefaultNodeRenderer extends AbstractNvgNodeRenderer {

    private static final float MIN_ALPHA = 0.001f;

    @Override
    protected void render(Node element, Context context, long nanovg) {
        nvgSave(nanovg);
        Vector4f bgColor = new Vector4f(1, 1, 1, 1);
        if (bgColor.w() <= MIN_ALPHA) {
            return;
        }
        try (NVGColor fillColor = NVGColor.calloc()) {
            fillColor.r(bgColor.x());
            fillColor.g(bgColor.y());
            fillColor.b(bgColor.z());
            fillColor.a(bgColor.w());

            nvgBeginPath(nanovg);
            nvgFillColor(nanovg, fillColor);
            nvgRect(nanovg, element.getPosition().x(), element.getPosition().y(), element.getSize().x(), element.getSize().y());
            nvgFill(nanovg);
        }
        nvgRestore(nanovg);
    }
}
