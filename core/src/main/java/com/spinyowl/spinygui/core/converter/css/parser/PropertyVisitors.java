package com.spinyowl.spinygui.core.converter.css.parser;

import com.spinyowl.spinygui.core.converter.css.parser.visitor.BackgroundVisitor;
import com.spinyowl.spinygui.core.converter.css.parser.visitor.LengthVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.spinyowl.spinygui.core.style.css.SupportedCssProperties.*;

public final class PropertyVisitors {
    private Map<String, CSS3BaseVisitor> visitorMap = new HashMap<>();

    private PropertyVisitors() {
        LengthVisitor lengthVisitor = new LengthVisitor();
        //@formatter:off
        setVisitorFor(BACKGROUND,  new BackgroundVisitor() );
        setVisitorFor(TOP,         lengthVisitor           );
        setVisitorFor(BOTTOM,      lengthVisitor           );
        setVisitorFor(LEFT,        lengthVisitor           );
        setVisitorFor(RIGHT,       lengthVisitor           );
        setVisitorFor(WIDTH,       lengthVisitor           );
        setVisitorFor(HEIGHT,      lengthVisitor           );
        setVisitorFor(MIN_WIDTH,   lengthVisitor           );
        setVisitorFor(MIN_HEIGHT,  lengthVisitor           );
        setVisitorFor(MAX_WIDTH,   lengthVisitor           );
        setVisitorFor(MAX_HEIGHT,  lengthVisitor           );
        //@formatter:on
    }

    /**
     * Getter for instance
     */
    public static PropertyVisitors getInstance() {
        return PropertyVisitorsHolder.INSTANCE;
    }

    public CSS3BaseVisitor getVisitorFor(String propertyName) {
        return visitorMap.get(propertyName);
    }

    public void setVisitorFor(String propertyName, CSS3BaseVisitor visitor) {
        Objects.requireNonNull(propertyName);
        visitorMap.put(propertyName, visitor);
    }

    /**
     * Instance holder.
     */
    private static class PropertyVisitorsHolder {
        private static final PropertyVisitors INSTANCE = new PropertyVisitors();
    }
}
