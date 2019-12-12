package com.spinyowl.spinygui.demo;

import com.spinyowl.spinygui.backend.core.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.backend.core.event.processor.SystemEventProcessorProvider;
import com.spinyowl.spinygui.backend.core.renderer.MasterRenderer;
import com.spinyowl.spinygui.backend.glfwutil.callback.CallbackKeeper;
import com.spinyowl.spinygui.backend.glfwutil.callback.DefaultCallbackKeeper;
import com.spinyowl.spinygui.backend.opengl32.renderer.NvgMasterRenderer;
import com.spinyowl.spinygui.backend.opengl32.service.SpinyGuiOpenGL32RendererProviderService;
import com.spinyowl.spinygui.core.Configuration;
import com.spinyowl.spinygui.core.animation.AnimatorProvider;
import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.event.MouseEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.layout.LayoutManager;
import com.spinyowl.spinygui.core.node.Button;
import com.spinyowl.spinygui.core.node.Label;
import com.spinyowl.spinygui.core.node.RadioButton;
import com.spinyowl.spinygui.core.node.RadioButtonGroup;
import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.system.Services;
import com.spinyowl.spinygui.core.system.context.Context;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.spinyowl.spinygui.core.NodeBuilder.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
public class SingleClassExample {

    public static final int WIDTH = 400;
    public static final int HEIGHT = 200;
    private static volatile boolean running = false;

    public static void main(String[] args) throws IOException {
        System.setProperty("joml.nounsafe", Boolean.TRUE.toString());
        System.setProperty("java.awt.headless", Boolean.TRUE.toString());

        // IMPORTANT STEP TO DISABLE SERVICE PROVIDER INITIALIZATION TO USE CUSTOM SERVICE PROVIDER
        // In this case service provider will be initialized with null value
        Configuration.INITIALIZE_SERVICES.setValue(false);
        Services.setTimeService(GLFW::glfwGetTime);
        Services.setRendererProviderService(SpinyGuiOpenGL32RendererProviderService.getInstance());

        // set our own service provider

        if (!GLFW.glfwInit()) {
            throw new RuntimeException("Can't initialize GLFW");
        }
        long window = glfwCreateWindow(WIDTH, HEIGHT, "Single Class Example", NULL, NULL);
        glfwShowWindow(window);

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSwapInterval(0);
// Firstly we need to create frame node for window.
        Frame frame = new Frame();
        // we can add elements here or on the fly
        createGuiElements(frame);

        // We need to create spinygui context which shared by renderer and event processor.
        // Also we need to pass event processor for ui events such as click on node, key typing and etc.
        Context context = new Context(window);

        // We need to create callback keeper which will hold all of callbacks.
        // These callbacks will be used in initialization of system event processor
        // (will be added callbacks which will push system events to event queue and after that processed by SystemEventProcessor)
        CallbackKeeper keeper = new DefaultCallbackKeeper();

        // register callbacks for window. Note: all previously binded callbacks will be unbinded.
        CallbackKeeper.registerCallbacks(window, keeper);

        GLFWKeyCallbackI glfwKeyCallbackI = (w1, key, code, action, mods) -> running = !(key == GLFW_KEY_ESCAPE && action != GLFW_RELEASE);
        GLFWWindowCloseCallbackI glfwWindowCloseCallbackI = w -> running = false;

        // if we want to create some callbacks for system events you should create and put them to keeper
        //
        // Wrong:
        // glfwSetKeyCallback(window, glfwKeyCallbackI);
        // glfwSetWindowCloseCallback(window, glfwWindowCloseCallbackI);
        //
        // Right:
        keeper.getChainKeyCallback().add(glfwKeyCallbackI);
        keeper.getChainWindowCloseCallback().add(glfwWindowCloseCallbackI);

        // Event processor for system events. System events should be processed and translated to gui events.
        SystemEventProcessor systemEventProcessor = SystemEventProcessorProvider.getSystemEventProcessor();
// Also we need to create renderer provider
        // and create renderer which will render our ui components.
        MasterRenderer renderer = new NvgMasterRenderer();

        // Initialization finished, so we can start render loop.
        running = true;

        // Everything can be done in one thread as well as in separated threads.
        // Here is one-thread example.

        // before render loop we need to initialize renderer
        renderer.initialize();

        while (running) {

            // Before rendering we need to update context with window size and window framebuffer size
            {
                int[] windowWidth = {0}, windowHeight = {0};
                int[] frameBufferWidth = {0}, frameBufferHeight = {0};
                int[] posX = {0}, posY = {0};
                double[] mx = {0}, my = {0};

                GLFW.glfwGetWindowSize(window, windowWidth, windowHeight);
                GLFW.glfwGetFramebufferSize(window, frameBufferWidth, frameBufferHeight);
                GLFW.glfwGetWindowPos(window, posX, posY);
                GLFW.glfwGetCursorPos(window, mx, my);

                context.setWindowSize(windowWidth[0], windowHeight[0]);
                context.setFrameBufferSize(frameBufferWidth[0], frameBufferHeight[0]);
                context.setWindowPos(posX[0], posY[0]);
                context.setCursorPos(mx[0], my[0]);
            }

            Vector2ic windowSize = context.getFramebufferSize();

            glClearColor(1, 1, 1, 1);
            // Set viewport size
            glViewport(0, 0, windowSize.x(), windowSize.y());
            // Clear screen
            glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            // render frame
            renderer.render(frame, context);

            // poll events to callbacks
            glfwPollEvents();
            glfwSwapBuffers(window);

            // Now we need to process events. Firstly we need to process system events.
            systemEventProcessor.processEvents();

            // When system events are translated to GUI events we need to process them.
            // This event processor calls listeners added to ui components
            EventProcessor.getInstance().processEvents();

            // When everything done we need to relayout components.
            LayoutManager.getInstance().layout(frame);

            // Run animations. Should be also called cause some components use animations for updating state.
            AnimatorProvider.getAnimator().runAnimations();
        }

        // And when rendering is ended we need to destroy renderer
        renderer.destroy();

        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private static void createGuiElements(Frame frame) {
        Button button = button(text("Add components"));
        AtomicBoolean added = new AtomicBoolean(false);
        button.getListenersFor(MouseEvent.class).add(event -> {
            if (event.getButton() != 0) {
                if (added.compareAndSet(false, true)) {
                    for (Node c : generateOnFly()) {
                        frame.getContainer().add(c);
                    }
                }
            }
        });
        frame.getContainer().add(button);
    }

    private static List<Node> generateOnFly() {
        List<Node> list = new ArrayList<>();

        Label label = label(text("Generated on fly label"));

        RadioButtonGroup group = new RadioButtonGroup();
        RadioButton radioButtonFirst = radioButton(text("first"));
        RadioButton radioButtonSecond = radioButton(text("Second"));

        radioButtonFirst.setRadioButtonGroup(group);
        radioButtonSecond.setRadioButtonGroup(group);

        list.add(label);
        list.add(radioButtonFirst);
        list.add(radioButtonSecond);

        return list;
    }
}