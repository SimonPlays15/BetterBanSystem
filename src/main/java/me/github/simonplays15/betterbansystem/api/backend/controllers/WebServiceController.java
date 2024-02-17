package me.github.simonplays15.betterbansystem.api.backend.controllers;/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebServiceController {

    @RequestMapping(value = {"/", "/index", "/app"})
    public String index() {
        return "index";
    }

}
