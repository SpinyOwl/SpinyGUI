package com.spinyowl.spinygui.core.style.stylesheet;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
public class Specificity implements Comparable<Specificity> {

  private final int idSpecificity;
  private final int classSpecificity;
  private final int typeSpecificity;

  public Specificity add(Specificity specificity) {
    return new Specificity(
        this.idSpecificity + specificity.idSpecificity,
        this.classSpecificity + specificity.classSpecificity,
        this.typeSpecificity + specificity.typeSpecificity);
  }

  @Override
  public int compareTo(Specificity o) {
    // @formatter:off
    if (this.idSpecificity > o.idSpecificity) return 1;
    if (this.idSpecificity < o.idSpecificity) return -1;
    if (this.classSpecificity > o.classSpecificity) return 1;
    if (this.classSpecificity < o.classSpecificity) return -1;
    return Integer.compare(this.typeSpecificity, o.typeSpecificity);
    // @formatter:on
  }

  public static Specificity max(Specificity left, Specificity right) {
    return left.compareTo(right) > 0 ? left : right;
  }
}
