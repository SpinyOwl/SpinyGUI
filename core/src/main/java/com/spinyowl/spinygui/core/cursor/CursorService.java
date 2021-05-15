package com.spinyowl.spinygui.core.cursor;

import com.spinyowl.spinygui.core.node.Frame;
import lombok.NonNull;

public interface CursorService {

  /**
   * Used to set specified cursor for specified frame.
   *
   * @param cursor cursor to set.
   * @param frame frame to set.
   */
  void serCursor(@NonNull Cursor cursor, @NonNull Frame frame);

  /**
   * Used to set specified cursor mod for specified frame.
   *
   * @param cursorMod cursor mod.
   * @param frame frame.
   */
  void setCursorMod(@NonNull CursorMod cursorMod, @NonNull Frame frame);
}
