package com.spinyowl.spinygui.core.node;

/** HTML {@code img} element. */
public class ImgElement extends EmptyElement {

  public static final String IMG_TAG_NAME = "img";
  public static final String SRC_ATTRIBUTE = "src";
  public static final String TEXTURE_ID_ATTRIBUTE = "texture-id";
  public static final String FLIP_Y_ATTRIBUTE = "flip-y";

  public ImgElement() {
    super(IMG_TAG_NAME);
  }

  /** Returns the conventional image source, or {@code null} when it is not specified. */
  public String src() {
    return attribute(SRC_ATTRIBUTE);
  }

  /**
   * Returns an external renderer texture identifier, or {@code null} when it is not specified.
   *
   * <p>The interpretation of this value is backend-specific. The LWJGL NanoVG backend interprets
   * it as an OpenGL texture handle and imports it without taking ownership of the texture.
   */
  public String textureId() {
    return attribute(TEXTURE_ID_ATTRIBUTE);
  }

  /** Whether the image should be vertically flipped by the renderer. */
  public boolean flipY() {
    return Boolean.parseBoolean(attribute(FLIP_Y_ATTRIBUTE));
  }

  private String attribute(String name) {
    String value = attributes().get(name);
    return value == null || value.isBlank() ? null : value.trim();
  }
}
