package org.shaharit.face2face.backend.push;

import com.google.appengine.repackaged.com.google.gson.Gson;

import org.shaharit.face2face.backend.Content;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class FcmMessenger {
    public static void sendPushMessage(String token, String title, String message, Map<String, String> extras) throws IOException {
        try {
            final String encoding = "UTF-8";

            Content content = new Content(token, title,
                    message, extras);


            // 1. URL
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            // 2. Open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 3. Specify POST method
            conn.setRequestMethod("POST");
            // 4. Set the headers
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Authorization", "key=" + "AIzaSyBgP8dRQC9xXN1z3piQ1CTuiTtvFy3ztTM");
            conn.setDoOutput(true);
            // 5. Add JSON data into POST request body
            // 5.1 Use Jackson object mapper to convert Contnet object into JSON
            // 5.2 Get connection output stream
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            Gson gson = new Gson();
            String json = gson.toJson(content);
            // 5.3 Copy Content "JSON" into

            wr.write(json.getBytes("UTF-8"));

            System.out.println("Sending: " + json);
            // 5.4 Send the request
            wr.flush();
            // 5.5 close
            wr.close();
            // 6. Get the response
            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            conn.disconnect();
            // 7. Print matchResult
            final String res = response.toString();
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String token, String title, String message, Map<String, String> extras)
            throws IOException {
        FcmMessenger.sendPushMessage(token, title, message, extras);
    }
}
