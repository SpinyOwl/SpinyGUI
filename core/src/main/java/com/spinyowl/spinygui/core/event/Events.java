package com.spinyowl.spinygui.core.event;

public final class Events {

  public static final Class<MouseEvent> MOUSE_EVENT = MouseEvent.class;
  public static final Class<ChangeSizeEvent> CHANGE_SIZE_EVENT = ChangeSizeEvent.class;
  public static final Class<WindowCloseEvent> WINDOW_CLOSE_EVENT = WindowCloseEvent.class;
  public static final Class<ChangePositionEvent> CHANGE_POSITION_EVENT = ChangePositionEvent.class;
  public static final Class<InvalidateTreeEvent> INVALIDATE_TREE_EVENT = InvalidateTreeEvent.class;

  public static final String INVALIDATE_TREE_EVENT_NAME = "INVALIDATE_TREE";
  public static final String CHANGE_SIZE_EVENT_NAME = "CHANGE_SIZE_EVENT";
  public static final String CHANGE_POSITION_EVENT_NAME = "CHANGE_POSITION_EVENT";

  private Events() {
  }
}
