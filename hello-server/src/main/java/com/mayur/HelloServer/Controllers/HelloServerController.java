package com.mayur.HelloServer.Controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("hello")
public class HelloServerController {
    @Autowired
    private ServerProperties serverProperties;

    @GetMapping
    public String hello() {
        log.info("Here");
        return "Hello form port " + serverProperties.getPort();
    }
}
