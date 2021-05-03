package com.spinyowl.spinygui.core.event;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
public class FileDropEvent extends Event {

  @NonNull private final ImmutableList<String> paths;
}
