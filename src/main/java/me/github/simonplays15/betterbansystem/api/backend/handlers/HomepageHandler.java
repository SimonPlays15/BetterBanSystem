package me.github.simonplays15.betterbansystem.api.backend.handlers;/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;

public class HomepageHandler implements Handler {

    public HomepageHandler() {

    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        context.result(new FileInputStream("/dist/index.html"));
    }
}
