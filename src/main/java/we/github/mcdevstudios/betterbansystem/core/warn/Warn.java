/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.warn;

import java.util.Date;

public record Warn(int id, String source, Date created, String reason) {
}
