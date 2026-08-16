package com.spinyowl.spinygui.core.font;

import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner;
import java.util.List;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Font {
  private static SemanticFontOwner semanticOwner;
  private static FontService semanticService;

  private static final String ROBOTO = "Roboto";

  public static final Font ROBOTO_LIGHT =
      new Font(
          ROBOTO,
          FontStyle.NORMAL,
          FontStretch.NORMAL,
          FontWeight.LIGHT,
          "fonts/Roboto-Light.ttf");

  public static final Font ROBOTO_BOLD =
      new Font(
          ROBOTO,
          FontStyle.NORMAL,
          FontStretch.NORMAL,
          FontWeight.BOLD,
          "fonts/Roboto-Bold.ttf");

  public static final Font ROBOTO_REGULAR =
      new Font(
          ROBOTO,
          FontStyle.NORMAL,
          FontStretch.NORMAL,
          FontWeight.REGULAR,
          "fonts/Roboto-Regular.ttf");
  public static final Font NOTO_SANS_CJK_SC_REGULAR =
      new Font(
          "Noto Sans CJK SC",
          FontStyle.NORMAL,
          FontStretch.NORMAL,
          FontWeight.REGULAR,
          "fonts/NotoSansCJKsc-Regular.otf");
  public static final Font DEFAULT = ROBOTO_REGULAR;

  @NonNull private final String fontFamily;
  @NonNull private final FontStyle style;
  @NonNull private final FontStretch stretch;
  @NonNull private final FontWeight weight;

  @NonNull private final String path;

  public Font(@NonNull String fontFamily, @NonNull String path) {
    this(fontFamily, FontStyle.NORMAL, FontStretch.NORMAL, FontWeight.NORMAL, path);
  }

  public Font(@NonNull String fontFamily, @NonNull FontStyle style, @NonNull String path) {
    this(fontFamily, style, FontStretch.NORMAL, FontWeight.NORMAL, path);
  }

  /**
   * This legacy descriptor-only mutation alias is unsupported because it cannot validate font
   * bytes or preserve atomic semantic identity and generation publication. Use
   * {@link com.spinyowl.spinygui.core.system.font.FontService#loadFont(String)} after explicitly
   * installing that service's semantic owner.
   *
   * @param font descriptor that would have been registered
   * @return never returns
   * @throws UnsupportedOperationException always
   * @deprecated use the installed font service mutation transaction
   */
  @Deprecated(forRemoval = true)
  public static Font addFont(@NonNull Font font) {
    throw new UnsupportedOperationException(
        "Font.addFont cannot publish validated semantic identity; use FontService.loadFont");
  }

  /**
   * Rejects owner-only installation because it cannot provide coordinated resource lifecycle.
   *
   * @param owner installed owner
   * @return never returns
   * @throws UnsupportedOperationException always; install through a production {@link FontService}
   * @deprecated semantic ownership must be installed with its lifecycle service aggregate
   */
  @Deprecated(forRemoval = true)
  public static SemanticFontOwner installSemanticOwner(@NonNull SemanticFontOwner owner) {
    throw new UnsupportedOperationException(
        "Semantic font owner installation requires a FontService lifecycle aggregate");
  }

  /**
   * Binds the production service that coordinates semantic clear and core-resource teardown.
   *
   * @param owner installed semantic owner
   * @param service production service attached to that owner
   * @return the bound owner
   * @throws IllegalStateException if either half of a prior binding is missing or a different owner
   *     or service aggregate is already installed
   */
  public static SemanticFontOwner installSemanticOwner(
      @NonNull SemanticFontOwner owner, @NonNull FontService service) {
    owner.verifyUse();
    if ((semanticOwner == null) != (semanticService == null)) {
      throw new IllegalStateException("Semantic font owner/service binding is incomplete");
    }
    if (semanticOwner != null && semanticOwner != owner) {
      throw new IllegalStateException("A different semantic font owner is already installed");
    }
    if (semanticService != null && semanticService != service) {
      throw new IllegalStateException("A different semantic font service aggregate is installed");
    }
    semanticOwner = owner;
    semanticService = service;
    return owner;
  }

  /** Returns the installed service aggregate after verifying owner-thread use. */
  public static FontService semanticService() {
    semanticOwner();
    if (semanticService == null) {
      throw new IllegalStateException("Semantic font resource lifecycle service is not installed");
    }
    return semanticService;
  }

  /** Releases the exact closed production owner/service binding. */
  public static void releaseSemanticOwner(
      @NonNull SemanticFontOwner owner, @NonNull FontService service) {
    if (semanticOwner == owner && semanticService == service) {
      semanticService = null;
      semanticOwner = null;
    }
  }

  /** Returns whether a production semantic owner has been explicitly bound. */
  public static boolean hasSemanticOwner() {
    return semanticOwner != null;
  }

  /** Returns and verifies the explicitly bound semantic owner on its install thread. */
  public static SemanticFontOwner semanticOwner() {
    if (semanticOwner == null) {
      throw new IllegalStateException("Semantic font owner is not installed");
    }
    semanticOwner.verifyUse();
    return semanticOwner;
  }

  /** Clears semantic registrations and owner-controlled resources through the installed service. */
  public static SemanticFontOwner.Mutation clear() {
    semanticOwner();
    if (semanticService == null) {
      throw new IllegalStateException("Semantic font resource lifecycle service is not installed");
    }
    return semanticService.clear();
  }

  /**
   * Returns an immutable snapshot of all currently registered descriptors.
   *
   * @return list of all fonts.
   */
  public static List<Font> fonts() {
    return semanticOwner().registeredFonts();
  }

  /**
   * Search for fonts with specified font family name.
   *
   * @param name font family name.
   * @return obtained font.
   */
  public static List<Font> find(String name) {
    return find(name, null, null, null);
  }

  /**
   * Search for fonts with specified parameters. Any parameter could be nullable. In this case this
   * parameter will not be used during search.
   *
   * @param name font family name.
   * @param weight font weight
   * @return obtained font.
   */
  public static List<Font> find(String name, FontWeight weight) {
    return find(name, null, weight, null);
  }

  /**
   * Search for fonts with specified parameters. Any parameter could be nullable. In this case this
   * parameter will not be used during search.
   *
   * @param name font family name.
   * @param style font style
   * @return obtained font.
   */
  public static List<Font> find(String name, FontStyle style) {
    return find(name, style, null, null);
  }

  /**
   * Search for fonts with specified parameters. Any parameter could be nullable. In this case this
   * parameter will not be used during search.
   *
   * @param name font family name.
   * @param style font style
   * @param weight font weight
   * @return obtained font.
   */
  public static List<Font> find(String name, FontStyle style, FontWeight weight) {
    return find(name, style, weight, null);
  }

  /**
   * Search for fonts with specified parameters. Any parameter could be nullable. In this case this
   * parameter will not be used during search.
   *
   * @param name font family name.
   * @param style font style
   * @param width font width
   * @param weight font weight
   * @return obtained font.
   */
  public static List<Font> find(
      String name, FontStyle style, FontWeight weight, FontStretch width) {
    return fonts().stream().filter(font -> checkFont(font, name, style, weight, width)).toList();
  }

  /**
   * Search for fonts with specified font names, style and weight.
   *
   * @param fontNames list of font names.
   * @param fontStyle font style.
   * @param fontWeight font weight.
   * @return list of fonts.
   */
  public static List<Font> find(
      List<String> fontNames, FontStyle fontStyle, FontWeight fontWeight) {
    return fontNames.stream()
        .map(fontName -> Font.find(fontName, fontStyle, fontWeight))
        .flatMap(List::stream)
        .toList();
  }
  /**
   * Returns true if there is any font with specified parameters.
   *
   * @param name font family name.
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(String name) {
    return hasFont(name, null, null, null);
  }

  /**
   * Returns true if there is any font with specified parameters.
   *
   * @param name font family name.
   * @param weight font weight
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(String name, FontWeight weight) {
    return hasFont(name, null, weight, null);
  }

  /**
   * Returns true if there is any font with specified parameters.
   *
   * @param name font family name.
   * @param style font style
   * @param weight font weight
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(String name, FontStyle style, FontWeight weight) {
    return hasFont(name, style, weight, null);
  }

  /**
   * Returns true if there is any font with specified parameters.
   *
   * @param name font family name.
   * @param style font style
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(String name, FontStyle style) {
    return hasFont(name, style, null, null);
  }

  /**
   * Returns true if there is any font with specified parameters. Any parameter could be nullable.
   * In this case this parameter will not be used during search.
   *
   * @param name font family name.
   * @param style font style
   * @param width font width
   * @param weight font weight
   * @return true if there is any font with specified parameters.
   */
  public static boolean hasFont(
      String name, FontStyle style, FontWeight weight, FontStretch width) {
    return fonts().stream().anyMatch(font -> checkFont(font, name, style, weight, width));
  }

  private static boolean checkFont(
      Font font, String name, FontStyle style, FontWeight weight, FontStretch width) {
    return (name == null || name.equalsIgnoreCase(font.fontFamily))
        && (style == null || style.equals(font.style))
        && (weight == null || weight.equals(font.weight))
        && (width == null || width.equals(font.stretch));
  }
}
