package me.github.simonplays15.betterbansystem.core.mute;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import java.util.Date;
import java.util.UUID;

public interface IMuteEntry {

    UUID uuid();

    String name();

    String source();

    Date created();

    Object expires();

    String reason();

}
