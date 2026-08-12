package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgTextOutcomeDiagnostics.TextPath.NORMAL;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.withPresentedOpacity;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import org.joml.Vector2f;

public class NvgTextRenderer {
  private final NvgTextCommandSink commands;
  private final DiagnosticSession diagnostics;
  private final NvgTextSubmission submission;
  private final TextSink legacySink;

  public NvgTextRenderer() {
    this(new NvgFontRegistry(), DiagnosticSession.disabled());
  }

  NvgTextRenderer(NvgFontRegistry fontRegistry) {
    this(fontRegistry, DiagnosticSession.disabled());
  }

  NvgTextRenderer(NvgFontRegistry fontRegistry, DiagnosticSession diagnostics) {
    this(new NanoVgTextCommandSink(fontRegistry, diagnostics), diagnostics);
  }

  NvgTextRenderer(NvgTextCommandSink commands) {
    this(commands, DiagnosticSession.disabled());
  }

  NvgTextRenderer(NvgTextCommandSink commands, DiagnosticSession diagnostics) {
    this.commands = commands;
    this.diagnostics = diagnostics;
    submission = new NvgTextSubmission(commands, diagnostics);
    legacySink = null;
  }

  NvgTextRenderer(TextSink legacySink) {
    commands = null;
    diagnostics = DiagnosticSession.disabled();
    submission = null;
    this.legacySink = legacySink;
  }

  public void render(Node node, long context) {
    Text text = node.asText();
    if (text.inlineFragments().isEmpty()) return;
    commands.beginScope(context, NvgTextCommand.TextPath.NORMAL);
    commands.align(context, NVG_ALIGN_LEFT | NVG_ALIGN_BASELINE);
    renderFragments(text, context, inlineFormattingOffset(text));
    commands.endScope(context, NvgTextCommand.TextPath.NORMAL);
  }

  Vector2f inlineFormattingOffset(Text text) {
    Element parent = text.parent();
    while (parent != null && Display.INLINE.equals(parent.resolvedStyle().display())) parent = parent.parent();
    if (parent != null) return parent.layoutAbsolutePosition();
    return text.offsetParent() == null ? new Vector2f() : text.offsetParent().layoutAbsolutePosition();
  }

  void renderFragments(Text text, long context, Vector2f offset) {
    for (InlineFragment fragment : text.inlineFragments()) {
      if (legacySink != null) {
        if (fragment.textFragment()) {
          Element element = fragment.node() == null ? text.parent() : fragment.node().parent();
          Color color = element == null ? fragment.color() : element.presentedStyle().color();
          legacySink.drawText(context, withFragmentColor(fragment, withColor(fragment.color(), color, element)), offset.x + fragment.x(), offset.y + fragment.baseline());
        }
      } else {
        renderFragment(text, fragment, context, offset);
      }
    }
  }

  private void renderFragment(Text text, InlineFragment fragment, long context, Vector2f offset) {
    if (!fragment.textFragment()) return;
    Element element = fragment.node() == null ? text.parent() : fragment.node().parent();
    Color color = element == null ? fragment.color() : element.presentedStyle().color();
    Color presented = withColor(fragment.color(), color, element);
    float x = offset.x + fragment.x();
    float baseline = offset.y + fragment.baseline();
    if (fragment.runs().isEmpty()) {
      submit(
          context,
          fragment.font(),
          fragment.fontSize(),
          presented,
          commands.displayText(fragment.font(), fragment.text()),
          x,
          baseline);
      return;
    }
    float runX = x;
    for (ResolvedTextRun run : fragment.runs()) {
      submit(context, run.font(), fragment.fontSize(), presented, run.renderedText(), runX, baseline);
      commands.advance(NvgTextCommand.TextPath.NORMAL, runX, run.advance());
      runX += run.advance();
    }
  }

  private void submit(long context, com.spinyowl.spinygui.core.font.Font font, float size, Color color, String text, float x, float baseline) {
    submission.submit(
        context,
        NvgTextCommand.TextPath.NORMAL,
        NORMAL,
        font,
        size,
        color,
        text,
        x,
        baseline);
  }

  private Color withColor(Color fallback, Color color, Element element) {
    Color resolved = color == null ? fallback : color;
    return element == null ? resolved : withPresentedOpacity(resolved, element);
  }

  private InlineFragment withFragmentColor(InlineFragment fragment, Color color) {
    return InlineFragment.builder()
        .node(fragment.node())
        .text(fragment.text())
        .x(fragment.x())
        .y(fragment.y())
        .width(fragment.width())
        .height(fragment.height())
        .baseline(fragment.baseline())
        .font(fragment.font())
        .fontSize(fragment.fontSize())
        .color(color)
        .runs(fragment.runs())
        .build();
  }

  interface TextSink {
    void drawText(long context, InlineFragment fragment, float x, float baseline);
  }
}
