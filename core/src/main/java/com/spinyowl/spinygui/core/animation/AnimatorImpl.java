package com.spinyowl.spinygui.core.animation;

import com.spinyowl.spinygui.core.time.TimeService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Default implementation of {@link Animator} interface. */
@RequiredArgsConstructor
public class AnimatorImpl implements Animator {

  @NonNull private final TimeService timeService;
  /** List of animations to initialize. */
  @NonNull private final List<Animation> animationsToInitialize;
  /** List of animations to animate. */
  @NonNull private final List<Animation> animations;
  /** List of animation to destroy. */
  @NonNull private final List<Animation> animationsToDestroy;
  /** List of animation to destroy. */
  @NonNull private final List<Animation> animationsToRemove;
  /** Used to store previous time. */
  private double previousTime;

  public AnimatorImpl(@NonNull TimeService timeService) {
    this.timeService = timeService;
    this.animationsToInitialize = new CopyOnWriteArrayList<>();
    this.animations = new CopyOnWriteArrayList<>();
    this.animationsToDestroy = new CopyOnWriteArrayList<>();
    this.animationsToRemove = new CopyOnWriteArrayList<>();
  }

  public AnimatorImpl(@NonNull TimeService timeService, List<Animation> animations) {
    this.timeService = timeService;
    this.animationsToInitialize = new CopyOnWriteArrayList<>();
    this.animations = animations;
    this.animationsToDestroy = new CopyOnWriteArrayList<>();
    this.animationsToRemove = new CopyOnWriteArrayList<>();
  }

  /** This method used to process animations. */
  public void runAnimations() {
    double currentTime = timeService.getCurrentTime();
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
