package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetInteger;
import com.spinyowl.spinygui.core.backend.renderer.Renderer;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.types.AffineTransform;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import java.util.List;
import java.util.Objects;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL30;

public class NvgRenderer implements Renderer {

  private final boolean antialiasingEnabled;
  private final ContextApi contextApi;
  private final SemanticPreflightInstaller semanticPreflightInstaller;
  private final NvgFontRegistry fontRegistry;
  private final NvgElementRenderer elementRenderer;
  private final NvgTextRenderer textRenderer;
  private final NvgBorderRenderer borderRenderer;
  private final NvgInputRenderer inputRenderer;
  private final NvgTextareaRenderer textareaRenderer;
  private final NvgScrollbarRenderer scrollbarRenderer;
  private final DiagnosticSession diagnostics;
  private final NvgTextCommandSink textCommands;
  private DebugRenderer debugRenderer;

  private boolean debugMode;
  private Vector2f debugMousePosition;
  private long nanovgContext;
  private ContextHandle contextHandle;
  private State state = State.NEW;
  private Thread uiThread;
  private SemanticFontOwner.MutationPreflightRegistration mutationPreflightRegistration;
  private NvgTransformState.Factory transformStateFactory;
  private SubtreeContentState.Factory subtreeContentStateFactory;
  private SubtreeContentRenderer subtreeContentRenderer = this::renderSubtreeContent;

  public NvgRenderer(boolean antialiasingEnabled) {
    this(antialiasingEnabled, DiagnosticSession.disabled());
  }

  public NvgRenderer(boolean antialiasingEnabled, DiagnosticSession diagnostics) {
    this(
        antialiasingEnabled,
        diagnostics,
        LwjglContextApi.INSTANCE,
        NvgFontRegistry.FaceCreator.NATIVE,
        (owner, registry) -> owner.registerMutationPreflight(registry::beforeReplacement));
  }

  NvgRenderer(
      boolean antialiasingEnabled,
      DiagnosticSession diagnostics,
      ContextApi contextApi,
      NvgFontRegistry.FaceCreator faceCreator) {
    this(
        antialiasingEnabled,
        diagnostics,
        contextApi,
        faceCreator,
        (owner, registry) -> owner.registerMutationPreflight(registry::beforeReplacement));
  }

  NvgRenderer(
      boolean antialiasingEnabled,
      DiagnosticSession diagnostics,
      ContextApi contextApi,
      NvgFontRegistry.FaceCreator faceCreator,
      SemanticPreflightInstaller semanticPreflightInstaller) {
    this.antialiasingEnabled = antialiasingEnabled;
    this.diagnostics = Objects.requireNonNull(diagnostics, "diagnostics");
    this.contextApi = Objects.requireNonNull(contextApi, "contextApi");
    this.semanticPreflightInstaller =
        Objects.requireNonNull(semanticPreflightInstaller, "semanticPreflightInstaller");
    this.fontRegistry = new NvgFontRegistry(this, faceCreator);
    this.textCommands = new NanoVgTextCommandSink(fontRegistry, this.diagnostics);
    this.elementRenderer = new NvgElementRenderer(this.diagnostics);
    this.textRenderer = new NvgTextRenderer(textCommands, this.diagnostics);
    this.borderRenderer = new NvgBorderRenderer();
    this.inputRenderer = new NvgInputRenderer(textCommands, this.diagnostics);
    this.textareaRenderer = new NvgTextareaRenderer(textCommands, this.diagnostics);
    this.scrollbarRenderer = new NvgScrollbarRenderer(this.diagnostics);
    this.debugRenderer = new NvgDebugRenderer(this.diagnostics);
    this.transformStateFactory =
        (context, transform) -> NvgTransformState.apply(context, transform, textCommands);
    this.subtreeContentStateFactory =
        (context, element) -> NvgSubtreeContentState.apply(context, element, textCommands);
  }

  public NvgRenderer() {
    this(true);
  }

  public void initialize() {
    SemanticFontOwner owner = Font.semanticOwner();
    if (state != State.NEW) {
      throw new IllegalStateException("NanoVG renderer cannot initialize from " + state);
    }
    uiThread = owner.ownerThread();
    state = State.INITIALIZING;
    try {
      contextHandle = contextApi.create(antialiasingEnabled);
      if (contextHandle == null || contextHandle.identity() == 0) {
        throw new IllegalStateException("NanoVG context creation failed");
      }
      nanovgContext = contextHandle.identity();
      mutationPreflightRegistration =
          Objects.requireNonNull(
              semanticPreflightInstaller.install(owner, fontRegistry),
              "semantic mutation preflight registration");
      state = State.INITIALIZED;
    } catch (RuntimeException | Error failure) {
      rollbackFailedInitialization(failure);
      state = State.FAILED;
      throw failure;
    }
  }

