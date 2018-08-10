package org.liquidengine.legui.backend;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by ShchAlexander on 09.08.2018.
 */
public abstract class Toolkit {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void loadLibraries() {
        LOGGER.debug("Load libraries enter.");
        getToolkit().load();
        LOGGER.debug("Load libraries exit.");
    }

    private static class ToolkitInitializer {
        private static Toolkit toolkit;

        static {
            try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
                ClassInfoList toolkitClasses = scanResult.getSubclasses("org.liquidengine.legui.backend.Toolkit");
                List<Class<?>> toolkitClassRefs = toolkitClasses.loadClasses();
                if (toolkitClassRefs != null && !toolkitClassRefs.isEmpty()) {
                    for (Class<?> aClass : toolkitClassRefs) {
                        LOGGER.debug("Toolkit class implementation found: " + aClass.getName());
                    }
                    for (var i = 0; i < toolkitClassRefs.size() && toolkit == null; i++) {
                        var aClass = toolkitClassRefs.get(i);
                        try {
                            toolkit = (Toolkit) aClass.getDeclaredConstructor().newInstance();
                        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                }
                if (toolkit == null) {
                    throw new IllegalStateException("Can't find any appropriate toolkit implementations");
                }
            }
        }
    }

    protected abstract void load();

    private static Toolkit getToolkit() {
        return ToolkitInitializer.toolkit;
    }
}
