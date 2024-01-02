/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.mute;

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
