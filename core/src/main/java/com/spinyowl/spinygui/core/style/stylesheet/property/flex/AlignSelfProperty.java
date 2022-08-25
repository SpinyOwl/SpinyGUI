package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_SELF;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;

public class AlignSelfProperty extends Property {

  public AlignSelfProperty() {
    super(
        ALIGN_SELF,
        new TermIdent(AlignSelf.AUTO.name()),
        !INHERITABLE,
        !ANIMATABLE,
        put(ALIGN_SELF, TermIdent.class, AlignSelf::find),
        check(TermIdent.class, AlignSelf::contains));
  }
}
