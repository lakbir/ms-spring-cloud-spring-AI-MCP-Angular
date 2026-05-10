package net.ebank.ms.ebankchatbot.controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lakbir.abderrahim on 10/05/2026
 */

@RestController
public class EbankChatbotController {

    private ChatClient chatClient;

    public EbankChatbotController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(name = "query", defaultValue = "Bonjour") String query) {
        return chatClient.prompt(query).call().content();
    }

}
