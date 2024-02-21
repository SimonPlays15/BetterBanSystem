package me.github.simonplays15.betterbansystem.api.backend.controller;/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import me.github.simonplays15.betterbansystem.api.backend.auth.entities.AdminUser;
import me.github.simonplays15.betterbansystem.api.backend.auth.entities.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UserController {

    private static final List<User> userStore = new ArrayList<>();

    static {
        userStore.add(new AdminUser()); // TODO
    }

    public static void updateUser(User newUser) {
        int index = userStore.indexOf(newUser);

        if (index == -1)
            return;

        userStore.remove(index);
        userStore.add(index, newUser);

    }

    public static @Nullable User getUserByUserId(String userId) {
        for (User user : userStore) {
            if (user.getUserid() == null) continue;
            if (user.getUserid().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public static @Nullable User getUserByUsername(String username) {
        for (User user : userStore) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static void addUser(User user) {
        if (userStore.contains(user))
            return;
        userStore.add(user);
    }

    public static void removeUserByUsername(String username) {
        User user = getUserByUsername(username);
        if (user == null)
            return;
        userStore.remove(user);
    }

    public static void removeUser(User user) {
        if (!userStore.contains(user))
            return;
        userStore.remove(user);
    }

    @Contract(pure = true)
    public static List<User> getUserStore() {
        return userStore;
    }

}
