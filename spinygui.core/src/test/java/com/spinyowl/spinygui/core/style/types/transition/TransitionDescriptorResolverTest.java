package com.spinyowl.spinygui.core.style.types.transition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class TransitionDescriptorResolverTest {
  @Test void initialValuesAndListRepeatingAreDeterministic() {
    var configuration = new TransitionConfiguration(
        List.of(new TransitionPropertySelection.Named(TransitionPropertyName.OPACITY), new TransitionPropertySelection.Named(TransitionPropertyName.COLOR)),
        List.of(new TransitionTime(1)), List.of(TransitionTimingFunction.Named.LINEAR), List.of(new TransitionTime(.2)));
    var resolved = TransitionDescriptorResolver.resolve(configuration);
    assertEquals(1d, resolved.get(TransitionPropertyName.OPACITY).duration().seconds());
    assertEquals(.2d, resolved.get(TransitionPropertyName.COLOR).delay().seconds());
    assertEquals(TransitionDescriptor.INITIAL, new TransitionDescriptor(TransitionPropertySelection.ALL, TransitionTime.ZERO, TransitionTimingFunction.EASE, TransitionTime.ZERO));
  }
  @Test void explicitLaterPropertyWinsOverAllAndDuplicates() {
    var configuration = new TransitionConfiguration(
        List.of(TransitionPropertySelection.ALL, new TransitionPropertySelection.Named(TransitionPropertyName.OPACITY), new TransitionPropertySelection.Named(TransitionPropertyName.OPACITY)),
        List.of(new TransitionTime(1), new TransitionTime(2), new TransitionTime(3)), List.of(TransitionTimingFunction.EASE), List.of(TransitionTime.ZERO));
    assertEquals(3d, TransitionDescriptorResolver.resolve(configuration).get(TransitionPropertyName.OPACITY).duration().seconds());
  }
  @Test void noneDisablesTheCompleteListAndInvalidValuesAreRejected() {
    var configuration = new TransitionConfiguration(List.of(TransitionPropertySelection.NONE), List.of(TransitionTime.ZERO), List.of(TransitionTimingFunction.EASE), List.of(TransitionTime.ZERO));
    assertEquals(0, TransitionDescriptorResolver.resolve(configuration).size());
    assertThrows(IllegalArgumentException.class, () -> new TransitionTime(-1));
    assertThrows(IllegalArgumentException.class, () -> new TransitionTimingFunction.CubicBezier(-.1, 0, 1, 1));
  }
}
