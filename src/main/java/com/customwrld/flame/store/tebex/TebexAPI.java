package com.customwrld.flame.store.tebex;

import com.customwrld.customlib.CustomLib;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TebexAPI {
    @Getter
    private static final String endpoint = "https://plugin.tebex.io";
    @Getter
    @Setter
    private static String secret;

    public static TebexAccount getAccountInformation() {
        String json = performAPI("/information", "GET");

        JsonObject info = CustomLib.getCustomLib().getGson().fromJson(json, JsonObject.class);

        if (info.has("account")) {
            return new TebexAccount(info.getAsJsonObject("account"));
        }

        return null;
    }

    public static List<TebexPackage> getPackages() {
        String json = performAPI("/packages?verbose=true", "GET");

        JsonArray array = CustomLib.getCustomLib().getGson().fromJson(json, JsonArray.class);

        List<TebexPackage> packages = new ArrayList<>();
        for (JsonElement element : array) {
            packages.add(new TebexPackage(element));
        }

        return packages;
    }

    private static String performAPI(String path, String method) {
        try {
            URL url = new URL(endpoint + path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestProperty("X-Tebex-Secret", secret);
            con.setRequestProperty("Content-Type", "application/json");

            con.setRequestMethod(method);

            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            con.disconnect();

            return content.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String mapToParameters(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return result.toString();
    }
}
