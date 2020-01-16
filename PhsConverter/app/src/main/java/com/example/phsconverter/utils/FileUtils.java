package com.example.phsconverter.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileUtils {
    private final static String CONVERTED = "Converted";

    public static String geApplicationDataPath(Context context) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getApplicationName(context);
    }

    private static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    private static String getConvertedDirPath(Context context) {
        File convertedDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getApplicationName(context) + File.separator + CONVERTED);

        if (!convertedDir.exists()) {
            if (convertedDir.mkdirs())
                return convertedDir.getAbsolutePath();
        }
        return "";
    }

    public static void writeToFile(String data, Context context, boolean isPhScript) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(getConvertedDirPath(context) + (isPhScript ? ".phs" : ".lab"), Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(Constants.TAG, "File write failed: " + e.toString());
        }
    }
}
