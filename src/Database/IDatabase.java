package Database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public interface IDatabase {

    static Gson gson = new Gson();
    static JsonObject jsonObject = null;
    public static void init(){}
    public static JsonObject getJsonObject(){return jsonObject;}
}
