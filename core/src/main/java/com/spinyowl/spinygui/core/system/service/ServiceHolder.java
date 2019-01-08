package com.spinyowl.spinygui.core.system.service;

import com.spinyowl.spinygui.core.Configuration;
import io.github.classgraph.ClassGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ServiceHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceHolder.class.getName());

    private static final ServiceProvider SERVICE_PROVIDER;
    private static final MonitorService MONITOR_SERVICE;
    private static final WindowService WINDOW_SERVICE;
    private static final ClipboardService CLIPBOARD_SERVICE;
    private static final RendererFactoryService RENDERER_FACTORY_SERVICE;

    static {
        SERVICE_PROVIDER = initializeService(ServiceProvider.class, Configuration.SERVICE_PROVIDER.getState());

        if (Configuration.WINDOW_SERVICE.getState() != null) {
            WINDOW_SERVICE = initializeService(WindowService.class, Configuration.WINDOW_SERVICE.getState());
        } else {
            WINDOW_SERVICE = SERVICE_PROVIDER.getWindowService();
        }

        if (Configuration.MONITOR_SERVICE.getState() != null) {
            MONITOR_SERVICE = initializeService(MonitorService.class, Configuration.MONITOR_SERVICE.getState());
        } else {
            MONITOR_SERVICE = SERVICE_PROVIDER.getMonitorService();
        }

        CLIPBOARD_SERVICE = SERVICE_PROVIDER.getClipboardService();

        RENDERER_FACTORY_SERVICE = SERVICE_PROVIDER.getRendererFactoryService();
    }

    public static MonitorService getMonitorService() {
        return MONITOR_SERVICE;
    }

    public static WindowService getWindowService() {
        return WINDOW_SERVICE;
    }

    public static ClipboardService getClipboardService() {
        return CLIPBOARD_SERVICE;
    }

    public static RendererFactoryService getRendererFactoryService() {
        return RENDERER_FACTORY_SERVICE;
    }

    private static <T> T initializeService(Class<T> serviceClass, String implementationClass) {
        T instance = null;

        try (var scanResult = new ClassGraph().enableAllInfo().scan()) {
            // get all subclasses
            String serviceClassName = serviceClass.getName();

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

    private static <T> T createInstance(Class<T> clazz) {
        T instance = null;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return instance;
    }

    public interface ServiceProvider {

        MonitorService getMonitorService();

        WindowService getWindowService();

        ClipboardService getClipboardService();

        RendererFactoryService getRendererFactoryService();

    }
}
