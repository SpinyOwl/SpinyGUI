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
    demo.start();
  }

  @Override
  protected Frame createGuiElements(int width, int height) {
    String xml = getXml();
    String styles = getFlexCss();

    // in case if root of xml is not 'frame' method will return null.
    Frame frame = nodeParser.fromXml(xml).frame();
    frame.styleSheets().add(styleSheetParser.parse(styles));

    Element c11 = frame.getElementById("c11");
    c11.addListener(
        CursorEnterEvent.class, event -> LOG.info("Entered at {}", event.cursorPosition()));

    return frame;
  }

  private String getFlexCss() {
    // language=css
    return """
winframe,
winframe * {
  border: 8px solid #8c8c8c;
  box-sizing: border-box; /* default behaviour */
  overflow: hidden; /* default behaviour */
 font-weight: 300;
}
winframe::scrollbar {
  width: 8px;
  height: 8px;
  background-color: orange;
}
winframe::scrollbar:hover {
  background-color: red;
}
winframe:hover,
winframe *:hover {
  border: 8px solid #fc3131;
}
#t2 {
  width: 500px;
}
#t2::before {
  content: "Before #t2 text!";
  background-color: yellow;
}
#t2::after {
  content: "After #t2 text!";
  background-color: orange;
}
""";
  }

  private String getXml() {
    // language=html
    return """
<winframe>
  <div class="wrapper" style="border-color:cyan">
    <div class="c1">c1
      <div class="c11" id='c11'>c11 Cfjg</div>
      <div class="c12">c12</div>
    </div>
    <div class="text">
      Hello world 1. Lorem ipsum dolor.
      Hello world. Lorem ipsum dolor.
      Hello world. Lorem ipsum dolor.s
      Hello World
    </div>

    <div class='twrapper' style="border-color: pink">
      <div class='twrapper' style="border-color: blue">
        <div class="text" id='t2' style="border-color: black">
          Hello world 2. Lorem ipsum dolor. Hello world A. Lorem ipsum dolor. Hello world. Lorem ipsum dolor. Hello world A. Lorem ipsum dolor.
          <div style='display: none'>Hello world 3. Lorem ipsum dolor.</div>
          Hello world 4. Lorem ipsum dolor. Hello world A. Lorem ipsum dolor. Hello world. Lorem ipsum dolor. Hello world A. Lorem ipsum dolor.
          <div>Hello world 5. Lorem ipsum dolor.</div>
          Hello world 6. Lorem ipsum dolor. Hello world A. Lorem ipsum dolor. Hello world. Lorem ipsum dolor. Hello world A. Lorem ipsum dolor.
        </div>
      </div>
    </div>
    <div class="c2">
      <div class="s">s1</div>
      <div class="s">s2</div>
    </div>
  </div>
</winframe>
""";
  }
}
