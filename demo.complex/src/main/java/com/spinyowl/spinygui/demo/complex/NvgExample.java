package com.spinyowl.spinygui.demo.complex;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.event.CursorEnterEvent;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NvgExample extends Demo {
  private static final Logger LOG = LoggerFactory.getLogger(NvgExample.class);

  public NvgExample() {
    super(400, 400, "Example", new NvgRenderer());
  }

  public static void main(String[] args) {
    Demo demo = new NvgExample();
    demo.run();
  }

  @Override
  protected Frame createGuiElements(int width, int height) {
    // language=html
    String xml = getXml();
    String styles = getAbsoluteCss();

    // in case if root of xml is not 'frame' method will return null.
    Frame frame = nodeParser.fromXml(xml).frame();
    frame.styleSheets().add(styleSheetParser.parseStyleSheet(styles));

    Element c11 = frame.getElementById("c11");
    c11.addListener(
        CursorEnterEvent.class, event -> LOG.info("Entered at {}", event.cursorPosition()));

    return frame;
  }

  private String getAbsoluteCss() {
    // language=CSS
    return """
winframe,
winframe * {
  border: 8px solid #8c8c8c;
  box-sizing: border-box; /* default behaviour */
  overflow: hidden; /* default behaviour */
}
winframe:hover,
winframe *:hover {
  border: 8px solid #fc3131;
}
winframe {
  background-color: green;
  padding: 20px;
  flex-direction: column;
}

.text {
  display: block;
  overflow: auto;
  height: 90px;
  font-size: 16px;
  border-color: #45AAFF;
}

#t2 {
  position: absolute;
  bottom: 10px;
  top: 10px;
  height: auto;
  background-color: rgba(190,200,255,.8);
  border-color: rgba(190,200,255,.8) rgba(90,200,255,.8);
}
""";
  }

  private String getFlexCss() {
    // language=css
    return """
winframe,
winframe * {
  border: 8px solid #8c8c8c;
  box-sizing: border-box; /* default behaviour */
  overflow: hidden; /* default behaviour */
}
winframe:hover,
winframe *:hover {
  border: 8px solid #fc3131;
}
winframe {
  display: flex;
  background-color: green;
  padding: 20px;
  flex-direction: column;
}

 .text {
   display: block;
   overflow: auto;
   height: 90px;
   font-size: 16px;
 }

.c1,
.c2 {
  background-color: #77aaff;
  height: 80px;
  left: 0;
  right: 0;
  display: flex;
  flex-direction: row;
}

.c1 {
  top: 0;
  padding: 20px;
}

.c2 {
  border-color: #95a6c7;
  position: absolute;
  bottom: 0;
}

.c11,
.c12 {
  position: absolute;
  top: 40px;
  width: 60px;
  height: 60px;
}

.c11 {
  background-color: #eee;
  right: 20px;
}

.c12 {
  background-color: #eca861;
  right: 100px;
}
.c11:hover {
  background-color: #e45;
}

.s {
  flex-grow: 1;
  min-width: 20px;
  min-height: 20px;
  border: 1px solid black;
  background-color: #fff8;
}
""";
  }

  private String getXml() {
    // language=html
    return """
<winframe>
  <div class="c1">c1
    <div class="c11" id='c11'>c11 Cfjg</div>
    <div class="c12">c12</div>
  </div>
  <div class="text">
    Hello world. Lorem ipsum dolor.
    Hello world. Lorem ipsum dolor.
    Hello world. Lorem ipsum dolor.s
    Hello World
  </div>

  <div class='twrapper' style="border-color: pink">
    <div class='twrapper' style="border-color: blue">
      <div class="text" id='t2' style="border-color: black">
        Hello world. Lorem ipsum dolor.
        Hello world A. Lorem ipsum dolor.
        Hello world B. Lorem ipsum dolor.
      </div>
    </div>
  </div>
  <div class="c2">
    <div class="s">s1</div>
    <div class="s">s2</div>
  </div>
</winframe>
""";
  }
}
