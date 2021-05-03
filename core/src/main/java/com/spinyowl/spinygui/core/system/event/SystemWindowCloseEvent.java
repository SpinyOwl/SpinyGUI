package com.spinyowl.spinygui.core.system.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Will be generated when the user attempts to close the specified window, for example by clicking
 * the close widget in the title bar.
 */
@Data
@SuperBuilder(toBuilder = true)
public class SystemWindowCloseEvent extends SystemEvent {}
