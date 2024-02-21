package me.github.simonplays15.betterbansystem.api.backend;/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import io.javalin.http.util.NaiveRateLimit;
import me.github.simonplays15.betterbansystem.api.backend.auth.AuthHandler;
import me.github.simonplays15.betterbansystem.api.backend.handlers.ApiError;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.server.session.SessionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static io.javalin.apibuilder.ApiBuilder.*;

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
        Thread thread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String string = scanner.nextLine();
                if (string.equals("stop")) {
                    stop();
                    scanner.close();
                    System.exit(0);
                }
            }
        });

        thread.start();
    }

    static @NotNull SessionHandler customSessionHandler() {
        final SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setHttpOnly(true);
        sessionHandler.setSecureRequestOnly(true);
        sessionHandler.getSessionCookieConfig().setSecure(true);
        sessionHandler.setMaxInactiveInterval(60 * 15);
        sessionHandler.setSameSite(HttpCookie.SameSite.STRICT);
        return sessionHandler;
    }

    /**
     * Starts the web service application on the specified port.
     *
     * @param port the port number on which the web service application will listen for incoming requests
     */
    public static void start(int port) {
        app = Javalin.create(config -> {
            config.staticFiles.add("dist", Location.CLASSPATH);
            config.http.defaultContentType = "application/json";
            config.showJavalinBanner = false;
            config.jetty.modifyServletContextHandler(handler -> {
                handler.setSessionHandler(customSessionHandler());
            });
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.allowHost("http://localhost:5173");
                    it.allowCredentials = true;
                    it.exposeHeader("x-server");
                });
            });
            config.router.apiBuilder(() -> {
                path("/api/v1", () -> {
                    get(context -> context.status(HttpStatus.METHOD_NOT_ALLOWED));
                    post(context -> context.status(HttpStatus.OK));
                    delete(context -> context.req().getSession().invalidate());
                });
                path("/health", () -> {
                    Handler handler = context -> {
                        context.status(HttpStatus.OK);
                    };
                    post(handler);
                    get(handler);
                });
            });
            config.router.mount(router -> {
                router.beforeMatched(AuthHandler::handleAccess);
                router.beforeMatched(context -> NaiveRateLimit.requestPerTimeUnit(context, 10, TimeUnit.SECONDS));
            });
            config.bundledPlugins.enableDevLogging();
        });
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.json(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), e.getMessage(), ctx.contextPath()));
            e.printStackTrace(System.err);
        });
        for (HttpStatus status : HttpStatus.values()) {
            if (status.isError() || status.isClientError() || status.isServerError()) {
                app.error(status, context -> {
                    context.status(status);
                    context.json(Map.of("status", status.getCode(),
                            "message", status.getMessage(), "path", context.path()));
                });
            }
        }
        app.start(port);
        Runtime.getRuntime().addShutdownHook(new Thread(WebServiceApplication::stop));
    }

    /**
     * Stops the web service application if it is running.
     */
    public static void stop() {
        if (app != null) {
            app.jettyServer().server().getSessionIdManager().getSessionHandlers().forEach(handlers -> {
                handlers.getSessionCache().setInvalidateOnShutdown(true);
                try {
                    handlers.getSessionCache().getSessionDataStore().stop();
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            });
            app.stop().javalinServlet().destroy();
            app = null;
        }
    }

}
