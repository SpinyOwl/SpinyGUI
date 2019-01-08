package com.spinyowl.spinygui.backend.opengl32.service;

import com.spinyowl.spinygui.core.component.base.Component;
import com.spinyowl.spinygui.core.system.render.Renderer;
import com.spinyowl.spinygui.core.system.service.RendererFactoryService;

import java.util.Map;

public class SpinyGuiOpenGL32RendererFactoryService implements RendererFactoryService {
    private static final SpinyGuiOpenGL32RendererFactoryService INSTANCE = new SpinyGuiOpenGL32RendererFactoryService();

    private Map<Class<Component>, Renderer> rendererMap;

    private SpinyGuiOpenGL32RendererFactoryService() {
    }

    public static SpinyGuiOpenGL32RendererFactoryService getInstance() {
        return INSTANCE;
    }

    @Override
    public <T> Renderer<T> getRenderer(Class<T> elementClass) {
        return null;
    }


}
