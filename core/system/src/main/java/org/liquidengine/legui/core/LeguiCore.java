package org.liquidengine.legui.core;

import io.github.classgraph.ClassGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liquidengine.legui.core.api.Monitor;
import org.liquidengine.legui.core.api.Window;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class LeguiCore {

    private static final Logger LOGGER = LogManager.getLogger();
    private static LeguiCore insance;

    static LeguiCore getInstance() {
        return SystemInitializer.instance;
    }

    public static Window createWindow(int width, int height, String title, boolean fullscreen) {
        return getInstance()._createWindow(width, height, title, fullscreen);
    }

    public static Monitor getPrimaryMonitor() {
        return getInstance()._getPrimaryMonitor();
    }

    public static List<Monitor> getMonitors() {
        return getInstance()._getMonitors();
    }

    protected abstract Monitor _getPrimaryMonitor();

    protected abstract List<Monitor> _getMonitors();

    protected abstract Window _createWindow(int width, int height, String title, boolean fullscreen);

    private static final class SystemInitializer {
        private static final LeguiCore instance;

        static {
            try (var scanResult = new ClassGraph().enableAllInfo().scan()) {
                LeguiCore core = null;
                var toolkitClasses = scanResult.getSubclasses("org.liquidengine.legui.core.LeguiCore");
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
                                core = (LeguiCore) aClass.getDeclaredConstructor().newInstance();
                            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        }
                    }
                }
                instance = core;
                if (core == null) {
                    throw new IllegalStateException("Can't find any appropriate toolkit implementations.");
                }
            }
        }
    }
}
