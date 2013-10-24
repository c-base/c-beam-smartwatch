package org.c_base.c_beam.smartwatch;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;


public class CbeamPreferenceActivity extends PreferenceActivity {
    private static final int DIALOG_CLEAR = 1;

    private static final int ERROR_CBEAM_NOT_INSTALLED = 1;
    private static final int ERROR_CBEAM_PERMISSION_ERROR = 2;


    private int mError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        Preference preference = findPreference(getString(R.string.preference_key_clear));
        preference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDialog(DIALOG_CLEAR);
                return true;
            }
        });

        preference = findPreference(getText(R.string.preference_key_email_developer));
        preference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                emailDeveloper();
                return true;
            }
        });

        // Remove preferences that are not supported by the accessory
        if (!ExtensionUtils.supportsHistory(getIntent())) {
            preference = findPreference(getString(R.string.preference_key_clear));
            getPreferenceScreen().removePreference(preference);
        }

        preference = findPreference(getString(R.string.preference_key_error));
        preference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                switch (mError) {
                    case ERROR_CBEAM_NOT_INSTALLED: {
                        openPlayStore();
                        break;
                    }
                    case ERROR_CBEAM_PERMISSION_ERROR: {
                        openAppInfo();
                        break;
                    }
                }
                return true;
            }
        });

        if (!CbeamHelper.isAppInstalled(this)) {
            preference.setSummary(R.string.preference_status_cbeam_not_installed);
            mError = ERROR_CBEAM_NOT_INSTALLED;
        } else if (!CbeamHelper.hasNotificationPermission(this)) {
            preference.setSummary(R.string.preference_status_cbeam_permission_error);
            mError = ERROR_CBEAM_PERMISSION_ERROR;
        } else {
            getPreferenceScreen().removePreference(preference);
        }
    }

    private void openPlayStore() {
        Uri uri = Uri.parse("market://details?id=" + CbeamHelper.PACKAGE_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e(CbeamExtensionService.LOG_TAG, "Couldn't open c-beam Play Store page", e);
        }
    }

    private void openAppInfo() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e(CbeamExtensionService.LOG_TAG, "Couldn't open app details", e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        switch (id) {
            case DIALOG_CLEAR:
                dialog = createClearDialog();
                break;
            default:
                Log.w(CbeamExtensionService.LOG_TAG, "Not a valid dialog id: " + id);
                break;
        }

        return dialog;
    }

    /**
     * Create the Clear events dialog
     *
     * @return the Dialog
     */
    private Dialog createClearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.preference_option_clear_txt)
                .setTitle(R.string.preference_option_clear)
                .setIcon(android.R.drawable.ic_input_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        new ClearEventsTask().execute();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

    private void emailDeveloper() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("mailto:" + getString(R.string.developer_email_uri)));
        startActivity(intent);
    }

    /**
     * Clear all messaging events
     */
    private class ClearEventsTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return NotificationUtil.deleteAllEvents(CbeamPreferenceActivity.this);
        }

        @Override
        protected void onPostExecute(Integer id) {
            if (id != NotificationUtil.INVALID_ID) {
                Toast.makeText(CbeamPreferenceActivity.this, R.string.clear_success,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CbeamPreferenceActivity.this, R.string.clear_failure,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