  @Override
  public void render(long window, Vector2fc windowSize, Vector2ic frameBufferSize, Frame frame) {
    requireInitializedUse("render", nanovgContext);
    try (var ignored = Font.semanticOwner().openReadUseScope(SemanticFontOwner.ReadUseKind.RENDER)) {
      renderFrame(windowSize, frameBufferSize, frame);
    }
  }

  private void renderFrame(Vector2fc windowSize, Vector2ic frameBufferSize, Frame frame) {

    float pixelRatio = windowSize.x() / frameBufferSize.x();

    preRender(windowSize, pixelRatio);

    renderLayoutTree(frame);
    if (debugMode) {
      renderDebug(frame);
    }

    postRender();
  }

  void renderLayoutTree(Frame layoutTree) {
    renderElement(layoutTree, layoutTree.layoutChildNodes());
  }

  void renderDebug(Frame frame) {
    if (debugMode) {
      debugRenderer.render(frame, nanovgContext, debugMousePosition);
    }
  }

  private void renderElement(Node node, List<Node> children) {
    Element element = node.asElement();
    try (var ignored =
        transformStateFactory.apply(
            nanovgContext, transformAroundBorderBox(element))) {
      subtreeContentRenderer.render(node, nanovgContext);

      if (children != null) {
        try (var contentState = subtreeContentStateFactory.apply(nanovgContext, element)) {
          children.forEach(this::renderLayoutNode);
        }
      }
      scrollbarRenderer.render(element, nanovgContext);
    }
  }

  private AffineTransform transformAroundBorderBox(Element element) {
    Vector2f position = element.layoutAbsolutePosition();
    return AffineTransform.translation(position.x, position.y)
        .multiply(element.presentationState().transform())
        .multiply(AffineTransform.translation(-position.x, -position.y));
  }

  void transformStateFactory(NvgTransformState.Factory transformStateFactory) {
    this.transformStateFactory = transformStateFactory;
  }

  void subtreeContentRenderer(SubtreeContentRenderer subtreeContentRenderer) {
    this.subtreeContentRenderer = subtreeContentRenderer;
  }

  void subtreeContentStateFactory(SubtreeContentState.Factory subtreeContentStateFactory) {
    this.subtreeContentStateFactory = subtreeContentStateFactory;
  }

  void debugRenderer(DebugRenderer debugRenderer) {
    this.debugRenderer = debugRenderer;
  }

  private void renderSubtreeContent(Node node, long context) {
    elementRenderer.render(node, context);
    borderRenderer.render(node, context);
    if (node instanceof InputElement input) {
      inputRenderer.render(input, context);
    } else if (node instanceof TextareaElement textarea) {
      textareaRenderer.render(textarea, context);
    }
  }

  @FunctionalInterface
  interface SubtreeContentRenderer {
    void render(Node node, long context);
  }

  @FunctionalInterface
  interface SubtreeContentState extends AutoCloseable {
    @Override
    void close();

    interface Factory {
      SubtreeContentState apply(long context, Element element);
    }
  }

  @FunctionalInterface
  interface DebugRenderer {
    void render(Frame frame, long context, Vector2fc mousePosition);

    default void textMeasurer(TextMeasurer textMeasurer) {}
  }

  private void renderLayoutNode(Node node) {
    if (node instanceof Element) {
      renderElement(node, node.layoutChildNodes());
    } else if (node instanceof Text) {
      textRenderer.render(node, nanovgContext);
    }
  }

  private void postRender() {

    nvgEndFrame(nanovgContext);

    glDisable(GL_BLEND);
    glEnable(GL_DEPTH_TEST);

    //    imageReferenceManager.removeOldImages(nvgContext);
    //    context.getContextData().remove(NVG_CONTEXT);
    //    context.getContextData().remove(IMAGE_REFERENCE_MANAGER);
  }

  private void preRender(Vector2fc windowSize, float pixelRatio) {
    //    loadFontsToNvg();
    //    context.getContextData().put(NVG_CONTEXT, nvgContext);

    glDisable(GL_DEPTH_TEST);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    nvgBeginFrame(nanovgContext, windowSize.x(), windowSize.y(), pixelRatio);
  }

  public void destroy() {
    requireUiThread();
    if (state == State.DESTROYED) {
      return;
    }
    if (state == State.INITIALIZING || state == State.DESTROYING) {
      throw new IllegalStateException("NanoVG renderer cannot destroy from " + state);
    }
    state = State.DESTROYING;
    try {
      if (contextHandle != null) {
        contextApi.delete(contextHandle);
      }
      closeMutationPreflight();
      contextHandle = null;
      nanovgContext = 0;
      state = State.DESTROYED;
    } catch (RuntimeException | Error failure) {
      state = State.FAILED;
      throw failure;
    }
    //
    // RendererProvider.getInstance().getComponentRenderers().forEach(ComponentRenderer::destroy);
    //    imageReferenceManager.destroy();}
  }

