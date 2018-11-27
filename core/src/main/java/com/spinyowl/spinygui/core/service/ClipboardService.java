package com.spinyowl.spinygui.core.service;

/**
 * Clipboard service interface
 */
public interface ClipboardService {

    /**
     * Used to get string from clipboard.
     *
     * @return string from clipboard.
     */
    String getClipboardString();

    /**
     * Used to set string to clipboard.
     *
     * @param string string to set to clipboard.
     */
    void setClipboardString(String string);
}
