# Font-family resolution policy

SpinyGUI resolves CSS `font-family` as an ordered fallback chain. The bundled
default is:

```css
font-family: Roboto, "Noto Sans CJK SC";
```

For each code point, the core resolver selects the first available face in the
declared order that supplies a glyph. Face matching remains deterministic for
style, weight, and stretch. An unavailable family is skipped; it does not cause
an arbitrary system font to be selected.

System fonts participate only when the application explicitly loads them and
the family is named in CSS. Merely being installed on the host does not alter
the bundled default or introduce implicit operating-system fallback.

Core measurement produces immutable resolved runs. Layout, caret geometry,
selection geometry, and NanoVG rendering consume those runs so that face
selection and cumulative advances remain consistent. A code point absent from
every configured face is rendered as a visible U+FFFD replacement marker while
retaining the original source UTF-16 range.

`ResolvedStyle.fontFamilies()` exposes the ordered CSS family list rather than
a set. Grapheme clustering, complex-script shaping, and generic-family aliases
remain outside this first-release policy.
