package Commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class TestCommand implements ICommand {
    @Override
    public String getName() {
        return "testcommand";
    }

    @Override
    public String getDescription() {
        return "Command used for testing";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> optionsList = new ArrayList<>();
        optionsList.add(new OptionData(OptionType.NUMBER, "lucky-number", "What is your lucky number?", true));
        optionsList.add(new OptionData(OptionType.BOOLEAN,"vroom", "Nyoom", false));
        return optionsList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping opt1 = event.getOption("lucky-number");
        assert opt1 != null;
        double Number = opt1.getAsDouble();
        OptionMapping opt2 = event.getOption("vroom");
        double res;
        if(opt2 != null){res = Number*Number;}
        else {res = Number*2;}
        String Reply = "The car drives with a speed of " + res + " Kph!";
        //event.getChannel().sendMessage(Reply).queue();
        event.reply(Reply).queue();
    }
}
