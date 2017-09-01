package com.maulss.core.net.urlshortener;

import com.google.common.collect.Maps;
import com.maulss.core.collect.EnhancedMap;
import com.maulss.core.net.http.HttpRequest;
import org.apache.commons.lang3.Validate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class GooGl extends URLShortener {

    private static final String URL = "https://www.googleapis.com/urlshortener/v1/url?key=";

    private String key = "AIzaSyBSeHdzFBZ-FVWO2CKFZ8LBSnpRHfSBG08";
    private Map<String, Object> projection = Maps.newHashMap();

    GooGl(final String longUrl) {
        super(longUrl);
    }

    public void setDeveloperKey(final String key) {
        this.key = Validate.notEmpty(key, "key");
    }

    public Map<String, Object> getFullResponse() {
        return projection;
    }

    @Override
    public URL process() throws Exception {

        URL url = HttpRequest.url(URL + key);

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(("{\"longUrl\": \"" + longUrl + "\"}").getBytes(StandardCharsets.UTF_8));
                wr.flush();
            }

            StringBuilder sb = new StringBuilder();

            try (BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line);
                }
            }

            projection = EnhancedMap.fromJson(sb.toString());

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new Exception(String.format(
                        "Response code returned is not 200, received %s instead. Error: %s",
                        responseCode,
                        ((Map<String, Object>) projection.get("error")).get("message")
                ));
            }
        } finally {
            if (connection != null) connection.disconnect();
        }

        return HttpRequest.url(projection.get("id").toString());
    }
}