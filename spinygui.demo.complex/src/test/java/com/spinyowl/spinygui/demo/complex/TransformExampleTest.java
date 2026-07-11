package com.spinyowl.spinygui.demo.complex;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.spinyowl.spinygui.core.util.IOUtil;
import org.junit.jupiter.api.Test;

class TransformExampleTest {

  @Test
  void transformDemoResourcesResolveEveryStaticTransform() {
    String xml = IOUtil.resourceAsString(TransformExample.XML_RESOURCE);
    String css = IOUtil.resourceAsString(TransformExample.CSS_RESOURCE);

    assertNotNull(xml);
    assertNotNull(css);
    TransformExample.validateTransformStyleContract(xml, css);
  }
}
