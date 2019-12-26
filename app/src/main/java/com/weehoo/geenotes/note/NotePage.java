package com.weehoo.geenotes.note;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Note page can only be directly accessed by note book.
 */
public class NotePage {

    private String mID;

    private static final String ID_KEY = "id";

    /**
     * Constructs a new NotePage object with empty data.
     */
    NotePage() {
        mID = UUID.randomUUID().toString();
    }

    /**
     * Constructs a new NotePage object using the specified ID.
     */
    private NotePage(String id) {
        mID = id;
    }

    /**
     * Gets the ID of this page.
     * @return Page ID.
     */
    public String getID() {
        return mID;
    }

    /**
     * Converts the note page to a JSON object.
     * @return JSON object.
     */
    JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(NotePage.ID_KEY, mID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * Converts a JSON object to a note page.
     * @param jsonObject JSON object to convert.
     * @return Note page.
     */
    static NotePage fromJSONObject(JSONObject jsonObject) {
        NotePage notePage = null;

        try {
            notePage = new NotePage(jsonObject.getString(NotePage.ID_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return notePage;
    }
}
