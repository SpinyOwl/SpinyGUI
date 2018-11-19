package com.spinyowl.spinygui.core.service;

import com.spinyowl.spinygui.core.Configuration;
import io.github.classgraph.ClassGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ServiceHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceHandler.class.getName());

    private static final ServiceProvider serviceProvider;
    private static final MonitorService monitorService;
    private static final WindowService windowService;

    static {
        serviceProvider = initializeService(ServiceProvider.class, Configuration.SERVICE_PROVIDER.getState());

        if (Configuration.WINDOW_SERVICE.getState() != null) {
            windowService = initializeService(WindowService.class, Configuration.WINDOW_SERVICE.getState());
        } else {
            windowService = serviceProvider.getWindowService();
        }

        if (Configuration.MONITOR_SERVICE.getState() != null) {
            monitorService = initializeService(MonitorService.class, Configuration.MONITOR_SERVICE.getState());
        } else {
            monitorService = serviceProvider.getMonitorService();
        }
    }

    public static MonitorService getMonitorService() {
        return monitorService;
    }

    public static WindowService getWindowService() {
        return windowService;
    }

    private static <T> T initializeService(Class<T> serviceClass, String implementationClass) {
        T instance = null;

        try (var scanResult = new ClassGraph().enableAllInfo().scan()) {
            // get all subclasses
            String serviceClassName = serviceClass.getCanonicalName();

            List<Class<?>> spinyguiClassRefs;
            if (serviceClass.isInterface()) {
                spinyguiClassRefs = scanResult.getClassesImplementing(serviceClassName).loadClasses();
            } else {
                spinyguiClassRefs = scanResult.getSubclasses(serviceClassName).loadClasses();
            }
            // check if found implementations.
            if (spinyguiClassRefs != null && !spinyguiClassRefs.isEmpty()) {
                // log existing implementations
                if (LOGGER.isDebugEnabled()) {
                    for (Class<?> classRef : spinyguiClassRefs) {
                        LOGGER.debug(String.format("%s class implementation found: %s", serviceClass.getSimpleName(), classRef.getName()));
                    }
                }

                //get property

                if (implementationClass != null) {
                    LOGGER.debug("Trying to load specified implementation: '" + implementationClass + "'.");
                    for (Class<?> aClass : spinyguiClassRefs) {
                        if (implementationClass.equals(aClass.getName()) || implementationClass.equals(aClass.getSimpleName())) {
                            instance = createInstance((Class<T>) aClass);
                        }
                    }

                    if (instance == null) {
                        LOGGER.debug("Specified implementation '" + implementationClass + "' not found.");
                    } else {
                        LOGGER.debug("Specified implementation '" + implementationClass + "' loaded.");
                    }
                } else {
                    instance = createInstance((Class<T>) spinyguiClassRefs.get(0));
                }
            }
        }
        return instance;
    }

    private static <T> T createInstance(Class<T> aClass2) {
        T instance = null;
        try {
            instance = aClass2.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return instance;
    }
}
