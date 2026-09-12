package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import java.util.ArrayList;
import java.util.List;

/** Backend-neutral state transitions for checkbox and radio inputs. */
public final class CheckableInputBehavior {

  private CheckableInputBehavior() {}

  /** Applies the primary activation and returns every input whose checked state changed. */
  public static List<InputElement> activate(InputElement input, Frame frame) {
    if (input.disabled() || !(input.checkboxInput() || input.radioInput())) {
      return List.of();
    }
    if (input.checkboxInput()) {
      input.checked(!input.checked());
      return List.of(input);
    }
    return selectRadio(input, frame);
  }

  /** Selects a radio and clears matching named peers in the same frame. */
  public static List<InputElement> selectRadio(InputElement input, Frame frame) {
    if (input.disabled() || !input.radioInput()) return List.of();
    List<InputElement> changed = new ArrayList<>();
    String name = input.getAttribute("name");
    if (name != null && !name.isBlank()) {
      for (InputElement candidate : inputs(frame)) {
        if (candidate != input && candidate.radioInput() && name.equals(candidate.getAttribute("name"))
            && candidate.checked()) {
          candidate.checked(false);
          changed.add(candidate);
        }
      }
    }
    if (!input.checked()) {
      input.checked(true);
      changed.add(input);
    }
    return changed;
  }

  /** Moves selection to the next enabled radio in the selected input's named frame-local group. */
  public static List<InputElement> selectRelativeRadio(InputElement input, Frame frame, int direction) {
    String name = input.getAttribute("name");
    if (!input.radioInput() || name == null || name.isBlank()) return List.of();
    List<InputElement> group = inputs(frame).stream()
        .filter(candidate -> candidate.radioInput() && !candidate.disabled()
            && name.equals(candidate.getAttribute("name")))
        .toList();
    int index = group.indexOf(input);
    if (index < 0 || group.size() < 2) return List.of();
    return selectRadio(group.get(Math.floorMod(index + direction, group.size())), frame);
  }

  private static List<InputElement> inputs(Element root) {
    List<InputElement> result = new ArrayList<>();
    collectInputs(root, result);
    return result;
  }

  private static void collectInputs(Element root, List<InputElement> result) {
    if (root instanceof InputElement input) result.add(input);
    for (Element child : root.children()) collectInputs(child, result);
  }
}
