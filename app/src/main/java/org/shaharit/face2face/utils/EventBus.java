package org.shaharit.face2face.utils;

import com.squareup.otto.Bus;

/**
 * Created by kalisky on 7/18/17.
 */

public class EventBus {

    private Bus eventBus = new Bus();
    private static EventBus instance = new EventBus();

    public static Bus getInstance() {
        return instance.eventBus;
    }
}
