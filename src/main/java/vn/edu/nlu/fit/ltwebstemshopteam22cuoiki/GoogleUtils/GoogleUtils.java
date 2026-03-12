package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.GoogleUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Form;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.GoogleUserDTO;

import java.io.IOException;

public class GoogleUtils {
    public static final String CLIENT_ID = "393823825163-e07kcqa0degfafb68obr3sda553uordj.apps.googleusercontent.com";
    public static final String CLIENT_SECRET = "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";
    public static final String REDIRECT_URI = "http://localhost:8080/login-google";

    public static String getToken(String code) throws IOException {
        String response = Request.Post("https://oauth2.googleapis.com/token")
                .bodyForm(Form.form()
                        .add("client_id", CLIENT_ID).add("client_secret", CLIENT_SECRET)
                        .add("redirect_uri", REDIRECT_URI).add("code", code)
                        .add("grant_type", "authorization_code").build())
                .execute().returnContent().asString();
        JsonObject jobj = new Gson().fromJson(response, JsonObject.class);
        return jobj.get("access_token").toString().replaceAll("\"", "");
    }

    public static GoogleUserDTO getUserInfo(String accessToken) throws IOException {
        String link = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken;
        String response = Request.Get(link).execute().returnContent().asString();
        return new Gson().fromJson(response, GoogleUserDTO.class);
    }
}