  public boolean debugMode() {
    return debugMode;
  }

  public void debugMode(boolean debugMode) {
    this.debugMode = debugMode;
  }

  public void toggleDebugMode() {
    debugMode(!debugMode);
  }

  public void debugMousePosition(Vector2fc debugMousePosition) {
    this.debugMousePosition =
        debugMousePosition == null ? null : new Vector2f(debugMousePosition);
  }

  public void textMeasurer(TextMeasurer textMeasurer) {
    inputRenderer.textMeasurer(textMeasurer);
    textareaRenderer.textMeasurer(textMeasurer);
    debugRenderer.textMeasurer(textMeasurer);
  }

  State state() {
    return state;
  }

  Thread uiThread() {
    return uiThread;
  }

  long contextIdentity() {
    return nanovgContext;
  }

  String fontFace(Font font, long context) {
    return fontRegistry.fontFace(font, context);
  }

  void requireFontFaceUse(long context) {
    requireInitializedUse("font face", context);
  }

  private void requireInitializedUse(String operation, long context) {
    requireUiThread();
    if (state != State.INITIALIZED) {
      throw new IllegalStateException(
          "NanoVG renderer " + operation + " requires INITIALIZED state, was " + state);
    }
    if (context != nanovgContext) {
      throw new IllegalStateException(
          "NanoVG renderer rejects context replacement or mismatch after initialization");
    }
  }

  private void requireUiThread() {
    SemanticFontOwner owner = Font.semanticOwner();
    Thread installedThread = owner.ownerThread();
    if (uiThread == null) {
      uiThread = installedThread;
    }
    if (Thread.currentThread() != uiThread || installedThread != uiThread) {
      throw new IllegalStateException("NanoVG renderer operation requires its UI thread");
    }
  }

  private void rollbackFailedInitialization(Throwable failure) {
    if (contextHandle != null) {
      try {
        contextApi.delete(contextHandle);
        contextHandle = null;
        nanovgContext = 0;
      } catch (RuntimeException | Error rollbackFailure) {
        failure.addSuppressed(rollbackFailure);
      }
    }
    if (contextHandle == null) {
      closeMutationPreflight();
    }
  }

  private void closeMutationPreflight() {
    if (mutationPreflightRegistration != null) {
      mutationPreflightRegistration.close();
      mutationPreflightRegistration = null;
    }
  }

  enum State {
    NEW,
    INITIALIZING,
    INITIALIZED,
    FAILED,
    DESTROYING,
    DESTROYED
  }

  record ContextHandle(long identity, Profile profile) {
    ContextHandle {
      Objects.requireNonNull(profile, "profile");
    }
  }

  enum Profile {
    GL2,
    GL3
  }

  interface ContextApi {
    ContextHandle create(boolean antialiasingEnabled);

    void delete(ContextHandle context);
  }

  @FunctionalInterface
  interface SemanticPreflightInstaller {
    SemanticFontOwner.MutationPreflightRegistration install(
        SemanticFontOwner owner, NvgFontRegistry registry);
  }

  private enum LwjglContextApi implements ContextApi {
    INSTANCE;

    @Override
    public ContextHandle create(boolean antialiasingEnabled) {
      boolean versionNew =
          (glGetInteger(GL30.GL_MAJOR_VERSION) > 3)
              || glGetInteger(GL30.GL_MAJOR_VERSION) == 3
                  && glGetInteger(GL30.GL_MINOR_VERSION) >= 2;
      if (versionNew) {
        int flags =
            antialiasingEnabled
                ? NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_ANTIALIAS
                : NanoVGGL3.NVG_STENCIL_STROKES;
        return new ContextHandle(NanoVGGL3.nvgCreate(flags), Profile.GL3);
      }
      int flags =
          antialiasingEnabled
              ? NanoVGGL2.NVG_STENCIL_STROKES | NanoVGGL2.NVG_ANTIALIAS
              : NanoVGGL2.NVG_STENCIL_STROKES;
      return new ContextHandle(NanoVGGL2.nvgCreate(flags), Profile.GL2);
    }

    @Override
    public void delete(ContextHandle context) {
      if (context.profile() == Profile.GL3) {
        NanoVGGL3.nnvgDelete(context.identity());
      } else {
        NanoVGGL2.nnvgDelete(context.identity());
      }
    }
  }
}
