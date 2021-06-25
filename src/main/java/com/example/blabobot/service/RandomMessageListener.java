package com.example.blabobot.service;

import com.example.blabobot.client.BalabobaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class RandomMessageListener extends ListenerAdapter {
    private static final Random r = new Random();

    private final BalabobaClient balabobaClient;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Message msg = event.getMessage();
        log.info("Got a message! {}", msg);
        String contentRaw = msg.getContentRaw();

        if (!msg.getAuthor().getAsTag().equals("TalkyBot#2382") && (System.getProperty("alwaysRespond").equals("true") || rollResponse())) {
            msg.reply(balabobaClient.callBalaboba(contentRaw)).queue();
        }
    }

    private boolean rollResponse() {
        return r.nextInt(100) % 10 == 0;
    }

}
