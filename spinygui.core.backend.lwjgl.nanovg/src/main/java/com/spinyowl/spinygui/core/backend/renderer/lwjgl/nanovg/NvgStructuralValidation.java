package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.style.types.AffineTransform;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.joml.Vector2f;

/** Portable validation that traverses production text renderers through the recording sink. */
public final class NvgStructuralValidation {
  public static final String VALIDATOR_VERSION = "nvg-production-command-validator-v1";
  private static final long VALIDATION_CONTEXT = 1L;
  private NvgStructuralValidation() {}

  public static Evidence validate(Frame frame, TextMeasurer textMeasurer) {
    return validate(
        frame,
        textMeasurer,
        new Requirements(
            "benchmark-normal-text",
            expectedNormalText(frame),
            List.of(Path.NORMAL),
            false,
            false,
            false,
            false));
  }

  public static Evidence validate(
      Frame frame, TextMeasurer textMeasurer, Requirements requirements) {
    Objects.requireNonNull(frame, "frame");
    Objects.requireNonNull(textMeasurer, "textMeasurer");
    Objects.requireNonNull(requirements, "requirements");
    NvgTextCommandRecorder recorder = new NvgTextCommandRecorder();
    RecorderTraversal traversal = new RecorderTraversal(recorder, textMeasurer);
    traversal.render(frame);
    List<NvgTextCommand.Text> textCommands =
        recorder.commands().stream()
            .filter(NvgTextCommand.Text.class::isInstance)
            .map(NvgTextCommand.Text.class::cast)
            .toList();
    List<String> actualText = textCommands.stream().map(NvgTextCommand.Text::value).toList();
    if (requirements.submittedText().isEmpty()
        || !actualText.equals(requirements.submittedText())) {
      throw new IllegalStateException(
          "Structural text commands do not match source-bound scene %s: expected=%s actual=%s"
              .formatted(requirements.sceneId(), requirements.submittedText(), actualText));
    }
    long faces = recorder.commands().stream().filter(NvgTextCommand.Face.class::isInstance).count();
    long alignments =
        recorder.commands().stream().filter(NvgTextCommand.Alignment.class::isInstance).count();
    long transforms =
        recorder.commands().stream().filter(NvgTextCommand.Transform.class::isInstance).count();
    long clips = recorder.commands().stream().filter(NvgTextCommand.Clip.class::isInstance).count();
    long selections =
        recorder.commands().stream().filter(NvgTextCommand.Selection.class::isInstance).count();
    long carets = recorder.commands().stream().filter(NvgTextCommand.Caret.class::isInstance).count();
    Map<Path, Long> paths = new EnumMap<>(Path.class);
    for (Path path : Path.values()) {
      paths.put(
          path,
          textCommands.stream()
              .filter(command -> command.path().name().equals(path.name()))
              .count());
    }
    boolean nonIdentityTransform =
        recorder.commands().stream()
            .filter(NvgTextCommand.Transform.class::isInstance)
            .map(NvgTextCommand.Transform.class::cast)
            .anyMatch(
                transform ->
                    Float.compare(transform.a(), 1) != 0
                        || Float.compare(transform.b(), 0) != 0
                        || Float.compare(transform.c(), 0) != 0
                        || Float.compare(transform.d(), 1) != 0
                        || Float.compare(transform.tx(), 0) != 0
                        || Float.compare(transform.ty(), 0) != 0);
    boolean allFacesSelected =
        recorder.commands().stream()
            .filter(NvgTextCommand.Face.class::isInstance)
            .map(NvgTextCommand.Face.class::cast)
            .allMatch(NvgTextCommand.Face::selected);
    List<String> selectedFaces =
        recorder.commands().stream()
            .filter(NvgTextCommand.Face.class::isInstance)
            .map(NvgTextCommand.Face.class::cast)
            .filter(NvgTextCommand.Face::selected)
            .map(face -> fontId(face.font()))
            .toList();
    boolean replacementSubmitted = actualText.stream().anyMatch(text -> text.indexOf('\uFFFD') >= 0);
    boolean overhangSubmitted =
        requirements.overhangBoundaryY() != null
            && textCommands.stream()
                .anyMatch(command -> command.baseline() > requirements.overhangBoundaryY());
    if (faces != actualText.size()
        || !allFacesSelected
        || alignments == 0
        || transforms == 0
        || requirements.requiredPaths().stream().anyMatch(path -> paths.get(path) == 0)
        || (requirements.requireClip() && clips == 0)
        || (requirements.requireSelection() && selections == 0)
        || (requirements.requireCaret() && carets == 0)
        || (requirements.requireNonIdentityTransform() && !nonIdentityTransform)
        || !selectedFaces.containsAll(requirements.requiredFaceIds())
        || (requirements.requireReplacement() && !replacementSubmitted)
        || (requirements.overhangBoundaryY() != null && !overhangSubmitted)) {
      throw new IllegalStateException(
          "Structural text state is incomplete for scene " + requirements.sceneId());
    }
    Evidence evidence = new Evidence(
        VALIDATOR_VERSION,
        requirements.sceneId(),
        sourceExpectationDigest(requirements),
        commandDigest(recorder.commands()),
        recorder.commands().size(),
        actualText,
        paths,
        selectedFaces,
        faces,
        alignments,
        transforms,
        clips,
        selections,
        carets,
        nonIdentityTransform,
        replacementSubmitted,
        overhangSubmitted,
        "");
    return evidence.withEvidenceDigest(evidenceDigest(evidence));
  }

