package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.base.Element;

public class MouseEvent<T extends Element> extends NodeEvent<T> {

    // modificators
    private boolean altKey;
    private boolean ctrlKey;
    private boolean shiftKey;

    // mouse button
    private int button;

    // position
    private float x;
    private float y;

    // offsets
    private float offsetX;
    private float offsetY;

    // screen position
    private float screenX;
    private float screenY;

    // page position
    private float pageX;
    private float pageY;

    // mouse movement delta
    private float movementX;
    private float movementY;

    public MouseEvent(T target) {
        super(target);
    }

    public MouseEvent(T target, double timeStamp) {
        super(target, timeStamp);
    }

    public MouseEvent(EventTarget source, T target, double timeStamp) {
        super(source, target, timeStamp);
    }

    public boolean isAltKey() {
        return altKey;
    }

    public boolean isCtrlKey() {
        return ctrlKey;
    }

    public boolean isShiftKey() {
        return shiftKey;
    }

    public int getButton() {
        return button;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getScreenX() {
        return screenX;
    }

    public float getScreenY() {
        return screenY;
    }

    public float getPageX() {
        return pageX;
    }

    public float getPageY() {
        return pageY;
    }

    public float getMovementX() {
        return movementX;
    }

    public float getMovementY() {
        return movementY;
    }

}
