package com.spinyowl.spinygui.core.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LineBox {
  private float x;
  private float y;
  private float width;
  private float height;
  private float baseline;
  private final List<InlineFragment> fragments = new ArrayList<>();

  public List<InlineFragment> fragments() {
    return Collections.unmodifiableList(fragments);
  }

  public void addFragment(InlineFragment fragment) {
    fragments.add(fragment);
    width = Math.max(width, fragment.x() + fragment.width() - x);
    height = Math.max(height, fragment.height());
    baseline = Math.max(baseline, fragment.baseline() - y);
  }

  public void replaceFragments(List<InlineFragment> newFragments) {
    fragments.clear();
    width = 0;
    height = 0;
    baseline = 0;
    newFragments.forEach(this::addFragment);
  }

  public void removeLastFragment() {
    if (fragments.isEmpty()) {
      return;
    }
    fragments.remove(fragments.size() - 1);
    var copy = List.copyOf(fragments);
    replaceFragments(copy);
  }

  public boolean empty() {
    return fragments.isEmpty();
  }
}
