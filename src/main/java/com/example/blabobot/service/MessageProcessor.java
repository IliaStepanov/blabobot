package com.example.blabobot.service;

import com.example.blabobot.client.BalabobaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Component;

import java.util.Random;

import static com.example.blabobot.service.MessageProcessor.MessageType.DIRECT;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProcessor {
    private static final Random r = new Random();
    private final BalabobaClient balabobaClient;

    private final int[] allowedStyles = new int[]{3, 4, 5};

    public void respondUsage(String text, Message msg) {
        String usageMessage = "```Я умею(или нет) выполнять две команды: \n " +
                "!usage чтобы увидеть это сообщение \n " +
                "!blabla [стиль ответа] [твое сообщение] чтобы точно получить ответ \n " +
                "Доступные стили:" +
                "0 Без стиля\n" +
                "1 Теории заговора\n" +
                "2 ТВ-Репортажи\n" +
                "3 Тосты\n" +
                "4 Пацанские цитаты\n" +
                "5 Рекламные слоганы\n" +
                "6 Короткие истории\n" +
                "7 Подписи в инстаграм\n" +
                "8 Википедия\n" +
                "9 Синопсы фильмов\n" +
                "10 Гороскоп\n" +
                "11 Народные Мудрости\n" +
                "Пример:" +
                "!blabla 3 Вот бы сейчас попасть на море!```";

        if (!msg.getAuthor().getAsTag().equals("TalkyBot#2382")) {
            msg.reply(usageMessage).queue();
        }
    }

    public void respondDirect(String text, Message msg) {
        try {
            String rawText = text.replace(DIRECT.value, "");
            int style = rawText.charAt(1);
            String message = rawText.substring(3);

            if (!msg.getAuthor().getAsTag().equals("TalkyBot#2382")) {
                msg.reply(balabobaClient.callBalaboba(message, style)).queue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void respondRandomly(String text, Message msg) {

        if (!msg.getAuthor().getAsTag().equals("TalkyBot#2382") && (System.getProperty("alwaysRespond").equals("true") || rollResponse())) {
            msg.reply(balabobaClient.callBalaboba(text, allowedStyles[r.nextInt(3)])).queue();
        }

    }

    private boolean rollResponse() {
        return r.nextInt(101) % 10 == 0;
    }


    public enum MessageType {
        USAGE("!usage"),
        DIRECT("!blabla"),
        UNKNOWN("unknown");

        private final String value;

        MessageType(String value) {
            this.value = value;
        }

        public static MessageType fromString(String string) {
            for (MessageType messageType : MessageType.values()) {
                if (messageType.value.equals(string)) {
                    return messageType;
                }
            }
            return UNKNOWN;
        }
    }

    public static void main(String[] args) {
        System.out.println(" 3 I want".substring(3));
    }
}
