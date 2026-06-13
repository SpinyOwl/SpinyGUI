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
    String styles = getFlexCss();

    // in case if root of xml is not 'frame' method will return null.
    Frame frame = nodeParser.fromHtml(xml).frame();
    frame.styleSheets().add(styleSheetParser.parse(styles));

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
  overflow: scroll; /* default behaviour */
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
winframe {
    overflow: scroll;
    box-sizing: border-box; /* default behaviour */
}
winframe * {
    border: 8px solid #8c8c8c;
    box-sizing: border-box; /* default behaviour */
    overflow: hidden; /* default behaviour */
}
winframe:hover,
winframe *:hover {
  border: 8px solid #fc3131;
}
#t2 {
  width: 500px;
}
.wrap-demo {
  display: block;
  width: 120px;
  font-size: 16px;
  border-color: #45AAFF;
}
.word-wrap-demo {
  word-wrap: break-word;
}
.word-break-demo {
  word-break: break-all;
  border-color: #AA45FF;
}
.inline-block-demo {
  display: block;
  width: 320px;
  font-size: 16px;
  border-color: #2dd4bf;
}
.inline-chip {
  display: inline-block;
  width: auto;
  padding: 4px 8px;
  border: 3px solid #0f766e;
  background-color: #ccfbf1;
  overflow: hidden;
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
    Hello world. Lorem ipsum dolor.
    Hello world. Lorem ipsum dolor.
    Hello world. Lorem ipsum dolor.s
    Hello World
  </div>
  <div class="wrap-demo word-wrap-demo">
    word-wrap: Supercalifragilisticexpialidocious
  </div>
  <div class="wrap-demo word-break-demo">
    word-break: Supercalifragilisticexpialidocious
  </div>
  <div class="inline-block-demo">
    before <span class="inline-chip">inline block</span> after
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
  </div>
</winframe>
""";
  }
}
