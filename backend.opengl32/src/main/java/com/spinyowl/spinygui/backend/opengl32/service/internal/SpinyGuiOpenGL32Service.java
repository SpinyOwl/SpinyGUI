package com.spinyowl.spinygui.backend.opengl32.service.internal;

import com.spinyowl.spinygui.backend.core.renderer.MasterRendererProvider;
import com.spinyowl.spinygui.backend.opengl32.renderer.NvgMasterRenderer;
import com.spinyowl.spinygui.backend.opengl32.service.SpinyGuiOpenGL32WindowService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpinyGuiOpenGL32Service {

    private static final Log LOGGER = LogFactory.getLog(SpinyGuiOpenGL32Service.class);
    private static final SpinyGuiOpenGL32Service INSTANCE = new SpinyGuiOpenGL32Service();

    private AtomicBoolean started = new AtomicBoolean(false);

    private SpinyGuiOpenGL32ServiceThread serviceThread;

    private SpinyGuiOpenGL32Service() {
    }

    public static SpinyGuiOpenGL32Service getInstance() {
        return INSTANCE;
    }


    public void startService() {
        if (started.compareAndSet(false, true)) {
            MasterRendererProvider.setRenderer(new NvgMasterRenderer());
            // register shutdown hook to release resources.
            Runtime.getRuntime().addShutdownHook(new Thread(this::stopService, "SpinyGui OpenGL 3.2 Service Thread Destroyer"));
            // create task executor.
            serviceThread = new SpinyGuiOpenGL32ServiceThread();
            serviceThread.start();
        }
    }

    public long getHiddenContext() {
        return serviceThread.getHiddenContext();
    }

    public void stopService() {
        LOGGER.info("WAITING FOR ALL WINDOWS ARE CLOSED");
        while (!SpinyGuiOpenGL32WindowService.getInstance().getWindows().isEmpty()) {
            Thread.yield();
        }

        LOGGER.info("STOPPING THE SERVICE");
        if (started.compareAndSet(true, false)) {
            Future<?> submit = serviceThread.addTask(this::destroyAllResources);
            while (!submit.isDone()) {
                Thread.yield();
            }
            serviceThread.stop();
        }
    }

    private void destroyAllResources() {
    }

    public FutureTask<Void> addTask(Runnable r) {
        return serviceThread.addTask(r);
    }

    public <T> FutureTask<T> addTask(Callable<T> t) {
        return serviceThread.addTask(t);
    }

    public <T> T addTaskAndGet(Callable<T> t) {
        return serviceThread.addTaskAndGet(t);
    }

    public void addTaskAndWait(Runnable r) {
        serviceThread.addTaskAndWait(r);
    }
}