  private static List<String> expectedNormalText(Node node) {
    List<String> expected = new ArrayList<>();
    collectExpected(node, expected);
    return List.copyOf(expected);
  }

  private static void collectExpected(Node node, List<String> expected) {
    if (node instanceof Text text) {
      NvgFontRegistry registry = new NvgFontRegistry();
      text.inlineFragments().stream()
          .filter(fragment -> fragment.textFragment())
          .forEach(
              fragment -> {
                if (fragment.runs().isEmpty()) {
                  expected.add(
                      registry.displayText(
                          VALIDATION_CONTEXT, fragment.font(), fragment.text()));
                } else {
                  fragment.runs().forEach(run -> expected.add(run.renderedText()));
                }
              });
    }
    if (node.layoutChildNodes() != null) {
      node.layoutChildNodes().forEach(child -> collectExpected(child, expected));
    }
  }

  public enum Path {
    NORMAL,
    INPUT,
    TEXTAREA
  }

  public record Requirements(
      String sceneId,
      List<String> submittedText,
      List<Path> requiredPaths,
      boolean requireClip,
      boolean requireSelection,
      boolean requireCaret,
      boolean requireNonIdentityTransform,
      List<String> requiredFaceIds,
      boolean requireReplacement,
      Float overhangBoundaryY,
      List<String> sourceText) {
    public Requirements(
        String sceneId,
        List<String> submittedText,
        List<Path> requiredPaths,
        boolean requireClip,
        boolean requireSelection,
        boolean requireCaret,
        boolean requireNonIdentityTransform) {
      this(
          sceneId,
          submittedText,
          requiredPaths,
          requireClip,
          requireSelection,
          requireCaret,
          requireNonIdentityTransform,
          List.of(),
          false,
          null,
          submittedText);
    }

    public Requirements {
      if (sceneId == null || sceneId.isBlank()) {
        throw new IllegalArgumentException("sceneId must not be blank");
      }
      submittedText = List.copyOf(submittedText);
      requiredPaths = List.copyOf(requiredPaths);
      requiredFaceIds = List.copyOf(requiredFaceIds);
      sourceText = List.copyOf(sourceText);
      if (requiredPaths.isEmpty()) {
        throw new IllegalArgumentException("At least one text path is required");
      }
    }
  }

  public record Evidence(
      String validatorVersion,
      String sceneId,
      String sourceExpectationSha256,
      String commandDigestSha256,
      int commandCount,
      List<String> submittedText,
      Map<Path, Long> submittedTextByPath,
      List<String> selectedFaceIds,
      long faceCommands,
      long alignmentCommands,
      long transformCommands,
      long clipCommands,
      long selectionCommands,
      long caretCommands,
      boolean nonIdentityTransform,
      boolean replacementSubmitted,
      boolean overhangSubmitted,
      String evidenceDigestSha256) {
    public Evidence {
      submittedText = List.copyOf(submittedText);
      submittedTextByPath = Map.copyOf(submittedTextByPath);
      selectedFaceIds = List.copyOf(selectedFaceIds);
    }

    private Evidence withEvidenceDigest(String digest) {
      return new Evidence(
          validatorVersion,
          sceneId,
          sourceExpectationSha256,
          commandDigestSha256,
          commandCount,
          submittedText,
          submittedTextByPath,
          selectedFaceIds,
          faceCommands,
          alignmentCommands,
          transformCommands,
          clipCommands,
          selectionCommands,
          caretCommands,
          nonIdentityTransform,
          replacementSubmitted,
          overhangSubmitted,
          digest);
    }
  }

  public static String sourceExpectationDigest(Requirements requirements) {
    StringBuilder value = new StringBuilder(VALIDATOR_VERSION).append("\nsource\n");
    appendList(value, requirements.sourceText());
    appendList(value, requirements.submittedText());
    appendList(value, requirements.requiredPaths().stream().map(Enum::name).toList());
    appendList(value, requirements.requiredFaceIds());
    value.append(requirements.requireClip()).append('\n')
        .append(requirements.requireSelection()).append('\n')
        .append(requirements.requireCaret()).append('\n')
        .append(requirements.requireNonIdentityTransform()).append('\n')
        .append(requirements.requireReplacement()).append('\n')
        .append(requirements.overhangBoundaryY()).append('\n');
    return sha256(value.toString());
  }

