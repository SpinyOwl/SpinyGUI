package com.spinyowl.spinygui.core.component;

import com.spinyowl.spinygui.core.component.base.Container;
import com.spinyowl.spinygui.core.converter.RawProcessor;

/**
 *
 */
public class Pre extends Container {

    {
        setAttribute(RawProcessor.PREFORMATTED_ATTRIBUTE, Boolean.TRUE.toString());
    }

}
