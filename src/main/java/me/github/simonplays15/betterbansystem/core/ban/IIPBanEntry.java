package me.github.simonplays15.betterbansystem.core.ban;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import java.util.Date;

public interface IIPBanEntry {

    String ip();

    String source();

    Date created();

    Object expires();

    String reason();

}
