package com.spinyowl.spinygui.backend.opengl32.service.internal;

import com.spinyowl.spinygui.backend.core.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.backend.core.event.processor.SystemEventProcessorProvider;
import com.spinyowl.spinygui.backend.core.renderer.MasterRenderer;
import com.spinyowl.spinygui.backend.core.renderer.MasterRendererProvider;
import com.spinyowl.spinygui.backend.opengl32.service.SpinyGuiOpenGL32WindowService;
import com.spinyowl.spinygui.core.api.Window;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.system.context.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.opengl.GL11.*;

public class SpinyGuiOpenGL32ServiceThread {
    private static final Log LOGGER = LogFactory.getLog(SpinyGuiOpenGL32ServiceThread.class);

    private Thread thread;

    private AtomicBoolean destroyed = new AtomicBoolean(false);
    private AtomicBoolean started = new AtomicBoolean(false);

    private volatile boolean failed;

    private volatile boolean initialized;
    private volatile boolean alive;
    private volatile boolean running;

    private Queue<FutureTask<?>> tasks = new LinkedBlockingQueue<>();

    private MasterRenderer renderer = MasterRendererProvider.getRenderer();
    private long hiddenContext;

    public void start() {
        if (destroyed.get()) {
            String message = "Service thread could not be started again when it was destroyed.";
            LOGGER.warn(message);
            throw new IllegalStateException(message);
        }

        if (started.compareAndSet(false, true)) {
            running = true;
            alive = true;
            thread = new Thread(this::service, "SpinyGui OpenGL 3.2 Service Thread");
            thread.setDaemon(true);
            thread.start();
        } else {
            LOGGER.warn("Service thread could not be started twice.");
        }
    }

    private void service() {
        try {
            initialize();
            initialized = true;

            while (running) {
                tick();
            }

            if (destroyed.compareAndSet(false, true)) {
                destroy();
            }
        } catch (Throwable t) {
            LOGGER.warn("Faced with some exception during executing service thread. Trying to destroy service thread.");
            LOGGER.warn("Exception: " + t.getMessage());
            if (destroyed.compareAndSet(failed, true)) {
                try {
                    destroy();
                } catch (Throwable dt) {
                    LOGGER.warn(dt.getMessage());
                }
            }

            alive = false;
            failed = true;

            throw t;
        }
        alive = false;
    }

    private void initialize() {
        boolean initialized = GLFW.glfwInit();

        if (!initialized) throw new RuntimeException("Can't initialize GLFW.");

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        hiddenContext = GLFW.glfwCreateWindow(1, 1, "HIDDEN CONTEXT", 0, 0);
        GLFW.glfwMakeContextCurrent(hiddenContext);
        GL.createCapabilities();

        renderer.initialize();

        LOGGER.info("GLFW initialized: " + initialized);
    }

    private void destroy() {
        renderer.destroy();
        GLFW.glfwDestroyWindow(hiddenContext);
        GLFW.glfwTerminate();
        LOGGER.info("GLFW destroyed.");
    }

    private void tick() {
        updateContext();
        render();
        pollEvents();
        swapBuffers();
        processExecutions();
        processSystemEvents();
        processLibraryEvents();
        updateLayouts();
    }

    private void updateContext() {
    }

    private void render() {
        List<Window> windows = SpinyGuiOpenGL32WindowService.getInstance().getWindows();
        for (Window window : windows) {
            Context context = SpinyGuiOpenGL32WindowService.getInstance().getContext(window);

            updateContext(window, context);

            Vector2ic windowSize = context.getWindowSize();

            glfwMakeContextCurrent(window.getPointer());

            glClearColor(1, 1, 1, 1);
            // Set viewport size
            glViewport(0, 0, windowSize.x(), windowSize.y());
            // Clear screen
            glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            renderer.render(window.getFrame(), context);
        }
    }

    private void updateContext(Window window, Context context) {
        int[] windowWidth = {0}, windowHeight = {0};
        int[] frameBufferWidth = {0}, frameBufferHeight = {0};
        int[] posX = {0}, posY = {0};
        double[] mx = {0}, my = {0};

        GLFW.glfwGetWindowSize(window.getPointer(), windowWidth, windowHeight);
        GLFW.glfwGetFramebufferSize(window.getPointer(), frameBufferWidth, frameBufferHeight);
        GLFW.glfwGetWindowPos(window.getPointer(), posX, posY);
        GLFW.glfwGetCursorPos(window.getPointer(), mx, my);

        context.setWindowSize(windowWidth[0], windowHeight[0]);
        context.setFrameBufferSize(frameBufferWidth[0], frameBufferHeight[0]);
        context.setWindowPos(posX[0], posY[0]);
        context.setCursorPos(mx[0], my[0]);
    }

    private void pollEvents() {
        GLFW.glfwPollEvents();
    }

    private void swapBuffers() {
        List<Long> windowPointers = SpinyGuiOpenGL32WindowService.getInstance().getWindowPointers();
        for (Long p : windowPointers) {
            GLFW.glfwSwapBuffers(p);
        }
    }

    private void processExecutions() {
        FutureTask<?> task;
        while ((task = tasks.poll()) != null) task.run();
    }

    private void processSystemEvents() {
        SystemEventProcessor processor = SystemEventProcessorProvider.getSystemEventProcessor();
        if (processor != null) {
            processor.processEvents();
        }
    }

    private void processLibraryEvents() {
        EventProcessor instance = EventProcessor.getInstance();
        if (instance != null) {
            instance.processEvents();
        }
    }

    private void updateLayouts() {
    }

    public void stop() {
        running = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isDestroyed() {
        return destroyed.get();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isStarted() {
        return started.get();
    }

    public boolean isRunning() {
        return running;
    }

    public FutureTask<Void> addTask(Runnable r) {
        FutureTask<Void> voidFutureTask = new FutureTask<>(r, null);
        addOrExecuteTask(voidFutureTask);
        return voidFutureTask;
    }

    public <T> FutureTask<T> addTask(Callable<T> t) {
        FutureTask<T> futureTask = new FutureTask<>(t);
        addOrExecuteTask(futureTask);
        return futureTask;
    }

    private void addOrExecuteTask(FutureTask<?> futureTask) {
        // need this check to not block any tasks that are created from same thread.
        if (Thread.currentThread() == thread) {
            futureTask.run();
        } else {
            tasks.add(futureTask);
        }
    }

    public <T> T addTaskAndGet(Callable<T> t) {
        try {
            return addTask(t).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warn(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    public void addTaskAndWait(Runnable r) {
        try {
            addTask(r).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warn(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    public long getHiddenContext() {
        return hiddenContext;
    }
}
