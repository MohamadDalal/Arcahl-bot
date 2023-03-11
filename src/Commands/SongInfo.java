package Commands;

import AUAManager.AUA_API;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SongInfo implements ICommand{
    @Override
    public String getName() {
        return "songinfo";
    }

    @Override
    public String getDescription() {
        return "Get info on a certain song";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> optionsList = new ArrayList<>();
        optionsList.add(new OptionData(OptionType.STRING, "name", "The name of the song to get info of.", true));
        optionsList.add(new OptionData(OptionType.INTEGER, "diff", "The chart difficulty", true).
                addChoice("Past", 0).
                addChoice("Present", 1).
                addChoice("Future", 2).
                addChoice("Beyond", 3));
        return optionsList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        JsonObject info;
        try {
            info = AUA_API.querySongInfo(event.getOption("name").getAsString().replaceAll("\\s+",""));
        } catch (Exception e) {
            event.reply("Caught an exception. Check logs for info").queue();
            e.printStackTrace();
            return;
        }
        //System.out.println(info);
        if (info.get("status").getAsInt() !=0){
            event.reply("Error in querying AUA with status " + info.get("status").getAsInt() + ":\n\t" + info.get("message").getAsString()).queue();
            return;
        }
        JsonArray diffList = info.get("content").getAsJsonObject().get("difficulties").getAsJsonArray();
        //JsonObject diffInfo;
        try{
            JsonObject diffInfo = diffList.get(event.getOption("diff").getAsInt()).getAsJsonObject();
            //System.out.println(diffInfo);
            String reply = "```" +
                           "\nSong name: " + diffInfo.get("name_en").getAsString() +
                           "\nSong artist: " + diffInfo.get("artist").getAsString() +
                           "\nChart difficulty: " + getLevel(diffInfo.get("difficulty").getAsInt()) +
                           "\nChart constant: " + diffInfo.get("rating").getAsDouble()/10 +
                           "\nNote count: " + diffInfo.get("note").getAsInt() +
                           "\nChart designer: " + diffInfo.get("chart_designer").getAsString() +
                           "\n```";
            event.reply(reply).queue();
        }
        catch (IndexOutOfBoundsException e){
            event.reply("The song does not contain a chart with the given difficulty").queue();
        }

        /*System.out.println(diffList);
        System.out.println(diffList.get(0));
        System.out.println(diffList.get(1));
        System.out.println(diffList.get(2));
        System.out.println(diffList.get(3));
        event.reply("Haha nope.").queue();*/

    }

    private String getLevel(int level){
        String res;
        if (level%2 == 0){
            res = Integer.toString(Math.floorDiv(level,2));
        }else{
            res = Math.floorDiv(level,2) + "+";
        }
        return res;
    }
}
