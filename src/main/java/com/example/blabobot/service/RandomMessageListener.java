package com.example.blabobot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static com.example.blabobot.service.MessageProcessor.isaBotMessage;

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

        if (isaBotMessage(msg.getReferencedMessage())) {
            messageType = MessageProcessor.MessageType.RESPONSE;
        }

        switch (messageType) {
            case USAGE:
                messageProcessor.respondUsage(contentRaw, msg);
                break;
            case DIRECT:
                messageProcessor.respondDirect(contentRaw, msg);
                break;
            case RESPONSE:
                messageProcessor.respond(contentRaw, msg);
                break;
            case SETTINGS:
                messageProcessor.setSettings(contentRaw, msg);
                break;
            default:
                messageProcessor.respondRandomly(contentRaw, msg);
        }
    }
}
