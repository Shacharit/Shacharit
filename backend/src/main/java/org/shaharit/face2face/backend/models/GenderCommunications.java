package org.shaharit.face2face.backend.models;

public class GenderCommunications {
    public String[][] texts = new String[Gender.values().length][Gender.values().length];

    public GenderCommunications addCommunication(Gender sender, Gender recipient, String text) {
        texts[sender.ordinal()][recipient.ordinal()] = text;
        return this;
    }

    public String getCommunication(Gender sender, Gender recipient) {
        return texts[sender.ordinal()][recipient.ordinal()];
    }
}
