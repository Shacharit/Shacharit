package org.shaharit.face2face.backend.models;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import java.util.List;

public class Buddy {
    public String uid;
    public String displayName;
    public Gender gender;
    public String imageUrl;
    public String email;
    public List<String> selfDefs;

    public Buddy() {
        this.selfDefs = Lists.newArrayList();
    }

    public Buddy(User user) {
        this.uid = user.uid;
        this.displayName = user.displayName;
        this.gender = user.gender;
        this.imageUrl = user.imageUrl;
        this.selfDefs = user.selfDefs;
        this.email = user.email;
    }

    public boolean caresAboutEvent(Event event) {
        for (String eventDef : event.selfDefinitions) {
            if (this.selfDefs.contains(eventDef)) {
                return true;
            }
        }

        return false;
    }
}
