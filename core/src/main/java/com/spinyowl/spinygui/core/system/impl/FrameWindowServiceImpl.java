package com.spinyowl.spinygui.core.system.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.FrameWindowService;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;

public class FrameWindowServiceImpl implements FrameWindowService {
  private final Map<Long, Frame> windowFrameMap = new HashMap<>();
  private final SetMultimap<Frame, Long> frameWindowMultimap = HashMultimap.create();

  @Override
  public void put(@NonNull Long window, @NonNull Frame frame) {
    windowFrameMap.put(window, frame);
    frameWindowMultimap.put(frame, window);
  }

  @Override
  public Frame getFrame(@NonNull Long window) {
    return windowFrameMap.get(window);
  }

  @Override
  public Set<Long> getWindows(@NonNull Frame frame) {
    return frameWindowMultimap.get(frame);
  }

  @Override
  public void remove(@NonNull Long window) {
    if (windowFrameMap.containsKey(window)) {
      var frame = windowFrameMap.remove(window);
      frameWindowMultimap.remove(frame, window);
    }
  }
}
