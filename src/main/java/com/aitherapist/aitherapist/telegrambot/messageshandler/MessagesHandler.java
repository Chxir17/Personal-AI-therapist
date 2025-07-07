package com.aitherapist.aitherapist.telegrambot.messageshandler;

import com.aitherapist.aitherapist.telegrambot.dto.MedicalAnalysisResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * FIXME: maybe add apache kafka?
 * MessagesHandler - message handler.
 * check is this medical data.
 * parse data, put to database and send to ai assistent
 */
@Getter
@Setter
@Component
@Slf4j
public class MessagesHandler implements IHandler {
    private final RestTemplate restTemplate = new RestTemplate();
    private String pythonServiceUrl;
    private MedicalAnalysisResult medicalAnalysisResult;

    /**
     * FIXME: add json parse
     * canHandle - check is this medical data.
     * @param messageText
     * @return
     */
    @Override
    public boolean canHandle(String messageText) {
        return false;
    }

    /**
     * FIXME: to implement
     * Put information from Update(json) to database.
     * @param update
     * @return
     */
    @Override
    public void handle(Update update) {

    }
}
