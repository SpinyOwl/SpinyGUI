package com.spinyowl.spinygui.core.node;

import static org.junit.jupiter.api.Assertions.*;

import com.spinyowl.spinygui.core.style.stylesheet.selector.simple.ClassAttributeSelector;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClassListTest {

  @Test
  void liveViewObservesAttributeChangesAndKeepsOrderedUniqueTokens() {
    Element element = new Element("div");
    ClassList classes = element.classList();
    assertSame(classes, element.classList());
    assertEquals(0, classes.size());
    element.setAttribute("class", " first\tsecond\nfirst\r\fthird ");
    assertEquals(List.of("first", "second", "third"), classes.values());
    classes.add("second", "fourth", "fourth");
    assertEquals("first second third fourth", element.getAttribute("class"));
    classes.remove("first", "third", "missing");
    assertEquals(List.of("second", "fourth"), classes.values());
    List<String> snapshot = classes.values();
    assertThrows(UnsupportedOperationException.class, () -> snapshot.add("new"));
    element.removeAttribute("class");
    assertEquals(0, classes.size());
    assertEquals(List.of("second", "fourth"), snapshot);
  }

  @Test
  void togglesAndClearsClasses() {
    Element element = new Element("div");
    ClassList classes = element.classList();
    assertTrue(classes.toggle("active"));
    assertTrue(classes.contains("active"));
    assertTrue(new ClassAttributeSelector("active").test(element));
    assertFalse(classes.toggle("active"));
    assertFalse(classes.contains("active"));
    assertFalse(new ClassAttributeSelector("active").test(element));
    assertTrue(classes.toggle("active", true));
    assertTrue(classes.toggle("active", true));
    assertFalse(classes.toggle("active", false));
    assertFalse(classes.toggle("active", false));
    classes.add("one", "two");
    classes.clear();
    assertEquals("", element.getAttribute("class"));
    assertEquals(0, classes.size());
  }

  @Test
  void rejectsInvalidTokensBeforeAnyMutation() {
    Element element = new Element("div");
    element.classList().add("original");
    for (String invalid : List.of("", "two words", "a\tb", "a\nb", "a\rb", "a\fb")) {
      assertThrows(IllegalArgumentException.class, () -> element.classList().add("new", invalid));
      assertThrows(IllegalArgumentException.class, () -> element.classList().remove("original", invalid));
      assertThrows(IllegalArgumentException.class, () -> element.classList().toggle(invalid));
      assertEquals("original", element.getAttribute("class"));
    }
    assertThrows(NullPointerException.class, () -> element.classList().add((String[]) null));
    assertThrows(NullPointerException.class, () -> element.classList().add("new", null));
    assertEquals("original", element.getAttribute("class"));
  }

  @Test
  void onlyRealChangesInvalidateTheOwningFrame() {
    Frame frame = new Frame();
    Element element = new Element("div");
    frame.addChild(element);
    element.setAttribute("class", "one  one");
    long revision = frame.revision();
    frame.completePreparation(revision, true, true, true);
    frame.markPainted(revision);
    element.classList().add("one");
    element.classList().remove("missing");
    element.classList().toggle("one", true);
    element.classList().add();
    element.classList().remove();
    assertEquals(revision, frame.revision());
    assertFalse(frame.invalidation().styleDirty());
    element.classList().add("two", "three");
    assertEquals(revision + 1, frame.revision());
    assertTrue(frame.invalidation().styleDirty());
    element.classList().clear();
    assertEquals(revision + 2, frame.revision());
    element.classList().clear();
    assertEquals(revision + 2, frame.revision());
  }
}
