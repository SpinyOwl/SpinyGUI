package com.spinyowl.spinygui.core.api;

import com.spinyowl.spinygui.core.converter.css.StyleSheet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.joml.Vector2f;

/**
 * Represents window content (scene/page).
 * <p>
 * Contains layers with nodes.
 */
public class Frame {

    /**
     * All other layers added to this list.
     */
    private final List<Layer> layers = new CopyOnWriteArrayList<>();
    /**
     * List of stylesheets attached to frame.
     */
    private final List<StyleSheet> styleSheets = new CopyOnWriteArrayList<>();
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
    public Frame(float width, float height) {
        initialize(width, height);
    }

    /**
     * Default frame constructor.
     */
    public Frame() {
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
        tooltipLayer.setFrame(this);

        defaultLayer = new Layer();
        defaultLayer.setFrame(this);

        setSize(width, height);
    }

    /**
     * Returns list of stylesheets attached to frame.
     *
     * @return list of stylesheets attached to frame.
     */
    public List<StyleSheet> getStyleSheets() {
        return styleSheets;
    }

    /**
     * Used to set layer containers size. NOTE: All LayerContainers will be resized to specified
     * size!
     *
     * @param size frame size.
     */
    public void setSize(Vector2f size) {
        setSize(size.x, size.y);
    }

    /**
     * Used to set layer containers size. NOTE: All LayerContainers will be resized to specified
     * size!
     *
     * @param width  width.
     * @param height height.
     */
    public void setSize(float width, float height) {
        getAllLayers().forEach(l -> l.setSize(width, height));
    }

    /**
     * Used to add layer to frame. <span style="color:red;">NOTE: layers processed in reverse order
     * - from top to bottom.</span>
     *
     * @param layer layer to add.
     */
    public void addLayer(Layer layer) {
        Objects.requireNonNull(layer);
        if (layer == tooltipLayer || layer == defaultLayer) {
            return;
        }
        if (!containsLayer(layer)) {
            layers.add(layer);
            changeFrame(layer);
        }
    }

    private void changeFrame(Layer layer) {
        Frame frame = layer.getFrame();
        if (frame == this) {
            return;
        }
        if (frame != null) {
            frame.removeLayer(layer);
        }
        layer.setFrame(this);
    }

    /**
     * Used to remove layer from frame.
     *
     * @param layer layer to remove.
     */
    public void removeLayer(Layer layer) {
        Objects.requireNonNull(layer);
        if (layer == tooltipLayer || layer == defaultLayer) {
            return;
        }

        Frame frame = layer.getFrame();
        if (frame == this && containsLayer(layer)) {
            boolean removed = layers.remove(layer);
            if (removed) {
                layer.setParent(null);
            }
        }
    }

    /**
     * Used to check if layer list contains provided layer.
     *
     * @param layer layer to check.
     * @return true if layer list contains provided layer.
     */
    public boolean containsLayer(Layer layer) {
        return (layer != null) && ((layer == tooltipLayer) || (layer == defaultLayer) || layers
            .stream().anyMatch(l -> l == layer));
    }

    /**
     * Used to retrieve default node layer. <span style="color:red;">NOTE: layers processed in
     * reverse order - from top to bottom.</span>
     *
     * @return default node layer.
     */
    public Layer getDefaultLayer() {
        return defaultLayer;
    }

    /**
     * Used to retrieve default tooltip layer. <span style="color:red;">NOTE: layers processed in
     * reverse order - from top to bottom.</span>
     *
     * @return default tooltip layer.
     */
    public Layer getTooltipLayer() {
        return tooltipLayer;
    }

    /**
     * Used to retrieve layers added by developer. <span style="color:red;">NOTE: layers processed
     * in reverse order - from top to bottom.</span>
     *
     * @return layers added by developer.
     */
    public List<Layer> getLayers() {
        return new ArrayList<>(layers);
    }

    /**
     * Used to retrieve all layers where <ul> <li><b>List[0]</b> - default node layer.</li>
     * <li><b>List[1]-List[length-2]</b> - layers added by developer.</li> <li><b>List[length-1]</b>
     * - default tooltip layer.</li> </ul> <p> <span style="color:red;">NOTE: layers processed in
     * reverse order - from top to bottom.</span>
     *
     * @return all layers.
     */
    public List<Layer> getAllLayers() {
        ArrayList<Layer> layerList = new ArrayList<>();
        layerList.add(defaultLayer);
        layerList.addAll(this.layers);
        layerList.add(tooltipLayer);
        return layerList;
    }
}
