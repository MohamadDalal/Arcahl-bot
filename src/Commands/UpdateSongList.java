package Commands;

import DatabaseAPI.DatabaseAPI;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class UpdateSongList implements ICommand{
    @Override
    public String getName() {
        return "updatesonglist";
    }

    @Override
    public String getDescription() {
        return "Updates the database with all the songs' info. (Owner only command)";
    }

    @Override
    public List<OptionData> getOptions() {
        return new ArrayList<>();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();
        if(event.getUser().getId().equals("310153696044515349")){
            //System.out.println("Peepee Poopoo");
            String reply = DatabaseAPI.updateAllSongInfo();
            event.getHook().sendMessage(reply).queue();
        }
        else{
            event.getHook().sendMessage("Who da hek are u?").queue();
        }
        //System.out.println("User ID is: " + event.getUser().getId());
        //event.reply(event.getUser().getId()).setEphemeral(true).queue();
    }
}
