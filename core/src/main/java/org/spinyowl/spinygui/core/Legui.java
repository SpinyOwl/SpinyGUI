package org.spinyowl.spinygui.core;

import io.github.classgraph.ClassGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spinyowl.spinygui.core.api.Monitor;
import org.spinyowl.spinygui.core.api.Window;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class SpinyGui {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Creates window with specified resolution (width, height) and title.
     * Creates fullscreen window if monitor specified.
     *
     * @param width the desired width, in screen coordinates, of the window
     * @param height the desired height, in screen coordinates, of the window
     * @param title initial, UTF-8 encoded window title
     * @param monitor monitor to use for fullscreen mode or null for windowed mode.
     * @return new window.
     */
    public static Window createWindow(int width, int height, String title, Monitor monitor) {
        return SystemInitializer.instance.createWindow(width, height, title, monitor);
    }

    /**
     * Destroys provided window.
     *
     * @param window window to destroy.
     */
    public static void destroyWindow(Window window) {
        SystemInitializer.instance.destroyWindow(window);
    }

    /**
     * Returns all existing windows that are alive.
     *
     * @return alive windows.
     */
    public static List<Window> getWindows() {
        return SystemInitializer.instance.getWindows();
    }

    /**
     * Returns primary monitor.
     *
     * @return primary monitor.
     */
    public static Monitor getPrimaryMonitor() {
        return SystemInitializer.instance.getPrimaryMonitor();
    }

    public static List<Monitor> getMonitors() {
        return SystemInitializer.instance.getMonitors();
    }

    private static final class SystemInitializer {
        private static SpinyGuiService instance;

        static {
            initializeSpinyGuiInstance();
        }

        private static void initializeSpinyGuiInstance() {
            try (var scanResult = new ClassGraph().enableAllInfo().scan()) {
                // get all subclasses
                String serviceClassName = SpinyGuiService.class.getCanonicalName();
                var spinyguiClassRefs = scanResult.getClassesImplementing(serviceClassName).loadClasses();
                // check if found implementations.
                if (spinyguiClassRefs != null && !spinyguiClassRefs.isEmpty()) {
                    // log existing implementations
                    if (LOGGER.isDebugEnabled()) {
                        spinyguiClassRefs.stream()
                                .map(lc -> String.format(serviceClassName + " class implementation found: %s", lc.getName()))
                                .forEach(LOGGER::debug);
                    }

                    //get property
                    var beImpl = Configuration.BACKEND.getState();

                    if (beImpl != null) {
                        LOGGER.debug("Trying to load specified implementation: '" + beImpl + "'.");
                        for (Class<?> aClass : spinyguiClassRefs) {
                            if (beImpl.equals(aClass.getName()) || beImpl.equals(aClass.getSimpleName())) {
                                createInstance(aClass);
                            }
                        }
                        if (instance == null) {
                            LOGGER.debug("Specified implementation '" + beImpl + "' not found.");
                        } else {
                            LOGGER.debug("Specified implementation '" + beImpl + "' loaded.");
                        }
                    }

                    if (instance == null) {
                        //TODO: Create backend selection based on system ability to work with opengl/vulkan/etc
                        createInstance(spinyguiClassRefs.get(0));
                    }
                }
                // if no implementation found - throwing exception
                if (instance == null) {
                    throw new IllegalStateException(String.format("Can't find any appropriate %s implementations.", serviceClassName));
                }
            }
        }

        private static void createInstance(Class<?> aClass2) {
            try {
                instance = (SpinyGuiService) aClass2.getDeclaredConstructor().newInstance();
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
