package org.spinyowl.spinygui.backend.opengl32.service;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spinyowl.spinygui.core.api.Monitor;
import org.spinyowl.spinygui.core.api.Window;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpinyGuiOpenGL32Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpinyGuiOpenGL32Service.class);
    private static final SpinyGuiOpenGL32Service INSTANCE = new SpinyGuiOpenGL32Service();

    private AtomicBoolean started = new AtomicBoolean(false);
    private ExecutorService glfwService;

    private SpinyGuiOpenGL32Service() {
    }

    public static SpinyGuiOpenGL32Service getInstance() {
        return INSTANCE;
    }

    private void check() {
        if (!started.get()) {
            throw new IllegalStateException("Service should be started before executing any service methods!");
        }
    }

    protected void startService() {
        if (started.compareAndSet(false, true)) {
            glfwService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                    r -> new Thread(r) {{
                        this.setDaemon(true);
                    }}
            );
            boolean initialized = GLFW.glfwInit();
            LOGGER.info("GLFW initialized: " + initialized);
        }
    }

    protected void stopService() {
        if (started.compareAndSet(true, false)) {
            glfwService.shutdown();
        }
    }

    public Monitor getPrimaryMonitor() {
        check();
        try {
            return (glfwService.submit(MonitorService::getPrimaryMonitor).get());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error occured during creating Monitor instance.", e);
            return null;
        }
    }


    public List<Monitor> getMonitors() {
        check();

        try {
            return (glfwService.submit(MonitorService::getMonitors).get());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error occured during creating Monitor instances.", e);
            return null;
        }
    }

    public Window createWindow(int width, int height, String title, Monitor monitor) {
        check();
        return null;
    }

    public void destroyWindow(Window window) {
        check();

    }

    public List<Window> getWindows() {
        check();
        return null;
    }
}
