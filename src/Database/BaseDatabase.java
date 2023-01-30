package Database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class BaseDatabase {
    static Gson gson = new Gson();
    static JsonObject jsonObject;
    public static void init(String jsonFile) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(jsonFile));
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
    }
    public static JsonObject getJsonObject(){return jsonObject;}
}
