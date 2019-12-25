package com.weehoo.geenotes.note;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Note page can only be directly accessed by note book.
 */
public class NotePage {

    private String mID;

    private static final String ID_KEY = "id";

    /**
     * Constructs a new NotePage object with empty data.
     */
    public NotePage() {
        mID = "1"; // TODO: Secure random ID generation.
    }

    /**
     * Constructs a new NotePage object using the specified ID.
     */
    public NotePage(String id) {
        mID = id;
    }

    /**
     * Converts the note page to a JSON object.
     * @return JSON object.
     */
    public JSONObject toJSONObject() {
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
    public static NotePage fromJSONObject(JSONObject jsonObject) {
        NotePage notePage = null;

        try {
            notePage = new NotePage(jsonObject.getString(NotePage.ID_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return notePage;
    }
}
