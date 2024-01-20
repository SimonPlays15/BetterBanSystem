package me.github.simonplays15.betterbansystem.core.warn;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import java.util.Date;

/**
 * Represents a warning.
 */
public record Warn(int id, String source, Date created, String reason) {
}
