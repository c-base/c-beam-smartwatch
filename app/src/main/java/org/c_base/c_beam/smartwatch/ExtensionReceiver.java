package org.c_base.c_beam.smartwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * The extension receiver receives the extension intents and starts the
 * extension service when it arrives.
 */
public class ExtensionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        intent.setClass(context, CbeamExtensionService.class);
        context.startService(intent);
    }
}
