package com.spinyowl.spinygui.core.style.types.background;

import com.spinyowl.spinygui.core.style.types.length.Unit;

public class BackgroundSize {
    private Type type;

    private Unit sizeX;
    private Unit sizeY;

    private BackgroundSize(Type type) {
        this.type = type;
    }

    private BackgroundSize(Type type, Unit size) {
        this.type = type;
        this.sizeX = size;
        this.sizeY = Unit.AUTO;
    }

    public static BackgroundSize contain() {
        return new BackgroundSize(Type.CONTAIN);
    }

    public static BackgroundSize cover() {
        return new BackgroundSize(Type.COVER);
    }

    public static BackgroundSize size(Unit size) {
        return new BackgroundSize(Type.UNIT);
    }


    public enum Type {
        CONTAIN,
        COVER,
        UNIT
    }
}


