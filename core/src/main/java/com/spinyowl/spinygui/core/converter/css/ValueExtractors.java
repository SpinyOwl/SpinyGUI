package com.spinyowl.spinygui.core.converter.css;

import com.spinyowl.spinygui.core.converter.css.extractor.ColorValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.IntegerExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.LengthValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.UnitValueExtractor;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ValueExtractors {

    private static final Map<Class, ValueExtractor> valueExtractorMap = new ConcurrentHashMap<>();

    static {
        add(Color.class, new ColorValueExtractor());
        add(Unit.class, new UnitValueExtractor());
        add(Length.class, new LengthValueExtractor());
        add(Integer.class, new IntegerExtractor());
    }

    private ValueExtractors() {
    }

    public static <T> void add(Class<T> targetValueClass,
        ValueExtractor<T> valueExtractor) {
        valueExtractorMap.put(targetValueClass, valueExtractor);
    }

    public static <T> ValueExtractor<T> of(Class<T> targetValueClass) {
        return valueExtractorMap.get(targetValueClass);
    }


}
