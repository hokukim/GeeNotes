package com.weehoo.geenotes;

import android.content.ContentProvider;
import android.content.Context;
import android.os.Build;

public class GeeNotesApplication extends android.app.Application {
    private static GeeNotesApplication mInstance;

    public static Context getContext() {
        return mInstance.getApplicationContext();
    }
    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     *
     * <p>Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.</p>
     *
     * <p>If you override this method, be sure to call {@code super.onCreate()}.</p>
     *
     * <p class="note">Be aware that direct boot may also affect callback order on
     * Android {@link Build.VERSION_CODES#N} and later devices.
     * Until the user unlocks the device, only direct boot aware components are
     * allowed to run. You should consider that all direct boot unaware
     * components, including such {@link ContentProvider}, are
     * disabled until user unlock happens, especially when component callback
     * order matters.</p>
     */
    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }
}
