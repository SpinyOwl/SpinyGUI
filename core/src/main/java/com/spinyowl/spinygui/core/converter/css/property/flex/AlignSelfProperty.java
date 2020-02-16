package com.spinyowl.spinygui.core.converter.css.property.flex;

import static com.spinyowl.spinygui.core.converter.css.Properties.ALIGN_SELF;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;
import java.util.Set;
import java.util.stream.Collectors;

public class AlignSelfProperty extends Property<AlignSelf> {

    public AlignSelfProperty() {
        super(ALIGN_SELF, "auto", !INHERITED, !ANIMATABLE,
            (s, v) -> s.getFlex().setAlignSelf(v), s -> s.getFlex().getAlignSelf(),
            AlignSelf::find, AlignSelf::contains);
    }

    @Override
    public Set<String> allowedValues() {
        return AlignSelf.values().stream().map(AlignSelf::getName).collect(Collectors.toSet());
    }
}
