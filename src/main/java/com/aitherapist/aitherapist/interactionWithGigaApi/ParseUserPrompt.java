package com.aitherapist.aitherapist.interactionWithGigaApi;

import chat.giga.model.completion.ChatMessage;
import com.aitherapist.aitherapist.domain.enums.Prompts;
import com.aitherapist.aitherapist.domain.model.entities.ClinicPatient;
import com.aitherapist.aitherapist.domain.model.entities.DailyHealthData;
import com.aitherapist.aitherapist.domain.model.entities.InitialHealthData;
import com.aitherapist.aitherapist.domain.model.entities.Patient;
import com.aitherapist.aitherapist.interactionWithGigaApi.llm.Llm;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.aitherapist.aitherapist.interactionWithGigaApi.MakeMedicalRecommendation.giveMedicalRecommendationBeta;
import static com.aitherapist.aitherapist.interactionWithGigaApi.MakeMedicalRecommendation.giveMedicalRecommendationWithScoreBeta;

@Component
public class ParseUserPrompt {

    public static String patientRegistrationParser(String userMessage) throws InterruptedException {
        String token = Llm.getGigaChatToken();
        Prompts prompt = Prompts.valueOf("PATIENT_REGISTRATION_PROMPT");
        String systemPrompt = prompt.getMessage();
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );

        //FIXME вынести это
        String response = "";
        for (int i = 0; i < 10; i++) {
            try {
                response = Llm.talkToChat(token, requestMessage);
                break;
            } catch (Exception e) {
                if (i == 10 - 1) throw e;
                Thread.sleep(1000);
            }
        }
        return response;
    }


    public static String doctorRegistrationParser(String userMessage) throws InterruptedException {
        String token = Llm.getGigaChatToken();
        Prompts prompt = Prompts.valueOf("DOCTOR_REGISTRATION_PROMPT");
        String systemPrompt = prompt.getMessage();
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        //FIXME вынести это
        String response = "";
        for (int i = 0; i < 10; i++) {
            try {
                response = Llm.talkToChat(token, requestMessage);
                break;
            } catch (Exception e) {
                if (i == 10 - 1) throw e;
                Thread.sleep(1000);
            }
        }
        return response;
    }


    public static String dailyQuestionnaireParser(String userMessage){
        String token = Llm.getGigaChatToken();
        Prompts prompt = Prompts.valueOf("DAILY_QUESTIONNAIRE_PROMPT");
        String systemPrompt = prompt.getMessage();
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        return Llm.talkToChat(token, requestMessage);
    }

    public static String parameterEditorParser(String userMessage){
        String token = Llm.getGigaChatToken();
        Prompts prompt = Prompts.valueOf("PARAMETERS_EDITOR_PROMPT");
        String systemPrompt = prompt.getMessage();
        List<ChatMessage> requestMessage = Arrays.asList(
                ChatMessage.builder().content(systemPrompt).role(ChatMessage.Role.SYSTEM).build(),
                ChatMessage.builder().content(userMessage).role(ChatMessage.Role.USER).build()
        );
        return Llm.talkToChat(token, requestMessage);
    }





    public static void main(String[] args) throws InterruptedException {
//        String userPrompt1 = "Я Саша мне 19 я родился в России много подтягиваюсь на турнике и еще я много пью колу. мой рост 197 вес 70000 г";
//        String userPrompt2 = "I'am Sasha and I'm 19. I was born in Russia. I love do push ups and also i drink a lot cola. My parameters is 197 and 70";
//        String userPrompt3 = "давление и температура как у всех. пульс 80 и сильно болит голова чо мне делать???";
//        String editParams = "birthDay - завтра";
//        String response = parameterEditorParser(editParams);
//        String response1 = doctorRegistrationParser(userPrompt1);
//        String response2 = dailyQuestionnaireParser(userPrompt3);
//        System.out.println(response);
//        System.out.println(response1);
//        System.out.println(response2);


        InitialHealthData initialData = new InitialHealthData();
        initialData.setHeartPain(true);
        initialData.setArrhythmia(true);
        initialData.setChronicDiseases(null);
        initialData.setHeight(175.5);
        initialData.setWeight(80.0);
        initialData.setBadHabits("Smoking");


        // Создаем ClinicPatient
        ClinicPatient patient = new ClinicPatient();
        patient.setClinicId(1L);
        patient.setMedicalCardNumber("192.168.20.2");
        patient.setInitialData(initialData);

        patient.setName("Джон Смит");
        patient.setBirthDate(LocalDate.of(1990, 5, 20));
        patient.setGender(true);
        patient.setPhoneNumber("+123456789");
        // Создаем DailyHealthData
        DailyHealthData dailyData = new DailyHealthData();
        dailyData.setBloodOxygenLevel(98.5);
        dailyData.setTemperature(36.7);
        dailyData.setHoursOfSleepToday(7.5);
        dailyData.setPulse(90L);
        dailyData.setPressure("190/150");
        dailyData.setPatient(patient);

        List<DailyHealthData> dailyList = new ArrayList<>();
        dailyList.add(dailyData);

        patient.setDailyHealthDataList(dailyList);
        String response4 = giveMedicalRecommendationBeta(patient);
//        System.out.println(response4);
        String response5 = giveMedicalRecommendationWithScoreBeta(patient);
        System.out.println(response5);
    }
}
