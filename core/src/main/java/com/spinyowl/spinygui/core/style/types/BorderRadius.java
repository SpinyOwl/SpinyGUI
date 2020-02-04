package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.types.length.Length;

/**
 * Class container for border radius properties.
 */
public class BorderRadius {
    /**
     * Top left border radius.
     */
    private Length topLeft;
    /**
     * Top right border radius.
     */
    private Length topRight;
    /**
     * Bottom right border radius.
     */
    private Length bottomRight;
    /**
     * Bottom left border radius.
     */
    private Length bottomLeft;

    /**
     * Returns top left border radius.
     *
     * @return top left border radius.
     */
    public Length getTopLeft() {
        return topLeft;
    }

    /**
     * Used to set top left border radius.
     *
     * @param topLeft top left border radius to set.
     */
    public void setTopLeft(Length topLeft) {
        this.topLeft = topLeft;
    }

    /**
     * Returns top right border radius.
     *
     * @return top right border radius.
     */
    public Length getTopRight() {
        return topRight;
    }

    /**
     * Used to set top right border radius.
     *
     * @param topRight top right border radius to set.
     */
    public void setTopRight(Length topRight) {
        this.topRight = topRight;
    }

    /**
     * Returns bottom right border radius.
     *
     * @return bottom right border radius.
     */
    public Length getBottomRight() {
        return bottomRight;
    }

    /**
     * Used to set bottom right border radius.
     *
     * @param bottomRight bottom right border radius to set.
     */
    public void setBottomRight(Length bottomRight) {
        this.bottomRight = bottomRight;
    }

    /**
     * Returns bottom left border radius.
     *
     * @return bottom left border radius.
     */
    public Length getBottomLeft() {
        return bottomLeft;
    }

    /**
     * Used to set bottom left border radius.
     *
     * @param bottomLeft bottom left border radius to set.
     */
    public void setBottomLeft(Length bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    /**
     * Used to set border radius.
     *
     * @param topLeftBottomRight top left and bottom right radius.
     * @param topRightBottomLeft top right and bottom left radius.
     */
    public void set(Length topLeftBottomRight, Length topRightBottomLeft) {
        topLeft = bottomRight = topLeftBottomRight;
        topRight = bottomLeft = topRightBottomLeft;
    }

    /**
     * Used to set border radius.
     *
     * @param topLeft            top left radius.
     * @param bottomRight        bottom right radius.
     * @param topRightBottomLeft top right and bottom left radius.
     */
    public void set(Length topLeft, Length topRightBottomLeft, Length bottomRight) {
        this.topLeft = topLeft;
        this.topRight = this.bottomLeft = topRightBottomLeft;
        this.bottomRight = bottomRight;
    }

    /**
     * Used to set border radius.
     *
     * @param topLeft     top left radius.
     * @param topRight    top right radius.
     * @param bottomRight bottom right radius.
     * @param bottomLeft  bottom left radius.
     */
    public void set(Length topLeft, Length topRight, Length bottomRight, Length bottomLeft) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
        this.bottomLeft = bottomLeft;
    }

    /**
     * Returns vector of four border radius elements where: x = top left, y = top right, z = bottom right, w = bottom left
     *
     * @return vector of four border radius.
     */
    public Length[] get() {
        return new Length[]{topLeft, topRight, bottomRight, bottomLeft};
    }

    /**
     * Used to set border radius for all four corners.
     *
     * @param radius radius to set. Sets border radius to all corners.
     */
    public void set(Length radius) {
        topLeft = topRight = bottomRight = bottomLeft = radius;
    }

    public void set(BorderRadius radius) {
        this.topLeft = radius.topLeft;
        this.topRight = radius.topRight;
        this.bottomRight = radius.bottomRight;
        this.bottomLeft = radius.bottomLeft;
    }
}
