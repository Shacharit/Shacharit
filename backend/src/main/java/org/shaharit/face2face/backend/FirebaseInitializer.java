package org.shaharit.face2face.backend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class FirebaseInitializer {
    private static boolean isInitialized = false;

    // This method only initializes firebase the first time it is called.
    public static synchronized void initializeFirebase(InputStream serviceAccountStream, String databaseUrl) {
        if (!isInitialized) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setServiceAccount(serviceAccountStream)
                    .setDatabaseUrl(databaseUrl)
                    .build();
            FirebaseApp.initializeApp(options);
        }
        isInitialized = true;
    }
}
