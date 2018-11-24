package com.spinyowl.spinygui.core.style.types;

import java.util.HashMap;
import java.util.Map;

public class Color {

    private static Map<String, Color> colors = new HashMap<>();

    static {
        colors.put("black", new Color(0, 0, 0));
        colors.put("silver", new Color(192, 192, 192));
        colors.put("gray", new Color(128, 128, 128));
        colors.put("white", new Color(255, 255, 255));
        colors.put("maroon", new Color(128, 0, 0));
        colors.put("red	    ", new Color(255, 0, 0));
        colors.put("purple", new Color(128, 0, 128));
        colors.put("fuchsia", new Color(255, 0, 255));
        colors.put("green", new Color(0, 128, 0));
        colors.put("lime", new Color(0, 255, 0));
        colors.put("olive", new Color(128, 128, 0));
        colors.put("yellow", new Color(255, 255, 0));
        colors.put("navy", new Color(0, 0, 128));
        colors.put("blue", new Color(0, 0, 255));
        colors.put("teal", new Color(0, 128, 128));
        colors.put("aqua", new Color(0, 255, 255));
    }

    private float red;
    private float green;
    private float blue;
    private float alpha;

    public Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Color(int red, int green, int blue, float alpha) {
        this(red / 255f, green / 255f, blue / 255f, alpha);
    }

    public Color(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color(int red, int green, int blue) {
        this(red / 255f, green / 255f, blue / 255f, 1f);
    }

    public static Color getColorByName(String name) {
        return colors.get(name);
    }

    public float getRed() {
        return red;
    }

    public void setRed(float red) {
        this.red = red;
    }

    public float getGreen() {
        return green;
    }

    public void setGreen(float green) {
        this.green = green;
    }

    public float getBlue() {
        return blue;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public String toString() {
        if (alpha != 1)
            return String.format("Color(%f, %f, %f, %f)", red, green, blue, alpha);
        return String.format("Color(%f, %f, %f)", red, green, blue);
    }


}