  public static boolean evidenceDigestValid(Evidence evidence) {
    return evidence != null && evidence.evidenceDigestSha256().equals(evidenceDigest(evidence));
  }

  private static String evidenceDigest(Evidence evidence) {
    StringBuilder canonical =
        new StringBuilder(VALIDATOR_VERSION)
            .append('\n')
            .append(evidence.sceneId()).append('\n')
            .append(evidence.sourceExpectationSha256()).append('\n')
            .append(evidence.commandDigestSha256()).append('\n')
            .append(evidence.commandCount()).append('\n');
    appendList(canonical, evidence.submittedText());
    for (Path path : Path.values()) {
      canonical.append(path.name()).append('=').append(evidence.submittedTextByPath().get(path)).append('\n');
    }
    appendList(canonical, evidence.selectedFaceIds());
    canonical.append(evidence.faceCommands()).append('\n')
        .append(evidence.alignmentCommands()).append('\n')
        .append(evidence.transformCommands()).append('\n')
        .append(evidence.clipCommands()).append('\n')
        .append(evidence.selectionCommands()).append('\n')
        .append(evidence.caretCommands()).append('\n')
        .append(evidence.nonIdentityTransform()).append('\n')
        .append(evidence.replacementSubmitted()).append('\n')
        .append(evidence.overhangSubmitted()).append('\n');
    return sha256(canonical.toString());
  }

  private static void appendList(StringBuilder target, List<String> values) {
    for (String value : values) {
      byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
      target.append(bytes.length).append(':').append(value).append('\n');
    }
  }

  public static String fontId(Font font) {
    return String.join(
        "|",
        font.fontFamily(),
        font.style().name(),
        font.stretch().name(),
        font.weight().name(),
        font.path());
  }

  private static String commandDigest(List<NvgTextCommand> commands) {
    StringBuilder canonical = new StringBuilder("nvg-structural-commands-v1\n");
    for (NvgTextCommand command : commands) {
      String value = command.toString();
      canonical.append(value.getBytes(StandardCharsets.UTF_8).length).append(':').append(value).append('\n');
    }
    try {
      return sha256(canonical.toString());
    } catch (RuntimeException exception) {
      throw exception;
    }
  }

  private static String sha256(String canonical) {
    try {
      byte[] digest =
          MessageDigest.getInstance("SHA-256")
              .digest(canonical.getBytes(StandardCharsets.UTF_8));
      return "sha256:" + java.util.HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("Required SHA-256 digest is unavailable", exception);
    }
  }

  private static final class RecorderTraversal {
    private final NvgTextCommandSink commands;
    private final NvgTextRenderer textRenderer;
    private final NvgInputRenderer inputRenderer;
    private final NvgTextareaRenderer textareaRenderer;

    private RecorderTraversal(NvgTextCommandSink commands, TextMeasurer textMeasurer) {
      this.commands = commands;
      textRenderer = new NvgTextRenderer(commands);
      inputRenderer = new NvgInputRenderer(commands, DiagnosticSession.disabled());
      textareaRenderer =
          new NvgTextareaRenderer(commands, DiagnosticSession.disabled());
      inputRenderer.textMeasurer(textMeasurer);
      textareaRenderer.textMeasurer(textMeasurer);
    }

    private void render(Frame frame) {
      renderElement(frame, frame.layoutChildNodes());
    }

    private void renderElement(Element element, List<Node> children) {
      try (var transform =
          NvgTransformState.apply(0, transformAroundBorderBox(element), commands)) {
        if (element instanceof InputElement input) {
          inputRenderer.render(input, 0);
        } else if (element instanceof TextareaElement textarea) {
          textareaRenderer.render(textarea, 0);
        }
        if (children != null) {
          try (var content = NvgSubtreeContentState.apply(0, element, commands)) {
            children.forEach(this::renderNode);
          }
        }
      }
    }

    private void renderNode(Node node) {
      if (node instanceof Element element) {
        renderElement(element, element.layoutChildNodes());
      } else if (node instanceof Text) {
        textRenderer.render(node, 0);
      }
    }

    private AffineTransform transformAroundBorderBox(Element element) {
      Vector2f position = element.layoutAbsolutePosition();
      return AffineTransform.translation(position.x, position.y)
          .multiply(element.presentationState().transform())
          .multiply(AffineTransform.translation(-position.x, -position.y));
    }
  }
}
