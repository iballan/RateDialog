package com.mbh.ratedialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created By MBH on 2016-10-13.
 */

public class RateDialog {
    private static final String PREF_NAME = "RateAzkari";
    private static final String KEY_INSTALL_DATE = "mbh_install_date";
    private static final String KEY_LAUNCH_TIMES = "mbh_launch_times";
    private static final String KEY_OPT_OUT = "mbh_opt_out";
    private static final String KEY_IS_REMIND_LATER = "mbh_remind_me";

    private static Date mInstallDate = new Date();
    private static int mLaunchTimes = 0;
    private static boolean mOptOut = false;

    private static Config sConfig = new Config();

    /**
     * If true, print LogCat
     */
    private static final boolean DEBUG = false;

    /**
     * Initialize RateThisApp configuration.
     *
     * @param config Configuration object.
     */
    private static void init(Config config) {
        sConfig = config;
    }

    /**
     * Initialize and start the dialog if needed
     * @param config : RateDialog.Config which contains all information needed for RateDialog
     * @param context: Context
     * @return true if needs to show, false if not yet
     */
    public static boolean onStart(Config config, Context context){
        init(config);
        onStart(context);
        return showRateDialogIfNeeded(context);
    }

    /**
     * Call this API when the launcher activity is launched.<br>
     * It is better to call this API in onStart() of the launcher activity.
     *
     * @param context Context
     */
    private static void onStart(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        // If it is the first launch, save the date in shared preference.
        if (pref.getLong(KEY_INSTALL_DATE, 0) == 0L) {
            Date now = new Date();
            editor.putLong(KEY_INSTALL_DATE, now.getTime());
            log("First install: " + now.toString());
        }
        // Increment launch times
        int launchTimes = pref.getInt(KEY_LAUNCH_TIMES, 0);
        launchTimes++;
        editor.putInt(KEY_LAUNCH_TIMES, launchTimes);
        log("Launch times; " + launchTimes);

        editor.commit();

        mInstallDate = new Date(pref.getLong(KEY_INSTALL_DATE, 0));
        mLaunchTimes = pref.getInt(KEY_LAUNCH_TIMES, 0);
        mOptOut = pref.getBoolean(KEY_OPT_OUT, false);

        printStatus(context);
    }

