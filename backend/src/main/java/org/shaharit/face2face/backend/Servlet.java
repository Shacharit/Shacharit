/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package org.shaharit.face2face.backend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.*;

import static org.shaharit.face2face.backend.FcmMessenger.sendPushMessage;

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
        String token = "dpabmemA4_o:APA91bE8Y6n2YLC4P_2lV460ZVgSgDRM30x8XoyLMKul5Vdx2FDfc4hvUtecUMqRXhfC8LO_mQ_woZMUm88r84PMvq3lVdexIWHx25j6jd0_G8cK-L_NEzvsFkE8_TOZE1X_9-rIa7Ty";
        Map<String, String> extras = new HashMap<>();

        sendPushMessage(token, "רפי", "רפי", extras);
    }
}
