package com.spinyowl.spinygui.visual;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

class ViewCaseTest {
  @Test
  void generatedIdsPreserveSourceIdsAndDoNotCollide() {
    var view = ViewCase.create("ids", "<winframe><div id='visual-0'></div><div></div></winframe>",
        "", 100, 100, "", 0, 0);
    var nodes = Jsoup.parse(view.xml()).select("winframe, winframe *");
    assertEquals(3, nodes.stream().map(node -> node.id()).distinct().count());
    assertEquals(1, nodes.stream().filter(node -> node.id().equals("visual-0")).count());
    assertThrows(IllegalArgumentException.class, () -> ViewCase.create("duplicate",
        "<winframe id='a'><div id='a'></div></winframe>", "", 100, 100, "", 0, 0));
    assertThrows(IllegalArgumentException.class, () -> ViewCase.create("scroll",
        "<winframe></winframe>", "", 100, 100, "absent", 0, 20));
  }

  @Test
  void suiteContainsDemosAndEveryApprovedFocusedCategory() throws Exception {
    Path root = Path.of("..").toAbsolutePath().normalize();
    var suite = ViewCase.load(root, "all");
    assertTrue(suite.stream().anyMatch(view -> view.id().equals("demo-grid-style-demo")));
    for (String id : new String[] {"layout", "text", "borders", "clipping", "scrolling",
        "scrolling-offset", "scrolling-clamped"}) {
      assertEquals(1, ViewCase.load(root, id).size());
    }
    assertThrows(IllegalArgumentException.class, () -> ViewCase.load(root, "typo"));
    assertThrows(IllegalArgumentException.class, () -> ViewCase.load(root, ""));
    assertThrows(IllegalArgumentException.class, () -> ViewCase.load(root, "layout,typo"));
  }
}