    /**
     * Show the rate dialog if the criteria is satisfied.
     *
     * @param context Context
     * @return true if shown, false otherwise.
     */
    private static boolean showRateDialogIfNeeded(final Context context) {
        if (shouldShowRateDialog()) {
            showRateDialog(context);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check whether the rate dialog shoule be shown or not
     *
     * @return
     */
    private static boolean shouldShowRateDialog() {
        if (mOptOut) {
            return false;
        } else {
            if (mLaunchTimes >= sConfig.mCriteriaLaunchTimes) {
                return true;
            }
            long threshold = sConfig.mCriteriaInstallDays * 24 * 60 * 60 * 1000L;    // msec
            if (new Date().getTime() - mInstallDate.getTime() >= threshold) {
                return true;
            }
            return false;
        }
    }

    /**
     * Show the rate dialog
     *
     * @param context
     */
    public static void showRateDialog(final Context context) {
        if (sConfig == null || sConfig.mTitleId == 0 || sConfig.mMessageId == 0)
            return;

        final MaterialDialog mMaterialDialog = new MaterialDialog(context);
        mMaterialDialog
                .setTitle(sConfig.mTitleId)
                .setMessage(sConfig.mMessageId)
                //mMaterialDialog.setBackgroundResource(R.drawable.background);
                .setPositiveButton(sConfig.mOkButton, new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        String appPackage = context.getPackageName();
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id="
                                        + appPackage));
                        context.startActivity(intent);
                        setOptOut(context, true);
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton(sConfig.mRemindMeLater,
                        new View.OnClickListener() {
                            @Override public void onClick(View v) {
                                clearSharedPreferences(context);
                                mMaterialDialog.dismiss();
                            }
                        })
                .setNeutralButton(sConfig.mNoThanks, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearSharedPreferences(context);
                        mMaterialDialog.dismiss();
                    }
                })
                .setCanceledOnTouchOutside(true)
                .setOnDismissListener(
                        new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                // No Thanks
                                //setOptOut(context, true);
                                clearSharedPreferences(context);
                                mMaterialDialog.dismiss();
                            }
                        })
                .setCanceledOnTouchOutside(true)
                .show();
    }

    /**
     * Clear data in shared preferences.<br>
     * This API is called when the rate dialog is approved or canceled.
     *
     * @param context
     */
    private static void clearSharedPreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(KEY_INSTALL_DATE);
        editor.remove(KEY_LAUNCH_TIMES);
        editor.commit();
    }

    /**
     * Set opt out flag. If it is true, the rate dialog will never shown unless app data is cleared.
     *
     * @param context
     * @param optOut
     */
    private static void setOptOut(final Context context, boolean optOut) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_OPT_OUT, optOut);
        editor.commit();
    }

    /**
     * Print values in SharedPreferences (used for debug)
     *
     * @param context
     */
    private static void printStatus(final Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        log("*** RateThisApp Status ***");
        log("Install Date: " + new Date(pref.getLong(KEY_INSTALL_DATE, 0)));
        log("Launch Times: " + pref.getInt(KEY_LAUNCH_TIMES, 0));
        log("Opt out: " + pref.getBoolean(KEY_OPT_OUT, false));
    }

    /**
     * Print log if enabled
     *
     * @param message
     */
    private static void log(String message) {
        if (DEBUG) {
            Log.v(TAG, message);
        }
    }

    /**
     * RateThisApp configuration.
     */
    public static class Config {
        private int mCriteriaInstallDays;
        private int mCriteriaLaunchTimes;
        private int mOkButton = 0;
        private int mRemindMeLater = 0;
        private int mNoThanks = 0;
        private int mTitleId = 0;
        private int mMessageId = 0;

        /**
         * Constructor with default criteria.
         */
        public Config() {
            // every 7 days, or 15 app launches
            this(7, 15);
        }

        /**
         * Constructor.
         *
         * @param criteriaInstallDays
         * @param criteriaLaunchTimes
         */
        public Config(int criteriaInstallDays, int criteriaLaunchTimes) {
            this.mCriteriaInstallDays = criteriaInstallDays;
            this.mCriteriaLaunchTimes = criteriaLaunchTimes;
        }

        /**
         * Show dialog after installation with how many days
         * @param criteriaInstallDays
         */
        public void setInstallDays(int criteriaInstallDays){
            this.mCriteriaInstallDays = criteriaInstallDays;
        }

        /**
         * Show dialog after how many launch times
         * @param criteriaLaunchTimes
         */
        public void setLaunchTimes(int criteriaLaunchTimes){
            this.mCriteriaLaunchTimes = criteriaLaunchTimes;
        }

        /**
         * Set title string ID.
         *
         * @param stringId
         */
        public void setTitle(int stringId) {
            this.mTitleId = stringId;
        }

        /**
         * Set message string ID.
         *
         * @param stringId
         */
        public void setMessage(int stringId) {
            this.mMessageId = stringId;
        }

        /**
         * Set Rate Us now message.
         *
         * @param mOkButton
         */
        public void setmOkButton(int mOkButton) {
            this.mOkButton = mOkButton;
        }

        /**
         * Set RemindMeLater message.
         *
         * @param mRemindMeLater
         */
        public void setmRemindMeLater(int mRemindMeLater) {
            this.mRemindMeLater = mRemindMeLater;
        }

        /**
         * Set NoThanks message.
         *
         * @param mNoThanks
         */
        public void setmNoThanks(int mNoThanks) {
            this.mNoThanks = mNoThanks;
        }
    }
}
