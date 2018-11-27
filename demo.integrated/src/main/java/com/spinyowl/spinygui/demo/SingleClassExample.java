//package com.spinyowl.spinygui.demo;
//
//import com.spinyowl.spinygui.backend.core.context.Context;
//import com.spinyowl.spinygui.backend.core.event.processor.SystemEventProcessor;
//import com.spinyowl.spinygui.backend.core.renderer.Renderer;
//import com.spinyowl.spinygui.backend.glfwutil.callback.CallbackKeeper;
//import com.spinyowl.spinygui.backend.glfwutil.callback.DefaultCallbackKeeper;
//import com.spinyowl.spinygui.core.animation.Animator;
//import com.spinyowl.spinygui.core.component.Frame;
//import com.spinyowl.spinygui.core.component.base.Container;
//import com.spinyowl.spinygui.core.event.processor.EventProcessor;
//import com.spinyowl.spinygui.core.layout.LayoutManager;
//import org.lwjgl.glfw.GLFW;
//import org.lwjgl.glfw.GLFWKeyCallbackI;
//import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
//import org.lwjgl.opengl.GL;
//
//import java.io.IOException;
//import java.util.List;
//
//import static org.lwjgl.glfw.GLFW.*;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.system.MemoryUtil.NULL;
//
//public class SingleClassExample {
//
//    public static final int WIDTH = 400;
//    public static final int HEIGHT = 200;
//    private static volatile boolean running = false;
//
//    public static void main(String[] args) throws IOException {
//        System.setProperty("joml.nounsafe", Boolean.TRUE.toString());
//        System.setProperty("java.awt.headless", Boolean.TRUE.toString());
//        if (!GLFW.glfwInit()) {
//            throw new RuntimeException("Can't initialize GLFW");
//        }
//        long window = glfwCreateWindow(WIDTH, HEIGHT, "Single Class Example", NULL, NULL);
//        glfwShowWindow(window);
//
//        glfwMakeContextCurrent(window);
//        GL.createCapabilities();
//        glfwSwapInterval(0);
//
//        // Firstly we need to create frame component for window.
//        Frame frame = new Frame(WIDTH, HEIGHT);
//        // we can add elements here or on the fly
//        createGuiElements(frame);
//
//        // We need to create spinygui context which shared by renderer and event processor.
//        // Also we need to pass event processor for ui events such as click on component, key typing and etc.
//        Context context = new Context(window);
//
//        // We need to create callback keeper which will hold all of callbacks.
//        // These callbacks will be used in initialization of system event processor
//        // (will be added callbacks which will push system events to event queue and after that processed by SystemEventProcessor)
//        CallbackKeeper keeper = new DefaultCallbackKeeper();
//
//        // register callbacks for window. Note: all previously binded callbacks will be unbinded.
//        CallbackKeeper.registerCallbacks(window, keeper);
//
//        GLFWKeyCallbackI glfwKeyCallbackI = (w1, key, code, action, mods) -> running = !(key == GLFW_KEY_ESCAPE && action != GLFW_RELEASE);
//        GLFWWindowCloseCallbackI glfwWindowCloseCallbackI = w -> running = false;
//
//        // if we want to create some callbacks for system events you should create and put them to keeper
//        //
//        // Wrong:
//        // glfwSetKeyCallback(window, glfwKeyCallbackI);
//        // glfwSetWindowCloseCallback(window, glfwWindowCloseCallbackI);
//        //
//        // Right:
//        keeper.getChainKeyCallback().add(glfwKeyCallbackI);
//        keeper.getChainWindowCloseCallback().add(glfwWindowCloseCallbackI);
//
//        // Event processor for system events. System events should be processed and translated to gui events.
//        SystemEventProcessor systemEventProcessor = SystemEventProcessor.getInstance();
//
//
//        // Also we need to create renderer provider
//        // and create renderer which will render our ui components.
//        Renderer renderer = new NvgRenderer();
//
//        // Initialization finished, so we can start render loop.
//        running = true;
//
//        // Everything can be done in one thread as well as in separated threads.
//        // Here is one-thread example.
//
//        // before render loop we need to initialize renderer
//        renderer.initialize();
//
//        while (running) {
//
//            // Before rendering we need to update context with window size and window framebuffer size
//            //{
//            //    int[] windowWidth = {0}, windowHeight = {0};
//            //    GLFW.glfwGetWindowSize(window, windowWidth, windowHeight);
//            //    int[] frameBufferWidth = {0}, frameBufferHeight = {0};
//            //    GLFW.glfwGetFramebufferSize(window, frameBufferWidth, frameBufferHeight);
//            //    int[] posX = {0}, posY = {0};
//            //    GLFW.glfwGetWindowPos(window, posX, posY);
//            //    double[] mx = {0}, my = {0};
//            //    GLFW.glfwGetCursorPos(window, mx, my);
//            //
//            //    context.update(windowWidth[0], windowHeight[0],
//            //            frameBufferWidth[0], frameBufferHeight[0],
//            //            posX[0], posY[0],
//            //            mx[0], my[0]
//            //    );
//            //}
//
//            // Also we can do it in one line
//            context.updateGlfwWindow();
//            Vector2i windowSize = context.getFramebufferSize();
//
//            glClearColor(1, 1, 1, 1);
//            // Set viewport size
//            glViewport(0, 0, windowSize.x, windowSize.y);
//            // Clear screen
//            glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
//
//            // render frame
//            renderer.render(frame, context);
//
//            // poll events to callbacks
//            glfwPollEvents();
//            glfwSwapBuffers(window);
//
//            // Now we need to process events. Firstly we need to process system events.
//            systemEventProcessor.processEvents(frame, context);
//
//            // When system events are translated to GUI events we need to process them.
//            // This event processor calls listeners added to ui components
//            EventProcessor.getInstance().processEvents();
//
//            // When everything done we need to relayout components.
//            LayoutManager.getInstance().layout(frame);
//
//            // Run animations. Should be also called cause some components use animations for updating state.
//            Animator.getInstance().runAnimations();
//        }
//
//        // And when rendering is ended we need to destroy renderer
//        renderer.destroy();
//
//        glfwDestroyWindow(window);
//        glfwTerminate();
//    }
//
//    private static void createGuiElements(Frame frame) {
////        // Set background color for frame
////        frame.getContainer().getStyle().getBackground().setColor(ColorConstants.lightBlue());
////
////        Button button = new Button("Add components", 20, 20, 160, 30);
////        SimpleLineBorder border = new SimpleLineBorder(ColorConstants.black(), 1);
////        button.getStyle().setBorder(border);
////
////        boolean[] added = {false};
////        button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
////            if (!added[0]) {
////                added[0] = true;
////                for (Container c : generateOnFly()) {
////                    frame.getContainer().add(c);
////                }
////            }
////        });
////
////        button.getListenerMap().addListener(CursorEnterEvent.class, (CursorEnterEventListener) System.out::println);
////
////        frame.getContainer().add(button);
//    }
//
//    private static List<Container> generateOnFly() {
////        List<Container> list = new ArrayList<>();
////
////        Label label = new Label(20, 60, 200, 20);
////        label.getTextState().setText("Generated on fly label");
////        label.getTextState().setTextColor(ColorConstants.red());
////
////        RadioButtonGroup group = new RadioButtonGroup();
////        RadioButton radioButtonFirst = new RadioButton("First", 20, 90, 200, 20);
////        RadioButton radioButtonSecond = new RadioButton("Second", 20, 110, 200, 20);
////
////        radioButtonFirst.setRadioButtonGroup(group);
////        radioButtonSecond.setRadioButtonGroup(group);
////
////        list.add(label);
////        list.add(radioButtonFirst);
////        list.add(radioButtonSecond);
////
////        return list;
//    }
//}