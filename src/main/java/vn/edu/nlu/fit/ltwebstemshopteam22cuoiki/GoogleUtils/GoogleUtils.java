package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.GoogleUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Form;
import vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model.GoogleUserDTO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GoogleUtils {
    public static String CLIENT_ID;
    public static String CLIENT_SECRET;
    // public static final String REDIRECT_URI = "http://localhost:8080/login-google";
    public static final String REDIRECT_URI = "https://stemshop.id.vn/login-google";

    // Khối static này sẽ chạy ngay khi class được load để đọc file properties
    static {
        try (InputStream input = GoogleUtils.class.getClassLoader().getResourceAsStream("system.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Cảnh báo: Không tìm thấy file system.properties tại src/main/resources/");
            } else {
                prop.load(input);
                CLIENT_ID = prop.getProperty("google.client.id");
                CLIENT_SECRET = prop.getProperty("google.client.secret");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getToken(String code) throws IOException {
        String response = Request.Post("https://oauth2.googleapis.com/token")
                .bodyForm(Form.form()
                        .add("client_id", CLIENT_ID)
                        .add("client_secret", CLIENT_SECRET)
                        .add("redirect_uri", REDIRECT_URI)
                        .add("code", code)
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