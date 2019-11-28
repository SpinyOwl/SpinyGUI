package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.types.length.Length;

import java.util.Objects;
import java.util.StringJoiner;

public class Background {

    private Color color;
    private Length backgroundPositionX;
    private Length backgroundPositionY;

    public Length getBackgroundPositionX() {
        return backgroundPositionX;
    }

    public void setBackgroundPositionX(Length backgroundPositionX) {
        this.backgroundPositionX = backgroundPositionX;
    }

    public Length getBackgroundPositionY() {
        return backgroundPositionY;
    }

    public void setBackgroundPositionY(Length backgroundPositionY) {
        this.backgroundPositionY = backgroundPositionY;
    }

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
