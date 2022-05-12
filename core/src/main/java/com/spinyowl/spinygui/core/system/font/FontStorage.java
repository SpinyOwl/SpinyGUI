package com.spinyowl.spinygui.core.system.font;

import java.nio.ByteBuffer;
import lombok.NonNull;

public interface FontStorage {

  ByteBuffer getFontData(@NonNull String path);

  ByteBuffer loadFont(String fontPath);
}
