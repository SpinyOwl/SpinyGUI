package com.spinyowl.spinygui.core.api;

import com.spinyowl.spinygui.core.converter.css.StyleSheet;
import com.spinyowl.spinygui.core.node.Element;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.joml.Vector2f;

public abstract class Frame {

  /**
   * List of stylesheets attached to frame.
   */
  protected final List<StyleSheet> styleSheets = new CopyOnWriteArrayList<>();

  /**
   * Used to set size for frame and all underlying layers.<br/>
   * <span style="color:red;">NOTE: All layers will be resized to specified size!</span>
   *
   * @param size size to set.
   */
  public abstract void size(Vector2f size);

  /**
   * Used to set size for frame and all underlying layers.<br/>
   * <span style="color:red;">NOTE: All layers will be resized to specified size!</span>
   *
   * @param width  width to set.
   * @param height height to set.
   */
  public abstract void size(float width, float height);

  /**
   * Used to add layer to frame. <br/>
   * <span style="color:red;">NOTE: layers processed in reverse order -
   * from top to bottom.</span>
   *
   * @param layer layer to add.
   */
  public abstract void addLayer(Layer layer);

  /**
   * Used to add layer to frame. <br/>
   * <span style="color:red;">NOTE: layers processed in reverse order -
   * from top to bottom.</span>
   *
   * @param index index to add layer at.
   * @param layer layer to add.
   */
  public abstract void addLayer(int index, Layer layer);

  /**
   * Used to remove layer from frame.
   *
   * @param index layer index to remove.
   */
  public abstract void removeLayer(int index);

  /**
   * Used to remove layer from frame.
   *
   * @param layer layer to remove.
   */
  public abstract void removeLayer(Layer layer);

  /**
   * Used to check if layer list contains provided layer.
   *
   * @param layer layer to check.
   * @return true if layer list contains provided layer.
   */
  public abstract boolean containsLayer(Layer layer);

  /**
   * Used to get layer by index.
   *
   * @param index layer index.
   * @return layer at specified index.
   * @throws IndexOutOfBoundsException – if the index is out of range (index < 0 || index >=
   *                                   size())
   */
  public abstract Layer layer(int index);

  /**
   * Returns unmodifiable list of all layers.
   *
   * @return unmodifiable list of all layers.
   */
  public abstract List<Layer> layers();

  /**
   * Returns reversed unmodifiable list of all layers.
   *
   * @return reversed unmodifiable list of all layers.
   */
  public abstract List<Layer> reversedLayers();

  /**
   * Returns default layer that contains all elements.
   *
   * @return default layer that contains all elements.
   */
  public abstract Layer defaultLayer();

  /**
   * Returns layer that used to store tooltips.
   *
   * @return layer that used to store tooltips.
   */
  public abstract Layer tooltipLayer();

  /**
   * Returns editable list of stylesheets attached to frame.
   *
   * @return editable list of stylesheets attached to frame.
   */
  public List<StyleSheet> styleSheets() {
    return styleSheets;
  }

  public abstract Element getFocusedElement();
}
