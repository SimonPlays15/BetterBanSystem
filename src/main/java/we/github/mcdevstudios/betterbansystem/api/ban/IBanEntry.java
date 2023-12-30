/*
 * Copyright (c) MCDevStudios 2023. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.api.ban;

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
