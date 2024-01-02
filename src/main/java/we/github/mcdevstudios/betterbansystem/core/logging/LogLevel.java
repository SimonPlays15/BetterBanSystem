/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.logging;

import java.util.logging.Level;

public enum LogLevel {

    INFO(Level.INFO), DEBUG(Level.INFO), TRACE(Level.INFO), WARNING(Level.WARNING), FATAL(Level.SEVERE), ERROR(Level.SEVERE);

    final Level realLevel;

    LogLevel(Level realLevel) {
        this.realLevel = realLevel;
    }
}