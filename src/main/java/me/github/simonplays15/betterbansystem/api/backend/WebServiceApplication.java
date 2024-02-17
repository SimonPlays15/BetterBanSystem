package me.github.simonplays15.betterbansystem.api.backend;/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The WebServiceApplication class is the main class responsible for starting and stopping the web service application.
 */
@SpringBootApplication
public class WebServiceApplication {

    /**
     * The ConfigurableApplicationContext context is a static variable that holds the application context of the web service application.
     * It is of type ConfigurableApplicationContext, which is a sub-interface of ApplicationContext interface in the Spring Framework.
     * <p>
     * The application context holds the beans and their configurations, and provides the means to retrieve and manage them.
     * Being static, the context variable can be accessed from anywhere in the code without the need for creating an instance of the WebServiceApplication class.
     * <p>
     * The context variable is initialized by the start(String[] args) method, which is responsible for starting the web service application.
     * It uses the SpringApplication.run() method to create the application context by scanning the classpath and loading all the bean definitions.
     * The start(String[] args) method typically gets called during the application startup process to set up the application context.
     * <p>
     * To stop the web service application, the stop() method needs to be called.
     * The stop() method checks if the context variable is not null to prevent any runtime exceptions,
     * and then closes the context by calling the close() method on it.
     * <p>
     * It is important to note that the context variable should not be directly modified or reassigned without proper understanding and consideration,
     * as it can lead to unexpected behavior in the application.
     */
    public static ConfigurableApplicationContext context;

    /**
     * The main method is the entry point of the application. It calls the start method to start the web service application.
     *
     * @param args the command line arguments passed to the application
     */
    public static void main(String[] args) {
        start(args);
    }

    /**
     * Starts the web service application.
     *
     * @param args the command line arguments passed to the application
     */
    public static void start(String[] args) {
        context = SpringApplication.run(WebServiceApplication.class, args);
    }

    /**
     * Stops the web service application.
     * If the application is running, it will be closed.
     */
    public static void stop() {
        if (context != null)
            context.close();
    }

}
