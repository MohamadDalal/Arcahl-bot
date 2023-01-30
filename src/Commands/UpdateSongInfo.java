package Commands;

import DatabaseAPI.DatabaseAPI;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class UpdateSongInfo implements ICommand{
    @Override
    public String getName() {
        return "updatesonginfo";
    }

    @Override
    public String getDescription() {
        return "Updates a song's info in the song info table. (Owner only command)";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> optionsList = new ArrayList<>();
        optionsList.add(new OptionData(OptionType.STRING, "name", "The name of the song to update the info of.", true));
        return optionsList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();
        if(event.getUser().getId().equals("310153696044515349")){
            //System.out.println("Peepee Poopoo");
            String reply = DatabaseAPI.updateSongInfo(event.getOption("name").getAsString());
            event.getHook().sendMessage(reply).queue();
        }
        else{
            event.getHook().sendMessage("Who da hek are u?").queue();
        }
        //System.out.println("User ID is: " + event.getUser().getId());
        //event.reply(event.getUser().getId()).setEphemeral(true).queue();
    }
}
