package org.shaharit.face2face.backend.servlets;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.shaharit.face2face.backend.FirebaseInitializer;
import org.shaharit.face2face.backend.tasks.MatchingTask;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

public abstract class ShaharitServlet extends HttpServlet {
    // Firebase keys shared with client applications
    protected DatabaseReference firebase;
    private static boolean hasBeenInitialized = false;
    private static final Logger logger = Logger.getLogger(ShaharitServlet.class.getName());

    @Override
    public void init(ServletConfig config) {
        String credential = config.getInitParameter("credential");
        String databaseUrl = config.getInitParameter("databaseUrl");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(config.getServletContext().getResourceAsStream(credential))
                .setDatabaseUrl(databaseUrl)
                .build();

        initFirebase(options);

        firebase = FirebaseDatabase.getInstance().getReference();
    }

    private synchronized void initFirebase(FirebaseOptions options) {
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
        for(FirebaseApp app : firebaseApps){
            if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)){
                hasBeenInitialized=true;
            }
        }

        if (!hasBeenInitialized) {
            try {
                FirebaseApp.initializeApp(options);
            } catch (Exception e) {
                logger.info("Exception on firebase init. Assuming already initialized");
                logger.info(e.getMessage());
            } finally {
                hasBeenInitialized = true;
            }

        }
    }
}
