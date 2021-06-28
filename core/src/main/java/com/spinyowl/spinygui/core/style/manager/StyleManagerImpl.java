package com.spinyowl.spinygui.core.style.manager;

import java.util.concurrent.LinkedBlockingQueue;

/** Default style manager implementation based on {@link LinkedBlockingQueue}. */
public class StyleManagerImpl extends AbstractStyleManager {

  public StyleManagerImpl() {
    super(new LinkedBlockingQueue<>());
  }
}
