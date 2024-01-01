/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.ban;

import java.util.Date;
import java.util.UUID;

public interface IBanEntry {

    UUID uuid();

    String name();

    String source();

    Date created();

    Object expires();

    String reason();

}
