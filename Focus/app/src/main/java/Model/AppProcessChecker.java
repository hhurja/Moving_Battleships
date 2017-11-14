package Model;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import java.security.Permission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import Controller.DialogActivity;
import Controller.MainActivity;
import movingbattleship.org.focus.R;

import static android.R.attr.button;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.os.Binder.getCallingUid;
import static java.security.AccessController.getContext;

/**
 * Created by shabina on 10/13/17.
 */

public class AppProcessChecker {
    public Context context;
    public PackageManager packageManager;
    public UsageStatsManager statsManager;
    public MainActivity mainActivity;

    public AppProcessChecker(Context c, PackageManager pm, UsageStatsManager usm, MainActivity ma) {
        context = c;
        packageManager = pm;
        statsManager = usm;
        mainActivity = ma;

        if(!permissionGranted()){
            Intent overlayPermission = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            overlayPermission.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(overlayPermission);
            Intent usagePermission = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            usagePermission.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(usagePermission);
        }
        AppIconGenerator ap = new AppIconGenerator(packageManager);
    }

    public void blockApplication(HashSet<String> package_names) {
        List<UsageStats> apps = statsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  System.currentTimeMillis() - 1000*1000, System.currentTimeMillis());
        if (apps != null && apps.size() > 0) {
            SortedMap<Long, UsageStats> map = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : apps) {
                map.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (map != null && !map.isEmpty()) {
                UsageStats currentApp;
                currentApp = map.get(map.lastKey());
                if (package_names.contains(currentApp.getPackageName())) {
                    Intent i = new Intent(mainActivity, DialogActivity.class);
                    mainActivity.startActivity(i);
                }
            }
        }

    }

    public String getForegroundedApp() {
        String packageName = "";
        List<UsageStats> apps = statsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  System.currentTimeMillis() - 1000*1000, System.currentTimeMillis());
        SortedMap<Long, UsageStats> map = new TreeMap<Long, UsageStats>();
        for (UsageStats usageStats : apps) {
            map.put(usageStats.getLastTimeUsed(), usageStats);
        }
        if (map != null && !map.isEmpty()) {
            UsageStats currentApp;
            currentApp = map.get(map.lastKey());
            packageName = currentApp.getPackageName();
        }

        return packageName;
    }

    public boolean permissionGranted() {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    //** this function was created with the help of this StackOverflow post: https://stackoverflow.com/questions/27215013/check-if-my-application-has-usage-access-enabled

}
