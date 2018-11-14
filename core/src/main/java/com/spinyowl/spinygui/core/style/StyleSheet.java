package com.spinyowl.spinygui.core.style;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class StyleSheet {
    private Map<String, List<Style>> stlyesheet = new ConcurrentHashMap<>();

    private List<Style> createValueList() {
        return new CopyOnWriteArrayList<>();
    }

    public List<Style> getStyleList(String rule) {
        List<Style> styles = stlyesheet.computeIfAbsent(rule, k -> createValueList());
        return new ArrayList<>(styles);
    }
}
