package com.spinyowl.spinygui.demo.complex;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import com.spinyowl.spinygui.core.util.IOUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransformExampleTest {

  private FontServiceImpl fontService;

  @BeforeEach
  void installSemanticFontOwner() {
    fontService = new FontServiceImpl(new FontStorageImpl(), true);
    fontService.installSemanticOwner();
  }

  @AfterEach
  void closeSemanticFontOwner() {
    if (Font.hasSemanticOwner()) {
      Font.semanticService().close();
    }
  }

  @Test
  void transformDemoResourcesResolveEveryStaticTransform() {
    String xml = IOUtil.resourceAsString(TransformExample.XML_RESOURCE);
    String css = IOUtil.resourceAsString(TransformExample.CSS_RESOURCE);

    assertNotNull(xml);
    assertNotNull(css);
    TransformExample.validateTransformStyleContract(xml, css);
  }
}
