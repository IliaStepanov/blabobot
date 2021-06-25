package com.example.blabobot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordService {

    private final RandomMessageListener randomMessageListener;

    @PostConstruct
    public void init() throws LoginException {
        JDA jda = JDABuilder.createDefault(System.getProperty("botToken")).build();

        jda.addEventListener(randomMessageListener);
    }
}
