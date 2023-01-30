import AUAManager.AUA_API;
import Commands.*;
import DatabaseAPI.DatabaseAPI;
import Listeners.EventListener;
import Listeners.SlashCommandListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import com.google.gson.Gson;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.*;
import java.nio.channels.AcceptPendingException;

import java.sql.*;

public class Arcahl_bot {

    public Arcahl_bot () throws FileNotFoundException, InterruptedException
    {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader("config.json"));
        JsonObject configJson = gson.fromJson(reader, JsonObject.class);
        JsonElement token =  configJson.get("DiscordToken");
        String tokenStr = token.getAsString();
        JDABuilder jdaBuilder = JDABuilder.createDefault(tokenStr);
        jdaBuilder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        JDA jda = jdaBuilder.build();
        SlashCommandListener SCL = new SlashCommandListener();
        SCL.add(new TestCommand());
        SCL.add(new TestQuery());
        SCL.add(new SongInfo());
        SCL.add(new AddSongInfo());
        SCL.add(new FillSongList());
        SCL.add(new UpdateSongInfo());
        SCL.add(new UpdateSongList());
        jda.addEventListener(new EventListener());
        jda.addEventListener(SCL);
        jda.awaitReady();
        AUA_API.init(configJson.get("AUAToken").getAsString(), configJson.get("AUAURI").getAsString());
        DatabaseAPI.init(configJson.get("DatabaseIP").getAsString(), configJson.get("DatabaseUsername").getAsString(), configJson.get("DatabasePassword").getAsString());
    }
    public static void main(String[] args) {
        /*Gson gson = new Gson();

        try(JsonReader reader = new JsonReader(new FileReader("config.json")))
        {
            JsonObject configJson = gson.fromJson(reader, JsonObject.class);
            JsonElement token =  configJson.get("Token");
            String tokenStr = token.getAsString();
            JDA jda = JDABuilder.createDefault(tokenStr).build();
            jda.awaitReady();
            //System.out.println(tokenStr);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }*/

        try {
            Arcahl_bot bot = new Arcahl_bot();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*String connectionUrl =
                "jdbc:sqlserver://localhost.database.windows.net:1433;"
                        + "database=TutorialDB;"
                        + "user=sa@localhost;"
                        + "password=300400;"
                        + "encrypt=true;"
                        + "trustServerCertificate=false;"
                        + "loginTimeout=3;";*/

        /*String connectionUrl =
                "jdbc:sqlserver://localhost;"
                        + "database=TutorialDB;"
                        + "user=sa;"
                        + "password=300400;"
                        + "encrypt=true;"
                        + "trustServerCertificate=true;"
                        + "loginTimeout=3;";*/

        /*try (Connection connection = DriverManager.getConnection(connectionUrl);) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM dbo.Customers");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                System.out.println(rs.getInt("CustomerId") + " - " + rs.getString("Name"));
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }*/

        /*HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(connectionUrl);

        try (Connection connection = ds.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM dbo.Customers");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                System.out.println(rs.getInt("CustomerId") + " - " + rs.getString("Name"));
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }*/

    }
}