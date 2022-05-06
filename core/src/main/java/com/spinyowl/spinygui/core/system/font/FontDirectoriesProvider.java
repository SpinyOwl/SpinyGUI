package com.spinyowl.spinygui.core.system.font;

import java.util.List;
import lombok.NonNull;

public interface FontDirectoriesProvider {

  @NonNull
  List<String> getFontDirectories();
}
