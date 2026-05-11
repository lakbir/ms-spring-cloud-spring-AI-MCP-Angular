package net.ebank.ms.ebankchatbot.discord;


import com.zgamelogic.discord.annotations.DiscordController;
import com.zgamelogic.discord.annotations.DiscordMapping;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ebank.ms.ebankchatbot.agents.EbankAgentAI;
import org.springframework.ai.chat.prompt.Prompt;

/**
 * Created by lakbir.abderrahim on 11/05/2026
 */

@DiscordController
public class DiscordBot {

    private final EbankAgentAI ebankAgentAI;

    public DiscordBot(EbankAgentAI ebankAgentAI) {
        this.ebankAgentAI = ebankAgentAI;
    }

    @DiscordMapping
    private  void perform(MessageReceivedEvent event){
        if(event.getAuthor().isBot()) return;
        String query = event.getMessage().getContentRaw();
        String response = ebankAgentAI.chat(new Prompt(query));
        event.getChannel().sendMessage(response).queue();
    }

}
