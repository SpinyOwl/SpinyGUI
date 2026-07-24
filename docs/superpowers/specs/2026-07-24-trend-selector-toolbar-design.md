# Trend Selector Toolbar Design

## Goal

Replace the history chart's interleaved vertical selector/chart grid with a compact, wrapping selector toolbar above one full-width selected chart. Additional metrics must wrap onto new rows instead of requiring selector scrolling or pushing later controls below the chart.

## Chosen Layout

- Render all native radio inputs and labels in one controls-only wrapper above the chart panels.
- Style labels as compact buttons in a horizontal flex container with wrapping enabled and a 4px gap.
- Render all chart panels in a separate full-width wrapper below the controls.
- Show only the panel associated with the checked radio by using generated CSS selectors and the existing stable series IDs.
- Retain selected, hover, and keyboard focus styling.

## Responsive Behavior

- Selector buttons wrap at every viewport width and never introduce horizontal selector scrolling.
- The selected chart uses the full available report width.
- The chart panel retains local horizontal scrolling when the readable SVG minimum width does not fit a narrow viewport.
- Page-level horizontal overflow remains prohibited.

## Accessibility

- Keep the selector as a native radio group so Tab enters the group and arrow keys change metrics.
- Associate every label with its radio input.
- Preserve a visible focus outline and selected-state contrast.
- Keep chart titles, descriptions, and keyboard-focusable data points unchanged.

## Alternatives Considered

- A contiguous vertical segmented rail would solve the current row gap but continue consuming chart width.
- A horizontally scrolling single-row selector would preserve chart width but make later metrics less discoverable.
- A native select element would be compact but would hide available metrics and reduce scanability.

## Testing

- Assert that the controls wrapper precedes the panels wrapper.
- Assert wrapping flex CSS and absence of selector overflow scrolling.
- Assert one checked radio and one visible selected panel mapping.
- Retain existing trend math, SVG coordinate, gap, accessibility, offline, and responsive chart tests.
- Regenerate the archive report and run the full project test suite.
