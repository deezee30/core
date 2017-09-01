package com.maulss.core.net.paster;

import com.maulss.core.collect.EnhancedMap;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

public class Hastebin extends Paster {

    private static final String URL = "https://www.hastebin.com/";

    Hastebin(final String content) {
        super(content);
    }

    @Override
    protected URL process() throws Exception {
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(URL + "documents");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setRequestProperty("charset", "utf-8");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(getContent());
            wr.flush();
            wr.close();

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Map<String, Object> jsonMap = EnhancedMap.fromJson(rd.readLine());
            return new URL(URL + jsonMap.get("key"));
        } finally {
            if (connection != null) connection.disconnect();
        }
    }
}