package com.spinyowl.spinygui.backend.opengl32.service.internal;

import com.spinyowl.spinygui.backend.opengl32.service.SpinyGuiOpenGL32WindowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpinyGuiOpenGL32Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpinyGuiOpenGL32Service.class);
    private static final SpinyGuiOpenGL32Service INSTANCE = new SpinyGuiOpenGL32Service();

    private AtomicBoolean started = new AtomicBoolean(false);

    private SpinyGuiOpenGL32ServiceThread thread;

    private SpinyGuiOpenGL32Service() {
    }

    public static SpinyGuiOpenGL32Service getInstance() {
        return INSTANCE;
    }


    public void startService() {
        if (started.compareAndSet(false, true)) {

            // register shutdown hook to release resources.
            Runtime.getRuntime().addShutdownHook(new Thread(this::stopService, "SpinyGui OpenGL 3.2 Service Thread Destroyer"));

            // create task executor.
            thread = new SpinyGuiOpenGL32ServiceThread();
            thread.start();
        }
    }

    public void stopService() {
        LOGGER.debug("WAITING FOR ALL WINDOWS ARE CLOSED");
        while (!SpinyGuiOpenGL32WindowService.getInstance().getWindows().isEmpty()) {
            Thread.yield();
        }

        LOGGER.debug("STOPPING THE SERVICE");
        if (started.compareAndSet(true, false)) {
            Future<?> submit = thread.addTask(this::destroyAllResources);
            while (!submit.isDone()) {
                Thread.yield();
            }
            thread.stop();
        }
    }

    private void destroyAllResources() {
    }

    public FutureTask<Void> addTask(Runnable r) {
        return thread.addTask(r);
    }

    public <T> FutureTask<T> addTask(Callable<T> t) {
        return thread.addTask(t);
        }

    public <T> T addTaskAndGet(Callable<T> t) {
        return thread.addTaskAndGet(t);
    }

    public void addTaskAndWait(Runnable r) {
        thread.addTaskAndWait(r);
    }
}
