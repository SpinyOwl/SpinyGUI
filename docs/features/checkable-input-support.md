# Checkable input support

SpinyGUI supports empty `<input type="checkbox">` and `<input type="radio">` controls.
The boolean `checked` attribute initializes their state; application code can update it with
`InputElement.checked(boolean)`. The supported `:checked` selector follows that runtime state.

Checkboxes toggle through pointer release or Space. Radios select through Space, and arrow keys
move through enabled peers with the same nonblank `name`. A radio group is limited to descendants
of its owning `Frame`; forms are not modeled. Selecting a radio clears its matching peers.

User-driven state changes emit both `ActionEvent` and `ChangeEvent`; XML handlers may use
`on-action` and `on-change`. Disabled checkable controls cannot change. NanoVG renders a native
16 px square checkbox or circular radio indicator when CSS leaves its size automatic.

Form submission, labels, indeterminate checkbox state, and browser-complete form semantics are
not implemented.
