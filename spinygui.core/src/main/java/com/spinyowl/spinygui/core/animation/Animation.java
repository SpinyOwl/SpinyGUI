package com.spinyowl.spinygui.core.animation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Draft animation realization.
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public abstract class Animation {

  @NonNull private final Animator animator;
  /** Flag that indicates if animation was started. */
  private boolean animationStarted = false;

  /** Adds animation to animator. */
  public void startAnimation() {
    if (!animationStarted) {
      animator.pushAnimation(this);
      animationStarted = true;
    }
  }

  /** Called once during animation lifetime before animate loop. */
  protected void initialize() {
    // Could be implemented later.
  }

  /**
   * This method used to update animated object. Called by animator every frame. Removed from
   * animator and stops when this method returns true.
   *
   * <p>Returns true if animation is finished and could be removed from animator.
   *
   * @param delta delta time (from previous call).
   * @return true if animation is finished and could be removed from animator.
   */
  protected abstract boolean animate(double delta);

  /** Called once during animation lifetime when animation ended. */
  protected void destroy() {
    // Could be implemented later.
  }

  /** Used to stop animation. Removes animation from animator. */
  public void stopAnimation() {
    animator.removeAnimation(this);
  }

  /**
   * Returns the flag that indicates if animation was started.
   *
   * @return the flag that indicates if animation was started.
   */
  public boolean isAnimationStarted() {
    return animationStarted;
  }
}
