package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgColorUtil.create;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRect;
import static org.lwjgl.nanovg.NanoVG.*;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.style.types.Color;
import org.joml.Vector2f;

final class NanoVgTextCommandSink implements NvgTextCommandSink, AutoCloseable {
  private final NvgFontRegistry fontRegistry;
  private final DiagnosticSession diagnostics;
  private final NvgUtf8Staging staging;
  private final NvgTextStateTracker state = new NvgTextStateTracker();
  private final FaceResolver faceResolver;
  private final NativeApi nativeApi;

  NanoVgTextCommandSink(NvgFontRegistry fontRegistry, DiagnosticSession diagnostics) {
    this(fontRegistry, diagnostics, fontRegistry::fontFace, NativeApi.NATIVE);
  }

  NanoVgTextCommandSink(
      NvgFontRegistry fontRegistry,
      DiagnosticSession diagnostics,
      FaceResolver faceResolver,
      NativeApi nativeApi) {
    this.fontRegistry = fontRegistry;
    this.diagnostics = diagnostics;
    this.faceResolver = faceResolver;
    this.nativeApi = nativeApi;
    staging = new NvgUtf8Staging(diagnostics);
  }

  public void beginScope(long c, NvgTextCommand.TextPath p) {
    diagnostics.increment(NvgDiagnosticCounter.SAVE_CALLS);
    nativeApi.save(c);
    state.beginScope();
  }

  public void endScope(long c, NvgTextCommand.TextPath p) {
    state.endScope();
    diagnostics.increment(NvgDiagnosticCounter.RESTORE_CALLS);
    nativeApi.restore(c);
  }

  public void scissor(long c,float x,float y,float w,float h) { state.invalidate(); diagnostics.increment(NvgDiagnosticCounter.SCISSOR_CALLS); nativeApi.scissor(c,x,y,w,h); }
  public void intersectScissor(long c,float x,float y,float w,float h) { state.invalidate(); diagnostics.increment(NvgDiagnosticCounter.INTERSECT_SCISSOR_CALLS); nativeApi.intersectScissor(c,x,y,w,h); }
  public void resetScissor(long c) { state.invalidate(); diagnostics.increment(NvgDiagnosticCounter.RESET_SCISSOR_CALLS); nativeApi.resetScissor(c); }
  public void beginTransform(long c) { state.invalidate(); diagnostics.increment(NvgDiagnosticCounter.SAVE_CALLS); nativeApi.save(c); }
  public void transform(long c,float a,float b,float d,float e,float tx,float ty) { state.invalidate(); diagnostics.increment(NvgDiagnosticCounter.TRANSFORM_CALLS); nativeApi.transform(c,a,b,d,e,tx,ty); }
  public void translate(long c, float x, float y) { state.invalidate(); diagnostics.increment(NvgDiagnosticCounter.TRANSLATE_CALLS); nativeApi.translate(c, x, y); }
  public void endTransform(long c) { state.invalidate(); diagnostics.increment(NvgDiagnosticCounter.RESTORE_CALLS); nativeApi.restore(c); }
  public void align(long c,int v) {
    if (!state.alignment(v)) { diagnostics.increment(NvgDiagnosticCounter.TEXT_ALIGN_CALLS_SUPPRESSED); return; }
    diagnostics.increment(NvgDiagnosticCounter.TEXT_ALIGN_CALLS);
    emitState(() -> nativeApi.align(c,v));
  }
  public boolean selectFace(long c,NvgTextCommand.TextPath p,Font f) {
    if (!state.selectFace(f)) { diagnostics.increment(NvgDiagnosticCounter.FONT_FACE_CALLS_SUPPRESSED); return true; }
    try {
      String face=faceResolver.resolve(f,c);
      if(face==null) { state.invalidate(); return false; }
      diagnostics.increment(NvgDiagnosticCounter.FONT_FACE_CALLS);
      emitState(() -> nativeApi.fontFace(c,face));
      return true;
    } catch (RuntimeException | Error failure) {
      state.invalidate();
      throw failure;
    }
  }
  public String displayText(long context, Font font, String text) {
    return fontRegistry.displayText(context, font, text);
  }
  public void fontSize(long c,float v) {
    if (!state.fontSize(v)) { diagnostics.increment(NvgDiagnosticCounter.FONT_SIZE_CALLS_SUPPRESSED); return; }
    diagnostics.increment(NvgDiagnosticCounter.FONT_SIZE_CALLS);
    emitState(() -> nativeApi.fontSize(c,v));
  }
  public void fillColor(long c,Color color) {
    if (!state.color(color)) { diagnostics.increment(NvgDiagnosticCounter.FILL_COLOR_CALLS_SUPPRESSED); return; }
    diagnostics.increment(NvgDiagnosticCounter.FILL_COLOR_CALLS);
    emitState(() -> nativeApi.fillColor(c,color));
  }
  public void text(long c,NvgTextCommand.TextPath p,NvgRenderedText text,float x,float y) {
    try {
      staging.submit(text, b -> { diagnostics.increment(NvgDiagnosticCounter.TEXT_CALLS); nativeApi.text(c,x,y,b); });
    } catch (RuntimeException | Error failure) {
      state.invalidate();
      throw failure;
    }
  }
  public void advance(NvgTextCommand.TextPath p,float x,float a) {}
  public void selection(long c,float x,float y,float w,float h,Color color) { state.invalidate(); drawRect(c,new Vector2f(x,y),new Vector2f(w,h),color); }
  public void caret(long c,float x,float y,float w,float h,Color color) { state.invalidate(); drawRect(c,new Vector2f(x,y),new Vector2f(w,h),color); }
  public void outcome(NvgTextCommand.TextPath p,NvgDiagnosticCounter counter) {}

