package com.spinyowl.spinygui.core.style.stylesheet;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
public class Specificity implements Comparable<Specificity> {

  public static final Specificity ZERO = Specificity.of(0, 0, 0);
  public static final Specificity ID = Specificity.of(1, 0, 0);
  public static final Specificity CLASS = Specificity.of(0, 1, 0);
  public static final Specificity TYPE = Specificity.of(0, 0, 1);

  private final int idSpecificity;
  private final int classSpecificity;
  private final int typeSpecificity;

  public static Specificity max(Specificity left, Specificity right) {
    return left.compareTo(right) > 0 ? left : right;
  }

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
}
