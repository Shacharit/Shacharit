/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.google.face2face.backend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.*;

import static com.google.face2face.backend.FcmMessenger.sendPushMessage;

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
        String token = "fmRZ6KFsuYI:APA91bHbYkBJ3GizRmOKp88Fc4O62ke2WaQJAfS1JsnwDkDcZ37NAvAy1ZK9yPJyt56o9fb3tkb_PWG4zr2F3WGq11VwsW4FWARWfSeIYKwMHZ-Wd12bbdWffRvdvsjpymkhEzAcqHME";
        Map<String, String> extras = new HashMap<>();

        sendPushMessage(token, "רפי", "רפי", extras);
    }
}