  @Override
  public void unknownMutation() { state.invalidate(); }

  void resetFrame() { state.invalidate(); staging.resetFrame(); }

  NvgTextStagingObservation stagingObservation() { return staging.observation(); }

  @Override
  public void close() { state.invalidate(); staging.close(); }

  private void emitState(Runnable emission) {
    try {
      emission.run();
    } catch (RuntimeException | Error failure) {
      state.invalidate();
      throw failure;
    }
  }

  @FunctionalInterface
  interface FaceResolver {
    String resolve(Font font, long context);
  }

  interface NativeApi {
    NativeApi NATIVE = new NativeApi() {
      public void save(long c) { nvgSave(c); }
      public void restore(long c) { nvgRestore(c); }
      public void scissor(long c,float x,float y,float w,float h) { nvgScissor(c,x,y,w,h); }
      public void intersectScissor(long c,float x,float y,float w,float h) { nvgIntersectScissor(c,x,y,w,h); }
      public void resetScissor(long c) { nvgResetScissor(c); }
      public void transform(long c,float a,float b,float d,float e,float tx,float ty) { nvgTransform(c,a,b,d,e,tx,ty); }
      public void translate(long c,float x,float y) { nvgTranslate(c,x,y); }
      public void align(long c,int value) { nvgTextAlign(c,value); }
      public void fontFace(long c,String face) { nvgFontFace(c,face); }
      public void fontSize(long c,float value) { nvgFontSize(c,value); }
      public void fillColor(long c,Color color) { try(var nativeColor=create(color)){ nvgFillColor(c,nativeColor); } }
      public void text(long c,float x,float y,java.nio.ByteBuffer utf8) { nvgText(c,x,y,utf8); }
    };

    void save(long context);
    void restore(long context);
    void scissor(long context,float x,float y,float width,float height);
    void intersectScissor(long context,float x,float y,float width,float height);
    void resetScissor(long context);
    void transform(long context,float a,float b,float c,float d,float tx,float ty);
    void translate(long context,float x,float y);
    void align(long context,int value);
    void fontFace(long context,String face);
    void fontSize(long context,float value);
    void fillColor(long context,Color color);
    void text(long context,float x,float y,java.nio.ByteBuffer utf8);
  }
}
