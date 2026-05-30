package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.types.Display;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

class NvgInlineFormattingOffsetTest {

  @Test
  void elementInlineFormattingOffset_subtractsContainingBlockScroll() {
    Element block = scrolledBlock();
    Element inline = inlineElement();
    block.addChild(inline);

    Vector2f offset = new NvgElementRenderer().inlineFormattingOffset(inline);

    assertEquals(80, offset.x());
    assertEquals(35, offset.y());
  }

  @Test
  void textInlineFormattingOffset_subtractsContainingBlockScroll() {
    Element block = scrolledBlock();
    Text text = new Text("content");
    block.addChild(text);

    Vector2f offset = new NvgTextRenderer().inlineFormattingOffset(text);

    assertEquals(80, offset.x());
    assertEquals(35, offset.y());
  }

  @Test
  void borderInlineFormattingOffset_subtractsContainingBlockScroll() {
    Element block = scrolledBlock();
    Element inline = inlineElement();
    block.addChild(inline);

    Vector2f offset = new NvgBorderRenderer().inlineFormattingOffset(inline);

    assertEquals(80, offset.x());
    assertEquals(35, offset.y());
  }

  private Element scrolledBlock() {
    Element block = NodeBuilder.div();
    block.box().contentPosition(100, 60);
    block.box().contentSize(200, 100);
    block.scrollLeft(20);
    block.scrollTop(25);
    return block;
  }

  private Element inlineElement() {
    Element inline = NodeBuilder.div();
    inline.resolvedStyle().display(Display.INLINE);
    return inline;
  }
}
