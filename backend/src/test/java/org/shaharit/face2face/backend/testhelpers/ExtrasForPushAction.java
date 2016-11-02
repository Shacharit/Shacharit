package org.shaharit.face2face.backend.testhelpers;

import org.mockito.ArgumentMatcher;
import org.shaharit.face2face.backend.models.User;

import java.util.Map;

public class ExtrasForPushAction extends ArgumentMatcher<Map<String, String>> {
    private String actionName;

    private ExtrasForPushAction(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public boolean matches(Object argument) {
        Map<String, String> extras = (Map<String, String>) argument;

        return extras.get("action").equals(actionName);
    }


    public static ExtrasForPushAction hasExtrasForPushAction(String actionName) {
        return new ExtrasForPushAction(actionName);
    }
}
