package com.spinyowl.spinygui.core.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class WindowCloseEvent extends Event {}
