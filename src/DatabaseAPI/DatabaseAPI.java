package DatabaseAPI;

import AUAManager.AUA_API;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class DatabaseAPI {

    public static HikariDataSource ds;

    public static void init(String serverIP, String username, String password){
        String connectionUrl =
                "jdbc:sqlserver://" + serverIP + ";"
                        + "database=ArcaeaScores;"
                        + "user=" + username + ";"
                        + "password=" + password + ";"
                        + "encrypt=true;"
                        + "trustServerCertificate=true;"
                        + "loginTimeout=30;";

        ds = new HikariDataSource();
        ds.setJdbcUrl(connectionUrl);
    }


    public static String testQuery(){
        String retStr = "";
        try (Connection connection = ds.getConnection()) {
            /*PreparedStatement ps = connection.prepareStatement("SELECT * FROM SongInfo");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                System.out.println(rs.getString("song_id") + " - " + rs.getString("name_en"));
                retStr = retStr + "\n" + rs.getString("song_id") + " - " + rs.getString("name_en");
            }*/
            PreparedStatement ps = connection.prepareStatement("SELECT TOP 1 * FROM SongInfo ORDER BY NEWID()");
            ResultSet rs = ps.executeQuery();
            rs.next();
            retStr = "Your random song is: " + rs.getString("name_en");
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
            retStr = "Error occured. Check log for info.";
        }
        return retStr;
    }

    public static String fillSongInfo(){
        String reply = "";
        JsonObject songList;
        try {
            songList = AUA_API.querySongList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (songList.get("status").getAsInt() !=0){
            reply = "Error in querying AUA with status" + songList.get("status").getAsInt() + ":\n\t" + songList.get("message").getAsString();
            System.out.println(reply);
            return reply;
        }
        int updatedRows = 0;
        JsonArray songs = songList.get("content").getAsJsonObject().get("songs").getAsJsonArray();
        for(int i=0; i<songs.size(); i++) {
            try (Connection connection = ds.getConnection()) {
                JsonArray diffList = songs.get(i).getAsJsonObject().get("difficulties").getAsJsonArray();
                //PreparedStatement ps = connection.prepareStatement("INSERT INTO SongInfo VALUES (?,?,?,?,?,?,?,?,?,?)");
                PreparedStatement ps = connection.prepareStatement("IF NOT EXISTS (SELECT 1 FROM SongInfo WHERE song_id = ?) " +
                        "BEGIN " +
                        "INSERT INTO SongInfo VALUES (?,?,?,?,?,?,?,?,?,?)" +
                        "END");
                ps.setString(1, songs.get(i).getAsJsonObject().get("song_id").getAsString());
                ps.setString(2, songs.get(i).getAsJsonObject().get("song_id").getAsString());
                ps.setString(3, diffList.get(0).getAsJsonObject().get("name_en").getAsString());
                ps.setInt(4, diffList.get(0).getAsJsonObject().get("difficulty").getAsInt());
                ps.setInt(5, diffList.get(0).getAsJsonObject().get("rating").getAsInt());
                ps.setInt(6, diffList.get(1).getAsJsonObject().get("difficulty").getAsInt());
                ps.setInt(7, diffList.get(1).getAsJsonObject().get("rating").getAsInt());
                ps.setInt(8, diffList.get(2).getAsJsonObject().get("difficulty").getAsInt());
                ps.setInt(9, diffList.get(2).getAsJsonObject().get("rating").getAsInt());
                if (diffList.size() > 3) {
                    ps.setInt(10, diffList.get(3).getAsJsonObject().get("difficulty").getAsInt());
                    ps.setInt(11, diffList.get(3).getAsJsonObject().get("rating").getAsInt());
                } else {
                    ps.setNull(10, Types.INTEGER);
                    ps.setNull(11, Types.INTEGER);
                }
                int insertCount = ps.executeUpdate();
                updatedRows += insertCount;
                reply = "Song list extended successfully. " + updatedRows + " rows effected.";
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                e.printStackTrace();
                reply = "SQL error. Read logs for more info.";
            }
        }
        return reply;
    }

    public static String addSongInfo(String songName){
        String reply = "";
        JsonObject songInfo;
        try {
            songInfo = AUA_API.querySongInfo(songName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (songInfo.get("status").getAsInt() !=0){
            reply = "Error in querying AUA with status" + songInfo.get("status").getAsInt() + ":\n\t" + songInfo.get("message").getAsString();
            System.out.println(reply);
            return reply;
        }
        try (Connection connection = ds.getConnection()) {
            JsonArray diffList = songInfo.get("content").getAsJsonObject().get("difficulties").getAsJsonArray();
            //PreparedStatement ps = connection.prepareStatement("INSERT INTO SongInfo VALUES (?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement ps = connection.prepareStatement("IF NOT EXISTS (SELECT 1 FROM SongInfo WHERE song_id = ?) " +
                    "BEGIN " +
                    "INSERT INTO SongInfo VALUES (?,?,?,?,?,?,?,?,?,?)" +
                    "END");
            ps.setString(1, songInfo.get("content").getAsJsonObject().get("song_id").getAsString());
            ps.setString(2, songInfo.get("content").getAsJsonObject().get("song_id").getAsString());
            ps.setString(3, diffList.get(0).getAsJsonObject().get("name_en").getAsString());
            ps.setInt(4, diffList.get(0).getAsJsonObject().get("difficulty").getAsInt());
            ps.setInt(5, diffList.get(0).getAsJsonObject().get("rating").getAsInt());
            ps.setInt(6, diffList.get(1).getAsJsonObject().get("difficulty").getAsInt());
            ps.setInt(7, diffList.get(1).getAsJsonObject().get("rating").getAsInt());
            ps.setInt(8, diffList.get(2).getAsJsonObject().get("difficulty").getAsInt());
            ps.setInt(9, diffList.get(2).getAsJsonObject().get("rating").getAsInt());
            if (diffList.size() > 3) {
                ps.setInt(10, diffList.get(3).getAsJsonObject().get("difficulty").getAsInt());
                ps.setInt(11, diffList.get(3).getAsJsonObject().get("rating").getAsInt());
            } else {
                ps.setNull(10, Types.INTEGER);
                ps.setNull(11, Types.INTEGER);
            }
            int insertCount = ps.executeUpdate();
            reply = "Successfully added the song " + songName + " to the song list.";
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
            reply = "SQL error. Read logs for more info.";
        }
        return reply;
    }

    public static String updateSongInfo(String songName){
        String reply = "";
        JsonObject songInfo;
        try {
            songInfo = AUA_API.querySongInfo(songName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (songInfo.get("status").getAsInt() !=0){
            reply = "Error in querying AUA with status" + songInfo.get("status").getAsInt() + ":\n\t" + songInfo.get("message").getAsString();
            System.out.println(reply);
            return reply;
        }
        try (Connection connection = ds.getConnection()) {
            JsonArray diffList = songInfo.get("content").getAsJsonObject().get("difficulties").getAsJsonArray();
            //PreparedStatement ps = connection.prepareStatement("INSERT INTO SongInfo VALUES (?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement ps = connection.prepareStatement("UPDATE SongInfo " +
                    "SET name_en=?, " +
                    "difficulty0=?, rating0=?, " +
                    "difficulty1=?, rating1=?, " +
                    "difficulty2=?, rating2=?, " +
                    "difficulty3=?, rating3=? " +
                    "WHERE song_id=?");
            ps.setString(1, diffList.get(0).getAsJsonObject().get("name_en").getAsString());
            ps.setInt(2, diffList.get(0).getAsJsonObject().get("difficulty").getAsInt());
            ps.setInt(3, diffList.get(0).getAsJsonObject().get("rating").getAsInt());
            ps.setInt(4, diffList.get(1).getAsJsonObject().get("difficulty").getAsInt());
            ps.setInt(5, diffList.get(1).getAsJsonObject().get("rating").getAsInt());
            ps.setInt(6, diffList.get(2).getAsJsonObject().get("difficulty").getAsInt());
            ps.setInt(7, diffList.get(2).getAsJsonObject().get("rating").getAsInt());
            if(diffList.size()>3){
                ps.setInt(8, diffList.get(3).getAsJsonObject().get("difficulty").getAsInt());
                ps.setInt(9, diffList.get(3).getAsJsonObject().get("rating").getAsInt());
            }
            else{
                ps.setNull(8, Types.INTEGER);
                ps.setNull(9, Types.INTEGER);
            }
            ps.setString(10, songInfo.get("content").getAsJsonObject().get("song_id").getAsString());
            int insertCount = ps.executeUpdate();
            reply = "Info of the song " + songName + " has been updated.";
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
            reply = "SQL error. Read logs for more info.";
        }
        return reply;
    }

    public static String updateAllSongInfo(){
        String reply = "";
        JsonObject songList;
        try {
            songList = AUA_API.querySongList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (songList.get("status").getAsInt() !=0){
            reply = "Error in querying AUA with status" + songList.get("status").getAsInt() + ":\n\t" + songList.get("message").getAsString();
            System.out.println(reply);
            return reply;
        }
        int updatedRows = 0;
        JsonArray songs = songList.get("content").getAsJsonObject().get("songs").getAsJsonArray();
        for(int i=0; i<songs.size(); i++) {
            try (Connection connection = ds.getConnection()) {
                JsonArray diffList = songs.get(i).getAsJsonObject().get("difficulties").getAsJsonArray();
                //PreparedStatement ps = connection.prepareStatement("INSERT INTO SongInfo VALUES (?,?,?,?,?,?,?,?,?,?)");
                PreparedStatement ps = connection.prepareStatement("UPDATE SongInfo " +
                        "SET name_en=?, " +
                        "difficulty0=?, rating0=?, " +
                        "difficulty1=?, rating1=?, " +
                        "difficulty2=?, rating2=?, " +
                        "difficulty3=?, rating3=? " +
                        "WHERE song_id=?");
                ps.setString(1, diffList.get(0).getAsJsonObject().get("name_en").getAsString());
                ps.setInt(2, diffList.get(0).getAsJsonObject().get("difficulty").getAsInt());
                ps.setInt(3, diffList.get(0).getAsJsonObject().get("rating").getAsInt());
                ps.setInt(4, diffList.get(1).getAsJsonObject().get("difficulty").getAsInt());
                ps.setInt(5, diffList.get(1).getAsJsonObject().get("rating").getAsInt());
                ps.setInt(6, diffList.get(2).getAsJsonObject().get("difficulty").getAsInt());
                ps.setInt(7, diffList.get(2).getAsJsonObject().get("rating").getAsInt());
                if (diffList.size() > 3) {
                    ps.setInt(8, diffList.get(3).getAsJsonObject().get("difficulty").getAsInt());
                    ps.setInt(9, diffList.get(3).getAsJsonObject().get("rating").getAsInt());
                } else {
                    ps.setNull(8, Types.INTEGER);
                    ps.setNull(9, Types.INTEGER);
                }
                ps.setString(10, songs.get(i).getAsJsonObject().get("song_id").getAsString());
                int insertCount = ps.executeUpdate();
                updatedRows += insertCount;
                reply = "Song list updated successfully. " + updatedRows + " rows effected.";
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                e.printStackTrace();
                reply = "SQL error. Read logs for more info.";
            }
        }
        return reply;
    }


}
