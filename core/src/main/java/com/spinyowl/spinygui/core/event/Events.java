package com.spinyowl.spinygui.core.event;

public final class Events {

  public static final Class<MouseEvent> MOUSE_EVENT = MouseEvent.class;
  public static final Class<ChangeSizeEvent> CHANGE_SIZE_EVENT = ChangeSizeEvent.class;
  public static final Class<WindowCloseEvent> WINDOW_CLOSE_EVENT = WindowCloseEvent.class;
  public static final Class<ChangePositionEvent> CHANGE_POSITION_EVENT = ChangePositionEvent.class;
  public static final Class<ElementTreeUpdateEvent> ELEMENT_TREE_UPDATE_EVENT = ElementTreeUpdateEvent.class;

  public static final String INVALIDATE_TREE = "INVALIDATE_TREE";
  public static final String CHANGE_SIZE_EVENT_NAME = "CHANGE_SIZE_EVENT";
  public static final String CHANGE_POSITION_EVENT_NAME = "CHANGE_POSITION_EVENT";

  private Events() {
  }
}
