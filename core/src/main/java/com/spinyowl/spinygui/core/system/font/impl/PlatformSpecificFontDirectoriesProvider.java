package com.spinyowl.spinygui.core.system.font.impl;

import com.spinyowl.spinygui.core.system.font.FontDirectoriesProvider;
import java.util.List;
import lombok.NonNull;

public class PlatformSpecificFontDirectoriesProvider implements FontDirectoriesProvider {

  private static final String OS_NAME = "os.name";

  /** Returns list of font directories available on current platform. */
  @Override
  public @NonNull List<String> getFontDirectories() {
    if (System.getProperty(OS_NAME).toLowerCase().contains("win")) {
      return getWindowsFontDirectories();
    } else if (System.getProperty(OS_NAME).toLowerCase().contains("mac")) {
      return getMacFontDirectories();
    } else if (System.getProperty(OS_NAME).toLowerCase().contains("nix")
        || System.getProperty(OS_NAME).toLowerCase().contains("nux")) {
      return getLinuxFontDirectories();
    } else {
      return List.of();
    }
  }

  /** Returns list of font directories available on windows. */
  private List<String> getWindowsFontDirectories() {
    return List.of(System.getenv("windir") + "\\Fonts\\");
  }

  /** Returns list of font directories available on mac. */
  private List<String> getMacFontDirectories() {
    return List.of(
        "/Library/Fonts/",
        "/System/Library/Fonts/",
        "/Network/Library/Fonts/",
        System.getProperty("user.home") + "/Library/Fonts");
  }

  /** Returns list of font directories available on linux. */
  private List<String> getLinuxFontDirectories() {
    return List.of(
        "/usr/share/fonts/",
        "/usr/local/share/fonts/",
        System.getProperty("user.home") + "/.fonts/");
  }
}
