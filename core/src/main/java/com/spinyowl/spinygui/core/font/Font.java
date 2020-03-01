package com.spinyowl.spinygui.core.font;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@RequiredArgsConstructor
public class Font {

    private static Map<FontKey, Font> fonts = new ConcurrentHashMap<>();

    @NonNull
    private final String name;

    @NonNull
    private final FontStyle style;

    @NonNull
    private final FontWidth width;

    @NonNull
    private final FontWeight weight;

    @NonNull
    private final String path;

    public Font(String name, String path) {
        this(name, FontStyle.NORMAL, FontWidth.NORMAL, FontWeight.NORMAL, path);
    }

    public Font(String name, FontStyle style, String path) {
        this(name, style, FontWidth.NORMAL, FontWeight.NORMAL, path);
    }

    public static void addFont(Font font) {
        Objects.requireNonNull(font);

        var key = new FontKey(font.getName(), font.getStyle(), font.getWidth(), font.getWeight());

        if (fonts.containsKey(key)) {
            log.warn("Font '{}' will be replaced.", font.getName());
        }
        fonts.put(key, font);
    }

    /**
     * Search for fonts with specified font family name.
     *
     * @param name font family name.
     * @return obtained font.
     */
    public static List<Font> getFonts(String name) {
        return getFonts(name, null, null, null);
    }

    /**
     * Search for fonts with specified parameters. Any parameter could be nullable. In this case
     * this parameter will not be used during search.
     *
     * @param name   font family name.
     * @param weight font weight
     * @return obtained font.
     */
    public static List<Font> getFonts(String name, FontWeight weight) {
        return getFonts(name, null, weight, null);
    }

    /**
     * Search for fonts with specified parameters. Any parameter could be nullable. In this case
     * this parameter will not be used during search.
     *
     * @param name   font family name.
     * @param style  font style
     * @param weight font weight
     * @return obtained font.
     */
    public static List<Font> getFonts(String name, FontStyle style, FontWeight weight) {
        return getFonts(name, style, weight, null);
    }

    /**
     * Search for fonts with specified parameters. Any parameter could be nullable. In this case
     * this parameter will not be used during search.
     *
     * @param name  font family name.
     * @param style font style
     * @return obtained font.
     */
    public static List<Font> getFonts(String name, FontStyle style) {
        return getFonts(name, style, null, null);
    }

    /**
     * Search for fonts with specified parameters. Any parameter could be nullable. In this case
     * this parameter will not be used during search.
     *
     * @param name   font family name.
     * @param style  font style
     * @param width  font width
     * @param weight font weight
     * @return obtained font.
     */
    public static List<Font> getFonts(
        String name, FontStyle style, FontWeight weight, FontWidth width
    ) {
        Stream<FontKey> stream = fonts.keySet().stream();
        if (name != null) {
            stream = stream.filter(k -> name.equalsIgnoreCase(k.getName()));
        }
        if (style != null) {
            stream = stream.filter(k -> style.equals(k.getStyle()));
        }
        if (width != null) {
            stream = stream.filter(k -> width.equals(k.getWidth()));
        }
        if (weight != null) {
            stream = stream.filter(k -> weight.equals(k.getWeight()));
        }
        return stream.map(fonts::get).collect(Collectors.toList());
    }

    @Data
    @RequiredArgsConstructor
    private static class FontKey {

        @NonNull
        private final String name;

        @NonNull
        private final FontStyle style;

        @NonNull
        private final FontWidth width;

        @NonNull
        private final FontWeight weight;
    }
}
