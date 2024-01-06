package me.github.simonplays15.betterbansystem.core.logging;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import java.util.logging.Level;

public enum LogLevel {

    INFO(Level.INFO), DEBUG(Level.INFO), TRACE(Level.INFO), WARNING(Level.WARNING), FATAL(Level.SEVERE), ERROR(Level.SEVERE);

    final Level realLevel;

    LogLevel(Level realLevel) {
        this.realLevel = realLevel;
    }
}
