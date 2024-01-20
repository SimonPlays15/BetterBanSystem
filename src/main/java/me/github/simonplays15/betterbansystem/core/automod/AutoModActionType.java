package me.github.simonplays15.betterbansystem.core.automod;/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/**
 * The AutoModActionType enum represents the types of actions that can be taken by an auto-moderation system.
 * These actions include delete, warn, instant ban, mute, time ban, and unknown.
 */
public enum AutoModActionType {

    /**
     * The DELETE variable represents the action of deleting a resource or data.
     */
    DELETE,
    /**
     *
     */
    WARN,
    /**
     * The INSTANTBAN variable represents the action type for instant banning in an auto-moderation system.
     * <p>
     * The INSTANTBAN action type is used when a user has committed a severe violation that requires an immediate ban.
     * <p>
     * Example usage:
     * AutoModActionType actionType = INSTANTBAN;
     *
     * @see AutoModActionType
     */
    INSTANTBAN,
    /**
     * The MUTE variable represents the action of muting a user in an auto-moderation system.
     * When this action is triggered, the user's ability to speak in the chat is temporarily removed.
     * This is often used as a punishment for violating community guidelines or rules.
     */
    MUTE,
    /**
     * The TIMEBAN variable represents the action of temporarily banning a user for a specific duration of time.
     * It is one of the actions that can be taken by an auto-moderation system.
     */
    TIMEBAN,
    /**
     * The UNKNOWN variable represents an unknown auto-moderation action type.
     * It is one of the possible values of the AutoModActionType enum.
     * The meaning and consequences of this action type are dependent on the specific implementation of the auto-moderation system.
     * Unknown actions may indicate errors, unexpected situations, or unsupported actions.
     * Developers should handle this action type appropriately based on the requirements and behavior of the auto-moderation system.
     */
    UNKNOWN

}

