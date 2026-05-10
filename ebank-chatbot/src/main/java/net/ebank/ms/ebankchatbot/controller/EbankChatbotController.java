package net.ebank.ms.ebankchatbot.controller;


import net.ebank.ms.ebankchatbot.agents.EbankAgentAI;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lakbir.abderrahim on 10/05/2026
 */

@RestController
public class EbankChatbotController {

    private final EbankAgentAI ebankAgentAI;

    public EbankChatbotController(EbankAgentAI ebankAgentAI) {
        this.ebankAgentAI = ebankAgentAI;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(name = "query", defaultValue = "Bonjour") String query) {
        return ebankAgentAI.chat(query);
    }

}
