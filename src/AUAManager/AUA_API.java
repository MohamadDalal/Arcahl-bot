package AUAManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AUA_API {
    // Todo: Implement a method that translated the status error messages into something better.
    static boolean initialized = false;
    static String APItoken;
    static String APIURI;

    static HttpClient httpClient;

    static Gson gson;

    public static void init(String token, String URI)
    {
        APItoken = token;
        APIURI = URI;
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();
        initialized = true;
    }

    public static boolean isInitialized(){return initialized;}

    private static JsonObject getRequest(String requestString) throws URISyntaxException, IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder().
                              uri(new URI(requestString)).
                              header("Authorization", "Bearer "+APItoken).
                              build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println(response.body());
        //JsonObject res = gson.fromJson(response.body(), JsonObject.class);
        //System.out.println(res);
        return gson.fromJson(response.body(), JsonObject.class);
        //return res;
    }

    public static JsonObject queryUserInfo(String userCode, int numRecent, boolean withSongInfo) throws URISyntaxException, IOException, InterruptedException {
        return getRequest(APIURI+"user/info?usercode="+userCode+"&recent="+numRecent+"&withsonginfo="+withSongInfo);
    }
    public static JsonObject queryUserBest(String userCode, String songName, int songDiff, boolean withRecent, boolean withSongInfo) throws URISyntaxException, IOException, InterruptedException {
        return getRequest(APIURI+"user/best?usercode="+userCode+"&songname="+songName+"&difficulty="+songDiff+"&withrecent="+withRecent+"&withsonginfo="+withSongInfo);
    }
    public static JsonObject queryUserBest30(String userCode, int extraScores, boolean withRecent, boolean withSongInfo) throws URISyntaxException, IOException, InterruptedException {
        return getRequest(APIURI+"user/best30?usercode="+userCode+"&overflow="+extraScores+"&withrecent="+withRecent+"withsonginfo="+withSongInfo);
    }
    public static JsonObject querySongInfo(String songName) throws URISyntaxException, IOException, InterruptedException {
        return getRequest(APIURI+"song/info?songname="+songName);
    }

    public static JsonObject querySongList() throws URISyntaxException, IOException, InterruptedException {
        // Note: It is a large data set, so it is not recommended to use this API frequently.
        return getRequest(APIURI+"song/list");
    }
    public static void querySongAlias(){/*Not implemented*/}
    public static JsonObject querySongRandom(String diffStart, String diffStop, boolean withSongInfo) throws URISyntaxException, IOException, InterruptedException {
        return getRequest(APIURI+"song/random?start="+diffStart+"&end="+diffStart+"&withsonginfo="+withSongInfo);
    }
}
