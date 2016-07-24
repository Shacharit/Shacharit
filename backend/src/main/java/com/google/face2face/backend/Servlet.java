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
        String token = "dnRQfgby6jk:APA91bHLTo6XluH1fo-AdIvx4kgQcn_q5WnprsaimszK2afWl3-5mDIVm0hIRkLVeCeEQ6XBG8m36DA3QKqULaF6AkcIFA-58GCHDmGPISHUID_IZEqMzoKpdR1PM_Twy0SFqzgcB8TT";

        sendPushMessage(token);
    }
}
