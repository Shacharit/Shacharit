package org.shaharit.face2face.backend.database.fakes;

import org.shaharit.face2face.backend.database.GiftDb;
import org.shaharit.face2face.backend.models.Gift;

import java.util.ArrayList;
import java.util.List;


public class MockGiftDb implements GiftDb {
    private final List<Gift> gifts;
    private List<String> sentGiftIds;

    public MockGiftDb(List<Gift> gifts) {
        this.gifts = gifts;
        this.sentGiftIds = new ArrayList<>();
    }

    @Override
    public void getUnsentGifts(GiftsHandler handler) {
        handler.processResult(gifts);
    }

    @Override
    public void markGiftAsSent(String uid, String giftId) {
        sentGiftIds.add(giftId);
    }

    public boolean wasGiftMarkedAsSent(String giftId) {
        return sentGiftIds.contains(giftId);
    }
}
