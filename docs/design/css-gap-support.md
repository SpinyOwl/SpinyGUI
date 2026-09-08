# CSS gap support

Grid and flex containers support `gap`, `row-gap`, and `column-gap`.
The legacy `grid-gap`, `grid-row-gap`, and `grid-column-gap` names remain aliases.
Declarations update both names in order, so a later longhand overrides the
corresponding part of an earlier shorthand.

```css
.container {
  display: flex;
  gap: 8px 12px;
}
```

One value sets both axes; two values set row spacing followed by column spacing.
Accepted values are nonnegative lengths supported by the existing CSS length
parser, percentages, unitless zero, and `normal`. For grid and flex, `normal`
uses zero spacing. Negative values and malformed shorthands are rejected.

Grid uses its existing track-spacing calculation. Flex maps both axes to Yoga
gutters, including wrapped lines and column direction. Intrinsic flex row widths
include fixed gaps between participating items; cyclic percentage gaps contribute
zero to that intrinsic width calculation. This change does not add multi-column
layout or CSS math expressions such as `calc()`.

Regression coverage is in `GridStyleManagerTest`, `LayoutServiceProviderGridTest`,
and `IntrinsicFlexLayoutTest`, covering shorthand expansion, invalid values,
alias precedence, fixed and percentage grid/flex geometry, and nested intrinsic
flex widths.

Semantics follow [CSS Box Alignment, gutters](https://www.w3.org/TR/css-align-3/#gaps).
