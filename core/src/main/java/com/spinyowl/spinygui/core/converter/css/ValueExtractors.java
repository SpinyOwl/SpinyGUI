package com.spinyowl.spinygui.core.converter.css;

import com.spinyowl.spinygui.core.converter.css.extractor.ColorValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.LengthValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.UnitValueExtractor;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ValueExtractors {

    private Map<Class, ValueExtractor> valueExtractorMap = new ConcurrentHashMap<>();

    //@formatter:off
    /** Hidden constructor. */
    private ValueExtractors() {
        addValueExtractor(Color.class, new ColorValueExtractor());
        addValueExtractor(Unit.class, new UnitValueExtractor());
        addValueExtractor(Length.class, new LengthValueExtractor());
    }

    /** Getter for instance. */
    public static ValueExtractors getInstance() { return ValueExtractorsHolder.INSTANCE; }

    public <T> void addValueExtractor(Class<T> targetValueClass, ValueExtractor<T> valueExtractor) {
        valueExtractorMap.put(targetValueClass, valueExtractor);
    }
    //@formatter:on

    public <T> ValueExtractor<T> getValueExtractor(Class<T> targetValueClass) {
        return valueExtractorMap.get(targetValueClass);
    }

    /**
     * Instance holder.
     */
    private static class ValueExtractorsHolder {
        private static final ValueExtractors INSTANCE = new ValueExtractors();
    }

}
