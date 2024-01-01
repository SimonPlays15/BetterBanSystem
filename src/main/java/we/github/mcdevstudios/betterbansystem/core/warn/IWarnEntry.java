/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.warn;

import java.util.List;
import java.util.UUID;

public interface IWarnEntry {
    UUID uuid();

    String name();

    List<Warn> warns();

    void addWarn(Warn warn);

    boolean removeWarn(int id);
}
