package com.spinyowl.spinygui.core.event;


import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyMod;
import com.spinyowl.spinygui.core.input.KeyboardKey;
import com.spinyowl.spinygui.core.node.Element;
import java.util.Arrays;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by ShchAlexander on 2/13/2017.
 */

@Getter
@ToString
@EqualsAndHashCode
public class KeyboardEvent<T extends Element> extends Event<T> {

  private final KeyAction action;
  private final KeyboardKey key;
  private final Set<KeyMod> mods;

  public KeyboardEvent(T target, KeyAction action, KeyboardKey key, Set<KeyMod> mods) {
    super(target);
    this.action = action;
    this.key = key;
    this.mods = mods;
  }

  public KeyboardEvent(T target, double timeStamp, KeyAction action,
      KeyboardKey key, Set<KeyMod> mods) {
    super(target, timeStamp);
    this.action = action;
    this.key = key;
    this.mods = mods;
  }

  public KeyboardEvent(EventTarget source, T target, double timeStamp,
      KeyAction action, KeyboardKey key, Set<KeyMod> mods) {
    super(source, target, timeStamp);
    this.action = action;
    this.key = key;
    this.mods = mods;
  }
}
