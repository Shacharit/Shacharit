package com.google.face2face.backend;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Content implements Serializable {
    private List<String> registration_ids = new LinkedList<>();
    private boolean dry_run = false;
    private String priority = "high";
    private Map<String, String> data = new HashMap<>();
    private Map<String, Object> notification = new HashMap<>();

    public Content() {
    }

    public Content(String regId, String title, String message, Map<String, String> extraData) {
        addRegId(regId);
//        notification.put("title", title);
//        notification.put("body", message);
//        notification.put("sound", "default");
//        notification.put("badge", "0");
        data.put("title", title);
        data.put("message", message);
        data.put("action", "send_email");
        data.put("email", "mikofink@gmail.com");

        for (Map.Entry<String, String> entry : extraData.entrySet()) {
            data.put(entry.getKey(), entry.getValue());
        }

    }

    public void addRegId(String regId) {
        registration_ids.add(regId);
    }

    public List<String> getRegistration_ids() {
        return registration_ids;
    }
}