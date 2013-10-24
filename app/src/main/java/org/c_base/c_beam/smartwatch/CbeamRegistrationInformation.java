package org.c_base.c_beam.smartwatch;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;

import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

import java.util.ArrayList;
import java.util.List;


public class CbeamRegistrationInformation extends RegistrationInformation {

    final Context mContext;

    protected CbeamRegistrationInformation(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        mContext = context;
    }

    @Override
    public boolean isSourcesToBeUpdatedAtServiceCreation() {
        return true;
    }

    @Override
    public int getRequiredNotificationApiVersion() {
        return 1;
    }

    @Override
    public int getRequiredWidgetApiVersion() {
        return 0;
    }

    @Override
    public int getRequiredControlApiVersion() {
        return 0;
    }

    @Override
    public int getTargetControlApiVersion() {
        return 0;
    }

    @Override
    public int getRequiredSensorApiVersion() {
        return 0;
    }

    @Override
    public ContentValues getExtensionRegistrationConfiguration() {
        String extensionIcon = ExtensionUtils.getUriString(mContext, R.drawable.cbeam_36x36);
        String iconHostapp = ExtensionUtils.getUriString(mContext, R.drawable.ic_launcher);
        String extensionIcon48 = ExtensionUtils.getUriString(mContext, R.drawable.cbeam_48x48);

        String configurationText = mContext.getString(R.string.configuration_text);
        String extensionName = mContext.getString(R.string.extension_name);

        ContentValues values = new ContentValues();
        values.put(Registration.ExtensionColumns.CONFIGURATION_ACTIVITY,
                CbeamPreferenceActivity.class.getName());
        values.put(Registration.ExtensionColumns.CONFIGURATION_TEXT, configurationText);
        values.put(Registration.ExtensionColumns.EXTENSION_ICON_URI, extensionIcon);
        values.put(Registration.ExtensionColumns.EXTENSION_48PX_ICON_URI, extensionIcon48);

        values.put(Registration.ExtensionColumns.EXTENSION_KEY,
                CbeamExtensionService.EXTENSION_KEY);
        values.put(Registration.ExtensionColumns.HOST_APP_ICON_URI, iconHostapp);
        values.put(Registration.ExtensionColumns.NAME, extensionName);
        values.put(Registration.ExtensionColumns.NOTIFICATION_API_VERSION,
                getRequiredNotificationApiVersion());
        values.put(Registration.ExtensionColumns.PACKAGE_NAME, mContext.getPackageName());
        values.put(Registration.ExtensionColumns.LAUNCH_MODE, Registration.LaunchMode.NOTIFICATION);

        return values;
    }

    @Override
    public ContentValues[] getSourceRegistrationConfigurations() {
        List<ContentValues> bulkValues = new ArrayList<ContentValues>();
        bulkValues
                .add(getSourceRegistrationConfiguration(CbeamExtensionService.EXTENSION_SPECIFIC_ID));
        return bulkValues.toArray(new ContentValues[bulkValues.size()]);
    }

    public ContentValues getSourceRegistrationConfiguration(String extensionSpecificId) {
        String iconSource1 = ExtensionUtils.getUriString(mContext, R.drawable.cbeam_30x30);
        String iconSource2 = ExtensionUtils.getUriString(mContext, R.drawable.cbeam_18x18);

        ContentValues sourceValues = new ContentValues();
        sourceValues.put(Notification.SourceColumns.ENABLED, true);
        sourceValues.put(Notification.SourceColumns.ICON_URI_1, iconSource1);
        sourceValues.put(Notification.SourceColumns.ICON_URI_2, iconSource2);
        sourceValues.put(Notification.SourceColumns.UPDATE_TIME, System.currentTimeMillis());
        sourceValues.put(Notification.SourceColumns.NAME, mContext.getString(R.string.source_name));
        sourceValues.put(Notification.SourceColumns.EXTENSION_SPECIFIC_ID, extensionSpecificId);
        sourceValues.put(Notification.SourceColumns.PACKAGE_NAME, mContext.getPackageName());
        sourceValues.put(Notification.SourceColumns.COLOR, Color.argb(0, 0, 0, 0));

        return sourceValues;
    }
}
