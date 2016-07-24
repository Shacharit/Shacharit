package com.google.face2face.backend.servlets;

import com.google.face2face.backend.FcmMessenger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ronyjac on 24/07/2016.
 */
public class GiftSendingServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO - Iterate over all gifts and send push notifiction to recipient

        String reg_id = "";
        String title = "";
        String message = "";
        Map<String, String> extras = new HashMap<>();

        //build message, title, user token (reg_id)
        // Mark gift sent
        // extras: all data base data

        FcmMessenger.sendPushMessage(reg_id, title, message, extras);



    }
}
