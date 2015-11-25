package sk.fei.mobv.pivarci.api;

import com.google.gson.Gson;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import sk.fei.mobv.pivarci.model.User;

@SuppressWarnings("deprecation")
public class MyServerApi implements ServerApi {
    public static final String API_KEY = "3C7e56ZRFQcMXXr";
    private final static String SERVER_URL_AUTH = "https://mobv.mcomputing.fei.stuba.sk/index.php?r=device/auth";
    private static final String SERVER_URL_NUMBER = "https://mobv.mcomputing.fei.stuba.sk/index.php?r=device/number";

    public static String md5(String string) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(string.getBytes(Charset.forName("UTF8")));
            final byte[] resultByte = messageDigest.digest();
            return new String(Hex.encodeHex(resultByte));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public User userSignIn(String username, String password) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(SERVER_URL_AUTH);

        password = md5(password);
        String mobv_data = "{\"api_key\":\"" + API_KEY + "\",\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        httpPost.addHeader("X-MOBV-Data", mobv_data);
        httpPost.addHeader("Content-Type", "application/json");

        User user = null;
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            user = new Gson().fromJson(responseString, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public int sendRandomNumber(String token) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(SERVER_URL_NUMBER);
        Random generator = new Random();
        int i = generator.nextInt(100);
        String mobv_data = "{\"api_key\":\"" + API_KEY + "\",\"token\":\"" + token + "\",\"number\":\"" + Integer.toString(i) + "\"}";
        httpPost.addHeader("X-MOBV-Data", mobv_data);
        httpPost.addHeader("Content-Type", "application/json");
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                return -1;
            }
            return i;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}