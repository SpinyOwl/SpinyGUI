package com.spinyowl.spinygui.core.animation;

import com.spinyowl.spinygui.core.time.TimeService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Default implementation of {@link Animator} interface. */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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

  /**
   * Creates Animator based on provided {@link TimeService}. By default uses {@link
   * CopyOnWriteArrayList} for all collections.
   *
   * @param timeService time service to use.
   */
  public AnimatorImpl(@NonNull TimeService timeService) {
    this(
        timeService,
        new CopyOnWriteArrayList<>(),
        new CopyOnWriteArrayList<>(),
        new CopyOnWriteArrayList<>(),
        new CopyOnWriteArrayList<>());
  }

  /**
   * Creates Animator based on provided {@link TimeService} and animations list.
   *
   * @param timeService time service to use.
   * @param animationsListSupplier animations list supplier.
   */
  public AnimatorImpl(
      @NonNull TimeService timeService, Supplier<List<Animation>> animationsListSupplier) {
    this(
        timeService,
        new CopyOnWriteArrayList<>(),
        animationsListSupplier.get(),
        new CopyOnWriteArrayList<>(),
        new CopyOnWriteArrayList<>());
  }

  /**
   * Creates Animator based on provided {@link TimeService} and animations list.
   *
   * @param timeService time service to use.
   * @param animationsToInitializeListSupplier animations to initialize list supplier.
   * @param animationsListSupplier animations list supplier.
   * @param animationsToDestroyListSupplier animations to destroy list supplier.
   * @param animationsToRemoveListSupplier animations to remove list supplier.
   */
  public AnimatorImpl(
      @NonNull TimeService timeService,
      Supplier<List<Animation>> animationsToInitializeListSupplier,
      Supplier<List<Animation>> animationsListSupplier,
      Supplier<List<Animation>> animationsToDestroyListSupplier,
      Supplier<List<Animation>> animationsToRemoveListSupplier) {
    this(
        timeService,
        animationsToInitializeListSupplier.get(),
        animationsListSupplier.get(),
        animationsToDestroyListSupplier.get(),
        animationsToRemoveListSupplier.get());
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
