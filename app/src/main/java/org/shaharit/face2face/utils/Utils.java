package org.shaharit.face2face.utils;

import org.shaharit.face2face.R;

/**
 * Created by kalisky on 7/18/17.
 */

public class Utils {

    public static int getGiftTypeStringResourceId(String type) {
        if (type == null) {
            return 0;
        } else if (type.equals("video")) {
            return R.string.videoDescription;
        } else if (type.equals("physical")) {
            return R.string.physicalDescription;
        } else if (type.equals("greeting")) {
            return R.string.greetingDescription;
        }
        return 0;
    }
}
