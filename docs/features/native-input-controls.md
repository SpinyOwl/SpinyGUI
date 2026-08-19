# Native input controls

This document describes the native `<input>` behavior implemented by SpinyGUI.

## Dispatch model

`InputElement` remains the runtime model for every `<input>` element. Type-specific behavior is selected by `InputBehaviorRegistry` rather than by adding one node subclass per HTML input type.

The registry currently classifies inputs into:

- `TEXT` — `text`, `email`, `search`, `tel`, `url`, `password`
- `BUTTON` — `button`, `submit`, `reset`
- `CHECKBOX` — `checkbox`
- `RADIO` — `radio`
- `RANGE` — `range`
- `UNSUPPORTED` — all other input types

Unsupported input types remain normal `InputElement` instances but receive no native text-editing fallback. Raw GUI events may still be delivered to application listeners when appropriate.

## Text-family inputs

`text`, `email`, `search`, `tel`, and `url` reuse the existing single-line text editing behavior, including caret, selection, clipboard shortcuts, mouse selection, and horizontal caret viewport handling.

`password` uses the same runtime text model but renders a masked value. The original value remains stored in `InputElement.value()`.

## Button-family inputs

`button`, `submit`, and `reset` use button activation and intrinsic sizing semantics. `submit` and `reset` currently emit the same control-level `ActionEvent` as a button; form submission/reset semantics are not implemented yet.

## Checkbox

`checkbox` provides:

- runtime `checked` state;
- initialization from the boolean `checked` HTML attribute;
- mouse activation;
- Space-key activation;
- `ActionEvent` on successful activation;
- NanoVG rendering;
- native intrinsic sizing;
- `disabled` interaction suppression.

Runtime checked state is reflected when serializing the node back to HTML.

## Radio

`radio` provides:

- runtime `checked` state;
- initialization from `checked`;
- mouse and Space-key activation;
- mutual exclusion for radios with the same non-empty `name` within the current frame;
- NanoVG rendering;
- native intrinsic sizing;
- `disabled` interaction suppression.

Arrow-key navigation between radio-group members is not implemented yet.

## Range

`range` provides:

- `min`, `max`, and `step` processing;
- default range `0..100` and default step `1`;
- `step="any"`;
- clamping and step normalization;
- mouse press and drag updates;
- Arrow, PageUp/PageDown, Home, and End keyboard updates;
- NanoVG track/thumb rendering;
- native intrinsic sizing;
- `disabled` interaction suppression.

Programmatic value changes remain possible while the control is disabled.

## Disabled controls

Native input controls integrate with the common `disabled` attribute support and `:disabled` selector. Disabled controls do not accept native mouse, keyboard, or character-editing behavior.

## Not yet implemented

The following still require dedicated native behavior:

- `number`;
- `color`;
- `date`, `time`, `datetime-local`, `month`, `week`;
- `file`;
- `image` input activation semantics;
- `<select>`, `<option>`, and `<optgroup>`;
- `<form>` submission/reset semantics;
- form-level `input`/`change` semantics beyond the existing SpinyGUI event model;
- radio-group arrow-key navigation;
- keyboard Tab focus traversal.
