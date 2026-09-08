package com.spinyowl.spinygui.visual;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class LaunchDemosTest {
  @Test
  void defaultsAndExactNamesSelectOneSharedView() throws Exception {
    var cases = ViewCase.load(Path.of("..").toAbsolutePath(), "all");
    assertEquals("demo-button-demo", LaunchDemosMain.select(cases, "").id());
    assertEquals("demo-button-demo", LaunchDemosMain.select(cases, null).id());
    assertEquals("demo-overflow-demo", LaunchDemosMain.select(cases, "overflow-demo").id());
    assertEquals("demo-overflow-demo", LaunchDemosMain.select(cases, "demo-overflow-demo").id());
    assertEquals("scrolling-offset", LaunchDemosMain.select(cases, "scrolling-offset").id());
  }

  @Test
  void invalidOrMultipleNamesFailWithAvailableChoices() throws Exception {
    var cases = ViewCase.load(Path.of("..").toAbsolutePath(), "all");
    for (String name : new String[] {"unknown", "all", "layout,text"}) {
      var failure = assertThrows(IllegalArgumentException.class, () -> LaunchDemosMain.select(cases, name));
      assertTrue(failure.getMessage().contains("Available:"));
      assertTrue(failure.getMessage().contains("demo-button-demo"));
    }
  }
}
