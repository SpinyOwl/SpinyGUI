package com.spinyowl.spinygui.core.converter.css.atrule;

import com.spinyowl.spinygui.core.converter.css.AtRule;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Font face at-rule. Used to store info about fonts that should be imported.
 */
@Data
@RequiredArgsConstructor
public class FontFaceRule implements AtRule {

  /**
   * Name of font family
   */
  private final String fontFamily;

  /**
   * Path to source. Could be url or relative path to source on file system/resources.
   */
  private final String src;

  /**
   * Font stretch. Default is {@link FontStretch#NORMAL}.
   */
  private FontStretch fontStretch = FontStretch.NORMAL;

  /**
   * Font style. Default is {@link FontStyle#NORMAL}
   */
  private FontStyle fontStyle = FontStyle.NORMAL;

  /**
   * Font weight. Default is {@link FontWeight#NORMAL}
   */
  private FontWeight fontWeight = FontWeight.NORMAL;

}
