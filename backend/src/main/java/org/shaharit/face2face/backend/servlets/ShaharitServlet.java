package org.shaharit.face2face.backend.servlets;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.shaharit.face2face.backend.FirebaseInitializer;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

public abstract class ShaharitServlet extends HttpServlet{
    // Firebase keys shared with client applications
    protected DatabaseReference firebase;

    @Override
    public void init(ServletConfig config) {
        String credential = config.getInitParameter("credential");
        String databaseUrl = config.getInitParameter("databaseUrl");
        FirebaseInitializer.initializeFirebase(
                config.getServletContext().getResourceAsStream(credential),
                databaseUrl);
        firebase = FirebaseDatabase.getInstance().getReference();
    }
}
