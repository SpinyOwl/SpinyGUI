package com.spinyowl.spinygui.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.Frame;
import org.junit.jupiter.api.Test;

class FrameNavigatorTest {

  @Test
  void navigatesBackAndForwardInBrowserOrder() {
    Frame first = new Frame();
    Frame second = new Frame();
    Frame third = new Frame();
    FrameNavigator navigator = new FrameNavigator(first, 4);

    navigator.navigate(second);
    navigator.navigate(third);

    assertSame(third, navigator.currentFrame());
    assertTrue(navigator.canGoBack());
    assertFalse(navigator.canGoForward());
    assertTrue(navigator.back());
    assertSame(second, navigator.currentFrame());
    assertTrue(navigator.back());
    assertSame(first, navigator.currentFrame());
    assertFalse(navigator.back());
    assertTrue(navigator.forward());
    assertSame(second, navigator.currentFrame());
    assertTrue(navigator.forward());
    assertSame(third, navigator.currentFrame());
    assertFalse(navigator.forward());
  }

  @Test
  void newNavigationClearsForwardHistory() {
    Frame first = new Frame();
    Frame second = new Frame();
    Frame replacement = new Frame();
    FrameNavigator navigator = new FrameNavigator(first, 4);
    navigator.navigate(second);
    navigator.back();

    navigator.navigate(replacement);

    assertSame(replacement, navigator.currentFrame());
    assertFalse(navigator.canGoForward());
    assertFalse(navigator.forward());
    assertTrue(navigator.back());
    assertSame(first, navigator.currentFrame());
  }

  @Test
  void navigatingToCurrentInstanceDoesNotChangeHistory() {
    Frame first = new Frame();
    Frame second = new Frame();
    FrameNavigator navigator = new FrameNavigator(first, 4);
    navigator.navigate(second);

    navigator.navigate(second);

    assertTrue(navigator.back());
    assertSame(first, navigator.currentFrame());
    assertFalse(navigator.canGoBack());
    assertTrue(navigator.canGoForward());
    assertTrue(navigator.forward());
    assertSame(second, navigator.currentFrame());
    assertFalse(navigator.canGoForward());
  }

  @Test
  void evictsOldestBackEntryAtConfiguredCapacity() {
    Frame first = new Frame();
    Frame second = new Frame();
    Frame third = new Frame();
    Frame fourth = new Frame();
    FrameNavigator navigator = new FrameNavigator(first, 2);

    navigator.navigate(second);
    navigator.navigate(third);
    navigator.navigate(fourth);

    assertTrue(navigator.back());
    assertSame(third, navigator.currentFrame());
    assertTrue(navigator.back());
    assertSame(second, navigator.currentFrame());
    assertFalse(navigator.back());
  }

  @Test
  void navigationDuringHostUpdateChangesSubsequentFrameLookup() {
    Frame first = new Frame();
    Frame second = new Frame();
    FrameNavigator navigator = new FrameNavigator(first, 4);
    Runnable hostUpdate = () -> navigator.navigate(second);

    Frame beforeUpdate = navigator.currentFrame();
    hostUpdate.run();
    Frame afterUpdate = navigator.currentFrame();

    assertSame(first, beforeUpdate);
    assertSame(second, afterUpdate);
  }

  @Test
  void rejectsInvalidConstructionAndNullNavigation() {
    Frame initial = new Frame();

    assertThrows(NullPointerException.class, () -> new FrameNavigator(null, 1));
    assertThrows(IllegalArgumentException.class, () -> new FrameNavigator(initial, 0));
    assertThrows(NullPointerException.class, () -> new FrameNavigator(initial, 1).navigate(null));
  }
}
