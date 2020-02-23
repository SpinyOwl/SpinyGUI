package com.spinyowl.spinygui.core.style.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public abstract class SideStyle<T> {

    @NonNull
    private T top;
    @NonNull
    private T bottom;
    @NonNull
    private T right;
    @NonNull
    private T left;

    public SideStyle(@NonNull T allSides) {
        this.top = this.bottom = this.left = this.right = allSides;
    }

    public SideStyle(@NonNull T sideTopBottom, @NonNull T sideRightLeft) {
        this.top = this.bottom = sideTopBottom;
        this.left = this.right = sideRightLeft;
    }

    public SideStyle(@NonNull T sideTop, @NonNull T sideRightLeft, @NonNull T sideBottom) {
        this.top = sideTop;
        this.left = this.right = sideRightLeft;
        this.bottom = sideBottom;
    }

    public SideStyle(@NonNull T sideTop, @NonNull T sideRight, @NonNull T sideBottom,
        @NonNull T sideLeft) {
        this.top = sideTop;
        this.left = sideLeft;
        this.bottom = sideBottom;
        this.right = sideRight;
    }
}
