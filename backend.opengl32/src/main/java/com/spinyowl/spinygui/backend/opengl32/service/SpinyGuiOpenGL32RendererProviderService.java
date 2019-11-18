package com.spinyowl.spinygui.backend.opengl32.service;

import com.spinyowl.spinygui.backend.opengl32.renderer.component.DefaultNodeRenderer;
import com.spinyowl.spinygui.core.node.base.Node;
import com.spinyowl.spinygui.core.system.render.NodeRenderer;
import com.spinyowl.spinygui.core.system.service.RendererProviderService;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SpinyGuiOpenGL32RendererProviderService implements RendererProviderService {
    private static final SpinyGuiOpenGL32RendererProviderService INSTANCE = new SpinyGuiOpenGL32RendererProviderService();

    private Map<Class<?>, NodeRenderer> rendererMap;

    private SpinyGuiOpenGL32RendererProviderService() {
        rendererMap = new ConcurrentHashMap<>();
        rendererMap.put(Node.class, new DefaultNodeRenderer());
    }

    public static SpinyGuiOpenGL32RendererProviderService getInstance() {
        return INSTANCE;
    }

    @Override
    public <T> NodeRenderer<T> getRenderer(Class<T> nodeClass) {
        if (Node.class.isAssignableFrom(nodeClass)) {
            Class<? extends Node> e = (Class<? extends Node>) nodeClass;
            return (NodeRenderer<T>) getComponentRenderer(e);
        }
        return null;
    }

    private <C extends Node> NodeRenderer<C> getComponentRenderer(Class<C> componentClass) {
        return this.cycledSearch(componentClass, Node.class, rendererMap, null);
    }

    private <A, B extends A, C> C cycledSearch(Class<B> classToSearch, Class<A> rootClass, Map map, C defaultRenderer) {
        Objects.requireNonNull(classToSearch);
        Objects.requireNonNull(rootClass);
        Objects.requireNonNull(map);
        C renderer = null;
        Class cClass = classToSearch;
        while (renderer == null) {
            renderer = ((Map<Class<B>, C>) map).get(cClass);
            if (cClass.isAssignableFrom(rootClass)) {
                break;
            }
            cClass = cClass.getSuperclass();
        }
        if (renderer == null) {
            renderer = defaultRenderer;
        }
        return renderer;
    }

}
