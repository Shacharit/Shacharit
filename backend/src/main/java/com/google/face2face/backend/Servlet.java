/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.google.face2face.backend;

import com.google.appengine.repackaged.com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.servlet.http.*;

public class Servlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Please use the form to POST to this url");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");
        String token = "dnRQfgby6jk:APA91bHLTo6XluH1fo-AdIvx4kgQcn_q5WnprsaimszK2afWl3-5mDIVm0hIRkLVeCeEQ6XBG8m36DA3QKqULaF6AkcIFA-58GCHDmGPISHUID_IZEqMzoKpdR1PM_Twy0SFqzgcB8TT";

        sendPushMessage(token);
    }

    private void sendPushMessage(String token) throws IOException {
        try {
            Content content = new Content(token, "Rafi!!", "Rafi!");

            // 1. URL
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            // 2. Open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 3. Specify POST method
            conn.setRequestMethod("POST");
            // 4. Set the headers
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + "AIzaSyBgP8dRQC9xXN1z3piQ1CTuiTtvFy3ztTM");
                    conn.setDoOutput(true);
            // 5. Add JSON data into POST request body
            // 5.1 Use Jackson object mapper to convert Contnet object into JSON
            // 5.2 Get connection output stream
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            Gson gson = new Gson();
            String json = gson.toJson(content);
            // 5.3 Copy Content "JSON" into
            wr.writeBytes(json);
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
            // 7. Print result
            final String res = response.toString();
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
