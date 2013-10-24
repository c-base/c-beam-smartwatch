package org.c_base.c_beam.smartwatch;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;


public class CbeamHelper {
    public static final String PACKAGE_NAME = "org.c_base.c_beam";

    public static final String NOTIFICATION_PERMISSION =
            "org.c_base.c_beam.permission.NOTIFICATION";

    public interface BroadcastIntents {
        public static final String ACTION_BOARDING =
                "org.c_base.c_beam.extension.NOTIFICATION_BOARDING";

        public static final String ACTION_ETA =
                "org.c_base.c_beam.extension.NOTIFICATION_ETA";

        public static final String ACTION_READ =
                "org.c_base.c_beam.extension.NOTIFICATION_READ";

        public static final String ACTION_CANCELLED =
                "org.c_base.c_beam.extension.NOTIFICATION_CANCELLED";
    }

    public interface IntentExtras {
        public static final String MEMBER = "member";
        public static final String ETA = "eta";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final Intent getAppStartIntent(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            Intent intent = manager.getLaunchIntentForPackage(PACKAGE_NAME);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            return intent;
        } catch (Exception e) {
            return null;
        }
    }

    public static final boolean isAppInstalled(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            manager.getPackageInfo(PACKAGE_NAME, 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static final boolean hasNotificationPermission(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            return (manager.checkPermission(NOTIFICATION_PERMISSION, context.getPackageName()) ==
                    PackageManager.PERMISSION_GRANTED);
        } catch (Exception e) {
            return false;
        }
    }
}
