package org.shaharit.face2face.backend.models;

public class Gift {
    public GenderCommunications greeting;
    public String url;
    public GenderCommunications cta;
    public String type;

    public Gift() {
    }

    public Gift(GenderCommunications greeting, GenderCommunications cta, String url,  String type) {
        this.greeting = greeting;
        this.url = url;
        this.cta = cta;
        this.type = type;
    }
}
