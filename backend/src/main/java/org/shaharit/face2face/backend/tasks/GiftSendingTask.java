package org.shaharit.face2face.backend.tasks;

import com.google.appengine.repackaged.com.google.common.base.Function;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

import org.shaharit.face2face.backend.database.GiftDb;
import org.shaharit.face2face.backend.database.UserDb;
import org.shaharit.face2face.backend.models.Gift;
import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.push.PushService;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class GiftSendingTask implements Task {

    private GiftDb giftDb;
    private final UserDb userDb;
    private final PushService pushService;

    public GiftSendingTask(GiftDb giftDb, UserDb userDb, PushService pushService) {
        this.giftDb = giftDb;
        this.userDb = userDb;
        this.pushService = pushService;
    }

    @Override
    public void execute() {
        giftDb.getUnsentGifts(new GiftsSender());
    }

    private class GiftsSender implements GiftDb.GiftsHandler {
        @Override
        public void processResult(final List<Gift> gifts) {
            List<String> recipientUserIds = Lists.transform(gifts, new Function<Gift, String>() {
                @Override
                public String apply(Gift gift) {
                    return gift.recipientUid;
                }
            });

            if (gifts.isEmpty()) {
                return;
            }

            userDb.getRegIdForUserIds(recipientUserIds, new UserDb.RegIdsHandler() {
                @Override
                public void processResult(Map<String, String> uidToRegIdMap) {
                    for (Gift gift : gifts) {
                        String recipientRegId = uidToRegIdMap.get(gift.recipientUid);

                        if (recipientRegId != null) {
                            // Only send gift push if we have reg ID for the user
                            // TODO: Ask Michael if mark as sent or not
                            pushService.sendPushAboutGift(recipientRegId,gift);
                        }

                        giftDb.markGiftAsSent(gift.id);
                    }
                }
            });
        }
    }
}