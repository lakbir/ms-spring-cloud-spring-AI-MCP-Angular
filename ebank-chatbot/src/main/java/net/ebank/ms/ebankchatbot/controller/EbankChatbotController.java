package net.ebank.ms.ebankchatbot.controller;


import net.ebank.ms.ebankchatbot.agents.EbankAgentAI;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
        return ebankAgentAI.chat(new Prompt(query));
    }

    @GetMapping(value = "/chatStream", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> chatStream(@RequestParam(name = "query", defaultValue = "Bonjour") String query){
        return this.ebankAgentAI.chatStream(new Prompt(query));
    }

}
