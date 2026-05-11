package net.ebank.ms.ebankchatbot.telegram;


import jakarta.annotation.PostConstruct;
import net.ebank.ms.ebankchatbot.agents.EbankAgentAI;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lakbir.abderrahim on 11/05/2026
 */

@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${telegram.token}")
    private String token;
    private EbankAgentAI agentAI;

    TelegramBot(EbankAgentAI agentAI) {
        this.agentAI = agentAI;
    }

    @PostConstruct
    public void registerTelegramBot(){
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
        } catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update telegramRequest) {
        try {
            if(!telegramRequest.hasMessage()) return;
            String messageText = telegramRequest.getMessage().getText();
            Long chatId = telegramRequest.getMessage().getChatId();
            List<PhotoSize> photos = telegramRequest.getMessage().getPhoto();
            List<Media> mediaList = new ArrayList<>();
            String caption = null;
            if(photos != null){
                caption = telegramRequest.getMessage().getCaption();
                if(caption ==null) caption = "What do see in this image";

                for(PhotoSize ps : photos){
                    String fileId = ps.getFileId();
                    GetFile getFile = new GetFile();
                    getFile.setFileId(fileId);
                    File file = execute(getFile);
                    String filePath = file.getFilePath();
                    String textUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;
                    URL fileUrl = new URL(textUrl);
                    mediaList.add(Media.builder()
                            .id(fileId)
                            .mimeType(MimeTypeUtils.IMAGE_PNG)
                            .data(new UrlResource(fileUrl))
                            .build());
                }
            }
            String query = messageText != null ? messageText : caption;
            UserMessage userMessage = UserMessage.builder()
                    .text(query)
                    .media(mediaList).build();
            sendTypingQuestion(chatId);
            String answer = agentAI.chat(new Prompt(userMessage));
            sendTextMessage(chatId, answer);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void sendTextMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        execute(sendMessage);
    }

    @Override
    public String getBotUsername() {
        return "ebank_1993_bot";
    }

    @Override
    public String getBotToken() {
        return token;
    }

    private void sendTypingQuestion(Long chatId) throws TelegramApiException {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(String.valueOf(chatId));
        sendChatAction.setAction(ActionType.TYPING);
        execute(sendChatAction);
    }
}