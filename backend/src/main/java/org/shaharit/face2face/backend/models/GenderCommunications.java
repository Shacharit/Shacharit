package org.shaharit.face2face.backend.models;

public class GenderCommunications {
    public String[][] texts = new String[Gender.values().length][Gender.values().length];

    public GenderCommunications addCommunication(Gender sender, Gender recipient, String text) {
        texts[sender.ordinal()][recipient.ordinal()] = text;
        return this;
    }

    public String getCommunication(Gender sender, Gender recipient) {
        if (recipient == null || sender ==null) {
            return texts[Gender.MALE.ordinal()][Gender.MALE.ordinal()];
        }

        return texts[sender.ordinal()][recipient.ordinal()];
    }
}
