package com.spinyowl.spinygui.core.animation;

import com.spinyowl.spinygui.core.time.TimeProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Default implementation of {@link Animator} interface. */
@RequiredArgsConstructor
public class AnimatorImpl implements Animator {

  @NonNull private final TimeProvider timeProvider;
  /** List of animations to initialize. */
  private final List<Animation> animationsToInitialize = new CopyOnWriteArrayList<>();
  /** List of animations to animate. */
  private final List<Animation> animations = new CopyOnWriteArrayList<>();
  /** List of animation to destroy. */
  private final List<Animation> animationsToDestroy = new CopyOnWriteArrayList<>();
  /** List of animation to destroy. */
  private final List<Animation> animationsToRemove = new CopyOnWriteArrayList<>();
  /** Used to store previous time. */
  private double previousTime;

  /** This method used to process animations. */
  public void runAnimations() {
    double currentTime = timeProvider.getCurrentTime();
    double delta = currentTime - previousTime;

    List<Animation> initializeList = new ArrayList<>(animationsToInitialize);
    for (Animation animation : initializeList) {
      animation.initialize();
      animationsToInitialize.remove(animation);
      animations.add(animation);
    }

    List<Animation> processList = new ArrayList<>(animations);
    for (Animation animation : processList) {
      if (animationsToRemove.contains(animation)) {
        animationsToRemove.remove(animation);
        animations.remove(animation);
        animationsToDestroy.add(animation);
      } else if (animation.animate(delta)) {
        animations.remove(animation);
        animationsToDestroy.add(animation);
      }
    }

    List<Animation> destroyList = new ArrayList<>(animationsToDestroy);
    for (Animation animation : destroyList) {
      animation.destroy();
      animationsToDestroy.remove(animation);
    }

    previousTime = currentTime;
  }

  /**
   * Used to add animation to animator.
   *
   * @param animation animation to add.
   */
  public void pushAnimation(Animation animation) {
    animationsToInitialize.add(animation);
  }

  /**
   * Used to remove animation from animator. In case if animation is not finished animation still
   * should be removed and terminated.
   *
   * @param animation animation to remove.
   */
  @Override
  public void removeAnimation(Animation animation) {
    animationsToRemove.add(animation);
  }
}
