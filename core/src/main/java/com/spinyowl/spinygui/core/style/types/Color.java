package com.spinyowl.spinygui.core.style.types;

import java.util.HashMap;
import java.util.Map;

public class Color {

    private static Map<String, Color> colors = new HashMap<>();
    //@formatter:off
    public static final Color BLACK        = new Color(0    , 0    , 0          );
    public static final Color SILVER       = new Color(192  , 192  , 192        );
    public static final Color GRAY         = new Color(128  , 128  , 128        );
    public static final Color WHITE        = new Color(255  , 255  , 255        );
    public static final Color MAROON       = new Color(128  , 0    , 0          );
    public static final Color RED          = new Color(255  , 0    , 0          );
    public static final Color PURPLE       = new Color(128  , 0    , 128        );
    public static final Color FUCHSIA      = new Color(255  , 0    , 255        );
    public static final Color GREEN        = new Color(0    , 128  , 0          );
    public static final Color LIME         = new Color(0    , 255  , 0          );
    public static final Color OLIVE        = new Color(128  , 128  , 0          );
    public static final Color YELLOW       = new Color(255  , 255  , 0          );
    public static final Color NAVY         = new Color(0    , 0    , 128        );
    public static final Color BLUE         = new Color(0    , 0    , 255        );
    public static final Color TEAL         = new Color(0    , 128  , 128        );
    public static final Color AQUA         = new Color(0    , 255  , 255        );
    public static final Color TRANSPARENT  = new Color(0f   , 0f   , 0f    , 0f );
    //@formatter:on

    static {
        colors.put("black", BLACK);
        colors.put("silver", SILVER);
        colors.put("gray", GRAY);
        colors.put("white", WHITE);
        colors.put("maroon", MAROON);
        colors.put("red", RED);
        colors.put("purple", PURPLE);
        colors.put("fuchsia", FUCHSIA);
        colors.put("green", GREEN);
        colors.put("lime", LIME);
        colors.put("olive", OLIVE);
        colors.put("yellow", YELLOW);
        colors.put("navy", NAVY);
        colors.put("blue", BLUE);
        colors.put("teal", TEAL);
        colors.put("aqua", AQUA);
        colors.put("transparent", TRANSPARENT);
    }

    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Color(int red, int green, int blue, int alpha) {
        this(red, green, blue, alpha / 255f);
    }

    public Color(int red, int green, int blue, float alpha) {
        this(red / 255f, green / 255f, blue / 255f, alpha);
    }

    public Color(float red, float green, float blue) {
        this(red, green, blue, 1f);
    }

    public Color(int red, int green, int blue) {
        this(red / 255f, green / 255f, blue / 255f, 1f);
    }

    public static Color getColorByName(String name) {
        return colors.get(name);
    }

    /**
     * Allowed to parse hex strings in RGB/RRGGBB/RRGGBBAA format
     *
     * @param value hex value
     * @return color
     */
    public static Color parseHexString(String value) {
        int hex = Integer.parseInt(value, 16);
        switch (value.length()) {
            case 8:
                return new Color((hex >> 24) & 0xFF,
                        (hex >> 16) & 0xFF,
                        (hex >> 8) & 0xFF,
                        hex & 0xFF);
            case 6:
                return new Color(
                        (hex >> 16) & 0xFF,
                        (hex >> 8) & 0xFF,
                        hex & 0xFF);
            case 3:
                return new Color(
                        (hex >> 8) & 0xF,
                        (hex >> 4) & 0xF,
                        hex & 0xF);
            default:
                return null;
        }
    }

    /**
     * Color expression is color represented by three or four integer values divided by comma 'R, G, B' or 'R, G, B, A'
     *
     * @param colorExpression color expression
     * @return color if able to parse or null.
     */
    public static Color parseColorString(String colorExpression) {
        var values = colorExpression.split(",");
        int length = values.length;
        if (length == 3 || length == 4) {
            int r = Integer.parseInt(values[0].trim());
            int g = Integer.parseInt(values[1].trim());
            int b = Integer.parseInt(values[2].trim());
            if (length == 3) {
                return new Color(r, g, b);
            } else {
                int a = Integer.parseInt(values[3].trim());
                return new Color(r, g, b, a);
            }

        }
        throw new IllegalArgumentException("Color expression should look like 'R, G, B' or 'R, G, B, A' but was '" + colorExpression + "'");
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public float getAlpha() {
        return alpha;
    }

    @Override
    public String toString() {
        if (alpha != 1)
            return String.format("Color(%f, %f, %f, %f)", red, green, blue, alpha);
        return String.format("Color(%f, %f, %f)", red, green, blue);
    }


}
