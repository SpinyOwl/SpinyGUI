package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Will be generated when the client area of the specified window needs to be redrawn, for example
 * if the window has been exposed after having been covered by another window.
 */
@Data
@SuperBuilder(toBuilder = true)
public class SystemWindowRefreshEvent extends SystemEvent {}
