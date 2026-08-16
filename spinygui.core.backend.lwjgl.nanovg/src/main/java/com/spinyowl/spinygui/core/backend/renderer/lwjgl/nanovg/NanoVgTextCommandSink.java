package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgColorUtil.create;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRect;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.system.MemoryUtil.memFree;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.style.types.Color;
import java.nio.ByteBuffer;
import org.joml.Vector2f;

final class NanoVgTextCommandSink implements NvgTextCommandSink {
  private final NvgFontRegistry fontRegistry;
  private final DiagnosticSession diagnostics;
  NanoVgTextCommandSink(NvgFontRegistry fontRegistry, DiagnosticSession diagnostics) { this.fontRegistry = fontRegistry; this.diagnostics = diagnostics; }
  public void beginScope(long c, NvgTextCommand.TextPath p) { diagnostics.increment(NvgDiagnosticCounter.SAVE_CALLS); nvgSave(c); }
  public void endScope(long c, NvgTextCommand.TextPath p) { diagnostics.increment(NvgDiagnosticCounter.RESTORE_CALLS); nvgRestore(c); }
  public void scissor(long c,float x,float y,float w,float h) { diagnostics.increment(NvgDiagnosticCounter.SCISSOR_CALLS); nvgScissor(c,x,y,w,h); }
  public void intersectScissor(long c,float x,float y,float w,float h) { diagnostics.increment(NvgDiagnosticCounter.INTERSECT_SCISSOR_CALLS); nvgIntersectScissor(c,x,y,w,h); }
  public void resetScissor(long c) { diagnostics.increment(NvgDiagnosticCounter.RESET_SCISSOR_CALLS); nvgResetScissor(c); }
  public void beginTransform(long c) { diagnostics.increment(NvgDiagnosticCounter.SAVE_CALLS); nvgSave(c); }
  public void transform(long c,float a,float b,float d,float e,float tx,float ty) { diagnostics.increment(NvgDiagnosticCounter.TRANSFORM_CALLS); nvgTransform(c,a,b,d,e,tx,ty); }
  public void translate(long c, float x, float y) { diagnostics.increment(NvgDiagnosticCounter.TRANSLATE_CALLS); nvgTranslate(c, x, y); }
  public void endTransform(long c) { diagnostics.increment(NvgDiagnosticCounter.RESTORE_CALLS); nvgRestore(c); }
  public void align(long c,int v) { diagnostics.increment(NvgDiagnosticCounter.TEXT_ALIGN_CALLS); nvgTextAlign(c,v); }
  public boolean selectFace(long c,NvgTextCommand.TextPath p,Font f) { String face=fontRegistry.fontFace(f,c); if(face==null)return false; diagnostics.increment(NvgDiagnosticCounter.FONT_FACE_CALLS); nvgFontFace(c,face); return true; }
  public String displayText(long context, Font font, String text) {
    return fontRegistry.displayText(context, font, text);
  }
  public void fontSize(long c,float v) { diagnostics.increment(NvgDiagnosticCounter.FONT_SIZE_CALLS); nvgFontSize(c,v); }
  public void fillColor(long c,Color color) { try(var nativeColor=create(color)){ diagnostics.increment(NvgDiagnosticCounter.FILL_COLOR_CALLS); nvgFillColor(c,nativeColor); } }
  public void text(long c,NvgTextCommand.TextPath p,String text,float x,float y) { ByteBuffer b=NvgUtf8Staging.encode(text,diagnostics); try { diagnostics.increment(NvgDiagnosticCounter.TEXT_CALLS); nvgText(c,x,y,b); } finally { memFree(b); } }
  public void advance(NvgTextCommand.TextPath p,float x,float a) {}
  public void selection(long c,float x,float y,float w,float h,Color color) { drawRect(c,new Vector2f(x,y),new Vector2f(w,h),color); }
  public void caret(long c,float x,float y,float w,float h,Color color) { drawRect(c,new Vector2f(x,y),new Vector2f(w,h),color); }
  public void outcome(NvgTextCommand.TextPath p,NvgDiagnosticCounter counter) {}
}
