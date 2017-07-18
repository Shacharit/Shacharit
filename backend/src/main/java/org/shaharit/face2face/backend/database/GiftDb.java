package org.shaharit.face2face.backend.database;

import org.shaharit.face2face.backend.models.Gift;

import java.util.List;

public interface GiftDb {
    void getUnsentGifts(GiftsHandler handler);
    void markGiftAsSent(String uid, String giftId);

    interface GiftsHandler extends ResultHandler<List<Gift>> {}
}
