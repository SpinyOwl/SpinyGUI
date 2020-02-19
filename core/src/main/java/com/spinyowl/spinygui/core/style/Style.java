package com.spinyowl.spinygui.core.style;

import com.spinyowl.spinygui.core.style.types.*;
import com.spinyowl.spinygui.core.style.types.background.BackgroundRepeat;
import com.spinyowl.spinygui.core.style.types.background.BackgroundSize;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.flex.*;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;

import java.util.List;

/**
 * Style interface that could be used to get/set element's inline style (style attribute)
 */
public class Style {
    private Color color;
    private Color backgroundColor;
    private String backgroundImage;
    private Length backgroundPositionX;
    private Length backgroundPositionY;
    private BackgroundSize backgroundSize;
    private BackgroundRepeat backgroundRepeat;
    //    private backgroundOrigin;
//    private backgroundClip;
//    private backgroundAttachment;
//    private borderRadius;
    private Length borderBottomLeftRadius;
    private Length borderBottomRightRadius;
    private Length borderTopLeftRadius;
    private Length borderTopRightRadius;
    private List<String> fontFamily;
    private Length fontSize;
    private FontStyle fontStyle;
    private Integer fontWeight;
    //    private padding;
    private Length paddingTop;
    private Length paddingRight;
    private Length paddingBottom;
    private Length paddingLeft;
    //    private margin;
    private Unit marginTop;
    private Unit marginRight;
    private Unit marginBottom;
    private Unit marginLeft;
    //    private border;
//    private borderWidth;
//    private borderColor;
//    private borderStyle;
//    private borderLeft;
//    private borderRight;
//    private borderTop;
//    private borderBottom;
    private Length borderLeftWidth;
    private Length borderRightWidth;
    private Length borderTopWidth;
    private Length borderBottomWidth;
    private Color borderLeftColor;
    private Color borderRightColor;
    private Color borderTopColor;
    private Color borderBottomColor;
    private BorderStyle borderLeftStyle;
    private BorderStyle borderRightStyle;
    private BorderStyle borderTopStyle;
    private BorderStyle borderBottomStyle;
    private AlignContent alignContent;
    private AlignItems alignItems;
    private AlignSelf alignSelf;
    private Unit flexBasis;
    private FlexDirection flexDirection;
    private int flexGrow;
    private int flexShrink;
    private FlexWrap flexWrap;
    private JustifyContent justifyContent;
    private Unit width;
    private Unit height;
    private Length minWidth;
    private Length minHeight;
    private Length maxWidth;
    private Length maxHeight;
    private Display display;
    private Position position;
    private Length top;
    private Length bottom;
    private Length right;
    private Length left;
    private WhiteSpace whiteSpace;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Length getBackgroundPositionX() {
        return backgroundPositionX;
    }

    public void setBackgroundPositionX(Length backgroundPositionX) {
        this.backgroundPositionX = backgroundPositionX;
    }

    public Length getBackgroundPositionY() {
        return backgroundPositionY;
    }

    public void setBackgroundPositionY(Length backgroundPositionY) {
        this.backgroundPositionY = backgroundPositionY;
    }

    public BackgroundSize getBackgroundSize() {
        return backgroundSize;
    }

    public void setBackgroundSize(BackgroundSize backgroundSize) {
        this.backgroundSize = backgroundSize;
    }

    public BackgroundRepeat getBackgroundRepeat() {
        return backgroundRepeat;
    }

    public void setBackgroundRepeat(BackgroundRepeat backgroundRepeat) {
        this.backgroundRepeat = backgroundRepeat;
    }

    public Length getBorderBottomLeftRadius() {
        return borderBottomLeftRadius;
    }

    public void setBorderBottomLeftRadius(Length borderBottomLeftRadius) {
        this.borderBottomLeftRadius = borderBottomLeftRadius;
    }

    public Length getBorderBottomRightRadius() {
        return borderBottomRightRadius;
    }

    public void setBorderBottomRightRadius(Length borderBottomRightRadius) {
        this.borderBottomRightRadius = borderBottomRightRadius;
    }

    public Length getBorderTopLeftRadius() {
        return borderTopLeftRadius;
    }

    public void setBorderTopLeftRadius(Length borderTopLeftRadius) {
        this.borderTopLeftRadius = borderTopLeftRadius;
    }

    public Length getBorderTopRightRadius() {
        return borderTopRightRadius;
    }

    public void setBorderTopRightRadius(Length borderTopRightRadius) {
        this.borderTopRightRadius = borderTopRightRadius;
    }

    public List<String> getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(List<String> fontFamily) {
        this.fontFamily = fontFamily;
    }

    public Length getFontSize() {
        return fontSize;
    }

    public void setFontSize(Length fontSize) {
        this.fontSize = fontSize;
    }

    public FontStyle getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(FontStyle fontStyle) {
        this.fontStyle = fontStyle;
    }

    public Integer getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(Integer fontWeight) {
        this.fontWeight = fontWeight;
    }

    public Length getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(Length paddingTop) {
        this.paddingTop = paddingTop;
    }

    public Length getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(Length paddingRight) {
        this.paddingRight = paddingRight;
    }

