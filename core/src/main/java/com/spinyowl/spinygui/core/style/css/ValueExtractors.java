package com.spinyowl.spinygui.core.style.css;

import com.spinyowl.spinygui.core.style.css.extractor.ColorValueExtractor;
import com.spinyowl.spinygui.core.style.css.extractor.UnitValueExtractor;
import com.spinyowl.spinygui.core.style.types.Color;
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
