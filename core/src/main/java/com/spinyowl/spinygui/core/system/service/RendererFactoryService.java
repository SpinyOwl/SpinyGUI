package com.spinyowl.spinygui.core.system.service;

import com.spinyowl.spinygui.core.system.render.Renderer;

public interface RendererFactoryService {

    <T> Renderer<T> getRenderer(Class<T> elementClass);
}
