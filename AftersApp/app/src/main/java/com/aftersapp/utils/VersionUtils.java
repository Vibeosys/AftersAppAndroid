package com.aftersapp.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.aftersapp.AftersAppApplication;

/**
 * Created by akshay on 23-09-2016.
 */
public class VersionUtils {
    public static int getAppVersion() {
        return getAppPackageInfo().versionCode;
    }

    public static String getAppVersionName() {
        return getAppPackageInfo().versionName;
    }

    private static PackageInfo getAppPackageInfo() {
        Context context = AftersAppApplication.getInstance();
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
