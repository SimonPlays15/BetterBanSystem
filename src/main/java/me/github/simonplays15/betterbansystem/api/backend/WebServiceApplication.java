package me.github.simonplays15.betterbansystem.api.backend;/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import me.github.simonplays15.betterbansystem.api.backend.handlers.ApiError;

import java.util.Map;

/**
 * The WebServiceApplication class represents a web service application that can be started
 * and stopped using the provided methods.
 */
public class WebServiceApplication {

    /**
     * The app variable represents the Javalin instance used to create and manage the web service application.
     *
     * <p>
     * The Javalin class is a lightweight Java web framework that facilitates the creation of RESTful web services.
     * It provides a simple and intuitive API for handling HTTP requests and responses.
     * </p>
     *
     * <p>
     * The Javalin instance is created and started using the {@link WebServiceApplication#start(int)} method.
     * The port number on which the web service application will listen for incoming requests should be specified as an argument.
     * </p>
     *
     * <p>
     * The web service application can be stopped by calling the {@link WebServiceApplication#stop()} method, which
     * will gracefully shut down the application and stop listening for new requests.
     * </p>
     */
    private static Javalin app;

    public static void main(String[] args) {
        int port = args.length >= 1 ? Integer.parseInt(args[0]) : 8080;
        start(port);
    }

    /**
     * Starts the web service application on the specified port.
     *
     * @param port the port number on which the web service application will listen for incoming requests
     */
    public static void start(int port) {
        app = Javalin.create(config -> {
            config.staticFiles.add("/dist", Location.CLASSPATH);
        });

        app.get("/", ctx -> ctx.redirect("/index.html"));
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.json(Map.of(
                    "status", "error",
                    "message", "An internal server error has occurred",
                    "exception", e.getMessage(),
                    "caused", e.getCause().toString(),
                    "path", ctx.path()
            ));
            e.printStackTrace(System.err);
        });

        app.get("/api", ctx -> {
            ctx.status(HttpStatus.UNAUTHORIZED);
        });

        for (HttpStatus status : HttpStatus.values()) {
            if (status.isError() || status.isServerError() || status.isClientError())
                app.error(status, context -> {
                    context.status(status);
                    ApiError error = new ApiError(status.getCode(), status.getMessage(), context.path());
                    context.json(error);
                });
        }

        app.start(port);

    }

    /**
     * Stops the web service application if it is running.
     */
    public static void stop() {
        if (app != null)
            app.stop();
    }

}
