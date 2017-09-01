package com.maulss.core.net.paster;

import com.maulss.core.net.http.Form;
import com.maulss.core.net.http.HttpRequest;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pastebin extends Paster {

    private static final Pattern URL_PATTERN = Pattern.compile("https?://pastebin.com/([^/]+)$");
    private boolean
            mungingLinks = true,
            raw = false;
    private String
            devKey = "3ac77cbbe9b883d8b4338ec64367471d",
            userKey = "84b48b516aaf29be161bc1042bce1c8a",
            pasteFormat = "text",
            pasteName = "",
            expireDate = "1W";
    private int
            // 0 = public
            // 1 = unlisted
            // 2 = private
            listed = 1;

    Pastebin(final String content) {
        super(content);
    }

    @Override
    public URL process() throws Exception {
        Form form = Form.create();

        form.add("api_option", "paste");
        form.add("api_dev_key", devKey);
        form.add("api_paste_code", mungingLinks ? getContent().replaceAll("http://", "http_//") : getContent());
        form.add("api_paste_private", Integer.toString(listed));
        form.add("api_paste_name", pasteName);
        form.add("api_paste_expire_date", expireDate);
        form.add("api_paste_format", pasteFormat);
        form.add("api_user_key", userKey);

        URL url = HttpRequest.url("http://pastebin.com/api/api_post.php");

        String result;

        try (HttpRequest http = HttpRequest.post(url)) {
            result = http
                    .bodyForm(form)
                    .execute()
                    .expectResponseCode(200)
                    .returnContent()
                    .asString("UTF-8")
                    .trim();
        }

        Matcher m = URL_PATTERN.matcher(result);

        if (m.matches()) {
            String raw = (Pastebin.this.raw ? "raw.php?i=" : "") + m.group(1);
            return new URL("http://pastebin.com/" + raw);
        } else if (result.matches("^https?://.+")) {
            return new URL(result);
        } else {
            throw new IOException("Failed to save paste; instead, got: " + result);
        }
    }

    public void setMungingLinks(boolean mungingLinks) {
        this.mungingLinks = mungingLinks;
    }

    public void setRaw(boolean raw) {
        this.raw = raw;
    }

    public void setDevKey(String devKey) {
        this.devKey = Validate.notNull(devKey, "devKey");
    }

    public void setUserKey(String userKey) {
        this.userKey = Validate.notNull(userKey, "userKey");
    }

    public void setPasteFormat(String pasteFormat) {
        this.pasteFormat = Validate.notNull(pasteFormat, "pasteFormat");
    }

    public void setPasteName(String pasteName) {
        this.pasteName = Validate.notNull(pasteName, "pasteName");
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = Validate.notNull(expireDate, "expireDate");
    }

    public void setListed(int listed) {
        this.listed = listed;
    }
}