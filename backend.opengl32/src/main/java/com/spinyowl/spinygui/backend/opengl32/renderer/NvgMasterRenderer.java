package com.spinyowl.spinygui.backend.opengl32.renderer;

import com.spinyowl.spinygui.backend.core.renderer.AbstractMasterRenderer;
import com.spinyowl.spinygui.core.system.context.Context;
import org.joml.Vector2ic;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL30;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.opengl.GL11.*;

/**
 * Master renderer based on NanoVG.
 */
public class NvgMasterRenderer extends AbstractMasterRenderer {

    public static final String NVG_CONTEXT = "NVG_CONTEXT";
    private boolean isVersionNew;
    private long nvgContext;

    private AtomicBoolean initialized = new AtomicBoolean(false);
    private AtomicBoolean destroyed = new AtomicBoolean(false);

    @Override
    public void initialize() {
        if (initialized.compareAndSet(false, true)) {
            isVersionNew = (glGetInteger(GL30.GL_MAJOR_VERSION) > 3) || (glGetInteger(GL30.GL_MAJOR_VERSION) == 3 && glGetInteger(GL30.GL_MINOR_VERSION) >= 2);

            if (isVersionNew) {
                int flags = NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_ANTIALIAS;
                nvgContext = NanoVGGL3.nvgCreate(flags);
            } else {
                int flags = NanoVGGL2.NVG_STENCIL_STROKES | NanoVGGL2.NVG_ANTIALIAS;
                nvgContext = NanoVGGL2.nvgCreate(flags);
            }
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized.get();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed.get();
    }

    @Override
    protected void preRender(Context context) {
        context.getContextData().put(NVG_CONTEXT, nvgContext);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Vector2ic windowSize = context.getWindowSize();
        nvgBeginFrame(nvgContext, windowSize.x(), windowSize.y(), context.getPixelRatio());
    }


    @Override
    protected void postRender(Context context) {
        nvgEndFrame(nvgContext);

        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);

        context.getContextData().remove(NVG_CONTEXT, nvgContext);
    }

    @Override
    public void destroy() {
        if (initialized.get()) {
            if (destroyed.compareAndSet(false, true)) {
                if (isVersionNew) {
                    NanoVGGL3.nnvgDelete(nvgContext);
                } else {
                    NanoVGGL2.nnvgDelete(nvgContext);
                }
            }
        }
    }
}
