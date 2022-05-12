package com.spinyowl.spinygui.core.backend.renderer.lwjgl.skija;

import com.spinyowl.spinygui.core.backend.renderer.Renderer;
import com.spinyowl.spinygui.core.node.Frame;
import org.jetbrains.skija.BackendRenderTarget;
import org.jetbrains.skija.Canvas;
import org.jetbrains.skija.DirectContext;
import org.jetbrains.skija.Surface;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

public class SkijaRenderer implements Renderer {

  private DirectContext context;
  private BackendRenderTarget renderTarget;
  private Surface surface;
  private Canvas canvas;

  @Override
  public void initialize() {
    if (surface != null) {
      surface.close();
    }
    if (renderTarget != null) {
      renderTarget.close();
    }

    //    renderTarget =
    //        BackendRenderTarget.makeGL(
    //            (int) (width * dpi),
    //            (int) (height * dpi),
    //            /*samples*/ 0,
    //            /*stencil*/ 8,
    //            /*fbId*/ 0,
    //            FramebufferFormat.GR_GL_RGBA8);
  }

  @Override
  public void render(long window, Vector2fc windowSize, Vector2ic frameBufferSize, Frame frame) {}

  @Override
  public void destroy() {}
}
