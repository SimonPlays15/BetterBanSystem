package me.github.simonplays15.betterbansystem.core.automod;

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AutoModAction {

    /**
     * Private final variable 'type' of type AutoModActionType.
     * Represents the type of action in AutoModAction.
     * <p>
     * Possible values: DELETE, WARN, INSTANTBAN, MUTE, TIMEBAN, UNKNOWN
     * <p>
     * Containing class:
     * Class name: AutoModAction
     * Class fields: type, actionLimit, parameters
     * Class methods:
     * - public AutoModAction(@NotNull String actionString)
     * - public static AutoModAction @NotNull [] parseActions(@NotNull String actionsString)
     * - public static void processActions(String actionsString)
     * - private AutoModActionType getAutoModActionType(String type)
     * - public AutoModActionType getType()
     * - public int getActionLimit()
     * - public String getParameter(AutoModActionParameters key)
     * <p>
     * Enum AutoModActionType:
     * - DELETE: Represents deletion action.
     * - WARN: Represents warning action.
     * - INSTANTBAN: Represents instant ban action.
     * - MUTE: Represents mute action.
     * - TIMEBAN: Represents timed ban action.
     * - UNKNOWN: Represents unknown action.
     * <p>
     * Enum AutoModActionParameters:
     * Enum to represent different parameters available for AutoModAction.
     * Possible values: LIMIT, REASON, DURATION
     * <p>
     * Method getAutoModActionType(String type):
     * Private method to get the AutoModActionType from the given type string.
     * <p>
     * Method parseActions(String actionsString):
     * Static method to parse a string containing multiple actions separated by semicolon (;) and create an array of AutoModAction objects.
     * <p>
     * Method getType():
     * Public method to get the type of action in AutoModAction.
     * <p>
     * Method getParameter(AutoModActionParameters key):
     * Public method to get the parameter value based on the given key in AutoModAction.
     */
    private final AutoModActionType type;
    /**
     * Represents the action limit for an AutoModAction.
     * The action limit is the numeric value that follows the "!" in the AutoModAction string.
     */
    private final int actionLimit;
    /**
     * Represents a set of parameters for an AutoMod action.
     * The parameters are stored in a map with {@link AutoModActionParameters} as the keys and {@link String} as the values.
     * <p>
     * Example usage:
     * AutoModAction action = new AutoModAction("DELETE;LIMIT=3;REASON=Spam");
     * String limit = action.getParameter(AutoModActionParameters.LIMIT); // returns "3"
     * String reason = action.getParameter(AutoModActionParameters.REASON); // returns "Spam"
     *
     * @see AutoModActionParameters
     */
    private final Map<AutoModActionParameters, String> parameters = new HashMap<>();

    /**
     * Constructs a new AutoModAction object by parsing the given action string.
     * The action string should be in the format "actionType!actionLimit:parameter1=value1:parameter2=value2:...".
     *
     * @param actionString the string representation of the auto moderation action
     * @throws NullPointerException if the actionString is null
     */
    public AutoModAction(@NotNull String actionString) {
        String[] parts = actionString.split(":");
        String[] actionParts = parts[0].split("!");
        this.type = getAutoModActionType(actionParts[0]);
        this.actionLimit = actionParts.length > 1 ? Integer.parseInt(actionParts[1]) : 0;

        for (int i = 1; i < parts.length; i++) {
            String[] parameterParts = parts[i].split("=");
            this.parameters.put(getAutoModActionParameters(parameterParts[0].toUpperCase()), parameterParts.length > 1 ? parameterParts[1] : "undefined");
        }
    }

    /**
     * Parses a string representation of auto moderation actions and returns an array of AutoModAction objects.
     *
     * @param actionsString the string representation of the auto moderation actions
     * @return the array of AutoModAction objects parsed from the actionsString
     */
    public static AutoModAction @NotNull [] parseActions(@NotNull String actionsString) {
        String[] actionParts = actionsString.split(";");
        AutoModAction[] actions = new AutoModAction[actionParts.length];
        for (int i = 0; i < actionParts.length; i++) {
            actions[i] = new AutoModAction(actionParts[i]);
        }
        return actions;
    }

    @Override
    public String toString() {
        String params = parameters.entrySet().stream()
                .map(e -> "{" + "key=" + e.getKey() + ", value=" + e.getValue() + "}")
                .collect(Collectors.joining(","));
        return String.format("AutoModAction{type=%s, actionLimit=%s, parameters=[%s]}", type, actionLimit, params);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoModAction that = (AutoModAction) o;
        return actionLimit == that.actionLimit && type == that.type && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, actionLimit, parameters);
    }

    /**
     * Retrieves an Enum value from a given value.
     *
     * @param enumType the Class representing the Enum type.
     * @param value    the value to retrieve the Enum from.
     * @param <T>      the Enum type.
     * @return the Enum value corresponding to the given value, or the Enum value corresponding to "UNKNOWN" if the given value does not exist in the Enum.
     */
    private <T extends Enum<T>> T getEnumFromValue(Class<T> enumType, String value) {
        T enumValue;
        try {
            enumValue = Enum.valueOf(enumType, value);
        } catch (Exception e) {
            enumValue = Enum.valueOf(enumType, "UNKNOWN");
        }
        return enumValue;
    }

    /**
     * Retrieves the AutoModActionType enum value based on the given type.
     *
     * @param type The type to match with an AutoModActionType enum value.
     * @return The matched AutoModActionType enum value, or null if no match is found.
     */
    private AutoModActionType getAutoModActionType(String type) {
        return getEnumFromValue(AutoModActionType.class, type);
    }

    /**
     * Returns an instance of AutoModActionParameters corresponding to the given type.
     *
     * @param type the type of AutoModActionParameters to retrieve
     * @return an instance of AutoModActionParameters
     */
    private AutoModActionParameters getAutoModActionParameters(String type) {
        return getEnumFromValue(AutoModActionParameters.class, type);
    }

    /**
     * Returns the type of the AutoMod action.
     *
     * @return the AutoModActionType representing the type of the action
     */
    public AutoModActionType getType() {
        return type;
    }

    /**
     * Returns the action limit for the AutoMod action.
     *
     * @return the action limit
     */
    public int getActionLimit() {
        return actionLimit;
    }

    /**
     * Retrieves the value of the specified AutoModActionParameters key from the parameters map.
     *
     * @param key the AutoModActionParameters key
     * @return the value associated with the specified key, or null if the key is not found in the map
     */
    public String getParameter(AutoModActionParameters key) {
        return parameters.get(key);
    }
}
