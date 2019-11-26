package com.spinyowl.spinygui.core.style.types;

import java.util.Objects;
import java.util.StringJoiner;

public class Background {

    private Color color;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Background.class.getSimpleName() + "[", "]")
                .add("color=" + color)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Background that = (Background) o;
        return Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
