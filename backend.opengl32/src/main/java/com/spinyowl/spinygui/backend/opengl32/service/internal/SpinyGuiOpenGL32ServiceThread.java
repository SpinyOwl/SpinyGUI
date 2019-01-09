package com.spinyowl.spinygui.backend.opengl32.service.internal;

import com.spinyowl.spinygui.backend.core.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.backend.opengl32.service.SpinyGuiOpenGL32WindowService;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpinyGuiOpenGL32ServiceThread {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpinyGuiOpenGL32ServiceThread.class);

    private Thread thread;

    private AtomicBoolean destroyed = new AtomicBoolean(false);
    private AtomicBoolean started = new AtomicBoolean(false);

    private volatile boolean failed;

    private volatile boolean initialized;
    private volatile boolean alive;
    private volatile boolean running;

    private Queue<FutureTask<?>> tasks = new LinkedBlockingQueue<>();

    public void start() {
        if (destroyed.get()) {
            String message = "Service thread could not be started again when it was destroyed.";
            LOGGER.error(message);
            throw new IllegalStateException(message);
        }

        if (started.compareAndSet(false, true)) {
            running = true;
            alive = true;
            thread = new Thread(this::service, "SpinyGui OpenGL 3.2 Service Thread") {{
                setDaemon(true);
            }};
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
            LOGGER.error("Faced with some exception during executing service thread. Trying to destroy service thread.");
            LOGGER.error("Exception: " + t.getMessage(), t);
            if (destroyed.compareAndSet(failed, true)) {
                try {
                    destroy();
                } catch (Throwable dt) {
                    LOGGER.error(dt.getMessage(), dt);
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
        LOGGER.info("GLFW initialized: " + initialized);
    }

    private void destroy() {
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
        FutureTask<?> task = null;
        while ((task = tasks.poll()) != null) task.run();
    }

    private void processSystemEvents() {
        SystemEventProcessor processor = SystemEventProcessor.getInstance();
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
            LOGGER.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    public void addTaskAndWait(Runnable r) {
        try {
            addTask(r).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

}
