package org.shaharit.face2face.backend.servlets;
import org.shaharit.face2face.backend.database.firebase.FirebaseUserDb;
import org.shaharit.face2face.backend.push.FcmMessenger;
import org.shaharit.face2face.backend.push.PushService;
import org.shaharit.face2face.backend.services.FirebaseMatchingLog;
import org.shaharit.face2face.backend.tasks.MatchingTask;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MatchingServlet extends ShaharitServlet {
    private static final Logger logger = Logger.getLogger(MatchingServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        logger.info("in matching-servlet doGet");
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.info("in matching-servlet doPost");

        verifyFirebaseInitialized();

        logger.info("Running matching task at " + Long.toString(System.currentTimeMillis()));
        new MatchingTask(new FirebaseUserDb(firebase),
                new PushService(new FcmMessenger()),
                new FirebaseMatchingLog(firebase)).execute();
        logger.info("Triggered match task");
    }
}