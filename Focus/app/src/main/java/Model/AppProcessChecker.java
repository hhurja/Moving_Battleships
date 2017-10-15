package Model;

import android.app.Activity;
import android.app.Dialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import java.security.Permission;
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

/**
 * Created by shabina on 10/13/17.
 */

public class AppProcessChecker {
    public Context context;
    public PackageManager packageManager;
    public UsageStatsManager statsManager;
    public MainActivity mainActivity;
    public String package_name = "";

    public AppProcessChecker(Context c, PackageManager pm, UsageStatsManager usm, MainActivity ma) {
        context = c;
        packageManager = pm;
        statsManager = usm;
        mainActivity = ma;

        Intent intent_1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent_1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent intent_2 = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent_2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent_1);
        context.startActivity(intent_2);
        AppIconGenerator ap = new AppIconGenerator(packageManager);
    }
    class test extends TimerTask {
        private String p_name;
        test(String str) {
            p_name = str;
        }
        public void run() {
            List<UsageStats> apps = statsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  System.currentTimeMillis() - 1000*1000, System.currentTimeMillis());
            if (apps != null && apps.size() > 0) {
                SortedMap<Long, UsageStats> map = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : apps) {
                    map.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (map != null && !map.isEmpty()) {
                    UsageStats currentApp;
                    currentApp = map.get(map.lastKey());
                    if (currentApp.getPackageName().equals(p_name)) {
                        Intent i = new Intent(mainActivity, DialogActivity.class);
                        mainActivity.startActivity(i);
                    }
                }
            }
        }
    }

    public void blockApplication(String packageName) {
        package_name = packageName;
        Timer timer = new Timer();
        timer.schedule(new test(package_name), 0, 2000);
    }

}
