package com.spinyowl.spinygui.core.system.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.FrameWindowService;
import lombok.NonNull;

public class FrameWindowServiceImpl implements FrameWindowService {
  private final BiMap<Long, Frame> windowFrameMap = HashBiMap.create();

  @Override
  public void put(@NonNull Long window, @NonNull Frame frame) {
    windowFrameMap.put(window, frame);
  }

  @Override
  public Frame getFrame(@NonNull Long window) {
    return windowFrameMap.get(window);
  }

  @Override
  public Long getWindow(@NonNull Frame frame) {
    return windowFrameMap.inverse().getOrDefault(frame, -1L);
  }

  @Override
  public void remove(@NonNull Long window) {
    windowFrameMap.remove(window);
  }
}
