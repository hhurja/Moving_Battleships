package Model;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.List;

/**
 * Created by shabina on 10/13/17.
 */

public class AppIconGenerator {
    public HashMap<String, Bitmap> icons;
    public PackageManager pm;
    public AppIconGenerator(PackageManager packageManager) {
        pm = packageManager;
        icons = new HashMap<String, Bitmap>();
        icons = getAppIcon();
    }
    public HashMap<String, Bitmap> getAppIcon() {
        //check this code with some hard work then you will rock
        List<PackageInfo> apps = pm.getInstalledPackages(0);
        HashMap<String, Bitmap> allIcons = new HashMap<String, Bitmap>();
        for(int i=0;i<apps.size();i++) {
            PackageInfo p = apps.get(i);
            String package_name = p.packageName;
            Drawable icon = p.applicationInfo.loadIcon(pm);
            Bitmap bm = drawableToBitmap(icon);
            allIcons.put(package_name, bm);
        }
        return allIcons;
    }
    public Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
