package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_CONTENT;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.flex.AlignContent;

public class AlignContentProperty extends Property {

  public AlignContentProperty() {
    super(
        ALIGN_CONTENT,
        new TermIdent(AlignContent.STRETCH.name()),
        !INHERITABLE,
        !ANIMATABLE,
        put(ALIGN_CONTENT, TermIdent.class, AlignContent::find),
        check(TermIdent.class, AlignContent::contains));
  }
}
