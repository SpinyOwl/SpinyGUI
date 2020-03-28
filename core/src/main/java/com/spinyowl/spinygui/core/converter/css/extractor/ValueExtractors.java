package com.spinyowl.spinygui.core.converter.css.extractor;

import com.spinyowl.spinygui.core.converter.css.extractor.impl.ColorValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.impl.IntegerExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.impl.LengthValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.impl.UnitValueExtractor;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class keeps all existing value extractors.
 */
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
