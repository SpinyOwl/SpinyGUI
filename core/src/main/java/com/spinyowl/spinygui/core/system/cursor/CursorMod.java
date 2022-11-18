package com.spinyowl.spinygui.core.system.cursor;

public enum CursorMod {
  /** Meaning the regular arrow cursor is used and cursor motion is not limited. */
  NORMAL,
  /**
   * Just hides cursor without any automatic centering. This mode puts no limit on the motion of the
   * cursor.
   */
  HIDDEN,
  /**
   * Hides cursor and automatically centers it. Used when you need to implement mouse motion based
   * camera controls or other input schemes that require unlimited mouse movement.
   *
   * <p>This will hide the cursor and lock it to the specified window. Backend should then take care
   * of all the details of cursor re-centering and offset calculation and providing the application
   * with a virtual cursor position. This virtual position is provided normally via both the cursor
   * position callback and through polling.
   */
  DISABLED
}
