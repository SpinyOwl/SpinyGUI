package com.spinyowl.spinygui.core.converter.css.atrule;

import com.spinyowl.spinygui.core.converter.css.AtRule;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FontFaceRule implements AtRule {

    private final String fontFamily;
    private final String src;

    private FontStretch fontStretch;
    private FontStyle fontStyle;
    private FontWeight fontWeight;

}
