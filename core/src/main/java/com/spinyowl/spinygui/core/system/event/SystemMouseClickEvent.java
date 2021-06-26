package com.spinyowl.spinygui.core.system.event;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.system.input.SystemKeyMod;
import com.spinyowl.spinygui.core.system.input.SystemMouseButton;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/** Will be generated when a mouse button is pressed or released. */
@Getter
@ToString
@EqualsAndHashCode
public class SystemMouseClickEvent extends SystemEvent {

  private final SystemMouseButton button;

  /**
   * The key action. One of:
   *
   * <ul>
   *   <li>{@link SystemKeyAction#PRESS PRESS}
   *   <li>{@link SystemKeyAction#RELEASE RELEASE}
   *   <li>{@link SystemKeyAction#REPEAT REPEAT}
   * </ul>
   */
  private final SystemKeyAction action;

  /** Describes which modifier keys were held down. */
  @NonNull private final ImmutableSet<SystemKeyMod> mods;

  @Builder
  protected SystemMouseClickEvent(
      Frame frame,
      SystemMouseButton button,
      SystemKeyAction action,
      @NonNull ImmutableSet<SystemKeyMod> mods) {
    super(frame);
    this.button = button;
    this.action = action;
    this.mods = mods;
  }

  public ImmutableSet<KeyMod> mappedMods() {
    return mods.stream().map(SystemKeyMod::keyMod).collect(toImmutableSet());
  }
}
