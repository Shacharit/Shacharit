package com.google.face2face.backend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FirebaseInitializer {
    public static final String CredentialJsonText = "{\n" +
            "  \"type\": \"service_account\",\n" +
            "  \"project_id\": \"il-hackathon\",\n" +
            "  \"private_key_id\": \"57153ee57f53853e162ad02230c9b8af9dc5affd\",\n" +
            "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCZJM35gD9oIX/L\\nBaXDa0ba7z3k/9a+ZcvewUG9ePYaA2Dvmz4gX7/TNREWOJ8oVfvizQbRiFbPlMHZ\\nN6g2YbdROgCkLXcED5sc9ZYtNZdAMRDe4nkfNUkfokYUYT5szfOzQn4KO8etqbNT\\nTEyfz2NWyvGMT9VYwBjsDtHvHccCMam8tsVT8jEBNdRqNA62tI5Oon7nxNq4K760\\nsyvr6w6ptcKez0vW1dR+Mgaf6sJ2AGECQ9+GR83tdQKAjSeBRLoEYQVztP8DxiwT\\nOuJm/hnYS7uijqNs8EjcPlG37H1PM9S8QjiZvH11BeWmVqF8tvMpmwjJRck7tvLx\\neKZNbMiPAgMBAAECggEBAJMGGJD+JOGNt6hrxZZ1OVMa+CsPy1SDWlSC6OBEbtDO\\niZO/UMe35OuaOMtClNoeonI9L9ydreogAHLP0jhTW7EppE/6LhbB/MKyq/i/SHQi\\nJvY0KuBhQdCS9ADA0zOWDe79WQ/uEGeTUkwYJkAO1GoQ8BEq28IWulZ2a+nKhrJ9\\nAAjK7Enz4tuuYP6zQQ7zFZ+tQe33qIyui1bMxYanYDItHLxBNNquzbOoL/a7TEh1\\nMdj5EfU10ywx6GMn8sOwDCuARqzGY5sutyXNzE/ysfY+/gxd3uipnyl6esKKBWg+\\nUvJsnpxvforRwRpKpXLh1gep+JMSCzTZTPNOR98os4ECgYEA/BtqEtxMfQ54GRqk\\npm0RUvt67KdVk14Bj9IYFGX3kAW4xd7zvS7TFshi566xrmi/hyBNQMUhnFu4YoB0\\nOr0Y+T2WpjCldI8DgZybVXKqtMdUu6dpbyMhTA6Ut4s1PZuKgqgHf1P4zBiaZyRL\\nmsjz100BYNYvYfiZDK4dbicmje8CgYEAm4IvjVUBQWfLjf8SBktMD0f6ZKChcdyM\\nd/zZW/qXbDX2f3lams5fZNiSv+6tzLHBrQLqs/Oky+X4/LcxAy28g1C0T3iFLymR\\nYiSC4aa87m7Pxo6IjxGmIzl6jMBtCMvnguKZnrL0OKnIWjTA3pgQkykeKtiroQ02\\nIDdp2qKAD2ECgYEAk4VDK6gnReJKmn4BrLUTtxga1RIRvDr89Ph+SKlmFKuUdIzC\\n2Yfl02YMof+STzlSIbLgIa94vHLbUSBTYu+2BlpopfS/TJZG+ff22ShRBZnCVlBf\\nHL5UxJmfokteKx+yDERgprwvUNZwuzv81aUTMoAH9289qLl8bXQRGpzkLZcCgYA+\\naG+m+49GbV5OfSgBqKQ8mblOOvyvkkA5QxsEA7xTLBDhtZT7/YhhE11Jc4MCW/AK\\n+9mvID9pqMBUwzwDN46o8HF8VzSz6a2zwk1YO68kzQnLeJYrPHz9M+sPo4Xeet5N\\nM51NCCpktxZ1xSgF7Obx+BVoQo4WUxP+t95eZMqZ4QKBgQCXMHqiVBd0JjWimJpD\\nXEWmokW1H/UDef7BBUMjyT4YXsj/a2hw4ESmtVBLvWSqVS5dehEd2mkSPObXqaZl\\nuQMRLDfC9BD93Fnj4Hzj2MG6S0NHfO/LGohF9tDNsM20bdeRUcq+WK9lEQ7JkUqN\\nPCTXiS7ur1OHXns9RINIcbYTWg==\\n-----END PRIVATE KEY-----\\n\",\n" +
            "  \"client_email\": \"shacharit-backend@il-hackathon.iam.gserviceaccount.com\",\n" +
            "  \"client_id\": \"113223052099295306294\",\n" +
            "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
            "  \"token_uri\": \"https://accounts.google.com/o/oauth2/token\",\n" +
            "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
            "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/shacharit-backend%40il-hackathon.iam.gserviceaccount.com\"\n" +
            "}\n";
    public static final String DatabaseUrl = "https://il-hackathon.firebaseio.com";

    private static boolean isInitialized = false;

    // This method only initializes firebase the first time it is called.
    public static synchronized void initializeFirebase() {
        if (!isInitialized) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setServiceAccount(new ByteArrayInputStream(CredentialJsonText.getBytes()))
                    .setDatabaseUrl(DatabaseUrl)
                    .build();
            FirebaseApp.initializeApp(options);
        }
        isInitialized = true;
    }
}
