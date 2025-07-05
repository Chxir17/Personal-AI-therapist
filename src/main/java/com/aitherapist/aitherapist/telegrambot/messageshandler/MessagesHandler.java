package com.aitherapist.aitherapist.telegrambot.messageshandler;

import com.aitherapist.aitherapist.telegrambot.dto.MedicalAnalysisResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.http.*;
import java.util.Map;
import org.springframework.http.*;

/**
 * MessagesHandler - message handler.
 * check is this medical data.
 * parse data, put to database and send to ai assistent
 */
@Getter
@Setter
@Component
@Slf4j
public class MessagesHandler implements IMessageHandler {
    private final RestTemplate restTemplate = new RestTemplate();
    //@Value("${python.service.url}")
    private String pythonServiceUrl;

    public JSONObject jsonObject = new JSONObject();

    /**
     * canHandle - check is this medical data.
     * Fix: add json parse
     * @param messageText
     * @return
     */
    @Override
    public boolean canHandle(String messageText) {// request to python part (parse)
        return false;
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            Map<String, String> request = Map.of("text", messageText); //FIX
//            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
//
//            ResponseEntity<MedicalAnalysisResult> response = restTemplate.exchange(
//                    pythonServiceUrl,
//                    HttpMethod.POST,
//                    entity,
//                    MedicalAnalysisResult.class
//            );
//
//            return response.getBody().isMedical();
//        } catch (Exception e) {
//            return false;
//        }
    }

    /**
     * Put information from Update(json) to database.
     * @param update
     * @return
     */
    @Override
    public void handle(Update update) {

    }
}
