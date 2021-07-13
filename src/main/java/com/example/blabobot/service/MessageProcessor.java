package com.example.blabobot.service;

import com.example.blabobot.client.BalabobaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.blabobot.service.MessageProcessor.MessageType.DIRECT;
import static com.example.blabobot.service.MessageProcessor.MessageType.SETTINGS;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProcessor {
    private static final Random r = new Random();
    private final BalabobaClient balabobaClient;

    private static ThreadLocal<Map<String, int[]>> serverSettings = ThreadLocal.withInitial(HashMap::new);

    private final int[] defaultAllowedStyles = new int[]{3, 4, 5};

    public void respondUsage(String text, Message msg) {

        String usageMessage = "```Я умею(или нет) выполнять две команды: \n " +
                "!blabla-help чтобы увидеть это сообщение \n " +
                "!blabla [стиль ответа] [твое сообщение] чтобы точно получить ответ \n " +
                "Доступные стили:\n" +
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
                "!blabla 11 Вот бы сейчас попасть на море!\n\n" +
                "Для установки стилей ответа на этом сервере:\n" +
                "!blabla-settings 1,2,3\n\n" +
                "Активные сейчас стили: %s \n\n" +
                "В остальное время я отвечаю на случайные сообщения и ответы на мои сообщения.```";

        if (!isaBotMessage(msg)) {
            msg.reply(String.format(usageMessage, Arrays.toString(getAllowedStyles(msg)))).queue();
        }
    }

    public void respondDirect(String text, Message msg) {

        try {

            String rawText = text.replace(DIRECT.value + " ", "");
            String[] split = rawText.split(" ");
            int style = Integer.parseInt(split[0]);

            StringBuilder messageBuilder = new StringBuilder();
            for (int i = 1; i < split.length; i++) {
                messageBuilder.append(split[i]).append(" ");
            }

            if (!isaBotMessage(msg)) {
                msg.reply(balabobaClient.callBalaboba(messageBuilder.toString(), style)).queue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void respond(String text, Message msg) {

        if (!isaBotMessage(msg) && (System.getProperty("alwaysRespond").equals("true"))) {

            int[] allowedStyles = getAllowedStyles(msg);
            msg.reply(balabobaClient.callBalaboba(text, allowedStyles[r.nextInt(allowedStyles.length - 1)])).queue();
        }
    }

    public void respondRandomly(String text, Message msg) {

        if (!isaBotMessage(msg) && (System.getProperty("alwaysRespond").equals("true") || rollResponse())) {

            int[] allowedStyles = getAllowedStyles(msg);
            msg.reply(balabobaClient.callBalaboba(text, allowedStyles[r.nextInt(allowedStyles.length)])).queue();
        }
    }

    public void setSettings(String text, Message msg) {

        String rawText = text.replace(SETTINGS.value + " ", "");
        String[] split = rawText.split(",");

        int[] newSettings = new int[split.length];

        for (int i = 0; i < split.length; i++) {
            newSettings[i] = Integer.parseInt(split[i]);
        }

        serverSettings.get().put(msg.getGuild().getId(), newSettings);

    }

    public static boolean isaBotMessage(Message msg) {
        return msg != null && msg.getAuthor().getAsTag().equals("TalkyBot#2382");
    }

    private boolean rollResponse() {
        return r.nextInt(101) % 10 == 0;
    }

    private int[] getAllowedStyles(Message msg) {
        int[] allowedStyles = serverSettings.get().get(msg.getGuild().getId());

        if (allowedStyles == null || allowedStyles.length == 0) {
            allowedStyles = defaultAllowedStyles;
        }

        return allowedStyles;
    }


    public enum MessageType {
        USAGE("!blabla-help"),
        DIRECT("!blabla"),
        RESPONSE("responce"),
        SETTINGS("!blabla-settings"),
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
}
