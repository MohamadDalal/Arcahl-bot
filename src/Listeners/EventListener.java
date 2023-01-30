package Listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User user = event.getAuthor();
        if (user.isBot())
        {
            return;
        }
        Message message = event.getMessage();
        String channelMention = event.getChannel().getAsMention();

        String reply = user.getAsTag() + " sent the message \"" + message.getContentDisplay() + "\" in the " + channelMention + " channel despite me not asking.";
        //System.out.println(reply);
        System.out.println(message.getContentDisplay());
        System.out.println(message.getContentRaw());
        System.out.println(message.getContentStripped());
        event.getChannel().sendMessage(reply).queue();
    }
}
