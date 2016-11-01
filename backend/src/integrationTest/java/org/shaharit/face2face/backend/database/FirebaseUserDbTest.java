package org.shaharit.face2face.backend.database;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Ignore;
import org.junit.Test;
import org.shaharit.face2face.backend.database.firebase.FirebaseUserDb;
import org.shaharit.face2face.backend.models.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@Ignore("Cannot make tests for DB due to firebase issue similar to http://stackoverflow.com/questions/38942180/firebase-database-connection-with-java-server-throws-invocationtargetexception. I've filed a bug to Firebase support")
public class FirebaseUserDbTest {
    @Test
    public void readPresetUsers() throws Exception {
        UserDb userDb = new FirebaseUserDb(getFirebaseReference());
        final CountDownLatch lock = new CountDownLatch(1);

        TestUsersHandler handler = new TestUsersHandler(lock);
        userDb.getUsers(handler);

        lock.await(1, TimeUnit.SECONDS);

        List<User> readUsers = handler.getReadUsers();
        assertThat(readUsers, is(empty()));
    }

    private DatabaseReference getFirebaseReference() throws FileNotFoundException {
        String credentialFilePath =
                "src/main/webapp/WEB-INF/IL Hackathon-deac621aef6f.json";

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://il-hackathon.firebaseio.com")
                .setServiceAccount(new FileInputStream(credentialFilePath))
                .build();

        return FirebaseDatabase
                .getInstance(FirebaseApp.initializeApp(firebaseOptions)).getReference();
    }

    private class TestUsersHandler implements UserDb.UsersHandler {
        private List<User> readUsers;
        private CountDownLatch lock;

        TestUsersHandler(CountDownLatch lock) {
            this.lock = lock;
        }

        @Override
        public void processResult(List<User> users) {
            readUsers = users;
            lock.countDown();
        }

        List<User> getReadUsers() {
            return readUsers;
        }
    }
}