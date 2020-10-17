package com.spinyowl.spinygui.core.api;

import com.spinyowl.spinygui.core.node.Element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.joml.Vector2f;

/**
 * Represents window content (scene/page).
 * <p>
 * Contains layers with nodes.
 */
@EqualsAndHashCode
public class DefaultFrame extends Frame {

  /**
   * All other layers added to this list.
   */
  private final List<Layer> layers = new CopyOnWriteArrayList<>();

  /**
   * Used to hold tooltips.
   */
  private Layer tooltipLayer;

  /**
   * Used to hold components.
   */
  private Layer defaultLayer;

  /**
   * Used to create frame and initialize layers with specified size.
   *
   * @param width  width.
   * @param height height.
   */
  public DefaultFrame(float width, float height) {
    initialize(width, height);
  }

  /**
   * Default frame constructor.
   */
  public DefaultFrame() {
    initialize(10, 10);
  }

  /**
   * Used to initialize frame and layers.
   *
   * @param width  initial layer containers width.
   * @param height initial layer containers height.
   */
  private void initialize(float width, float height) {
    tooltipLayer = new Layer();
    tooltipLayer.frame(this);

    defaultLayer = new Layer();
    defaultLayer.frame(this);

    layers.add(defaultLayer);
    layers.add(tooltipLayer);

    size(width, height);
  }

  /**
   * Used to set size for frame and all underlying layers.<br/>
   * <span style="color:red;">NOTE: All layers will be resized to specified size!</span>
   *
   * @param size frame size.
   */
  @Override
  public void size(Vector2f size) {
    size(size.x, size.y);
  }

  /**
   * Used to set size for frame and all underlying layers.<br/>
   * <span style="color:red;">NOTE: All layers will be resized to specified size!</span>
   *
   * @param width  width.
   * @param height height.
   */
  @Override
  public void size(float width, float height) {
    layers().forEach(l -> l.size(width, height));
  }

  /**
   * Used to add layer to frame. <br/>
   * <span style="color:red;">NOTE: layers processed in reverse order -
   * from top to bottom.</span>
   *
   * @param layer layer to add.
   */
  @Override
  public void addLayer(@NonNull Layer layer) {
    if (!containsLayer(layer)) {
      layers.add(layer);
      changeFrame(layer);
    }
  }

  /**
   * Used to add layer to frame. <br/>
   * <span style="color:red;">NOTE: layers processed in reverse order -
   * from top to bottom.</span>
   *
   * @param index index to add layer at.
   * @param layer layer to add.
   */
  @Override
  public void addLayer(int index, @NonNull Layer layer) {
    if (!containsLayer(layer)) {
      layers.add(index, layer);
      changeFrame(layer);
    }
  }

  /**
   * Used to change frame for specified layer.
   *
   * @param layer layer which frame should be changed to this.
   */
  private void changeFrame(@NonNull Layer layer) {
    Frame frame = layer.frame();
    if (frame == this) {
      return;
    }
    if (frame != null) {
      frame.removeLayer(layer);
    }
    layer.frame(this);
  }

  /**
   * Used to remove layer from frame.
   *
   * @param index layer index to remove.
   */
  @Override
  public void removeLayer(int index) {
    Layer layer = layers.get(index);
    boolean removed = layers.remove(layer);
    if (removed) {
      layer.parent(null);
    }
  }

  /**
   * Used to remove layer from frame.
   *
   * @param layer layer to remove.
   */
  @Override
  public void removeLayer(@NonNull Layer layer) {
    Frame frame = layer.frame();
    if (frame == this && containsLayer(layer)) {
      boolean removed = layers.remove(layer);
      if (removed) {
        layer.parent(null);
      }
    }
  }

  /**
   * Used to check if layer list contains provided layer.
   *
   * @param layer layer to check.
   * @return true if layer list contains provided layer.
   */
  @Override
  public boolean containsLayer(@NonNull Layer layer) {
    return layers.stream().anyMatch(l -> l == layer);
  }

  /**
   * Used to get layer by index.
   *
   * @param index layer index.
   * @return layer at specified index.
   * @throws IndexOutOfBoundsException – if the index is out of range (index < 0 || index >=
   *                                   size())
   */
  public Layer layer(int index) {
    return layers.get(index);
  }

  /**
   * Returns unmodifiable list of all layers.
   *
   * @return unmodifiable list of all layers.
   */
  @Override
  public List<Layer> layers() {
    return List.copyOf(layers);
  }

  /**
   * Returns reversed unmodifiable list of all layers.
   *
   * @return reversed unmodifiable list of all layers.
   */
  @Override
  public List<Layer> reversedLayers() {
    var rl = new ArrayList<>(layers);
    Collections.reverse(rl);
    return List.copyOf(rl);
  }

  /**
   * Returns default layer that contains all elements.
   *
   * @return default layer that contains all elements.
   */
  @Override
  public Layer defaultLayer() {
    return defaultLayer;
  }


  /**
   * Returns layer that used to store tooltips.
   *
   * @return layer that used to store tooltips.
   */
  @Override
  public Layer tooltipLayer() {
    return tooltipLayer;
  }

  @Override
  public Element getFocusedElement() {
    return findFocused(layers);
  }

  private Element findFocused(List<? extends Element> elements) {
    for (Element element : elements) {
      if (element.focused()) {
        return element;
      }
      Element focusedInChildren = findFocused(element.children());
      if (focusedInChildren != null) {
        return focusedInChildren;
      }
    }
    return null;
  }
}
