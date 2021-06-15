open module com.spinyowl.spinygui {
  requires transitive cbchain;

  requires transitive com.spinyowl.spinygui.core;
  requires transitive com.spinyowl.spinygui.core.backend;
  requires transitive com.spinyowl.spinygui.core.backend.lwjgl.nanovg;
  requires transitive com.spinyowl.spinygui.core.parser;
  requires transitive com.spinyowl.spinygui.core.parser.impl;
}
