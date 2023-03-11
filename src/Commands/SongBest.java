package Commands;

import AUAManager.AUA_API;
import DatabaseAPI.DatabaseAPI;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SongBest implements ICommand{

    private final String[] DiffNames = {"Past", "Present", "Future", "Beyond"};

    @Override
    public String getName() {
        return "songbest";
    }

    @Override
    public String getDescription() {
        return "Get info about your best score on a specific chart.";
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
        event.deferReply().queue();
        String reply;
        try (Connection connection = DatabaseAPI.ds.getConnection()){
            PreparedStatement ps = connection.prepareStatement("SELECT Arcaea_code FROM UserInfo WHERE Discord_id=?");
            ps.setString(1, event.getUser().getId());
            ResultSet rs = ps.executeQuery();
            if(!rs.isBeforeFirst()){
                //event.getHook().sendMessage("You do not have a registered arcaea friend code. " +
                //        "Make sure to register your code using /registeruser.").queue();
                //return;
                reply = "You do not have a registered arcaea friend code. " +
                        "Make sure to register your code using /registeruser.";
            }
            else{
                rs.next();
                String code = rs.getString("Arcaea_code");
                JsonObject scoreInfo = AUA_API.queryUserBest(code, event.getOption("name").getAsString().replaceAll("\\s+",""), event.getOption("diff").getAsInt(), false, true);
                if(scoreInfo.get("status").getAsInt() != 0){
                    reply = "Error in querying AUA with status " + scoreInfo.get("status").getAsInt() + ":\n\t" + scoreInfo.get("message").getAsString();
                }
                else {
                    JsonObject accInfo = scoreInfo.get("content").getAsJsonObject().get("account_info").getAsJsonObject();
                    JsonObject record = scoreInfo.get("content").getAsJsonObject().get("record").getAsJsonObject();
                    JsonObject songInfo = scoreInfo.get("content").getAsJsonObject().get("songinfo").getAsJsonArray().get(0).getAsJsonObject();
                    reply = "```" +
                            "\nPlayer name: " + accInfo.get("name").getAsString() +
                            "\nSong name: " + songInfo.get("name_en") +
                            "\nScore: " + record.get("score").getAsInt() +
                            "\nPure (Shiny): " + record.get("perfect_count").getAsInt() +
                            " ("  + record.get("shiny_perfect_count").getAsInt() + ")" +
                            "\nFar: " + record.get("near_count").getAsInt() +
                            "\nMiss: " + record.get("miss_count").getAsInt() +
                            "\nSong Difficulty: " + DiffNames[event.getOption("diff").getAsInt()] + " " + getLevel(songInfo.get("difficulty").getAsInt()) +
                            "\nSong potential: " + ((double) songInfo.get("rating").getAsInt()) / 10 +
                            "\nPlay potential: " + String.format("%.4f", record.get("rating").getAsDouble()) +
                            "\n```";
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            reply = "SQL error. Read logs for more info.";
        }
        catch (Exception e) {
            e.printStackTrace();
            reply = "Caught an exception. Check logs for info";
        }
        event.getHook().sendMessage(reply).queue();
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
