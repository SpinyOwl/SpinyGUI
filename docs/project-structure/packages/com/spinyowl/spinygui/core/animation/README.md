# com.spinyowl.spinygui.core.animation

Frame-time animation contracts and a simple animator loop.

- Modules: core
- Source sets: main
- Direct classes: 3
- Descendant packages: 0

## Classes

### Animation

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/animation/Animation.java`
- Declaration: `abstract class Animation`
- Responsibility: Draft animation realization.

### Animator

- Kind: interface
- Source: `core/src/main/java/com/spinyowl/spinygui/core/animation/Animator.java`
- Declaration: `public interface Animator`
- Responsibility: Animation processor.

### AnimatorImpl

- Kind: class
- Source: `core/src/main/java/com/spinyowl/spinygui/core/animation/AnimatorImpl.java`
- Declaration: `public class AnimatorImpl implements Animator`
- Responsibility: Default implementation of Animator interface.
