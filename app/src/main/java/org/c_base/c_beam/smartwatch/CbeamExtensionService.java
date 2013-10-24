package org.c_base.c_beam.smartwatch;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CbeamExtensionService extends ExtensionService {

    /**
     * Extensions specific id for the source
     */
    public static final String EXTENSION_SPECIFIC_ID = "CBEAM_NOTIFICATION";

    /**
     * Extension key
     */
    public static final String EXTENSION_KEY = "org.c_base.c_beam.smartwatch";

    /**
     * Log tag
     */
    public static final String LOG_TAG = "SmartCbeam";


    public CbeamExtensionService() {
        super(EXTENSION_KEY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int retVal = super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            String action = intent.getAction();
            if (CbeamHelper.BroadcastIntents.ACTION_BOARDING.equals(action)) {
                onBoardingNotification(intent);
                stopSelfCheck();
            } else if (CbeamHelper.BroadcastIntents.ACTION_ETA.equals(action)) {
                onEtaNotification(intent);
                stopSelfCheck();
            } else if (CbeamHelper.BroadcastIntents.ACTION_READ.equals(action) ||
                    CbeamHelper.BroadcastIntents.ACTION_CANCELLED.equals(action)) {
                onReadNotification();
                stopSelfCheck();
            }
        }

        return retVal;
    }

    private void onBoardingNotification(Intent intent) {
        String member = intent.getStringExtra(CbeamHelper.IntentExtras.MEMBER);
        long timestamp = intent.getLongExtra(CbeamHelper.IntentExtras.TIMESTAMP, 0);
        String time = formatTime(timestamp);
        String message = getString(R.string.notification_boarding_body_format, member, time);
        createEvent(member, timestamp, R.string.notification_boarding_title, message);
    }

    private void onEtaNotification(Intent intent) {
        String member = intent.getStringExtra(CbeamHelper.IntentExtras.MEMBER);
        String eta = intent.getStringExtra(CbeamHelper.IntentExtras.ETA);
        long timestamp = intent.getLongExtra(CbeamHelper.IntentExtras.TIMESTAMP, 0);
        String time = formatTime(timestamp);
        String message = getString(R.string.notification_eta_body_format, member, time, eta);
        createEvent(member, timestamp, R.string.notification_eta_title, message);
    }

    private String formatTime(long timestamp) {
        DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(new Date(timestamp));
    }

    private void createEvent(String member, long timestamp, int titleRes, String message) {

        long sourceId = NotificationUtil.getSourceId(this, EXTENSION_SPECIFIC_ID);
        if (sourceId == NotificationUtil.INVALID_ID) {
            Log.e(LOG_TAG, "Failed to insert data");
            return;
        }

        ContentValues eventValues = new ContentValues();
        eventValues.put(Notification.EventColumns.EVENT_READ_STATUS, false);
        eventValues.put(Notification.EventColumns.DISPLAY_NAME, member);
        eventValues.put(Notification.EventColumns.TITLE, getString(titleRes));
        eventValues.put(Notification.EventColumns.MESSAGE, message);
        eventValues.put(Notification.EventColumns.PERSONAL, 1);
        eventValues.put(Notification.EventColumns.PUBLISHED_TIME, timestamp);
        eventValues.put(Notification.EventColumns.SOURCE_ID, sourceId);

        try {
            getContentResolver().insert(Notification.Event.URI, eventValues);
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Failed to insert event", e);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "Failed to insert event, is Live Ware Manager installed?", e);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Failed to insert event", e);
        }
    }

    private void onReadNotification() {
        ContentValues cv = new ContentValues();
        cv.put(Notification.EventColumns.EVENT_READ_STATUS, true);
        NotificationUtil.updateEvents(this, cv, null, null);
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new CbeamRegistrationInformation(this);
    }

    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }

    @Override
    public void startActivity(Intent intent) {
        // We're a service. So we need to start all activities with FLAG_ACTIVITY_NEW_TASK.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivity(intent);
    }
}
