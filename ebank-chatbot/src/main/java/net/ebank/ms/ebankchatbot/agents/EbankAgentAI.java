package net.ebank.ms.ebankchatbot.agents;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

/**
 * Created by lakbir.abderrahim on 11/05/2026
 */

@Service
public class EbankAgentAI {

    private final ChatClient chatClient;

    public EbankAgentAI(ChatClient.Builder chatClient,
                        ChatMemory chatMemory,
                        ToolCallbackProvider tools) {
        this.chatClient =  chatClient
                .defaultSystem("""
                        Vous etes un assistant qui se charge de répondre aux questions de l'utilisateur
                        à propos des clients et des comptes bancaires
                         en fonction du contexte fourni, Si aucun contexte n'est fourni, répond par JE NE SAIS PAS
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(tools)
                .build();
    }


    public String chat(String query) {
        return chatClient.prompt(query).call().content();
    }
}
