package me.github.simonplays15.betterbansystem.core.warn;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import java.util.List;
import java.util.UUID;

public interface IWarnEntry {
    UUID uuid();

    String name();

    List<Warn> warns();

    void addWarn(Warn warn);

    boolean removeWarn(int id);
}
