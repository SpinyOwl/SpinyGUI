package com.spinyowl.spinygui.core.converter.css.property.background;

import static com.spinyowl.spinygui.core.converter.css.Properties.BACKGROUND_SIZE;
import static com.spinyowl.spinygui.core.converter.css.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.types.Background.BackgroundSize;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.List;

public class BackgroundSizeProperty extends Property<BackgroundSize> {

  public static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);
  private static final String COVER = "cover";
  private static final String CONTAIN = "contain";
  private static final List<String> values = List.of(COVER, CONTAIN);

  public BackgroundSizeProperty() {
    super(BACKGROUND_SIZE, "auto", !INHERITED, ANIMATABLE,
        (s, c) -> s.background().size(c), nodeStyle -> nodeStyle.background().size(),
        BackgroundSizeProperty::extract, BackgroundSizeProperty::test);
  }

  //@formatter:off
  private static BackgroundSize extract(String value, Element element) {
    BackgroundSize backgroundSize = new BackgroundSize();
    switch (value) {
      case COVER: backgroundSize.cover(true); break;
      case CONTAIN: backgroundSize.contain(true); break;
      default: {
        String[] v = value.split("\\s+");
        if(v.length == 1) {
          Unit extracted = extractor.extract(v[0]);
          backgroundSize.width(extracted);
          backgroundSize.height(extracted);
        } else if (v.length == 2) {
          backgroundSize.width(extractor.extract(v[0]));
          backgroundSize.height(extractor.extract(v[1]));
        }
      }
    }
    return backgroundSize;
  }
  //@formatter:on

  private static boolean test(String value) {
    return values.contains(value) || testMultipleValues(value, "\\s+", 1, 2, extractor::isValid);
  }
}
