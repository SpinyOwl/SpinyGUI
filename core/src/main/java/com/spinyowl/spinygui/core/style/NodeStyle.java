package com.spinyowl.spinygui.core.style;

import com.spinyowl.spinygui.core.style.types.Background;
import com.spinyowl.spinygui.core.style.types.BorderRadius;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Margin;
import com.spinyowl.spinygui.core.style.types.Padding;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.border.Border;
import com.spinyowl.spinygui.core.style.types.flex.Flex;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class NodeStyle {

    @NonNull
    private Flex flex = new Flex();

    @NonNull
    private Background background = new Background();

    @NonNull
    private Border border = new Border();

    @NonNull
    private BorderRadius borderRadius = new BorderRadius();

    @NonNull
    private Padding padding = new Padding();

    @NonNull
    private Margin margin = new Margin();

    @NonNull
    private Display display = Display.BLOCK;

    @NonNull
    private Position position = Position.RELATIVE;

    @NonNull
    private Color color;

    @NonNull
    private Unit width;
    @NonNull
    private Unit height;

    @NonNull
    private Length minWidth;
    @NonNull
    private Length minHeight;

    @NonNull
    private Length maxWidth;
    @NonNull
    private Length maxHeight;

    @NonNull
    private Length top;
    @NonNull
    private Length bottom;
    @NonNull
    private Length right;
    @NonNull
    private Length left;

    @NonNull
    private WhiteSpace whiteSpace = WhiteSpace.NORMAL;

    @NonNull
    private Set<String> fontFamilies = new LinkedHashSet<>();

    @NonNull
    private Length fontSize;

}
