package Commands;

import DatabaseAPI.DatabaseAPI;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class TestQuery implements ICommand{

    @Override
    public String getName() {
        return "testquery";
    }

    @Override
    public String getDescription() {
        return "Command used for testing a query request";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> optionsList = new ArrayList<>();
        return optionsList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String Reply = DatabaseAPI.testQuery();
        //event.getChannel().sendMessage(Reply).queue();
        event.reply(Reply).queue();
    }
}
