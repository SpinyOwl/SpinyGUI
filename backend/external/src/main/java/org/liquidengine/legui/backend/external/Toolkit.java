package org.liquidengine.legui.backend.external;

import io.github.classgraph.ClassGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liquidengine.legui.core.system.Configuration;

import java.lang.reflect.InvocationTargetException;

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
            try (var scanResult = new ClassGraph().enableAllInfo().scan()) {
                var toolkitClasses = scanResult.getSubclasses("org.liquidengine.legui.backend.Toolkit");
                var toolkitClassRefs = toolkitClasses.loadClasses();
                if (toolkitClassRefs != null && !toolkitClassRefs.isEmpty()) {
                    for (var toolkitClass : toolkitClassRefs) {
                        LOGGER.debug("Toolkit class implementation found: " + toolkitClass.getName());
                    }
                    var backendImplementation = Configuration.BACKEND.getState();
                    for (Class<?> aClass : toolkitClassRefs) {
                        if (backendImplementation == null ||
                            backendImplementation.equals(aClass.getName()) ||
                            backendImplementation.equals(aClass.getSimpleName())
                        ) {
                            try {
                                toolkit = (Toolkit) aClass.getDeclaredConstructor().newInstance();
                            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                    }
                }
                if (toolkit == null) {
                    throw new IllegalStateException("Can't find any appropriate toolkit implementations.");
                }
            }
        }
    }

    protected abstract void load();

    private static Toolkit getToolkit() {
        return ToolkitInitializer.toolkit;
    }
}
