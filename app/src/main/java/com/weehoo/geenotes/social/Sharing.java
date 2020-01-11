package com.weehoo.geenotes.social;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

public class Sharing {
    /**
     * Invokes Chooser intent to send a file.
     * @param activity Calling activity.
     * @param fileUri URI of file to send.
     * @param message Message title displayed in Chooser UI.
     */
    public static void sendFile(AppCompatActivity activity, Uri fileUri, String message) {
        // Create "send" intent.
        Intent sendIntent = new Intent();
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        sendIntent.setType(activity.getContentResolver().getType(fileUri));

        // Start chooser intent activity.
        activity.startActivity(Intent.createChooser(sendIntent, message));
    }
}
