package io.mindit.dockerdemo;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;

public class Wait {
    public static void untilApplicationIsUp() {
        Awaitility.await().atMost(1, TimeUnit.MINUTES).until(() -> isApplicationUp("http://localhost:8080/actuator/health"));
    }

    public static boolean isApplicationUp(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.connect();
            return huc.getResponseCode() == 200;
        } catch (Exception ex) {
            // ignore
            return false;
        }
    }
}
