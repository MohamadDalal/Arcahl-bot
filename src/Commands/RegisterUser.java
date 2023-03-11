package Commands;

import AUAManager.AUA_API;
import DatabaseAPI.DatabaseAPI;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegisterUser implements ICommand{
    @Override
    public String getName() {
        return "registeruser";
    }

    @Override
    public String getDescription() {
        return "Register or update your Arcaea friend code.";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> optionsList = new ArrayList<>();
        optionsList.add(new OptionData(OptionType.STRING, "code", "Your Arcaea friend code.", true));
        return optionsList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();
        try {
            JsonObject userInfo = AUA_API.queryUserInfo(event.getOption("code").getAsString(), 0, false);
            if(userInfo.get("status").getAsInt() != 0){
                event.getHook().sendMessage("Error in querying AUA with status " + userInfo.get("status").getAsInt() + ":\n\t" + userInfo.get("message").getAsString()) .queue();
            }
            else{
                //String reply = DatabaseAPI.updateUserCode(userInfo, event.getUser().getId());
                String reply;
                String discordID = event.getUser().getId();
                JsonObject accInfo = userInfo.getAsJsonObject("content").getAsJsonObject().getAsJsonObject("account_info").getAsJsonObject();
                try (Connection connection = DatabaseAPI.ds.getConnection()) {
                    PreparedStatement ps = connection.prepareStatement("IF NOT EXISTS (SELECT 1 FROM UserInfo WHERE Discord_id = ?) " +
                            "BEGIN " +
                            "INSERT INTO UserInfo VALUES (?,?,?,?,?) " +
                            "END " +
                            "ELSE " +
                            "BEGIN " +
                            "UPDATE UserInfo " +
                            "SET Arcaea_code=?, Arcaea_name=?, Arcaea_user_id=?, Arcaea_rating=? " +
                            "WHERE Discord_id=? " +
                            "END");
                    ps.setString(1, discordID); ps.setString(6, discordID); ps.setString(11, discordID);
                    ps.setString(2, accInfo.get("code").getAsString()); ps.setString(7, accInfo.get("code").getAsString());
                    ps.setString(3, accInfo.get("name").getAsString()); ps.setString(8, accInfo.get("name").getAsString());
                    ps.setInt(4, accInfo.get("user_id").getAsInt()); ps.setInt(9, accInfo.get("user_id").getAsInt());
                    ps.setInt(5, accInfo.get("rating").getAsInt()); ps.setInt(10, accInfo.get("rating").getAsInt());
                    ps.executeUpdate();
                    reply = "Friend code registered/updated successfully.";
                }
                // Handle any errors that may have occurred.
                catch (SQLException e) {
                    e.printStackTrace();
                    reply = "SQL error. Read logs for more info.";
                }
                event.getHook().sendMessage(reply).queue();
            }
        } catch (Exception e) {
            event.getHook().sendMessage("Caught an exception. Check logs for info").queue();
            e.printStackTrace();
        }
    }
}
