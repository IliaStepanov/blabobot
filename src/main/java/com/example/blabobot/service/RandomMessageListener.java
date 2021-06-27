package com.example.blabobot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RandomMessageListener extends ListenerAdapter {

    private final MessageProcessor messageProcessor;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Message msg = event.getMessage();
        log.info("Got a message! {}", msg);
        String contentRaw = msg.getContentRaw();

        String[] splitMessage = contentRaw.split(" ");

        MessageProcessor.MessageType messageType = MessageProcessor.MessageType.fromString(splitMessage[0]);

        switch (messageType) {
            case USAGE:
                messageProcessor.respondUsage(contentRaw, msg);
                break;
            case DIRECT:
                messageProcessor.respondDirect(contentRaw, msg);
                break;
            default:
                messageProcessor.respondRandomly(contentRaw, msg);
        }
    }
}
