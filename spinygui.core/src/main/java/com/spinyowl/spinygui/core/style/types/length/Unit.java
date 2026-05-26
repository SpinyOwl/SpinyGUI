package com.spinyowl.spinygui.core.style.types.length;

public interface Unit {

  // DECLARATION OF AUTO VALUE.
  Unit AUTO =
      new Unit() {
        @Override
        public String toString() {
          return "auto";
        }
      };

  default boolean isLength() {
    return this instanceof Length;
  }

  default boolean isAuto() {
    return AUTO.equals(this);
  }

  default Length asLength() {
    return (Length) this;
  }
}