    public Length getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(Length paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public Length getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(Length paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public Unit getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(Unit marginTop) {
        this.marginTop = marginTop;
    }

    public Unit getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(Unit marginRight) {
        this.marginRight = marginRight;
    }

    public Unit getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(Unit marginBottom) {
        this.marginBottom = marginBottom;
    }

    public Unit getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(Unit marginLeft) {
        this.marginLeft = marginLeft;
    }

    public Length getBorderLeftWidth() {
        return borderLeftWidth;
    }

    public void setBorderLeftWidth(Length borderLeftWidth) {
        this.borderLeftWidth = borderLeftWidth;
    }

    public Length getBorderRightWidth() {
        return borderRightWidth;
    }

    public void setBorderRightWidth(Length borderRightWidth) {
        this.borderRightWidth = borderRightWidth;
    }

    public Length getBorderTopWidth() {
        return borderTopWidth;
    }

    public void setBorderTopWidth(Length borderTopWidth) {
        this.borderTopWidth = borderTopWidth;
    }

    public Length getBorderBottomWidth() {
        return borderBottomWidth;
    }

    public void setBorderBottomWidth(Length borderBottomWidth) {
        this.borderBottomWidth = borderBottomWidth;
    }

    public Color getBorderLeftColor() {
        return borderLeftColor;
    }

    public void setBorderLeftColor(Color borderLeftColor) {
        this.borderLeftColor = borderLeftColor;
    }

    public Color getBorderRightColor() {
        return borderRightColor;
    }

    public void setBorderRightColor(Color borderRightColor) {
        this.borderRightColor = borderRightColor;
    }

    public Color getBorderTopColor() {
        return borderTopColor;
    }

    public void setBorderTopColor(Color borderTopColor) {
        this.borderTopColor = borderTopColor;
    }

    public Color getBorderBottomColor() {
        return borderBottomColor;
    }

    public void setBorderBottomColor(Color borderBottomColor) {
        this.borderBottomColor = borderBottomColor;
    }

    public BorderStyle getBorderLeftStyle() {
        return borderLeftStyle;
    }

    public void setBorderLeftStyle(BorderStyle borderLeftStyle) {
        this.borderLeftStyle = borderLeftStyle;
    }

    public BorderStyle getBorderRightStyle() {
        return borderRightStyle;
    }

    public void setBorderRightStyle(BorderStyle borderRightStyle) {
        this.borderRightStyle = borderRightStyle;
    }

    public BorderStyle getBorderTopStyle() {
        return borderTopStyle;
    }

    public void setBorderTopStyle(BorderStyle borderTopStyle) {
        this.borderTopStyle = borderTopStyle;
    }

    public BorderStyle getBorderBottomStyle() {
        return borderBottomStyle;
    }

    public void setBorderBottomStyle(BorderStyle borderBottomStyle) {
        this.borderBottomStyle = borderBottomStyle;
    }

    public AlignContent getAlignContent() {
        return alignContent;
    }

    public void setAlignContent(AlignContent alignContent) {
        this.alignContent = alignContent;
    }

    public AlignItems getAlignItems() {
        return alignItems;
    }

    public void setAlignItems(AlignItems alignItems) {
        this.alignItems = alignItems;
    }

    public AlignSelf getAlignSelf() {
        return alignSelf;
    }

    public void setAlignSelf(AlignSelf alignSelf) {
        this.alignSelf = alignSelf;
    }

    public Unit getFlexBasis() {
        return flexBasis;
    }

    public void setFlexBasis(Unit flexBasis) {
        this.flexBasis = flexBasis;
    }

    public FlexDirection getFlexDirection() {
        return flexDirection;
    }

    public void setFlexDirection(FlexDirection flexDirection) {
        this.flexDirection = flexDirection;
    }

    public int getFlexGrow() {
        return flexGrow;
    }

    public void setFlexGrow(int flexGrow) {
        this.flexGrow = flexGrow;
    }

    public int getFlexShrink() {
        return flexShrink;
    }

    public void setFlexShrink(int flexShrink) {
        this.flexShrink = flexShrink;
    }

    public FlexWrap getFlexWrap() {
        return flexWrap;
    }

    public void setFlexWrap(FlexWrap flexWrap) {
        this.flexWrap = flexWrap;
    }

    public JustifyContent getJustifyContent() {
        return justifyContent;
    }

    public void setJustifyContent(JustifyContent justifyContent) {
        this.justifyContent = justifyContent;
    }

    public Unit getWidth() {
        return width;
    }

    public void setWidth(Unit width) {
        this.width = width;
    }

    public Unit getHeight() {
        return height;
    }

    public void setHeight(Unit height) {
        this.height = height;
    }

    public Length getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(Length minWidth) {
        this.minWidth = minWidth;
    }

    public Length getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(Length minHeight) {
        this.minHeight = minHeight;
    }

    public Length getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(Length maxWidth) {
        this.maxWidth = maxWidth;
    }

    public Length getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(Length maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Length getTop() {
        return top;
    }

    public void setTop(Length top) {
        this.top = top;
    }

    public Length getBottom() {
        return bottom;
    }

    public void setBottom(Length bottom) {
        this.bottom = bottom;
    }

    public Length getRight() {
        return right;
    }

    public void setRight(Length right) {
        this.right = right;
    }

    public Length getLeft() {
        return left;
    }

    public void setLeft(Length left) {
        this.left = left;
    }

    public WhiteSpace getWhiteSpace() {
        return whiteSpace;
    }

    public void setWhiteSpace(WhiteSpace whiteSpace) {
        this.whiteSpace = whiteSpace;
    }
}
